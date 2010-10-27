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
import org.kabeja.entities.Line;
import org.kabeja.math.Point3D;
import org.kabeja.util.Constants;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 *
 */
public class DXFLineHandler extends AbstractEntityHandler {
   
    protected  Line line;
   

    public String getDXFEntityType() {
        return Constants.ENTITY_TYPE_LINE;
    }

    public void parseGroup(int groupCode, DXFValue value) {
        switch (groupCode) {
        case GROUPCODE_START_X:
            line.getStartPoint().setX(value.getDoubleValue());
           break;

        case GROUPCODE_START_Y:
            line.getStartPoint().setY(value.getDoubleValue());
            break;

        case GROUPCODE_START_Z:
            line.getStartPoint().setZ(value.getDoubleValue());
            break;

        case END_X:
            line.getEndPoint().setX(value.getDoubleValue());
            break;

        case END_Y:
            line.getEndPoint().setY(value.getDoubleValue());
            break;

        case END_Z:
            line.getEndPoint().setZ(value.getDoubleValue());
            break;
        }

        super.parseCommonProperty(groupCode, value, line);
    }

 
    public Entity getDXFEntity() {
        return line;
    }


    public void startDXFEntity() {
        line = new Line();
        line.setStartPoint(new Point3D());
        line.setEndPoint(new Point3D());
    }

 
    public void endDXFEntity() {
    }

 

    public boolean isFollowSequence() {
        return false;
    }
}
