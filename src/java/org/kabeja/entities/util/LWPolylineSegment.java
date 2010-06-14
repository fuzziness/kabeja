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

package org.kabeja.entities.util;

import org.kabeja.entities.LW2DVertex;
import org.kabeja.entities.LWPolyline;
import org.kabeja.math.MathUtils;
import org.kabeja.math.Point3D;
import org.kabeja.math.Vector;


/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 */
public class LWPolylineSegment {
    public static final double DELTA = 0.001;
    private boolean bulged = false;
    private Point3D point1 = new Point3D();
    private Point3D point2 = new Point3D();
    private Point3D point3 = new Point3D();
    private Point3D point4 = new Point3D();
    private double radius;
    private double bulgeHeight;
    private LW2DVertex start;
    private LWPolyline p;

    public LWPolylineSegment(LW2DVertex start, LW2DVertex  end, LWPolyline p) {
        this.start = start;

        this.p = p;
        Point3D startPoint = new Point3D(start.getX(),start.getY(),0.0);
    	Point3D endPoint = new Point3D(end.getX(),end.getY(),0.0);
    	
        if (this.start.getBulge() != 0.0) {	
            double l = MathUtils.distance(startPoint, endPoint);
            // do nothing if the points are the same
            this.radius = getRadius(Math.abs(start.getBulge()), l);
            this.bulgeHeight = (start.getBulge() * l) / 2;

            setBulged(true);
            createCurvedTrapezium(startPoint, endPoint, this.radius, l);
        } else {
            createTrapezium(startPoint, endPoint);
        }
    }

    /**
     * @return Returns the bulge.
     */
    public double getBulge() {
        return this.start.getBulge();
    }

    /**
     * @return Returns the bulged.
     */
    public boolean isBulged() {
        return bulged;
    }

    /**
     * @param bulged
     *            The bulged to set.
     */
    public void setBulged(boolean bulged) {
        this.bulged = bulged;
    }

    /**
     * @return Returns the point1.
     */
    public Point3D getPoint1() {
        return point1;
    }

    /**
     * @param point1
     *            The point1 to set.
     */
    public void setPoint1(Point3D point1) {
        this.point1 = point1;
    }

    /**
     * @return Returns the point2.
     */
    public Point3D getPoint2() {
        return point2;
    }

    /**
     * @param point2
     *            The point2 to set.
     */
    public void setPoint2(Point3D point2) {
        this.point2 = point2;
    }

    /**
     * @return Returns the point3.
     */
    public Point3D getPoint3() {
        return point3;
    }

    /**
     * @param point3
     *            The point3 to set.
     */
    public void setPoint3(Point3D point3) {
        this.point3 = point3;
    }

    /**
     * @return Returns the point4.
     */
    public Point3D getPoint4() {
        return point4;
    }

    /**
     * @param point4
     *            The point4 to set.
     */
    public void setPoint4(Point3D point4) {
        this.point4 = point4;
    }

    /**
     * @return Returns the radius.
     */
    public double getRadius() {
        return radius;
    }

    /**
     * @param radius
     *            The radius to set.
     */
    public void setRadius(double radius) {
        this.radius = radius;
    }

