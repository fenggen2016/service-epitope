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

package org.nmdp.service.epitope.resource.impl;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.nmdp.service.epitope.EpitopeServiceTestData.anAllele;
import static org.nmdp.service.epitope.EpitopeServiceTestData.anAlleleList;
import static org.nmdp.service.epitope.EpitopeServiceTestData.getTestEpitopeService;
import static org.nmdp.service.epitope.EpitopeServiceTestData.getTestGlClient;
import static org.nmdp.service.epitope.EpitopeServiceTestData.getTestGlStringFilter;
import static org.nmdp.service.epitope.EpitopeServiceTestData.group1Alleles;
import static org.nmdp.service.epitope.EpitopeServiceTestData.group2Alleles;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nmdp.gl.Allele;
import org.nmdp.gl.client.GlClient;
import org.nmdp.service.epitope.resource.AlleleListRequest;
import org.nmdp.service.epitope.resource.AlleleView;
import org.nmdp.service.epitope.service.EpitopeService;
import org.nmdp.service.epitope.service.FrequencyService;

import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;

@RunWith(MockitoJUnitRunner.class)
public class AlleleResourceTest {

	EpitopeService epitopeService = getTestEpitopeService();
	GlClient glClient = getTestGlClient(); 
	Function<String, String> glStringFilter = getTestGlStringFilter();

	@Mock
	FrequencyService freqService;

	
	private AlleleResource resource;

	@Before
	public void setUp() throws Exception {
		resource = new AlleleResource(epitopeService, glClient, glStringFilter, freqService);
	}

	public List<String> allelesToStrings(List<Allele> alleleList) {
		return alleleList.stream().map(a -> a.getGlstring()).collect(toList());
	}

	public List<String> alleleViewsToStrings(List<AlleleView> alleleViewList) {
		return alleleViewList.stream().map(av -> av.getAllele()).collect(toList());
	}
	
	@Test
	public void testGetAlleles_NoInputs() throws Exception {
		List<String> test = alleleViewsToStrings(resource.getAlleles(null, null, null));
		List<String> expect = allelesToStrings(epitopeService.getAllAlleles());
		assertThat(test, containsInAnyOrder(expect.toArray()));
	}

	@Test
	public void testGetAlleles_Alleles() throws Exception {
		List<Allele> al = anAlleleList().getAlleles();
		String gls = Joiner.on(",").join(al);
		List<String> test = alleleViewsToStrings(resource.getAlleles(gls, null, null));
		List<String> expect = allelesToStrings(al);
		assertThat(test, contains(expect.toArray()));
	}

	@Test
	public void testGetAlleles_UnknownGroup() throws Exception {
		List<Allele> al = anAlleleList().getAlleles();
		String gls = Joiner.on(",").join(al);
		List<String> test = alleleViewsToStrings(resource.getAlleles(gls, null, null));
		List<String> expect = allelesToStrings(al);
		assertThat(test, contains(expect.toArray()));
	}

	@Test
	public void testGetAlleles_Groups() throws Exception {
		List<String> test = alleleViewsToStrings(resource.getAlleles(null, "1,2", null));
		List<String> expect = allelesToStrings(
		        FluentIterable.from(group1Alleles()).append(group2Alleles()).toList());
		assertThat(test, contains(expect.toArray()));
	}
	
	@Test
	public void testGetAlleles_AlleleListRequest_NoInputs() throws Exception {
		AlleleListRequest r = new AlleleListRequest(null, null, null);
		List<String> test = alleleViewsToStrings(resource.getAlleles(r));
		assertThat(test, emptyIterable());
	}

	@Test
	public void testGetAlleles_AlleleListRequest_Alleles() throws Exception {
		List<String> al = anAlleleList().getAlleles().stream().map(a -> a.getGlstring()).collect(Collectors.toList());
		String gls = Joiner.on(",").join(al);
		AlleleListRequest r = new AlleleListRequest(al, null, null);
		List<String> test = alleleViewsToStrings(resource.getAlleles(gls, null, null));
		assertThat(test, contains(al.toArray()));
	}

	@Test
	public void testGetAlleles_AlleleListRequest_Groups() throws Exception {
		AlleleListRequest r = new AlleleListRequest(null, Arrays.asList(1, 2), null);
		List<String> test = alleleViewsToStrings(resource.getAlleles(r));
		List<String> expect = allelesToStrings(
		        FluentIterable.from(group1Alleles()).append(group2Alleles()).toList());
		assertThat(test, contains(expect.toArray()));
	}
	
	@Test
	public void testGetAllele() throws Exception {
		Allele a = anAllele();
		AlleleView test = resource.getAllele(a.getGlstring(), null);
		assertThat(test.getGroup(), equalTo(epitopeService.getImmuneGroupForAllele(a)));
	}

}
