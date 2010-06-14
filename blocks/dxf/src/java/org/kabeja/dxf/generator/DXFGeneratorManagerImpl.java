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
package org.kabeja.dxf.generator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.kabeja.dxf.generator.conf.DXFProfile;

public class DXFGeneratorManagerImpl implements DXFGeneratorManager {

    protected Map entityGenerators = new HashMap();
    protected Map sectionGenerators = new HashMap();
    protected Map tableGenerators = new HashMap();
    protected Map profiles = new HashMap();
	
	
	
	
	public DXFEntityGenerator getDXFEntityGenerator(String entityType) {
		return (DXFEntityGenerator)this.entityGenerators.get(entityType);
	}

	public DXFSectionGenerator getDXFSectionGenerator(String section) {	
		return (DXFSectionGenerator)this.sectionGenerators.get(section);
	}

	public boolean hasDXFEntityGenerator(String entityType) {
		return this.entityGenerators.containsKey(entityType);
	}

	public boolean hasDXFSectionGenerator(String section) {
		return this.sectionGenerators.containsKey(section);
	}
	



	/* (non-Javadoc)
	 * @see org.kabeja.dxf.generator.DXFGeneratorManager#hasDXFTableGenerator(java.lang.String)
	 */
	public boolean hasDXFTableGenerator(String tableType) {
		return this.tableGenerators.containsKey(tableType);
	}

	/* (non-Javadoc)
	 * @see org.kabeja.dxf.generator.DXFGeneratorManager#getDXFTableGenerator(java.lang.String)
	 */
	public DXFTableGenerator getDXFTableGenerator(String tableType) {
		return (DXFTableGenerator)this.tableGenerators.get(tableType);
	}
	
	
	
   public void addHandler(String key,Object obj){
	   if(obj instanceof DXFTableGenerator){
		   this.tableGenerators.put(key,(DXFTableGenerator)obj);
	   }else if(obj instanceof DXFEntityGenerator){
		   this.entityGenerators.put(key, (DXFEntityGenerator)obj);
	   }else if(obj instanceof DXFSectionGenerator){
		   this.sectionGenerators.put(key,(DXFSectionGenerator)obj);
	   }
   }

/* (non-Javadoc)
 * @see org.kabeja.dxf.generator.DXFGeneratorManager#getDXFProfile(java.lang.String)
 */
public DXFProfile getDXFProfile(String name) {
	   return (DXFProfile) this.profiles.get(name);
}
	
	

public void addDXFProfile(DXFProfile profile){
    this.profiles.put(profile.getName(),profile);
}

   

public Set getDXFProfileNames() {
        return this.profiles.keySet();
}

public boolean hasDXFProfile(String name){
	return this.profiles.containsKey(name);
}
   
}
