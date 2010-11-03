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

public class NURBSTest {

    public static final double DELTA = 1.0E-5;

    @Test
    public void pointAt() {
        Point3D[] points = new Point3D[]{
            new Point3D(0, 0, 0), new Point3D(1.0, 1.0, 0.0),
            new Point3D(3.0, 2.0, 0), new Point3D(4.0, 1.0, 0),
            new Point3D(5.0, -1.0, 0)
        };
        double[] knots = new double[]{0.0, 0.0, 0.0, 1.0, 2.0, 3.0, 3.0, 3.0};
        double[] w = new double[]{1.0, 4.0, 1.0, 1.0, 1.0};
        NURBS n = new NURBS(points, knots, w, 2);
        int index = n.findSpawnIndex(1.0);

        Point3D p = n.getPointAt(index, 1.0);

        assertEquals(7.0 / 5.0, p.getX(), DELTA);
        assertEquals(6.0 / 5.0, p.getY(), DELTA);
    }

    @Test
    public void baseFunctions1() {
        Point3D[] points = new Point3D[]{};
        double[] knots = new double[]{0, 0, 0, 1, 2, 3, 4, 4, 5, 5};
        double[] w = new double[]{};
        NURBS n = new NURBS(points, knots, w, 2);
        double[] bf = n.getBasicFunctions(4, 2.5);

        assertEquals(3, bf.length);
        assertEquals(1.0 / 8.0, bf[0], DELTA);
        assertEquals(6.0 / 8.0, bf[1], DELTA);
        assertEquals(1.0 / 8.0, bf[2], DELTA);
    }

    @Test
    public void baseFunctions2() {
        Point3D[] points = new Point3D[]{
            new Point3D(0, 0, 0), new Point3D(1, 1, 0), new Point3D(3, 2, 0),
            new Point3D(4, 1, 0), new Point3D(5, -1, 0)
        };
        double[] knots = new double[]{0, 0, 0, 1, 2, 3, 4, 4, 5, 5, 5};
        double[] w = new double[]{};
        NURBS n = new NURBS(points, knots, w, 2);
        int index = n.findSpawnIndex(2.5);

        double[] bf = n.getBasicFunctions(index, 2.5);

        assertEquals(3, bf.length);
        assertEquals(1.0 / 8.0, bf[0], DELTA);
        assertEquals(6.0 / 8.0, bf[1], DELTA);
        assertEquals(1.0 / 8.0, bf[2], DELTA);
    }
}
