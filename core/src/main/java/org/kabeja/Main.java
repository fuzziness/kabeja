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
package org.kabeja;

import java.util.HashMap;
import java.util.Map;



/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 * 
 * 
 */
public class Main {
	
	public static String DEFAULT_APP="org.kabeja.ui.UIApplication"; 
	public static String CLI_APP="org.kabeja.CLIApplication"; 
	
	public Main() {
	
	
	}

	public static void main(String[] args) {
			
		Main main = new Main();
        Map settings = main.parseParameters(args);
        String launchClass = null;
        if(settings.containsKey("cli")){
        	launchClass = CLI_APP;
        }else{
        	launchClass = DEFAULT_APP;
        }

        try {
			Class clazz  =  main.getClass().getClassLoader().loadClass(launchClass);
			Application app = (Application)clazz.newInstance();
			app.start(settings);
			
		} catch (Exception e) {
		
			e.printStackTrace();
		}
        
	}
	
	
	   protected Map parseParameters(String[] args){
	    	Map parameters = new HashMap();
	    	String key = null;
	    	for(int i=0;i<args.length;i++){
	    		if(args[i].startsWith("-")|| args[i].startsWith("--")){
	    			if(key!=null){
	    				//option set before
	    			    parameters.put(key, "true");	
	    			 	
	    			}
	    			key=args[i].replaceAll("^-+", "");
	    			
	    			
	    		}else{
	    			//value for key
	    			parameters.put(key,args[i]);
	    			key=null;
	    			
	    		}
	    		
	    	}
	    	
	    	return parameters;
	    	
	    }
	    


}
