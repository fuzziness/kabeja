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
import org.kabeja.entities.Ellipse;
import org.kabeja.entities.Entity;
import org.kabeja.util.Constants;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 *
 *
 *
 */
public class DXFEllipseHandler extends AbstractEntityHandler {
   
    public final static int RATIO = 40;
    public final static int START_PARAMETER = 41;
    public final static int END_PARAMTER = 42;
    public static final int COUNTERCLOCKWISE = 73;
    private Ellipse ellipse;

    public void endDXFEntity() {
    }

    public Entity getDXFEntity() {
        return ellipse;
    }

    public String getDXFEntityType() {
        return Constants.ENTITY_TYPE_ELLIPSE;
    }

    public boolean isFollowSequence() {
        return false;
    }

    public void parseGroup(int groupCode, DXFValue value) {
     
        switch (groupCode) {
        case GROUPCODE_START_X:
            ellipse.getCenterPoint().setX(value.getDoubleValue());

            break;

        case GROUPCODE_START_Y:
            ellipse.getCenterPoint().setY(value.getDoubleValue());

            break;

        case GROUPCODE_START_Z:
            ellipse.getCenterPoint().setZ(value.getDoubleValue());

            break;

        case END_X:
            ellipse.getMajorAxisDirection().setX(value.getDoubleValue());

            break;

        case END_Y:
            ellipse.getMajorAxisDirection().setY(value.getDoubleValue());

            break;

        case END_Z:
            ellipse.getMajorAxisDirection().setZ(value.getDoubleValue());

            break;

        case RATIO:
            ellipse.setRatio(value.getDoubleValue());

            break;

        case START_PARAMETER:
            ellipse.setStartParameter(value.getDoubleValue());

            break;

        case END_PARAMTER:
            ellipse.setEndParameter(value.getDoubleValue());

            break;

        case COUNTERCLOCKWISE:
            ellipse.setCounterClockwise(value.getBooleanValue());

            break;

        default:
            super.parseCommonProperty(groupCode, value, ellipse);

            break;
        }
    }

    public void startDXFEntity() {
      
        ellipse = new Ellipse();
        ellipse.setDocument(doc);
    }
}
