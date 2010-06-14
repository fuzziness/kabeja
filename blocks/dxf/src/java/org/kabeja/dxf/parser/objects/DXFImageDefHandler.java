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

package org.kabeja.dxf.parser.objects;

import org.kabeja.dxf.parser.DXFValue;
import org.kabeja.objects.DraftObject;
import org.kabeja.objects.ImageDefObject;
import org.kabeja.util.Constants;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 *
 */
public class DXFImageDefHandler extends AbstractDXFObjectHandler {
    public final static int GROUPCODE_FILENAME = 1;
    protected ImageDefObject imageDef;

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.kabeja.parser.objects.DXFObjectHandler#getObjectType()
     */
    public String getObjectType() {
        return Constants.OBJECT_TYPE_IMAGEDEF;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.kabeja.parser.objects.DXFObjectHandler#startObject()
     */
    public void startObject() {
        imageDef = new ImageDefObject();
        imageDef.setDocument(this.doc);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.kabeja.parser.objects.DXFObjectHandler#endObject()
     */
    public void endObject() {
        // TODO Auto-generated method stub
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.kabeja.parser.objects.DXFObjectHandler#getDXFObject()
     */
    public DraftObject getDXFObject() {
        // TODO Auto-generated method stub
        return imageDef;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.kabeja.parser.objects.DXFObjectHandler#parseGroup(int,
     *      de.miethxml.kabeja.parser.DXFValue)
     */
    public void parseGroup(int groupCode, DXFValue value) {
        switch (groupCode) {
        case GROUPCODE_FILENAME:
            imageDef.setImagePath(value.getValue());

            break;

        default:
            super.parseCommonGroupCode(groupCode, value, imageDef);

            break;
        }
    }
}
