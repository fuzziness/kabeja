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
import org.kabeja.entities.Circle;
import org.kabeja.entities.Entity;
import org.kabeja.math.Point3D;
import org.kabeja.util.Constants;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 *
 */
public class DXFCircleHandler extends AbstractEntityHandler {

    public final static int RADIUS = 40;
    private Circle circle;




    public Entity getDXFEntity() {
        return circle;
    }


    public String getDXFEntityType() {
        return Constants.ENTITY_TYPE_CIRCLE;
    }


    public void parseGroup(int groupCode, DXFValue value) {
        switch (groupCode) {
        case GROUPCODE_START_X:
            circle.getCenterPoint().setX(value.getDoubleValue());

            break;

        case GROUPCODE_START_Y:
            circle.getCenterPoint().setY(value.getDoubleValue());

            break;

        case RADIUS:
            circle.setRadius(value.getDoubleValue());

            break;

        default:
            super.parseCommonProperty(groupCode, value, circle);

            break;
        }
    }


    public void startDXFEntity() {
        circle = new Circle();
        circle.setCenterPoint(new Point3D());
        circle.setDocument(doc);
    }


    public boolean isFollowSequence() {
        return false;
    }


    public void endDXFEntity() {
   
        
    }
}
