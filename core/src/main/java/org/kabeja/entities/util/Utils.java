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
 Copyright 2005 Simon Mieth

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package org.kabeja.entities.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import org.kabeja.DraftDocument;
import org.kabeja.common.Header;
import org.kabeja.common.Layer;
import org.kabeja.common.Variable;
import org.kabeja.entities.Line;
import org.kabeja.entities.Polyline;
import org.kabeja.entities.Vertex;
import org.kabeja.math.MathUtils;
import org.kabeja.math.Point3D;
import org.kabeja.math.Vector;
import org.kabeja.util.Constants;
import org.kabeja.util.LayerComparator;

/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 * 
 */
public class Utils {

  

    /**
     *
     */
    public Utils() {
        super();
    }

    public static double distance(Point3D start, Point3D end) {
        double length;
        length = Math.sqrt(Math.pow((end.getX() - start.getX()), 2)
                + Math.pow((end.getY() - start.getY()), 2));

        return length;
    }

    public static double rotateAngleX(Point3D start, Point3D end) {
        if (end.getY() == start.getY()) {
            return 0.0;
        }

        double dx = end.getX() - start.getX();
        double dy = end.getY() - start.getY();

        return Math.toDegrees(Math.atan(1 / (dy / dx)));
    }

    public static double vectorValue(double[] x) {
        double v = 0.0;

        for (int i = 0; i < x.length; i++) {
            v += (x[i] * x[i]);
        }

        return Math.sqrt(v);
    }

    public static Point3D scalePoint(Point3D p, double scale) {
        Point3D r = new Point3D();
        r.setX(p.getX() * scale);
        r.setY(p.getY() * scale);
        r.setZ(p.getZ() * scale);

        return r;
    }

    public static Point3D getPointFromParameterizedLine(Point3D basePoint,
            Vector direction, double parameter) {
        Point3D r = scalePoint(direction, parameter);

        r.setX(r.getX() + basePoint.getX());
        r.setY(r.getY() + basePoint.getY());
        r.setZ(r.getZ() + basePoint.getZ());

        return r;
    }

    public static void reverseLine(Line line) {
        Point3D start = line.getStartPoint();
        line.setStartPoint(line.getEndPoint());
        line.setEndPoint(start);
    }

    public static void reversePolyline(Polyline pline) {
        ArrayList list = new ArrayList();
        double bulge = 0;
        int size = pline.getVertexCount();

        for (int i = 0; i < size; i++) {
            Vertex v = pline.getVertex(0);
            double b = v.getBulge();

            if (b != 0) {
                v.setBulge(0);
            }

            // the predecessor becomes the reversed bulge
            if (bulge != 0.0) {
                v.setBulge(bulge * (-1.0));
            }

            bulge = b;

            list.add(v);
            pline.removeVertex(0);
        }

        // reverse now
        for (int i = 1; i <= size; i++) {
            pline.addVertex((Vertex) list.get(size - i));
        }
    }

    public static double getArcRadius(Vertex start, Vertex end) {
        double alpha = 4 * Math.atan(Math.abs(start.getBulge()));
        double l = MathUtils.distance(start.getPoint(), end.getPoint());
        double r = l / (2 * Math.sin(alpha / 2));

        return r;
    }

    /**
     * Tests if the two points are the same for a given radius. In other words
     * the distance between the two points is lower then the radius.
     * 
     * @param p1
     * @param p2
     * @param radius
     * @return
     */
    public static boolean equals(Point3D p1, Point3D p2, double radius) {
        return distance(p1, p2) < radius;
    }

    /**
     * Converts the default iterator to a sorted iterator by the z-index of
     * layers
     * 
     * @param i
     * @return
     */

    public static Iterator<Layer> sortedLayersByZIndexIterator(Iterator<Layer> i) {
        TreeSet<Layer> set = new TreeSet<Layer>(new LayerComparator());
        while (i.hasNext()) {
            set.add(i.next());
        }
        return set.iterator();
    }

    /**
     * Set the give bit on the int.
     * 
     * @param flags
     * @param bitPos
     * @return
     */

    public static int enableBit(int flags, int bitPos) {

        int v = (int) Math.pow(2, bitPos);

        return flags | v;
    }
    
    public static int setBit(int bitmask,int bit,boolean enabled){
    	if(enabled){
    		  int v = (int) Math.pow(2, bit);
    	      return bitmask| v;
    	      
    	}else{
    		return  bitmask & ~bit;
    	}
    }
    
    public static boolean isBitEnabled(int flags, int bitPosition) {
        int mask = (int) Math.pow(2.0, bitPosition);
        return (flags & mask) == mask;

    }
    
    

    public static String generateNewID(DraftDocument doc) {
        Header header = doc.getHeader();
        Variable v = header
                .getVariable(Constants.HEADER_VARIABLE_HANDSEED);
        String hex = v.getValue("5");
        long lastFreeID = Long.decode("#"+hex).intValue();
        long id = ++lastFreeID;
        String currentID = Long.toHexString(id).toUpperCase();
        v.setValue("5", currentID);
        return currentID;
    }

    
    public static long generateID(DraftDocument doc) {
        Header header = doc.getHeader();
        long lastFreeID = header.getLastID();
        long id = ++lastFreeID;
        return id;
    }

    public static long parseIDString(String id){
    	try{
    	  return Long.decode("#"+id).longValue();
    	}catch(Exception e){
    		return -1;
    	}
    }

}
