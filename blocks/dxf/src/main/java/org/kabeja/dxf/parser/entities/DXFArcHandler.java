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
import org.kabeja.entities.Arc;
import org.kabeja.entities.Entity;
import org.kabeja.util.Constants;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 *
 */
public class DXFArcHandler extends AbstractEntityHandler {
   
    public static final int RADIUS = 40;
    public static final int START_ANGLE = 50;
    public static final int END_ANGLE = 51;
    public static final int COUNTERCLOCKWISE = 73;
    private Arc arc;

    /**
     *
     */
    public DXFArcHandler() {
        super();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.dxf2svg.parser.entities.DXFEntityHandler#endDXFEntity()
     */
    public void endDXFEntity() {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.dxf2svg.parser.entities.DXFEntityHandler#getDXFEntity()
     */
    public Entity getDXFEntity() {
        return arc;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.dxf2svg.parser.entities.DXFEntityHandler#getDXFEntityName()
     */
    public String getDXFEntityType() {
        return Constants.ENTITY_TYPE_ARC;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.dxf2svg.parser.entities.DXFEntityHandler#isFollowSequence()
     */
    public boolean isFollowSequence() {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.dxf2svg.parser.entities.DXFEntityHandler#parseGroup(int,
     *      org.dxf2svg.parser.DXFValue)
     */
    public void parseGroup(int groupCode, DXFValue value) {
        switch (groupCode) {
        case GROUPCODE_START_X:
            arc.getCenterPoint().setX(value.getDoubleValue());

            break;

        case GROUPCODE_START_Y:
            arc.getCenterPoint().setY(value.getDoubleValue());

            break;

        case RADIUS:
            arc.setRadius(value.getDoubleValue());

            break;

        case START_ANGLE:
            arc.setStartAngle(value.getDoubleValue());

            break;

        case END_ANGLE:
            arc.setEndAngle(value.getDoubleValue());

            break;

        case COUNTERCLOCKWISE:
            arc.setCounterClockwise(value.getBooleanValue());

            break;

        default:
            super.parseCommonProperty(groupCode, value, arc);

            break;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.dxf2svg.parser.entities.DXFEntityHandler#startDXFEntity()
     */
    public void startDXFEntity() {
        arc = new Arc();
    }
}
