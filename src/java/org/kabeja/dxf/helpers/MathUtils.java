/*
 * Created on 19.10.2005
 *
 */
package org.kabeja.dxf.helpers;

/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 */
public class MathUtils {

	/**
	 * Calculate the scalar product of vector a and vector b
	 *
	 * @param a
	 * @param b
	 * @return the result of the scalar product
	 */

	public static double scalarProduct(Vector a, Vector b) {

		double r = a.getX() * b.getX() + a.getY() * b.getY() + a.getZ()
				* b.getZ();
		return r;
	}

	/**
	 * Returns the absalute value (or length) of the vector
	 *
	 * @param v
	 * @return
	 */

	public static double absoluteValue(Vector v) {
		double r = Math.sqrt(Math.pow(v.getX(), 2) + Math.pow(v.getY(), 2)
				+ Math.pow(v.getZ(), 2));
		return r;
	}

	/**
	 * Calculate the cross product of 2 vectors.
	 *
	 * @param a
	 * @param b
	 * @return a new vector as result of the cross product of a and b
	 */

	public static Vector crossProduct(Vector a, Vector b) {

		Vector r = new Vector();
		r.setX(a.getY() * b.getZ() - a.getZ() * b.getY());
		r.setY(a.getZ() * b.getX() - a.getX() * b.getZ());
		r.setZ(a.getX() * b.getY() - a.getY() * b.getX());

		return r;
	}

	/**
	 * Scale a vector with the given value
	 *
	 * @param a
	 *            the vector
	 * @param scale
	 *            the value to scale
	 * @return a new vector scaled with the given
	 */

	public static Vector scaleVector(Vector a, double scale) {
		Vector v = new Vector();

		v.setX(a.getX() * scale);
		v.setY(a.getY() * scale);
		v.setZ(a.getZ() * scale);

		return v;
	}

	/**
	 * Calculate a point of a straigt line.
	 *
	 * @param a
	 *            the startpoint of the straight line
	 * @param direction
	 *            the direction vector of the straight line
	 * @param parameter
	 *            the parameter
	 * @return a new point
	 */

	public static Point getPointOfStraightLine(Point a, Vector direction,
			double parameter) {

		// p = a + v*scale

		Point p = new Point();
		Vector v = scaleVector(direction, parameter);

		p.setX(a.getX() + v.getX());
		p.setY(a.getY() + v.getY());
		p.setZ(a.getZ() + v.getZ());

		return p;
	}

	/**
	 * Calculate the vector from point a to point b
	 *
	 * @param a
	 * @param b
	 * @return the vector from a to b
	 */

	public static Vector getVector(Point a, Point b) {
		Vector v = new Vector();

		v.setX(b.getX() - a.getX());
		v.setY(b.getY() - a.getY());
		v.setZ(b.getZ() - a.getZ());

		return v;
	}

	public static Point getIntersection(Point a, Vector u, Point b, Vector v) {

		//u = normalize(u);
		//v = normalize(v);

		Vector n = crossProduct(u, v);
		Vector m = crossProduct(getVector(a, b), v);
		double s = 0;
		if (n.getZ() != 0.0) {
			s = m.getZ() / n.getZ();
		} else if (n.getY() != 0.0) {
			s = m.getY() / n.getY();
		} else if (n.getX() != 0.0) {
			s = m.getX() / n.getX();
		}

		Point p = getPointOfStraightLine(a, u, s);
		return p;
	}

	public static double distance(Point start, Point end) {

		double length = Math.sqrt(Math.pow((end.getX() - start.getX()), 2)
				+ Math.pow((end.getY() - start.getY()), 2)
				+ Math.pow((end.getZ() - start.getZ()), 2));
		return length;
	}

	/**
	 * Calculate the angle between vector a vector b
	 *
	 * @param a
	 * @param b
	 * @return the angle in radian
	 */

	public static double getAngle(Vector a, Vector b) {
		double cos = scalarProduct(a, b)
				/ (absoluteValue(a) * absoluteValue(b));
		return Math.acos(cos);
	}

	/**
	 * Rotate the given point around centerpoint with the given angle in X-Y
	 * plane.
	 *
	 * @param p
	 *            the point to rotate
	 * @param center
	 *            the centerpoint
	 * @param angle
	 *            in radian
	 * @return the rotated point
	 */

	public static Point rotatePointXY(Point p, Point center, double angle) {

		Point r = new Point();
		r.setX(center.getX() + Math.cos(angle) * (p.getX() - center.getX())
				- (p.getY() - center.getY()) * Math.sin(angle));
		r.setY(center.getY() + Math.cos(angle) * (p.getY() - center.getY())
				- (p.getX() - center.getX()) * Math.sin(angle));
		r.setZ(p.getZ());
		return r;
	}

	public static Vector normalize(Vector v) {
		double l = absoluteValue(v);
		return scaleVector(v, (1 / l));
	}


	/**
	 * Returns the qaudrant:<br/>
	 * 0,1,2 or 3
	 * @param p
	 * @param center
	 * @return
	 */



	public static int getQuadrant(Point p,Point center){

        if(p.getX()<center.getX()){
        	if(p.getY()>=center.getY()){
        		return 1;
        	}else{
        		return 2;
        	}

        }else{
        	if(p.getY()>=center.getY()){
        		return 0;
        	}else{
        		return 3;
        	}
        }


	}

}
