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

import java.util.Iterator;

import junit.framework.TestCase;

import org.kabeja.math.Point3D;


public class PointParserTest extends TestCase {
    public void testParsePoint3D() {
        String pointString = "10.00 20.00 30.00";

        PointParser parser = new PointParser();
        parser.setCoordinateType(PointParser.COORDINATE_FORMAT_3D);
        parser.appendPointString(pointString);

        Iterator i = parser.getPointIterator();
        Point3D p = (Point3D) i.next();

        assertEquals(10.00, p.getX(), 0.000000001);
        assertEquals(20.00, p.getY(), 0.000000001);
        assertEquals(30.00, p.getZ(), 0.000000001);
    }

    public void testParseTwoPoints3D() {
        String pointString = "10.00,20.00,30.00,11.00,12.00,13.00";

        PointParser parser = new PointParser();
        parser.setCoordinateType(PointParser.COORDINATE_FORMAT_3D);
        parser.setSeparator(',');
        parser.setNumberGrouping('!');
        parser.appendPointString(pointString);
            
        Iterator i = parser.getPointIterator();
        Point3D p = (Point3D) i.next();

        assertEquals(10.00, p.getX(), 0.000000001);
        assertEquals(20.00, p.getY(), 0.000000001);
        assertEquals(30.00, p.getZ(), 0.000000001);

        p = (Point3D) i.next();

        assertEquals(11.00, p.getX(), 0.000000001);
        assertEquals(12.00, p.getY(), 0.000000001);
        assertEquals(13.00, p.getZ(), 0.000000001);
    }
    
    public void testParseTwoPoints2D() {
        String pointString = "10.00 20.00 30.00 11.00 12.00 13.00";

        PointParser parser = new PointParser();
        parser.setCoordinateType(PointParser.COORDINATE_FORMAT_2D);
        parser.appendPointString(pointString);

        Iterator i = parser.getPointIterator();
        Point3D p = (Point3D) i.next();

        assertEquals(10.00, p.getX(), 0.000000001);
        assertEquals(20.00, p.getY(), 0.000000001);
       

        p = (Point3D) i.next();

        assertEquals(30.00, p.getX(), 0.000000001);
        assertEquals(11.00, p.getY(), 0.000000001);
        
        p = (Point3D) i.next();

        assertEquals(12.00, p.getX(), 0.000000001);
        assertEquals(13.00, p.getY(), 0.000000001);
       
    }
    public void testParseOnePoints2D() {
        String pointString = "1515530.44513354 5035814.823278";

        PointParser parser = new PointParser();
        parser.appendPointString(pointString);

        Iterator i = parser.getPointIterator();
        Point3D p = (Point3D) i.next();

        assertEquals(1515530.44513354, p.getX(), 0.000000001);
        assertEquals(5035814.823278, p.getY(), 0.000000001);
       


    }
    
    
}
