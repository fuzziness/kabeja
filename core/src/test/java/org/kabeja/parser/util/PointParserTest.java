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
package org.kabeja.parser.util;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Iterator;
import org.kabeja.math.Point3D;

public class PointParserTest {

    public static final double DELTA = 1.0E-9;

    @Test
    public void parsePoint3D() {
        String pointString = "10.00 20.00 30.00";

        PointParser parser = new PointParser();
        parser.setCoordinateType(PointParser.COORDINATE_FORMAT_3D);
        parser.appendPointString(pointString);

        Iterator i = parser.getPointIterator();
        Point3D p = (Point3D) i.next();

        assertEquals(10.00, p.getX(), DELTA);
        assertEquals(20.00, p.getY(), DELTA);
        assertEquals(30.00, p.getZ(), DELTA);
    }

    @Test
    public void parseTwoPoints3D() {
        String pointString = "10.00,20.00,30.00,11.00,12.00,13.00";

        PointParser parser = new PointParser();
        parser.setCoordinateType(PointParser.COORDINATE_FORMAT_3D);
        parser.setSeparator(',');
        parser.setNumberGrouping('!');
        parser.appendPointString(pointString);

        Iterator i = parser.getPointIterator();
        Point3D p = (Point3D) i.next();

        assertEquals(10.00, p.getX(), DELTA);
        assertEquals(20.00, p.getY(), DELTA);
        assertEquals(30.00, p.getZ(), DELTA);

        p = (Point3D) i.next();

        assertEquals(11.00, p.getX(), DELTA);
        assertEquals(12.00, p.getY(), DELTA);
        assertEquals(13.00, p.getZ(), DELTA);
    }

    @Test
    public void parseTwoPoints2D() {
        String pointString = "10.00 20.00 30.00 11.00 12.00 13.00";

        PointParser parser = new PointParser();
        parser.setCoordinateType(PointParser.COORDINATE_FORMAT_2D);
        parser.appendPointString(pointString);

        Iterator i = parser.getPointIterator();
        Point3D p = (Point3D) i.next();

        assertEquals(10.00, p.getX(), DELTA);
        assertEquals(20.00, p.getY(), DELTA);

        p = (Point3D) i.next();

        assertEquals(30.00, p.getX(), DELTA);
        assertEquals(11.00, p.getY(), DELTA);

        p = (Point3D) i.next();

        assertEquals(12.00, p.getX(), DELTA);
        assertEquals(13.00, p.getY(), DELTA);
    }

    @Test
    public void parseOnePoints2D() {
        String pointString = "1515530.44513354 5035814.823278";

        PointParser parser = new PointParser();
        parser.appendPointString(pointString);

        Iterator i = parser.getPointIterator();
        Point3D p = (Point3D) i.next();

        assertEquals(1515530.44513354, p.getX(), DELTA);
        assertEquals(5035814.823278, p.getY(), DELTA);
    }
}
