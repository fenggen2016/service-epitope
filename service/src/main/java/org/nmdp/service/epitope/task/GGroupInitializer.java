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

package org.nmdp.service.epitope.task;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import org.nmdp.service.epitope.db.DbiManager;
import org.nmdp.service.epitope.db.GGroupRow;
import org.nmdp.service.epitope.guice.ConfigurationBindings.HlaAmbigUrls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class GGroupInitializer {

    static final String ns = "http://www.example.org/ambig-aw";
    static final QName geneQn = new QName(ns, "gene");
    static final QName geneSystemAttrQn = new QName("geneSystem");
    static final QName nameAttrQn = new QName("name");
    static final QName gGroupsListQn = new QName(ns, "gGroupsList");
    static final QName gGroupQn = new QName(ns, "gGroup");
    static final QName gGroupAlleleQn = new QName(ns, "gGroupAllele");
    Logger logger = LoggerFactory.getLogger(getClass());
    
    DbiManager dbiManager;
	private URL[] urls;

    @Inject
    public GGroupInitializer(@HlaAmbigUrls URL[] urls, DbiManager dbiManager) {
		this.dbiManager = dbiManager;
		this.urls = urls;
    }

    public void loadGGroups() {
    	logger.info("loading G-groups");
        Long datasetDate = dbiManager.getDatasetDate("hla_g_group");
        if (null == datasetDate) datasetDate = 0L;
        URLProcessor urlProcessor = new URLProcessor(urls, true);
        datasetDate = urlProcessor.process(is -> {
        	XMLInputFactory xmlif = XMLInputFactory.newInstance();
            try {
                XMLEventReader xmler = xmlif.createXMLEventReader(is);
                ElementReader er = new ElementReader(xmler);
                while (er.hasNextStartElement()) {
                    StartElement se = er.nextStartElement();
                    if (se.getName().equals(geneQn)
                            && se.getAttributeByName(geneSystemAttrQn).getValue().equals("HLA")
                            && se.getAttributeByName(nameAttrQn).getValue().matches("(HLA-)?DPB1"))
                    {
                        while (er.hasNextStartElement()) {
                            se = er.nextStartElement();
                            if (se.getName().equals(gGroupsListQn)) {
                                dbiManager.loadGGroups(createGGroupsIterator(er), true);
                                return;
                            }
                        }
                    }
                }
                throw new RuntimeException("failed to parse G groups file (hla-ambigs)");
            } catch (RuntimeException e) {
            	throw e;
            } catch (Exception e) {
                throw new RuntimeException("failed to load G-groups", e);
            } finally {
            	try { 
            		is.close(); 
            	} catch (IOException e) { 
            		throw new RuntimeException("failed to close stream", e); 
            	} 
            }
        }, datasetDate);
        dbiManager.updateDatasetDate("hla_g_group", datasetDate);
        logger.debug("done loading G-groups");
    }

    // xmlns:tns="http://www.example.org/ambig-aw"
    //<tns:gene geneSystem="HLA" name="DPB1">
    //  <tns:gGroupsList>
    //    <tns:gGroup name="DPB1*01:01:01G" gid="HGG0198">
    //      <tns:gGroupAllele name="DPB1*01:01:01" alleleid="HLA00514" />
    
    private Iterator<GGroupRow> createGGroupsIterator(ElementReader er) throws XMLStreamException {
        return new Iterator<GGroupRow>() {
            String group = null;
            GGroupRow row = getNextRow(); // if null, iterator is exhausted
            @Override
            public boolean hasNext() {
                return null != row;
            }
            @Override
            public GGroupRow next() {
                if (null == row) throw new NoSuchElementException();
                try {
                    return row;
                } finally {
                    fetchNextRow();
                }
            }
            private void fetchNextRow() {
                if (row == null) return;
                row = getNextRow();
            }
            private GGroupRow getNextRow() { 
                try {
                    if (!er.hasNextStartElement()) return null;
                    StartElement se = er.nextStartElement();
                    if (se.getName().equals(gGroupQn)) {
                        group = se.getAttributeByName(nameAttrQn).getValue();
                        se = er.nextStartElement();
                    }
                    if (se.getName().equals(gGroupAlleleQn)) {
                        return new GGroupRow(group, se.getAttributeByName(nameAttrQn).getValue());
                    } else {
                        return null;
                    }
                } catch (XMLStreamException e) {
                    throw new IllegalStateException("failed to parse g groups", e);
                }
            }
        };
    }
            
}
