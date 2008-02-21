package org.kabeja.processing.scripting.impl;

import java.io.PrintStream;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class Global extends ImporterTopLevel {

	
	private static PrintStream out;

	protected String[]   functionNames  = new String[]{"print","alert"};
	
	public Global(){
		this.init();
	}
	
	

	
	protected void init(){
		this.defineFunctionProperties(functionNames, Global.class,
                ScriptableObject.DONTENUM);
	}
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
    public static void alert(Context cx,
                             Scriptable thisObj,
                             Object[] args,
                             Function funObj) {
        int length = args.length;
        Global  g = (Global)thisObj;
       
        if (length >= 1) {
            String message =
                (String)Context.jsToJava(args[0], String.class);
             JOptionPane.showMessageDialog(null, message);
        }
    }
    public static void setOutput(PrintStream out){
    	Global.out=out;
    }
}
