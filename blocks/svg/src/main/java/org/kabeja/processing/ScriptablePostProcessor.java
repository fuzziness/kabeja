/*
   Copyright 2007 Simon Mieth

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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

import org.kabeja.dxf.DXFDocument;
import org.kabeja.processing.scripting.ScriptEngine;
import org.kabeja.processing.scripting.ScriptException;
import org.kabeja.processing.scripting.impl.JavaScriptEngine;


public class ScriptablePostProcessor extends AbstractPostProcessor {
    public final static String PROPERTY_SCRIPT_TYPE = "type";
    public final static String PROPERTY_SCRIPT_SRC = "src";
    public final static String PROPERTY_SCRIPT_INPUTSTREAM = "inputstream";
    protected ScriptEngine engine;
    protected InputStream scriptStream;

    public void process(DXFDocument doc, Map context) throws ProcessorException {
        ScriptEngine engine = new JavaScriptEngine();

        try {
            engine.eval(doc, this.scriptStream);
        } catch (ScriptException e) {
            throw new ProcessorException(e);
        }
    }

    public void setProperties(Map properties) {
        try {
            if (properties.containsKey(PROPERTY_SCRIPT_SRC)) {
                this.scriptStream = new FileInputStream((String) properties.get(
                            PROPERTY_SCRIPT_SRC));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (properties.containsKey(PROPERTY_SCRIPT_INPUTSTREAM)) {
            this.scriptStream = (InputStream) properties.get(PROPERTY_SCRIPT_INPUTSTREAM);
        }
    }
}
