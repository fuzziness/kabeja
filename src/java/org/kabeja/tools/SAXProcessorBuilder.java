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
package org.kabeja.tools;

import org.kabeja.processing.PostProcessor;
import org.kabeja.processing.PostProcessorConfig;
import org.kabeja.processing.ProcessPipeline;
import org.kabeja.processing.ProcessorManager;
import org.kabeja.xml.AggregatorGenerator;
import org.kabeja.xml.SAXFilter;
import org.kabeja.xml.SAXGenerator;
import org.kabeja.xml.SAXSerializer;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.io.InputStream;

import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 *
 */
public class SAXProcessorBuilder implements ContentHandler {
    public static final String ELEMENT_CONFIGURATION = "configuration";
    public static final String ELEMENT_PARSER = "parser";
    public static final String ELEMENT_SAXSERIALIZER = "serializer";
    public static final String ELEMENT_SAXSERIALIZERS = "serializers";
    public static final String ELEMENT_SAXFILTER = "filter";
    public static final String ELEMENT_FILTER = "filter";
    public static final String ELEMENT_SAXFILTERS = "filters";
    public static final String ELEMENT_PIPELINE = "pipeline";
    public static final String ELEMENT_PIPELINES = "pipelines";
    public static final String ELEMENT_SERIALIZE = "serialize";
    public static final String ELEMENT_PROPERTY = "property";
    public static final String ELEMENT_POSTPROCESSOR = "postprocessor";
    public static final String ELEMENT_POSTPROCESS = "postprocess";
    public static final String ELEMENT_AGGREGATE = "aggregate";
    public static final String ELEMENT_SAXGENERATOR = "generator";
    public static final String ELEMENT_GENERATE = "generate";
    public static final String ATTRIBUTE_NAME = "name";
    public static final String ATTRIBUTE_CLASS = "class";
    public static final String ATTRIBUTE_VALUE = "value";
    private ProcessorManager manager;
    private SAXFilter saxfilter;
    private SAXSerializer saxserializer;
    private PostProcessor postprocessor;
    private SAXGenerator saxgenerator;
    private AggregatorGenerator aggregator;
    private Map properties;
    private StringBuffer buf = new StringBuffer();
    private String name;
    private ProcessPipeline pipeline;
    private boolean config = false;
    private boolean aggregate = false;
    

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#characters(char[], int, int)
     */
    public void characters(char[] ch, int start, int length)
        throws SAXException {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#endDocument()
     */
    public void endDocument() throws SAXException {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public void endElement(String namespaceURI, String localName, String qName)
        throws SAXException {
    	
        if (ELEMENT_SAXFILTER.equals( localName ) && this.config) {
        	
            this.saxfilter.setProperties(properties);
            this.manager.addSAXFilter(this.saxfilter, this.name);
            
        } else if (ELEMENT_SAXSERIALIZER.equals( localName )) {
        	
            this.saxserializer.setProperties(this.properties);
            this.manager.addSAXSerializer(this.saxserializer, this.name);
            
        } else if (ELEMENT_PIPELINE.equals( localName )) {
        	
            this.manager.addProcessPipeline(this.pipeline);
            
        } else if (ELEMENT_SERIALIZE.equals( localName )) {
            this.pipeline.setSAXSerializer(this.manager.getSAXSerializer(
                    this.name));
            
            this.pipeline.setSerializerProperties(this.properties);
            
            
        } else if (ELEMENT_FILTER.equals( localName )) {
        	
            SAXFilterConfig config = new SAXFilterConfig(this.properties);
            config.setFilterName(this.name);
            this.pipeline.addSAXFilterConfig(config);
            
        } else if (ELEMENT_POSTPROCESS.equals( localName )) {
        	
            PostProcessorConfig config = new PostProcessorConfig(this.properties);
            config.setPostProcessorName(this.name);
            this.pipeline.addPostProcessorConfig(config);
            
        } else if (ELEMENT_POSTPROCESSOR.equals( localName )) {
        	
            this.postprocessor.setProperties(this.properties);
            this.manager.addPostProcessor(this.postprocessor, this.name);
            
        } else if (ELEMENT_CONFIGURATION.equals( localName )) {
        	
            this.config = false;
            
        }else if (ELEMENT_GENERATE.equals( localName )) {
        	if(this.aggregate){
        	    this.aggregator.addSAXGenerator(this.manager.getSAXGenerator(
                    this.name));
        	}else{
            this.pipeline.setSAXGenerator(this.manager.getSAXGenerator(
                    this.name));
                    
        	}
        }else if (ELEMENT_SAXGENERATOR.equals( localName )) {
        	
            this.saxgenerator.setProperties(this.properties);
            this.manager.addSAXGenerator(this.saxgenerator, this.name);
            
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
     */
    public void endPrefixMapping(String prefix) throws SAXException {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
     */
    public void ignorableWhitespace(char[] ch, int start, int length)
        throws SAXException {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String,
     *      java.lang.String)
     */
    public void processingInstruction(String target, String data)
        throws SAXException {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
     */
    public void setDocumentLocator(Locator locator) {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
     */
    public void skippedEntity(String name) throws SAXException {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#startDocument()
     */
    public void startDocument() throws SAXException {
        manager = new ProcessorManager();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String,
     *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String namespaceURI, String localName,
        String qName, Attributes atts) throws SAXException {
   
    
    	if (ELEMENT_SAXFILTER.equals( localName ) && this.config) {
        	
            this.properties = new HashMap();
            name = atts.getValue(ATTRIBUTE_NAME);
            saxfilter = (SAXFilter) createInstance(atts.getValue(
                        ATTRIBUTE_CLASS));
            
        } else if (ELEMENT_SAXSERIALIZER.equals( localName )) {
        	
        	this.properties = new HashMap();
            name = atts.getValue(ATTRIBUTE_NAME);
            saxserializer = (SAXSerializer) createInstance(atts.getValue(
                        ATTRIBUTE_CLASS));
            
        } else if (ELEMENT_POSTPROCESSOR.equals( localName )) {
        	
            this.properties = new HashMap();
            this.name = atts.getValue(ATTRIBUTE_NAME);
            String clazz = (atts.getValue(ATTRIBUTE_CLASS));
            postprocessor = (PostProcessor) createInstance(clazz);
            
        } else if (ELEMENT_PIPELINE.equals( localName )) {
        	this.aggregate=false;
            this.pipeline = new ProcessPipeline();
            this.pipeline.setName(atts.getValue(ATTRIBUTE_NAME));
            
        } else if (ELEMENT_SERIALIZE.equals( localName )) {
        	
            this.properties = new HashMap();
            this.name = atts.getValue(ATTRIBUTE_NAME);
            
        } else if (ELEMENT_FILTER.equals( localName )) {
        	
            this.properties = new HashMap();
            name = atts.getValue(ATTRIBUTE_NAME);
            
        } else if (ELEMENT_PROPERTY.equals( localName )) {
        	
            this.properties.put(atts.getValue(ATTRIBUTE_NAME),
                atts.getValue(ATTRIBUTE_VALUE));
            
        } else if (ELEMENT_POSTPROCESS.equals( localName )) {
        	
            this.properties = new HashMap();
            name = atts.getValue(ATTRIBUTE_NAME);
            
        } else if (ELEMENT_CONFIGURATION.equals( localName )) {
        	
            this.config = true;
            
        }else if (ELEMENT_SAXGENERATOR.equals( localName )) {
        	
            this.properties = new HashMap();
            this.name = atts.getValue(ATTRIBUTE_NAME);
            String clazz = (atts.getValue(ATTRIBUTE_CLASS));
            this.saxgenerator = (SAXGenerator) createInstance(clazz);
            
        }else if (ELEMENT_GENERATE.equals( localName )) {
        	
          //  this.properties = new HashMap();
            this.name = atts.getValue(ATTRIBUTE_NAME);
            
        } else if (ELEMENT_AGGREGATE.equals( localName )) {
        	this.aggregate=true;
        	this.aggregator = new AggregatorGenerator();
        	this.pipeline.setSAXGenerator(this.aggregator);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String,
     *      java.lang.String)
     */
    public void startPrefixMapping(String prefix, String uri)
        throws SAXException {
    }

    public ProcessorManager getManager() {
        return this.manager;
    }

    protected Object createInstance(String clazz) {
        try {
            Class cl = this.getClass().getClassLoader().loadClass(clazz);
            Object obj = cl.newInstance();

            return obj;
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    /**
     *
     * @param in
     *            the InputStream
     * @return The DXFParser build from the XML description
     */
    public static ProcessorManager buildFromStream(InputStream in) {
        SAXProcessorBuilder builder = new SAXProcessorBuilder();

        try {
        	
        	SAXParserFactory factory = SAXParserFactory.newInstance();
        	factory.setNamespaceAware(true);
        	XMLReader saxparser = factory.newSAXParser().getXMLReader();
        	
        
            saxparser.setContentHandler(builder);
            saxparser.parse(new InputSource(in));
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException ioe) {
        	ioe.printStackTrace();
        }catch(ParserConfigurationException e){
        	 e.printStackTrace();
        }

        return builder.getManager();
    }
}
