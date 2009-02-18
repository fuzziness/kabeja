/*
   Copyright 2009 Simon Mieth

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
package org.kabeja.parser.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kabeja.dxf.DXFDocument;
import org.kabeja.dxf.DXFEntity;
import org.kabeja.dxf.DXFLayer;
import org.kabeja.dxf.helpers.DXFUtils;
import org.kabeja.parser.util.DXFParsingValidator;

public class ParsingContext {

     
    protected List  validatorList = new ArrayList();
    protected Map properties = new HashMap();
    
    protected DXFLayer currentLayer;
    protected DXFDocument doc;
    
    public ParsingContext(DXFDocument doc){
        this.doc = doc;
    }
       
    public void addDXFEntityParsingValidator(DXFParsingValidator validator){
        this.validatorList.add(validator);
    }
    
    
    public void removeDXFEntityParsingValidator(DXFParsingValidator validator){
        this.validatorList.remove(validator);
    }
    
    
    public void setProperty(String key,String value){
        this.properties.put(key, value);
    }
    
    public String getProperty(String key){
        return (String)this.properties.get(key);
    }
    
    public boolean hasProperty(String key){
        return this.properties.containsKey(key);
    }
    
    public void addDXFEntity(DXFEntity entity){
       
        Iterator i = this.validatorList.iterator();
        boolean add=true;
        while(i.hasNext()&& add){
            add = ((DXFParsingValidator)i.next()).isValid(entity);
        }
        if(add){
            entity.setID(DXFUtils.generateNewID(this.doc));
            entity.setLayerName(this.currentLayer.getName());
            this.doc.addDXFEntity(entity);
        }
    }


    /**
     * @return the currentLayer
     */
    public DXFLayer getCurrentLayer() {
        return currentLayer;
    }


    /**
     * @param currentLayer the currentLayer to set
     */
    public void setCurrentLayer(DXFLayer currentLayer) {
        this.currentLayer = currentLayer;
        if(!this.doc.containsDXFLayer(currentLayer.getName())){
            this.currentLayer.setID(DXFUtils.generateNewID(this.doc));
            this.doc.addDXFLayer(currentLayer);
        }
    }
    
    
    public String generateID(){
       return DXFUtils.generateNewID(this.doc);
    }
    
}
