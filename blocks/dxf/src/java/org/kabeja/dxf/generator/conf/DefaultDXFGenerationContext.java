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
/*
 * Created on 07.11.2008
 *
 */
package org.kabeja.dxf.generator.conf;

import java.util.HashMap;
import java.util.Map;

import org.kabeja.dxf.generator.DXFGenerationContext;
import org.kabeja.dxf.generator.DXFGeneratorManager;
import org.kabeja.tools.ConfigHelper;

public class DefaultDXFGenerationContext implements DXFGenerationContext {

   
    
    protected String defaultProfile="";
  
    protected Map properties =new HashMap();
    
    protected DXFGeneratorManager manager;
    
    public DefaultDXFGenerationContext(DXFGeneratorManager manager){
        this.manager = manager;
    }
    

    
    public void addAttribute(String key,Object value){
        this.properties.put(key,value);
    }
    
 
    


    public Object getAttribute(String key) {
      return this.properties.get(key);
    }

    /* (non-Javadoc)
     * @see org.kabeja.dxf.generator.DXFGenerationContext#getDXFGeneratorManager()
     */
    public DXFGeneratorManager getDXFGeneratorManager() {
        return this.manager;
    }

    /* (non-Javadoc)
     * @see org.kabeja.dxf.generator.DXFGenerationContext#hasAttribute(java.lang.String)
     */
    public boolean hasAttribute(String key) {
        return this.properties.containsKey(key);
    }



    
    
    protected void initialize(){
    	this.properties = ConfigHelper.getProperties(DefaultDXFGenerationContext.class.getClassLoader(), "conf/dxf.properties");
      
    }
    
    
}
