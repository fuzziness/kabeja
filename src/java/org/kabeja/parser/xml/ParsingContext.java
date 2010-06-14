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

package org.kabeja.parser.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kabeja.DraftDocument;
import org.kabeja.common.Layer;
import org.kabeja.entities.Entity;
import org.kabeja.entities.util.Utils;
import org.kabeja.parser.util.ParsingValidator;

public class ParsingContext {

     
    protected List  validatorList = new ArrayList();
    protected Map properties = new HashMap();
    
    protected Layer currentLayer;
    protected DraftDocument doc;
    
    public ParsingContext(DraftDocument doc){
        this.doc = doc;
    }
       
    public void addParsingValidator(ParsingValidator validator){
        this.validatorList.add(validator);
    }
    
    
    public void removeParsingValidator(ParsingValidator validator){
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
    
    public void addEntity(Entity entity){
       
        Iterator i = this.validatorList.iterator();
        boolean add=true;
        while(i.hasNext()&& add){
            add = ((ParsingValidator)i.next()).isValid(entity);
        }
        if(add){
            entity.setID(Utils.generateID(this.doc));
            entity.setLayer(this.currentLayer);
            this.doc.addEntity(entity);
        }
    }


    /**
     * @return the currentLayer
     */
    public Layer getCurrentLayer() {
        return currentLayer;
    }


    /**
     * @param currentLayer the currentLayer to set
     */
    public void setCurrentLayer(Layer currentLayer) {
        this.currentLayer = currentLayer;
        if(!this.doc.containsLayer(currentLayer.getName())){
            this.currentLayer.setID(Utils.generateNewID(this.doc));
            this.doc.addLayer(currentLayer);
        }
    }
    
    
    public long generateID(){
       return Utils.generateID(this.doc);
    }
    
}
