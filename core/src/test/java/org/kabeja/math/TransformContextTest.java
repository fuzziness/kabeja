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
 * Created on 24.11.2008
 *
 */
package org.kabeja.math;

import org.junit.Test;
import static org.junit.Assert.*;

public class TransformContextTest {

    public static final double DELTA = 1.0E-7;

    @Test
    public void translation2D() {
        TransformContext context = new TransformContext();
        Vector translation = new Vector(10.0, 10.0, 0);

        Point3D p = new Point3D(0, 0, 0);

        context.setTranslation(translation);
        Point3D result = context.transform(p);

        assertEquals(10.0, result.getX(), DELTA);
        assertEquals(10.0, result.getY(), DELTA);
        assertEquals(0.0, result.getZ(), DELTA);
    }

    @Test
    public void scale2D() {
        TransformContext context = new TransformContext();

        Point3D p = new Point3D(1, 1, 0);

        context.setScale(2.5, 3.0, 0);
        Point3D result = context.transform(p);

        assertEquals(2.5, result.getX(), DELTA);
        assertEquals(3.0, result.getY(), DELTA);
        assertEquals(0.0, result.getZ(), DELTA);
    }

    @Test
    public void translationAndScale2D() {
        TransformContext context = new TransformContext();
        Vector translation = new Vector(10.0, 10.0, 0);

        Point3D p = new Point3D(0, 0, 0);

        context.setTranslation(translation);
        context.debug();
        context.appendScale(2.5, 3.0, 0);
        context.debug();
        Point3D result = context.transform(p);

        assertEquals(25.0, result.getX(), DELTA);
        assertEquals(30.0, result.getY(), DELTA);
        assertEquals(0.0, result.getZ(), DELTA);
    }
}
