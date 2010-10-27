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

package org.kabeja.dxf.parser.entities;

import org.kabeja.dxf.parser.DXFValue;
import org.kabeja.entities.Attrib;
import org.kabeja.util.Constants;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 *
 */
public class DXFAttribHandler extends DXFTextHandler {
    public static final int GROUPCODE_ATTRIB_VERTICAL_ALIGN = 74;
    public static final int GROUPCODE_ATTRIB_TEXT_LENGTH = 73;
    public static final int GROUPCODE_ATTRIB_PROMPT=3;
    public static final int GROUPCODE_ATTRIB_TAG=2;

    
    protected Attrib attrib;
    
    public DXFAttribHandler() {
        super();
    }

    /* (non-Javadoc)
     * @see de.miethxml.kabeja.parser.entities.DXFEntityHandler#parseGroup(int, de.miethxml.kabeja.parser.DXFValue)
     */
    public void parseGroup(int groupCode, DXFValue value) {
       
    	switch (groupCode) {
   
        case GROUPCODE_ATTRIB_VERTICAL_ALIGN:
            text.setValign(value.getIntegerValue());

            break;
            
        case GROUPCODE_ATTRIB_PROMPT:
            attrib.setPrompt(value.getValue());

            break;

        case GROUPCODE_ATTRIB_TAG:
            attrib.setTag(value.getValue());

            break;


        default:
            super.parseGroup(groupCode, value);
        }
    }

    public void startDXFEntity() {
    	this.attrib = new Attrib();
    	this.text = this.attrib;
    	this.text.setDocument(this.doc);
    }


    public String getDXFEntityType() {
        return Constants.ENTITY_TYPE_ATTRIB;
    }
}
