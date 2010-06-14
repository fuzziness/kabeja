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
/*
 * Created on 07.11.2008
 *
 */
package org.kabeja.dxf.parser.entities;

import org.kabeja.dxf.parser.DXFValue;
import org.kabeja.entities.Entity;
import org.kabeja.entities.Point;
import org.kabeja.util.Constants;

public class DXFPointHandler extends AbstractEntityHandler {

    protected Point point;

    public String getDXFEntityType() {
        return Constants.ENTITY_TYPE_POINT;
    }

    public void endDXFEntity() {
         
    }

    public Entity getDXFEntity() {
        Point p = point;
        point = null;
        return p;
    }

    public boolean isFollowSequence() {

        return false;
    }

    public void parseGroup(int groupCode, DXFValue value) {
        switch (groupCode) {
        case 10:
            point.setX(value.getDoubleValue());
            break;
        case 20:
            point.setY(value.getDoubleValue());
            break;
        case 30:
            point.setZ(value.getDoubleValue());
            break;
        case 39:
             point.setThickness(value.getDoubleValue());
             break;
        case 50:
            point.setAngle(value.getDoubleValue());
            break;
             
        default:
            super.parseCommonProperty(groupCode, value, point);

        }

    }

    public void startDXFEntity() {
        point = new Point();

    }

}
