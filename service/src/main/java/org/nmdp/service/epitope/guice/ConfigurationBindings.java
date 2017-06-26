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

package org.nmdp.service.epitope.guice;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;

public @interface ConfigurationBindings {
    
	/**
	 * number of milliseconds between refreshes of upstream data sources (alleles, g groups, immune groups, etc)
	 */
	@BindingAnnotation
    @Target({FIELD, PARAMETER, METHOD})
    @Retention(RUNTIME)
	@interface RefreshMillis {}
	
	/**
	 * number of milliseconds to cache immunogenity groups for
	 */
	@BindingAnnotation
    @Target({FIELD, PARAMETER, METHOD})
    @Retention(RUNTIME)
	@interface GroupCacheMillis {}

	/**
	 * number of milliseconds to cache immunogenity groups for
	 */
	@BindingAnnotation
	@Target({FIELD, PARAMETER, METHOD})
	@Retention(RUNTIME)
	@interface HlaGroupCacheMillis {}

	/**
	 * number of milliseconds to cache immunogenity groups for
	 */
	@BindingAnnotation
	@Target({FIELD, PARAMETER, METHOD})
	@Retention(RUNTIME)
	@interface HlaGroupCacheSize {}

	/**
	 * number of milliseconds to cache immunogenity groups for
	 */
	@BindingAnnotation
	@Target({FIELD, PARAMETER, METHOD})
	@Retention(RUNTIME)
	@interface AlleleFilterCacheMillis {}

	/**
	 * number of milliseconds to cache immunogenity groups for
	 */
	@BindingAnnotation
	@Target({FIELD, PARAMETER, METHOD})
	@Retention(RUNTIME)
	@interface AlleleFilterCacheSize {}

	/**
	 * number of milliseconds to cache genotype lists for
	 */
	@BindingAnnotation
    @Target({FIELD, PARAMETER, METHOD})
    @Retention(RUNTIME)
	@interface GlCacheMillis {}

	/**
	 * size of the genotype list cache
	 */
	@BindingAnnotation
    @Target({FIELD, PARAMETER, METHOD})
    @Retention(RUNTIME)
	@interface GlCacheSize {}
	
	/**
	 * number of milliseconds to cache allele codes for
	 */
	@BindingAnnotation
    @Target({FIELD, PARAMETER, METHOD})
    @Retention(RUNTIME)
	@interface AlleleCodeCacheMillis {}

	/**
	 * number of milliseconds to cache allele codes for
	 */
	@BindingAnnotation
    @Target({FIELD, PARAMETER, METHOD})
    @Retention(RUNTIME)
	@interface AlleleCodeCacheSize {}
	
	/**
	 * number of milliseconds to cache frequencies for
	 */
	@BindingAnnotation
    @Target({FIELD, PARAMETER, METHOD})
    @Retention(RUNTIME)
	@interface FrequencyCacheMillis {}

	/**
	 * number of milliseconds to cache frequencies for
	 */
	@BindingAnnotation
    @Target({FIELD, PARAMETER, METHOD})
    @Retention(RUNTIME)
	@interface FrequencyCacheSize {}

	/**
	 * URL for NMDP allele list file, published daily
	 * (typically: https://bioinformatics.bethematchclinical.org/HLA/alpha.v3.zip)
	 */
	@BindingAnnotation
    @Target({FIELD, PARAMETER, METHOD})
    @Retention(RUNTIME)
	@interface NmdpV3AlleleCodeUrls {}
	
	/**
	 * Refresh interval for NMDP allele list file in milliseconds 
	 */
	@BindingAnnotation
    @Target({FIELD, PARAMETER, METHOD})
    @Retention(RUNTIME)
	@interface NmdpV3AlleleCodeRefreshMillis {}

    /**
     * If null, use LocalGlClient.  If not null, use JsonGlClientModule and enable the following: 
     * <ul>
     * <li>GL Strings are created using the provided namespace URL, 
     * <li>GL Service IDs are accepted as input,
     * <li>Immunogenicity groups are retrieved from the provided namespace URL given suffixes group1Suffix, group2Suffix, group3Suffix)
     * <li>GL Service IDs are optionally lifted over to internal namespace (if liftoverServiceUrl is provided).
     * </ul>
     */
	@BindingAnnotation
    @Target({FIELD, PARAMETER, METHOD})
    @Retention(RUNTIME)
	@interface NamespaceUrl {}
	
	/**
     * if namespace is not null, use this url to retrieve group 1 allele list, 
     * otherwise retrieve from internal sqlite db.
     */
	@BindingAnnotation
    @Target({FIELD, PARAMETER, METHOD})
    @Retention(RUNTIME)
	@interface Group1Suffix {}
	
