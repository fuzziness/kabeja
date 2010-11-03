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
package org.kabeja.math;

import org.junit.Test;
import static org.junit.Assert.*;

public class ParametricPlaneTest {

    public static final double DELTA = 1.0E-4;

    @Test
    public void directionY() {
        ParametricPlane p = new ParametricPlane(new Point3D(0, 0, 0),
                new Point3D(1, 0, 0), new Vector(0, 0, 1));
        Vector y = p.getDirectionY();
        assertEquals(0.0, y.getX(), DELTA);
        assertEquals(1.0, y.getY(), DELTA);
        assertEquals(0.0, y.getZ(), DELTA);
    }

    @Test
    public void directionX() {
        ParametricPlane p = new ParametricPlane(new Point3D(0, 0, 0),
                new Point3D(0, 1, 0), new Vector(0, 0, 1));
        Vector y = p.getDirectionY();
        assertEquals(-1.0, y.getX(), DELTA);
        assertEquals(0.0, y.getY(), DELTA);
        assertEquals(0.0, y.getZ(), DELTA);
    }

    @Test
    public void point1() {
        ParametricPlane plane = new ParametricPlane(new Point3D(0, 0, 0),
                new Point3D(1, 0, 0), new Vector(0, 0, 1));

        Point3D p = plane.getPoint(2.0, 3.0);

        assertEquals(2.0, p.getX(), DELTA);
        assertEquals(3.0, p.getY(), DELTA);
        assertEquals(0.0, p.getZ(), DELTA);
    }

    @Test
    public void parameters() {
        ParametricPlane plane = new ParametricPlane(new Point3D(0, 0, 0),
                new Point3D(1, 0, 0), new Vector(0, 0, 1));

        Point3D p = new Point3D(2.0, 3.0, 0.0);
        double[] paras = plane.getParameter(p);
        assertEquals(2.0, paras[0], DELTA);
        assertEquals(3.0, paras[1], DELTA);
    }

    @Test
    public void isOnPlane() {
        ParametricPlane plane = new ParametricPlane(new Point3D(0, 0, 0),
                new Point3D(1, 0, 0), new Vector(0, 0, 1));

        Point3D p = new Point3D(2.0, 3.0, 0.0);
        assertTrue(plane.isOnPlane(p));
    }

    @Test
    public void isNotPlane() {
        ParametricPlane plane = new ParametricPlane(new Point3D(0, 0, 0),
                new Point3D(1, 0, 0), new Vector(0, 0, 1));

        Point3D p = new Point3D(2.0, 3.0, -0.01);
        assertFalse(plane.isOnPlane(p));
    }

    @Test
    public void normal() {
        ParametricPlane plane = new ParametricPlane(new Point3D(0, 0, 0),
                new Point3D(1, 0, 0), new Point3D(1.0, DELTA, 0));

        Vector n = plane.getNormal();
        assertEquals(0.0, n.getX(), DELTA);
        assertEquals(0.0, n.getY(), DELTA);
        assertEquals(1.0, n.getZ(), DELTA);
    }
}
