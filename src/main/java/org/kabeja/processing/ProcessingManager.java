/*******************************************************************************
 * Copyright 2010 Simon Mieth
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package org.kabeja.processing;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.kabeja.DraftDocument;
import org.kabeja.parser.ParseException;
import org.kabeja.parser.Parser;
import org.kabeja.processing.event.ProcessingListener;
import org.kabeja.xml.SAXFilter;
import org.kabeja.xml.SAXGenerator;
import org.kabeja.xml.SAXSerializer;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 *
 */
public class ProcessingManager {
	
    private Map saxfilters = new HashMap();
    private Map saxserializers = new HashMap();
    private Map postprocessors = new HashMap();
    private Map pipelines = new HashMap();
    private Map saxgenerators = new HashMap();
    private Map parsers = new HashMap();
    private Map generators = new HashMap();
  
    

    public void addSAXFilter(SAXFilter filter, String name) {
        this.saxfilters.put(name, filter);
    }

    public SAXFilter getSAXFilter(String name) {
        return (SAXFilter) this.saxfilters.get(name);
    }

    public Map getSAXFilters() {
        return this.saxfilters;
    }

    public void addSAXSerializer(SAXSerializer serializer, String name) {
        this.saxserializers.put(name, serializer);
    }

    public SAXSerializer getSAXSerializer(String name) {
        return (SAXSerializer) this.saxserializers.get(name);
    }

    public Map getSAXSerializers() {
        return this.saxserializers;
    }

    public void addPostProcessor(PostProcessor pp, String name) {
        this.postprocessors.put(name, pp);
    }

    public void addParser(Parser parser,String name) {
        this.parsers.put(name,parser);
    }

    public Map getParsers() {
        return this.parsers;
    }

    public Parser getParser(String extension) {
        Iterator i = this.parsers.values().iterator();

        while (i.hasNext()) {
            Parser parser = (Parser) i.next();

            if (parser.supportedExtension(extension)) {
                return parser;
            }
        }

        return null;
    }

    public PostProcessor getPostProcessor(String name) {
        return (PostProcessor) this.postprocessors.get(name);
    }

    public Map getPostProcessors() {
        return this.postprocessors;
    }

    public void addProcessPipeline(ProcessPipeline pp) {
        pp.setProcessorManager(this);
        this.pipelines.put(pp.getName(), pp);
    }

    public ProcessPipeline getProcessPipeline(String name) {
        return (ProcessPipeline) this.pipelines.get(name);
    }

    public Map getProcessPipelines() {
        return this.pipelines;
    }

    public void process(InputStream stream, String extension, Map context,
        String pipeline, OutputStream out) throws ProcessorException {
        Parser parser = this.getParser(extension);

        if (parser != null) {
            try {
                DraftDocument doc = parser.parse(stream, new HashMap());
                this.process(doc, context, pipeline, out);
            } catch (ParseException e) {
                throw new ProcessorException(e);
            }
        }
    }

    public void process(DraftDocument doc, Map context, String pipeline,
        OutputStream out) throws ProcessorException {
        if (this.pipelines.containsKey(pipeline)) {
            ProcessPipeline pp = (ProcessPipeline) this.pipelines.get(pipeline);
            pp.prepare();
            pp.process(doc, context, out);
        } else {
            throw new ProcessorException("No pipeline found for name:" +
                pipeline);
        }
    }

    public void process(DraftDocument doc, Map context, String pipeline,
        String sourceFile) throws ProcessorException {
        if (this.pipelines.containsKey(pipeline)) {
            try {
                ProcessPipeline pp = (ProcessPipeline) this.pipelines.get(pipeline);
                String suffix = pp.getGenerator().getSuffix();
                String file = sourceFile.substring(0,
                        sourceFile.lastIndexOf('.') + 1) + suffix;
                FileOutputStream out = new FileOutputStream(file);
                process(doc, context, pipeline, out);
            } catch (FileNotFoundException e) {
                throw new ProcessorException(e);
            }
        } else {
            throw new ProcessorException("No pipeline found for name:" +
                pipeline);
        }
    }

    public void addSAXGenerator(SAXGenerator saxgenerator, String name) {
        this.saxgenerators.put(name, saxgenerator);
    }

    public SAXGenerator getSAXGenerator(String name) {
        return (SAXGenerator) this.saxgenerators.get(name);
    }

    public Map getGenerators() {
        return this.generators;
    }
    
    public Generator getGenerator(String name){
    	return (Generator)this.generators.get(name);
    }

    public void addGenerator(Generator generator,String name){
    	this.generators.put(name,generator);
    }
    
    
    public void addProcessingListener(ProcessingListener l) {
    }

    public void removeProcessingListener(ProcessingListener l) {
    }
}
