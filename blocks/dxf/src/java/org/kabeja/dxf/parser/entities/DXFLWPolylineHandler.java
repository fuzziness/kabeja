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
 * Created on 13.04.2005
 *
 */
package org.kabeja.dxf.parser.entities;

import org.kabeja.dxf.parser.DXFValue;
import org.kabeja.entities.Entity;
import org.kabeja.entities.LW2DVertex;
import org.kabeja.entities.LWPolyline;
import org.kabeja.util.Constants;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 *
 */
public class DXFLWPolylineHandler extends AbstractEntityHandler {
   
    public static final int VERTEX_BULGE = 42;
    public static final int START_WIDTH = 40;
    public static final int END_WIDTH = 41;
    public static final int CONSTANT_WIDTH = 43;
    public static final int ELEVATION = 38;
    public static final int THICKNESS = 39;
    private LW2DVertex vertex;
    private LWPolyline lwpolyline;

    /**
     *
     */
    public DXFLWPolylineHandler() {
        super();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.dxf2svg.parser.entities.EntityHandler#endParsing()
     */
    public void endDXFEntity() {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.dxf2svg.parser.entities.EntityHandler#getEntity()
     */
    public Entity getDXFEntity() {
        return lwpolyline;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.dxf2svg.parser.entities.EntityHandler#getEntityName()
     */
    public String getDXFEntityType() {
        return Constants.ENTITY_TYPE_LWPOLYLINE;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.dxf2svg.parser.entities.EntityHandler#isFollowSequence()
     */
    public boolean isFollowSequence() {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.dxf2svg.parser.entities.EntityHandler#parseGroup(int,
     *      org.dxf2svg.parser.DXFValue)
     */
    public void parseGroup(int groupCode, DXFValue value) {
        // the different between polyline and lwpolyline is,
        // that the vertices comes not as sequence here.
        switch (groupCode) {
        case GROUPCODE_START_X:
            // every new vertex starts with 10
            createVertex();
            vertex.setX(value.getDoubleValue());

            break;

        case GROUPCODE_START_Y:
            vertex.setY(value.getDoubleValue());

            break;

        case GROUPCODE_START_Z:
            //vertex.getPoint().setZ(value.getDoubleValue());

            break;

        case VERTEX_BULGE:
            vertex.setBulge(value.getDoubleValue());

            break;

        case START_WIDTH:
            vertex.setStartWidth(value.getDoubleValue());

            break;

        case END_WIDTH:
            vertex.setEndWidth(value.getDoubleValue());

            break;

        case CONSTANT_WIDTH:
            lwpolyline.setConstantWidth(value.getDoubleValue());

            break;

        case ELEVATION:
            lwpolyline.getElevation().setZ(value.getDoubleValue());

            break;

        case THICKNESS:
            lwpolyline.setThickness(value.getDoubleValue());

            break;

        default:
            super.parseCommonProperty(groupCode, value, lwpolyline);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.dxf2svg.parser.entities.EntityHandler#startParsing()
     */
    public void startDXFEntity() {
        lwpolyline = new LWPolyline();
    }

    private void createVertex() {
        vertex = new LW2DVertex();
        lwpolyline.addVertex(vertex);
    }
}
