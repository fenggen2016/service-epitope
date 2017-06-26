/*

    epitope-service  T-cell epitope group matching service for HLA-DPB1 locus.
    Copyright (c) 2014-2015 National Marrow Donor Program (NMDP)
    
    This library is free software; you can redistribute it and/or modify it
    under the terms of the GNU Lesser General Public License as published
    by the Free Software Foundation; either version 3 of the License, or (at
    your option) any later version.
    
    This library is distributed in the hope that it will be useful, but WITHOUT
    ANY WARRANTY; with out even the implied warranty of MERCHANTABILITY or
    FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
    License for more details.
    
    You should have received a copy of the GNU Lesser General Public License
    along with this library;  if not, write to the Free Software Foundation,
    Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307  USA.
    
    > http://www.gnu.org/licenses/lgpl.html

*/

package org.nmdp.service.epitope;

import static com.google.common.base.CharMatcher.anyOf;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.nmdp.service.epitope.domain.DetailRace.CAU;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.nmdp.gl.Allele;
import org.nmdp.gl.AlleleList;
import org.nmdp.gl.Genotype;
import org.nmdp.gl.GenotypeList;
import org.nmdp.gl.Haplotype;
import org.nmdp.gl.Locus;
import org.nmdp.gl.client.GlClient;
import org.nmdp.service.epitope.db.DbiManager;
import org.nmdp.service.epitope.service.AllelePair;
import org.nmdp.service.epitope.service.EpitopeService;
import org.nmdp.service.epitope.service.EpitopeServiceImpl;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.io.ByteStreams;



public class EpitopeServiceTestData {

	// some allele group mappings
	//01:01|3
	//02:01|3
	//02:02|3
	//03:01|2
	//04:01|3
	//04:02|3
	//05:01|3
	//06:01|2
	//08:01|2
	//09:01|1
	//10:01|1

	public static Locus aLocus() {
		return new Locus("1", "HLA-DPB1");
	}
	
	public static Locus aLocus(String glstring) {
		return new Locus(glstring, glstring);
	}
	
	public static Allele anAllele() {
		return group3Alleles().get(0);
	}
	
	public static Allele anAllele(String glstring) {
		return new Allele(glstring, glstring, glstring, aLocus());
	}

    public static List<Allele> group0Alleles() {
        return Arrays.asList( 
                anAllele("61:01N"),
                anAllele("64:01N"));
    }

    public static List<Allele> group1Alleles() {
        return Arrays.asList( 
                anAllele("09:01"),
                anAllele("10:01"));
    }

	public static List<Allele> group2Alleles() {
		return Arrays.asList( 
				anAllele("03:01"),
				anAllele("06:01"));
	}

	public static List<Allele> group3Alleles() {
		return Arrays.asList( 
				anAllele("01:01"),
				anAllele("02:01"));
	}

    public static AlleleList anAlleleList(Allele... alleles) {
        return anAlleleList(Arrays.asList(alleles));
    }

    public static AlleleList anAlleleList(List<Allele> alleles) {
        return new AlleleList(Joiner.on("/").join(alleles), alleles);
    }

	public static AlleleList anAlleleList() {
		return anAlleleList(
				group1Alleles().get(0),
				group2Alleles().get(0),
				group3Alleles().get(0));
	}

	public static AlleleList anAlleleList2() {
		return anAlleleList(
				group1Alleles().get(1),
				group2Alleles().get(1),
				group3Alleles().get(1));
	}
	
	public static Haplotype aHaplotype(AlleleList... alleleLists) {
		return new Haplotype(Joiner.on("~").join(alleleLists), Arrays.asList(alleleLists));
	}

	public static Haplotype aHaplotype() {
		return aHaplotype(anAlleleList());
	}
	
	public static Haplotype aHaplotype2() {
		return aHaplotype(anAlleleList2());
	}

	public static Genotype aGenotype(Haplotype... haplotypes) {
		return new Genotype(Joiner.on("+").join(haplotypes), Arrays.asList(haplotypes));
	}
	
	public static Genotype aGenotype(AlleleList al1, AlleleList al2) {
		return new Genotype(Joiner.on("+").join(al1, al2), Arrays.asList(aHaplotype(al1), aHaplotype(al2)));
	}
	
	public static Genotype aGenotype() {
		return aGenotype(aHaplotype(), aHaplotype2()); 
	}
	
