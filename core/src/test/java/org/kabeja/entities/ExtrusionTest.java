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
package org.kabeja.entities;

import org.junit.Test;
import static org.junit.Assert.*;

import org.kabeja.entities.Line;
import org.kabeja.math.Extrusion;
import org.kabeja.math.Point3D;
import org.kabeja.math.Vector;

public class ExtrusionTest {

    public static final double DELTA = 1.0E-7;

    @Test
    public void lineExtrusion() {
        Line line = new Line();
        line.setStartPoint(new Point3D(0, 0, 0));
        line.setEndPoint(new Point3D(100, 100, 0));
        line.setThickness(10.0);

        Extrusion e = line.getExtrusion();
        Point3D p1 = e.extrudePoint(line.getStartPoint(), line.getThickness());
        Point3D p2 = e.extrudePoint(line.getEndPoint(), line.getThickness());
        assertEquals(10.0, p1.getZ(), DELTA);
        assertEquals(10.0, p2.getZ(), DELTA);
    }

    @Test
    public void linePlaneExtrusion() {
        Line line = new Line();
        line.setStartPoint(new Point3D(0, 0, 0));
        line.setEndPoint(new Point3D(100, 100, 0));
        line.setThickness(10.0);

        Extrusion e = line.getExtrusion();
        Vector v1 = e.getDirectionX();
        Vector v2 = e.getDirectionY();

        assertEquals(1.0, v1.getX(), DELTA);
        assertEquals(0.0, v1.getY(), DELTA);
        assertEquals(0.0, v1.getZ(), DELTA);
        assertEquals(0.0, v2.getX(), DELTA);
        assertEquals(1.0, v2.getY(), DELTA);
        assertEquals(0.0, v2.getZ(), DELTA);
    }
}
