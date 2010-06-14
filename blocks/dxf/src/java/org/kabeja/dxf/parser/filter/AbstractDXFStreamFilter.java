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

package org.kabeja.dxf.parser.filter;

import java.util.Map;

import org.kabeja.DraftDocument;
import org.kabeja.dxf.parser.DXFHandler;


public abstract class AbstractDXFStreamFilter implements DXFStreamFilter {
    protected Map properties;
    protected DXFHandler handler;

    public void setProperties(Map properties) {
        this.properties = properties;
    }

    public void setDXFHandler(DXFHandler handler) {
        this.handler = handler;
    }
    


    public void releaseDocument() {
       
        
    }


    public void setDocument(DraftDocument doc) {
        
        
    }
    
    
}