    protected void createCurvedTrapezium(Point3D startPoint, Point3D endPoint, double radius, double length) {
        // first get the center point of the arc

        // middle point of chord
        double s = length / 2;
        Vector edgeDirection = MathUtils.getVector(startPoint,
                endPoint);
        edgeDirection = MathUtils.normalize(edgeDirection);

        Point3D mp = MathUtils.getPointOfStraightLine(startPoint,
                edgeDirection, s);

        Vector d = p.getExtrusion().getNormal();
        d = MathUtils.crossProduct(d, edgeDirection);
        d = MathUtils.normalize(d);

        double h = Math.abs(start.getBulge() * length) / 2;
        double r = p.getRadius(start.getBulge(), length);
    

        if (start.getBulge() > 0.0) {
            double t = h - r;
            mp = MathUtils.getPointOfStraightLine(mp, d, t);
        } else {
            double t = r - h;
            mp = MathUtils.getPointOfStraightLine(mp, d, t);
        }

        double c = 0.0;

        if (start.getStartWidth() > 0.0) {
            c = start.getStartWidth() / 2;
        } else {
            c = p.getStartWidth() / 2;
        }

        if (start.getBulge() > 0) {
            c = -1 * c;
        }

        // direction vector from start to center point
        d = MathUtils.getVector(startPoint, mp);
        d = MathUtils.normalize(d);
        point1 = MathUtils.getPointOfStraightLine(startPoint, d, c);
        point2 = MathUtils.getPointOfStraightLine(startPoint, d,
                (c * (-1)));

        if (start.getEndWidth() > 0.0) {
            c = start.getEndWidth() / 2;
        } else {
            c = this.p.getEndWidth() / 2;
        }

        if (start.getBulge() > 0) {
            c = -1 * c;
        }

        d = MathUtils.getVector(endPoint, mp);
        d = MathUtils.normalize(d);
        point3 = MathUtils.getPointOfStraightLine(endPoint, d, (c * (-1)));
        point4 = MathUtils.getPointOfStraightLine(endPoint, d, c);
    }

    protected void createTrapezium(Point3D startPoint, Point3D endPoint) {
        // we start at the start side
        double c = 0.0;

        if (start.getStartWidth() > 0.0) {
            c = start.getStartWidth() / 2;
        } else {
            c = this.p.getStartWidth() / 2;
        }

        Vector v = this.p.getExtrusion().getNormal();

     
        Vector x = MathUtils.getVector(startPoint, endPoint);

        // calculate the y vector
        v = MathUtils.crossProduct(v, x);
        v = MathUtils.normalize(v);
        point1 = MathUtils.getPointOfStraightLine(startPoint, v, c);
        point2 = MathUtils.getPointOfStraightLine(startPoint, v,
                (-1.0 * c));

        // on the end side
        if (start.getEndWidth() > 0.0) {
            c = start.getEndWidth() / 2;
        } else {
            c = this.p.getEndWidth() / 2;
        }

        point3 = MathUtils.getPointOfStraightLine(endPoint, v, (-1.0 * c));
        point4 = MathUtils.getPointOfStraightLine(endPoint, v, c);
    }

    /**
     * Caculate the radius of a cut circle segment between 2 Vertex
     *
     * @param bulge
     *            the vertex bulge
     * @param length
     *            the length of the circle cut
     */
    protected double getRadius(double bulge, double length) {
        double h = (bulge * length) / 2;
        double value = (h / 2) + (Math.pow(length, 2) / (8 * h));

        return value;
    }

    /**
     * @return Returns the bulgeHeight.
     */
    public double getBulgeHeight() {
        return bulgeHeight;
    }

    public void connect(LWPolylineSegment next) {
        // connect only if the angle between the
        // segments is > 0

        // first connection point
        Vector d1 = MathUtils.getVector(point1, point4);
        Vector d2 = MathUtils.getVector(next.getPoint4(), next.getPoint1());
        double angle = MathUtils.getAngle(d1, d2);

        if (Math.abs(angle) > DELTA) {
            Point3D p = MathUtils.getIntersection(point1, d1, next.getPoint4(), d2);
            setPoint4(p);

            next.setPoint1(p);

            d1 = MathUtils.getVector(point2, point3);
            d2 = MathUtils.getVector(next.getPoint3(), next.getPoint2());
            p = MathUtils.getIntersection(point2, d1, next.getPoint3(), d2);
            setPoint3(p);
            next.setPoint2(p);
        }
    }

    public double getInnerRadius() {
        double r = (this.start.getStartWidth() + this.start.getEndWidth()) / 2;

        if (r == 0.0) {
            r = (this.p.getStartWidth() + this.p.getEndWidth()) / 2;
        }

        return getRadius() - r;
    }

    public double getOuterRadius() {
        double r = (this.start.getStartWidth() + this.start.getEndWidth()) / 2;

        if (r == 0.0) {
            r = (this.p.getStartWidth() + this.p.getEndWidth()) / 2;
        }

        return getRadius() + r;
    }
}