	public static URL getMockUrl(InputStream data, boolean zip) throws IOException {
		if (zip) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ZipOutputStream zos = new ZipOutputStream(baos);
			zos.putNextEntry(new ZipEntry("mock.file"));
			ByteStreams.copy(data, zos);
			zos.closeEntry();
			zos.close();
			data = new ByteArrayInputStream(baos.toByteArray());
		}
	    final URLConnection mockConnection = Mockito.mock(URLConnection.class);
	    when(mockConnection.getInputStream()).thenReturn(data);
	    when(mockConnection.getLastModified()).thenReturn(System.currentTimeMillis());
	    final URLStreamHandler handler = new URLStreamHandler() {
	        @Override
	        protected URLConnection openConnection(final URL url) throws IOException {
	            return mockConnection;
	        }
	    };
	    final URL url = new URL("foobar", "foobar", 4242, "", handler);
	    return url;
	}
	
	public static URL getMockUrl(final String data, boolean zip) throws IOException {
		return getMockUrl(new StringBufferInputStream(data), zip);
	}
	
	public static URL getMockUrl(final File file, boolean zip) throws IOException {
		return getMockUrl(new FileInputStream(file), zip);
	}

	public static AllelePair aHomozygousAllelePair() {
		return new AllelePair(group1Alleles().get(0), 1, group1Alleles().get(0), 1, CAU);
	}

	public static AllelePair aHeterozygousAllelePair() {
		return new AllelePair(group1Alleles().get(0), 1, group2Alleles().get(0), 2, CAU);
	}

	public static GenotypeList aGenotypeList() {
		return new GenotypeList(aGenotype().getGlstring(), aGenotype());
	}	
	
	public static GenotypeList aGenotypeList(Genotype... genotypes) {
		return new GenotypeList(Joiner.on("|").join(genotypes), Arrays.asList(genotypes));
	}
	
	public static RuntimeException asRuntimeException(Exception e) {
		if (e instanceof RuntimeException) {
			return (RuntimeException) e;
		}
		RuntimeException re = new RuntimeException("got exception", e) {
			private static final long serialVersionUID = 1L;
			public synchronized Throwable fillInStackTrace() {
				Throwable st = super.fillInStackTrace();
				setStackTrace(Arrays.copyOfRange(st.getStackTrace(), 1, st.getStackTrace().length));
				return this;
			}};
		return re;
	}
	
	public static GlClient getTestGlClient() {
		GlClient glClient = mock(GlClient.class);
		try {
			doAnswer(new Answer<Locus>() {
				@Override public Locus answer(InvocationOnMock invocation) throws Throwable {
					return aLocus(invocation.getArgumentAt(0, String.class));
				}}).when(glClient).createLocus(anyString());
            doAnswer(new Answer<Allele>() {
                @Override public Allele answer(InvocationOnMock invocation) throws Throwable {
                    return anAllele(invocation.getArgumentAt(0, String.class));
                }}).when(glClient).createAllele(anyString());
            doAnswer(new Answer<AlleleList>() {
                @Override public AlleleList answer(InvocationOnMock invocation) throws Throwable {
                    List<Allele> al = FluentIterable.from(Splitter.on(anyOf(",/"))
                            .split(invocation.getArgumentAt(0, String.class)))
                            .transform(e -> anAllele(e)).toList();
                    return anAlleleList(al);
                }}).when(glClient).createAlleleList(anyString());
		} catch (Exception e) {
			throw asRuntimeException(e);
		}
		return glClient;
	}
	
	public static Function<String, String> getTestGlStringFilter() {
		return a -> a;
	}

	public static DbiManager getTestDbiManager() {
		DbiManager mock = mock(DbiManager.class);
		List<List<Allele>> alleleLists = Arrays.asList(
				group0Alleles(), group1Alleles(), group2Alleles(), group3Alleles());

		doAnswer(new Answer<List<String>>() {
			@Override public List<String> answer(InvocationOnMock invocation) throws Throwable {
				return Arrays.asList(invocation.getArgumentAt(0, String.class));
			}
		}).when(mock).getGGroupAllelesForAllele(anyString());
		
		doAnswer(new Answer<List<String>>() {
			@Override public List<String> answer(InvocationOnMock invocation) throws Throwable {
				return alleleLists.get(invocation.getArgumentAt(0, Integer.class))
						.stream()
						.map(a -> a.getGlstring())
						.collect(Collectors.toList());
			}
		}).when(mock).getAllelesForImmuneGroup(any(Integer.class));

		doAnswer(new Answer<List<String>>() {
			@Override public List<String> answer(InvocationOnMock invocation) throws Throwable {
				return alleleLists
						.stream()
						.flatMap(al -> al.stream())
						.map(a -> a.getGlstring())
						.collect(Collectors.toList());
			}
		}).when(mock).getAllelesForLocus(any(String.class));

		doAnswer(new Answer<Map<String, Integer>>() {
			@Override public Map<String, Integer> answer(InvocationOnMock invocation) throws Throwable {
				Map<String, Integer> map = new HashMap<>();
				for (int i = 0; i < alleleLists.size(); i++) {
					int group = i;
					alleleLists.get(i).forEach(a -> map.put(a.getGlstring(), group));
				}
				return map;
			}
		}).when(mock).getAlleleGroupMap();
		
		
		
		return mock;
	}
	
	public static EpitopeService getTestEpitopeService() {
		EpitopeServiceImpl service = new EpitopeServiceImpl(getTestGlClient(), getTestGlStringFilter(), getTestDbiManager());
		service.buildImmuneGroupMaps();
		return service;
	}
}
