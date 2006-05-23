/*
   Copyright 2005 Simon Mieth

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package org.kabeja.processing;

import org.kabeja.dxf.DXFDocument;

import org.kabeja.tools.SAXFilterConfig;
import org.kabeja.xml.SAXFilter;
import org.kabeja.xml.SAXSerializer;
import org.kabeja.xml.SAXGenerator;

import org.xml.sax.ContentHandler;

import java.io.OutputStream;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 *
 */
public class ProcessPipeline {
    private ProcessorManager manager;
    private List postProcessors = new ArrayList();
    private List filters = new ArrayList();
    private SAXGenerator generator;
    private Map serializerProperties;
    private SAXSerializer serializer;
    private String name;

    public void process(DXFDocument doc, Map context, OutputStream out)
        throws ProcessorException {
    	
        ContentHandler handler = null;

        if (this.filters.size() > 0) {
            Iterator i = filters.iterator();
            SAXFilter last = getSAXFilter((SAXFilterConfig) i.next());

            while (i.hasNext()) {
                SAXFilter f = getSAXFilter((SAXFilterConfig) i.next());
                f.setContentHandler(last);
                last = f;
            }

            last.setContentHandler(this.serializer);
            handler = last;
        } else {
            // no filter
            handler = this.serializer;
        }

        //postprocess
        Iterator i = this.postProcessors.iterator();

        while (i.hasNext()) {
            PostProcessor pp = getPostProcessor((PostProcessorConfig) i.next());
            pp.process(doc, context);
        }

        this.serializer.setProperties(this.serializerProperties);

        //invoke the filter and serialier
        this.serializer.setOutput(out);
        this.generator.generate(doc, handler);
       // doc.toSAX(handler, context);
    }

    /**
     * @return Returns the serializer.
     */
    public SAXSerializer getSAXSerializer() {
        return serializer;
    }

    /**
     * @param serializer
     *            The serializer to set.
     */
    public void setSAXSerializer(SAXSerializer serializer) {
        this.serializer = serializer;
    }

    /**
     * @return Returns the manager.
     */
    public ProcessorManager getProcessorManager() {
        return manager;
    }

    /**
     * @param manager
     *            The manager to set.
     */
    public void setProcessorManager(ProcessorManager manager) {
        this.manager = manager;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    public void prepare() {
    }

    protected SAXFilter getSAXFilter(SAXFilterConfig config) {
        SAXFilter f = this.manager.getSAXFilter(config.getFilterName());
        f.setProperties(config.getProperties());

        return f;
    }

    protected PostProcessor getPostProcessor(PostProcessorConfig config) {
        PostProcessor pp = this.manager.getPostProcessor(config.getPostProcessorName());
        pp.setProperties(config.getProperties());

        return pp;
    }

    public void addSAXFilterConfig(SAXFilterConfig config) {
        this.filters.add(config);
    }

    public void addPostProcessorConfig(PostProcessorConfig config) {
        this.postProcessors.add(config);
    }

    /**
     * @return Returns the serializerProperties.
     */
    public Map getSerializerProperties() {
        return serializerProperties;
    }

    /**
     * @param serializerProperties The serializerProperties to set.
     */
    public void setSerializerProperties(Map serializerProperties) {
        this.serializerProperties = serializerProperties;
    }
    
    
    public void setSAXGenerator(SAXGenerator generator){
    	this.generator=generator;
    }
    
    public SAXGenerator getSAXGenerator(){
    	return this.generator;
    }
}
