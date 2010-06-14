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
import org.kabeja.entities.Entity;
import org.kabeja.entities.MLine;
import org.kabeja.entities.util.MLineSegment;
import org.kabeja.entities.util.MLineSegmentElement;
import org.kabeja.entities.util.Utils;
import org.kabeja.util.Constants;


public class DXFMLineHandler extends AbstractEntityHandler {
    public final static int GROUPCODE_MLINE_STYLENAME = 2;
    public final static int GROUPCODE_MLINE_STYLE_ID = 340;
    public final static int GROUPCODE_MLINE_SCALE_FACTOR = 40;
    public final static int GROUPCODE_MLINE_JUSTIFICATION = 70;
    public final static int GROUPCODE_MLINE_FLAGS = 71;
    public final static int GROUPCODE_MLINE_NUMBER_OF_VERTICES = 72;
    public final static int GROUPCODE_MLINE_NUMBER_OF_LINESTYLEELEMENTS = 73;
    public final static int GROUPCODE_MLINE_VERTEX_X = 11;
    public final static int GROUPCODE_MLINE_VERTEX_Y = 21;
    public final static int GROUPCODE_MLINE_VERTEX_Z = 31;
    public final static int GROUPCODE_MLINE_DIRECTION_X = 12;
    public final static int GROUPCODE_MLINE_DIRECTION_Y = 22;
    public final static int GROUPCODE_MLINE_DIRECTION_Z = 32;
    public final static int GROUPCODE_MLINE_MITER_DIRECTION_X = 13;
    public final static int GROUPCODE_MLINE_MITER_DIRECTION_Y = 23;
    public final static int GROUPCODE_MLINE_MITER_DIRECTION_Z = 33;
    public final static int GROUPCODE_MLINE_ELEMENT_PARAMETER_COUNT = 74;
    public final static int GROUPCODE_MLINE_ELEMENT_PARAMETER = 41;
    public final static int GROUPCODE_MLINE_FILL_PARAMETER_COUNT = 75;
    public final static int GROUPCODE_MLINE_FILL_PARAMETER = 42;
    protected MLine mLine;
    protected MLineSegment seg;
    protected MLineSegmentElement el;
    protected int index = 0;

    public String getDXFEntityType() {
        return Constants.ENTITY_TYPE_MLINE;
    }

    public void endDXFEntity() {
    }

    public Entity getDXFEntity() {
        return this.mLine;
    }

    public boolean isFollowSequence() {
        return false;
    }

    public void parseGroup(int groupCode, DXFValue value) {
        switch (groupCode) {
        case GROUPCODE_MLINE_VERTEX_X:
            this.seg = new MLineSegment();
            this.mLine.addMLineSegement(this.seg);
            this.seg.getStartPoint().setX(value.getDoubleValue());

            break;

        case GROUPCODE_MLINE_VERTEX_Y:
            this.seg.getStartPoint().setY(value.getDoubleValue());

            break;

        case GROUPCODE_MLINE_VERTEX_Z:
            this.seg.getStartPoint().setZ(value.getDoubleValue());

            break;

        case GROUPCODE_MLINE_DIRECTION_X:
            this.seg.getDirection().setX(value.getDoubleValue());

            break;

        case GROUPCODE_MLINE_DIRECTION_Y:
            this.seg.getDirection().setY(value.getDoubleValue());

            break;

        case GROUPCODE_MLINE_DIRECTION_Z:
            this.seg.getDirection().setZ(value.getDoubleValue());

            break;

        case GROUPCODE_MLINE_MITER_DIRECTION_X:
            this.seg.getMiterDirection().setX(value.getDoubleValue());

            break;

        case GROUPCODE_MLINE_MITER_DIRECTION_Y:
            this.seg.getMiterDirection().setY(value.getDoubleValue());

            break;

        case GROUPCODE_MLINE_MITER_DIRECTION_Z:
            this.seg.getMiterDirection().setZ(value.getDoubleValue());

            break;

        case GROUPCODE_MLINE_ELEMENT_PARAMETER:
            this.el.setLengthParameter(index, value.getDoubleValue());
            this.index++;

            break;

        case GROUPCODE_MLINE_ELEMENT_PARAMETER_COUNT:
            this.el = new MLineSegmentElement();
            this.seg.addMLineSegmentElement(el);
            this.el.setLengthParameters(new double[value.getIntegerValue()]);
            this.index = 0;

            break;

        case GROUPCODE_MLINE_FILL_PARAMETER:
            this.el.setFillParameter(index, value.getDoubleValue());
            this.index++;

            break;

        case GROUPCODE_MLINE_FILL_PARAMETER_COUNT:
            this.el.setFillParameters(new double[value.getIntegerValue()]);
            this.index = 0;

            break;

        case GROUPCODE_START_X:
            this.mLine.getStartPoint().setX(value.getDoubleValue());

            break;

        case GROUPCODE_START_Y:
            this.mLine.getStartPoint().setY(value.getDoubleValue());

            break;

        case GROUPCODE_START_Z:
            this.mLine.getStartPoint().setZ(value.getDoubleValue());

            break;

        case GROUPCODE_MLINE_FLAGS:
            this.mLine.setFlags(value.getIntegerValue());

            break;

        case GROUPCODE_MLINE_JUSTIFICATION:
            this.mLine.setJustification(value.getIntegerValue());

            break;

        case GROUPCODE_MLINE_NUMBER_OF_LINESTYLEELEMENTS:
            this.mLine.setLineCount(value.getIntegerValue());

            break;

        case GROUPCODE_MLINE_SCALE_FACTOR:
            this.mLine.setScale(value.getDoubleValue());

            break;

        case GROUPCODE_MLINE_STYLENAME:
            this.mLine.setMLineStyleName(value.getValue());

            break;

        case GROUPCODE_MLINE_STYLE_ID:
            this.mLine.setMLineStyleID(Utils.parseIDString(value.getValue()));

            break;

        default:
            super.parseCommonProperty(groupCode, value, this.mLine);
        }
    }

    public void startDXFEntity() {
        this.mLine = new MLine();
    }
}