	/**
     * if namespace is not null, use this url to retrieve group 2 allele list, 
     * otherwise retrieve from internal sqlite db.
     */
    @BindingAnnotation
    @Target({FIELD, PARAMETER, METHOD})
    @Retention(RUNTIME)
	@interface Group2Suffix {}

	/**
     * if namespace is not null, use this url to retrieve group 3 allele list, 
     * otherwise retrieve from internal sqlite db.
     */
    @BindingAnnotation
    @Target({FIELD, PARAMETER, METHOD})
    @Retention(RUNTIME)
	@interface Group3Suffix {}

    /**
     * if not null (and if namespace not null, enabling , lift over input GL service IDs 
     * to specified namespace, otherwise error if provided namespace doesn't match internal
     */
    @BindingAnnotation
    @Target({FIELD, PARAMETER, METHOD})
    @Retention(RUNTIME)
	@interface LiftoverServiceUrl {}

    /**
     * if true, accept allele codes in GL string input (e.g. DPB1*ABCD+DPB1*EFGH) and resolve 
     */
    @BindingAnnotation
    @Target({FIELD, PARAMETER, METHOD})
    @Retention(RUNTIME)
    @interface ResolveCodes {}

    /**
     * if true, accept allele codes in GL string input (e.g. DPB1*ABCD+DPB1*EFGH) and resolve 
     */
    @BindingAnnotation
    @Target({FIELD, PARAMETER, METHOD})
    @Retention(RUNTIME)
    @interface BaselineAlleleFrequency {}

    /**
     * threshold above which to consider possible outcomes as reported match grade 
     */
    @BindingAnnotation
    @Target({FIELD, PARAMETER, METHOD})
    @Retention(RUNTIME)
    @interface MatchProbabilityPrecision {}

	/**
	 * URLs for IMGT HLA XML file, published quarterly
	 * (typically: ftp://ftp.ebi.ac.uk/pub/databases/ipd/imgt/hla/xml/hla_ambigs.xml.zip)
	 */
	@BindingAnnotation
	@Target({FIELD, PARAMETER, METHOD})
	@Retention(RUNTIME)
	@interface ImgtHlaUrls {}

    /**
     * URLs for HLA allele file, published quarterly
     * (typically: ftp://ftp.ebi.ac.uk/pub/databases/ipd/imgt/hla/Allelelist.txt)
     */
    @BindingAnnotation
    @Target({FIELD, PARAMETER, METHOD})
    @Retention(RUNTIME)
    @interface HlaAlleleUrls {}

	/**
	 * URLs for HLA allele protein fasta file, published quarterly
	 * (typically: ftp://ftp.ebi.ac.uk/pub/databases/ipd/imgt/hla/hla_prot.fasta)
	 */
	@BindingAnnotation
	@Target({FIELD, PARAMETER, METHOD})
	@Retention(RUNTIME)
	@interface HlaProtUrls {}

	/**
	 * URLs for HLA allele protein fasta file, published quarterly
	 * (typically: ftp://ftp.ebi.ac.uk/pub/databases/ipd/imgt/hla/hla_prot.fasta)
	 */
	@BindingAnnotation
	@Target({FIELD, PARAMETER, METHOD})
	@Retention(RUNTIME)
	@interface GlstringTransformer {}

	/**
	 * URLs for HLA allele protein fasta file, published quarterly
	 * (typically: ftp://ftp.ebi.ac.uk/pub/databases/ipd/imgt/hla/hla_prot.fasta)
	 */
	@BindingAnnotation
	@Target({FIELD, PARAMETER, METHOD})
	@Retention(RUNTIME)
	@interface MatchGlstringTransformer {}

	/**
	 * URLs for HLA allele protein fasta file, published quarterly
	 * (typically: ftp://ftp.ebi.ac.uk/pub/databases/ipd/imgt/hla/hla_prot.fasta)
	 */
	@BindingAnnotation
	@Target({FIELD, PARAMETER, METHOD})
	@Retention(RUNTIME)
	@interface GenotypeListResolver {}

	/**
	 * URLs for HLA allele protein fasta file, published quarterly
	 * (typically: ftp://ftp.ebi.ac.uk/pub/databases/ipd/imgt/hla/hla_prot.fasta)
	 */
	@BindingAnnotation
	@Target({FIELD, PARAMETER, METHOD})
	@Retention(RUNTIME)
	@interface ImmuneGroupResolver {}

	/**
	 * URLs for HLA allele protein fasta file, published quarterly
	 * (typically: ftp://ftp.ebi.ac.uk/pub/databases/ipd/imgt/hla/hla_prot.fasta)
	 */
	@BindingAnnotation
	@Target({FIELD, PARAMETER, METHOD})
	@Retention(RUNTIME)
	@interface AlleleCodeResolver {}

}
