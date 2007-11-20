/*
 * Created on Jul 15, 2004
 *
 */
package org.kabeja.dxf;

import org.kabeja.dxf.helpers.MathUtils;
import org.kabeja.dxf.helpers.Point;
import org.kabeja.dxf.helpers.Vector;

/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 * 
 */
public class DXFEllipse extends DXFEntity {

	public static final double DEFAULT_END_PARAMETER = Math.PI * 2;

	public static final double DEFAULT_START_PARAMETER = 0.0;

	public static final int INTEGRATION_STEPS = 15;

	private double ratio = 1.0;

	private double startParameter = DEFAULT_START_PARAMETER;

	private double endParameter = DEFAULT_END_PARAMETER;

	private Point center = new Point();

	private Vector majorAxisDirection = new Vector();

	/**
	 * 
	 */
	public DXFEllipse() {

		center = new Point();

	}



	public Bounds getBounds() {
		// this are the bounds of a circle with major-radius of the ellipse
		// TODO change this to the correct bounds
		double alpha = Math.toRadians(this.getRotationAngle());
		Bounds bounds = new Bounds();
		if (this.startParameter == DEFAULT_START_PARAMETER
				&& this.endParameter == DEFAULT_END_PARAMETER && alpha == 0.0) {
			double length = this.getHalfMajorAxisLength();

			bounds.addToBounds(center.getX() + length, center.getY() + length);
			bounds.addToBounds(center.getX() - length, center.getY() - length);
		} else {
			int n = 40;

			// we walking over the the ellipse or elliptical arc
			double h = (this.endParameter - this.startParameter) / n;

			double start = this.startParameter;
			double major = this.getHalfMajorAxisLength();
			double minor = major * this.ratio;
			for (int i = 0; i <= n; i++) {
				double x = major * Math.cos(start);
				double y = minor * Math.sin(start);
				
				if (alpha != 0.0) {

					double lx=x;
					x = lx * Math.cos(alpha) - y * Math.sin(alpha);
					y = lx * Math.sin(alpha) + y * Math.cos(alpha);
				}
				bounds.addToBounds(this.center.getX() + x, this.center.getY()
						+ y);

				start += h;
			}

		}
		return bounds;
	}

	public Point getCenterPoint() {
		return center;
	}

	public void setCenterPoint(Point center) {
		this.center = center;
	}

	public Vector getMajorAxisDirection() {
		return majorAxisDirection;
	}

	public void setMajorAxisDirection(Vector d) {
		this.majorAxisDirection = d;
	}

	public double getEndParameter() {
		return endParameter;
	}

	public void setEndParameter(double endParameter) {
		this.endParameter = endParameter;
	}

	public double getRatio() {
		return ratio;
	}

	public void setRatio(double ratio) {
		this.ratio = ratio;
	}

	public double getStartParameter() {
		return startParameter;
	}

	public void setStartParameter(double startParameter) {
		this.startParameter = startParameter;
	}

	public String getType() {
		return DXFConstants.ENTITY_TYPE_ELLIPSE;
	}


	public double getHalfMajorAxisLength() {
		return majorAxisDirection.getLength();
	}

	
	
	public Point getLocalPointAt(double para){
		Point p = new Point();
		double major = getHalfMajorAxisLength();
		double minor = major * this.ratio;
		double x = major * Math.cos(para);
		double y = minor * Math.sin(para);
		double alpha = this.getRotationAngle();
		if (alpha != 0.0) {
			double lx = x;
			x = lx * Math.cos(alpha) - y * Math.sin(alpha);
			y = lx * Math.sin(alpha) + y * Math.cos(alpha);
		}
		p.setX(x);
		p.setY(y);
		p.setZ(0.0);
		return p;
	}
	
	
	
	
	
	
	
	
	
	public Point getLocalStartPoint() {
	   return this.getLocalPointAt(this.startParameter);
	}

	public Point getLocalEndPoint() {
	      return this.getLocalPointAt(this.endParameter);
	}

	public double getRotationAngle() {

		return MathUtils.getAngle(DXFConstants.DEFAULT_X_AXIS_VECTOR, majorAxisDirection);
	}

	public double getLength() {

		int n = INTEGRATION_STEPS;
		double h = (this.endParameter - this.startParameter) / n;

		double a = this.getHalfMajorAxisLength();
		double b = a * this.ratio;
		double start = this.startParameter;

		double end = 0.0;
		double length = 0.0;

		// length = integral (sqrt((major*sin(t))^2+(minor*cos(t))^2))
		for (int i = 0; i < n; i++) {

			double center = h / 2 + start;
			end = start + h;

			double w1 = Math.sqrt(Math.pow(a * Math.sin(start), 2)
					+ Math.pow(b * Math.cos(start), 2));
			double w2 = Math.sqrt(Math.pow(a * Math.sin(center), 2)
					+ Math.pow(b * Math.cos(center), 2));
			double w3 = Math.sqrt(Math.pow(a * Math.sin(end), 2)
					+ Math.pow(b * Math.cos(end), 2));
			// SIMPSON where (h/2)/3 is h/6 here
			length += (w1 + 4 * w2 + w3) * (h / 6);
			start = end;

		}

		return length;
	}

}