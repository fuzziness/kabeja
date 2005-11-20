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

import org.kabeja.dxf.DXFDocument;

import org.kabeja.xml.SAXFilter;
import org.kabeja.xml.SAXSerializer;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import java.util.HashMap;
import java.util.Map;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 *
 */
public class ProcessorManager {
    private Map saxfilters = new HashMap();
    private Map saxserializers = new HashMap();
    private Map postprocessors = new HashMap();
    private Map pipelines = new HashMap();

    public void addSAXFilter(SAXFilter filter, String name) {
        this.saxfilters.put(name, filter);
    }

    public SAXFilter getSAXFilter(String name) {
        return (SAXFilter) this.saxfilters.get(name);
    }

    public void addSAXSerializer(SAXSerializer serializer, String name) {
        this.saxserializers.put(name, serializer);
    }

    public SAXSerializer getSAXSerializer(String name) {
        return (SAXSerializer) this.saxserializers.get(name);
    }

    public void addPostProcessor(PostProcessor pp, String name) {
        this.postprocessors.put(name, pp);
    }

    public PostProcessor getPostProcessor(String name) {
        return (PostProcessor) this.postprocessors.get(name);
    }

    public void addProcessPipeline(ProcessPipeline pp) {
        pp.setProcessorManager(this);
        this.pipelines.put(pp.getName(), pp);
    }

    public ProcessPipeline getProcessPipeline(String name) {
        return (ProcessPipeline) this.pipelines.get(name);
    }

    public void process(DXFDocument doc, Map context, String pipeline,
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

    public void process(DXFDocument doc, Map context, String pipeline,
        String sourceFile) throws ProcessorException {
        if (this.pipelines.containsKey(pipeline)) {
            try {
                ProcessPipeline pp = (ProcessPipeline) this.pipelines.get(pipeline);
                String suffix = pp.getSAXSerializer().getSuffix();
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
}
