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
package org.kabeja.processing.scripting.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.kabeja.dxf.DXFDocument;
import org.kabeja.processing.scripting.ScriptEngine;
import org.kabeja.processing.scripting.ScriptException;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;


public class JavaScriptEngine implements ScriptEngine {
    public void eval(DXFDocument doc, String script) throws ScriptException {
        Context ctx = Context.enter();
        Scriptable scope = ctx.initStandardObjects();
        Object jsOut = Context.javaToJS(doc, scope);
        ScriptableObject.putProperty(scope, "dxf", jsOut);

        Object result = ctx.evaluateString(scope, script, "<cmd>", 1, null);
        Context.exit();
    }

    public void eval(DXFDocument doc, InputStream script)
        throws ScriptException {
        try {
            Context ctx = Context.enter();
            Scriptable scope = ctx.initStandardObjects();
            Object jsOut = Context.javaToJS(doc, scope);
            ScriptableObject.putProperty(scope, "dxf", jsOut);

            Object result = ctx.evaluateReader(scope,
                    new InputStreamReader(script), "<cmd>", 1, null);

            Context.exit();
        } catch (IOException e) {
            throw new ScriptException(e);
        }
    }
}
