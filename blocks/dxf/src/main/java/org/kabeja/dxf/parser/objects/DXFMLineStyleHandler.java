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
import org.kabeja.objects.MLineStyle;
import org.kabeja.objects.MLineStyleElement;
import org.kabeja.util.Constants;


public class DXFMLineStyleHandler extends AbstractDXFObjectHandler {
    public static final int GROUPCODE_MLINE_STYLE_NAME = 2;
    public static final int GROUPCODE_MLINE_STYLE_FLAGS = 70;
    public static final int GROUPCODE_MLINE_STYLE_DESCRIPTION = 3;
    public static final int GROUPCODE_MLINE_STYLE_FILL_COLOR = 62;
    public static final int GROUPCODE_MLINE_STYLE_START_ANGLE = 51;
    public static final int GROUPCODE_MLINE_STYLE_END_ANGLE = 52;
    public static final int GROUPCODE_MLINE_STYLE_ELEMENT_COUNT = 71;
    public static final int GROUPCODE_MLINE_STYLE_ELEMENT_OFFSET = 49;
    public static final int GROUPCODE_MLINE_STYLE_ELEMENT_COLOR = 62;
    public static final int GROUPCODE_MLINE_STYLE_ELEMENT_LINE_STYLE = 6;
    protected MLineStyle style;
    protected MLineStyleElement element;
    protected boolean processLineElement = false;

    public void endObject() {
    }

    public DraftObject getDXFObject() {
        return this.style;
    }

    public String getObjectType() {
        return Constants.OBJECT_TYPE_MLINESTYLE;
    }

    public void parseGroup(int groupCode, DXFValue value) {
        switch (groupCode) {
        case GROUPCODE_MLINE_STYLE_ELEMENT_OFFSET:
            this.element = new MLineStyleElement();
            this.element.setOffset(value.getDoubleValue());
            this.style.addMLineStyleElement(element);
            this.processLineElement = true;

            break;

        case GROUPCODE_MLINE_STYLE_ELEMENT_COLOR:

            if (this.processLineElement) {
                this.element.setLineColor(value.getIntegerValue());
            } else {
                this.style.setFillColor(value.getIntegerValue());
            }

            break;

        case GROUPCODE_MLINE_STYLE_ELEMENT_LINE_STYLE:
            this.element.setLineType(value.getValue());

            break;

        case GROUPCODE_MLINE_STYLE_NAME:
            this.style.setName(value.getValue());

            break;

        case GROUPCODE_MLINE_STYLE_DESCRIPTION:
            this.style.setDescrition(value.getValue());

            break;

        case GROUPCODE_MLINE_STYLE_START_ANGLE:
            this.style.setStartAngle(value.getDoubleValue());

            break;

        case GROUPCODE_MLINE_STYLE_END_ANGLE:
            this.style.setEndAngle(value.getDoubleValue());

            break;

        case GROUPCODE_MLINE_STYLE_FLAGS:
            this.style.setFlags(value.getIntegerValue());

            break;

        default:
            super.parseCommonGroupCode(groupCode, value, this.style);
        }
    }

    public void startObject() {
        this.style = new MLineStyle();
        this.processLineElement = false;
    }
}
