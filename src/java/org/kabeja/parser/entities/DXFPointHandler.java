/*
 * Created on 07.11.2008
 *
 */
package org.kabeja.parser.entities;

import org.kabeja.dxf.DXFConstants;
import org.kabeja.dxf.DXFEntity;
import org.kabeja.dxf.DXFPoint;
import org.kabeja.parser.DXFValue;

public class DXFPointHandler extends AbstractEntityHandler {

    protected DXFPoint point;

    public String getDXFEntityName() {
        return DXFConstants.ENTITY_TYPE_POINT;
    }

    public void endDXFEntity() {
         
    }

    public DXFEntity getDXFEntity() {
        DXFPoint p = point;
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
        point = new DXFPoint();

    }

}
