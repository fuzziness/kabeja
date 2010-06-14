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
 * Created on 04.10.2009
 *
 */
package org.kabeja.dxf.generator.conf;

import java.util.ArrayList;
import java.util.List;

public class DXFType {

    protected List<DXFSubType> subtypes = new ArrayList<DXFSubType> ();
    protected String name="";
    
    
    
    public DXFType(String name){
        this.name=name;
    }
    
    
    
    public void addDXFSubType(DXFSubType subtype){
        this.subtypes.add(subtype);
    }
 
    /**
     * Get the list of DXFSubTypes of this DXFType
     * @return a @see java.util.List of @see org.kabeja.dxf.generator.conf.DXFSubType
     */
    
    public List<DXFSubType> getDXFSubTypes(){
        return this.subtypes;
    }
    
    
    public String getName(){
        return this.name;
    }
    
}
