/*
   Copyright 2008 Simon Mieth

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

import java.io.PrintStream;

import javax.swing.JOptionPane;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;


public class Global extends ImporterTopLevel {
    private static PrintStream out;

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    protected String[] functionNames = new String[] { "print", "alert" };

    public Global() {
        this.init();
    }

    protected void init() {
        this.defineFunctionProperties(functionNames, Global.class,
            ScriptableObject.DONTENUM);
    }

    public static void print(Context cx, Scriptable thisObj, Object[] args,
        Function funObj) {
        for (int i = 0; i < args.length; i++) {
            out.print(Context.toString(args[i]));
        }

        out.print('\n');
    }

    /**
    *Alert method
    */
    public static void alert(Context cx, Scriptable thisObj, Object[] args,
        Function funObj) {
        int length = args.length;
        Global g = (Global) thisObj;

        if (length >= 1) {
            String message = (String) Context.jsToJava(args[0], String.class);
            JOptionPane.showMessageDialog(null, message);
        }
    }

    public static void setOutput(PrintStream out) {
        Global.out = out;
    }
}
