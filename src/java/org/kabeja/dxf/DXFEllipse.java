/*
 * Created on Jul 15, 2004
 *
 */
package org.kabeja.dxf;

import java.util.Map;

import org.kabeja.dxf.helpers.MathUtils;
import org.kabeja.dxf.helpers.Point;
import org.kabeja.dxf.helpers.Vector;
import org.kabeja.svg.SVGConstants;
import org.kabeja.svg.SVGPathBoundaryElement;
import org.kabeja.svg.SVGUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 * 
 */
public class DXFEllipse extends DXFEntity implements SVGPathBoundaryElement {

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dxf2svg.svg.SVGGenerator#toSAX(org.xml.sax.ContentHandler)
	 */
	public void toSAX(ContentHandler handler, Map svgContext)
			throws SAXException {

		AttributesImpl attr = new AttributesImpl();
		super.setCommonAttributes(attr);
		if (startParameter == DEFAULT_START_PARAMETER
				&& endParameter == DEFAULT_END_PARAMETER) {

			SVGUtils.addAttribute(attr, "cx", "" + this.center.getX());
			SVGUtils.addAttribute(attr, "cy", "" + this.center.getY());
			double major = this.getHalfMajorAxisLength();
			double minor = this.ratio * major;
			SVGUtils.addAttribute(attr, "rx", "" + major);
			SVGUtils.addAttribute(attr, "ry", "" + minor);
			// chek for rotation

			if (this.majorAxisDirection.getY() != 0.0) {
				double angle = Math
						.atan(1 / (this.majorAxisDirection.getY() / this.majorAxisDirection
								.getX()));
				SVGUtils.addAttribute(attr, "transform", "rotate(" + angle
						+ "  " + this.center.getX() + " " + this.center.getY()
						+ ")");
			}

			double angle = this.getRotationAngle();
			if (angle != 0.0) {
				StringBuffer buf = new StringBuffer();
				buf.append("rotate(");
				buf.append((-1.0 * Math.toDegrees(angle)));
				buf.append(' ');
				buf.append(this.center.getX());
				buf.append(' ');
				buf.append(this.center.getY());
				buf.append(')');
				SVGUtils.addAttribute(attr, "transform", buf.toString());
			}

			SVGUtils.addAttribute(attr, "fill", "none");
			SVGUtils.emptyElement(handler, SVGConstants.SVG_ELLIPSE, attr);

		} else {

			SVGUtils.addAttribute(attr, "d", getSVGPath());
			SVGUtils.emptyElement(handler, SVGConstants.SVG_PATH, attr);

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dxf2svg.dxf.DXFEntity#getBounds()
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kabeja.dxf.helpers.HatchBoundaryEdge#getSVGPath()
	 */
	public String getSVGPath() {

		// TODO test the output

		StringBuffer buf = new StringBuffer();

		Point start = this.getLocalStartPoint();
		// translate to centerpoint
		start.setX(start.getX() + this.center.getX());
		start.setY(start.getY() + this.center.getY());

		Point end = this.getLocalEndPoint();
		// translate to centerpoint
		end.setX(end.getX() + this.center.getX());
		end.setY(end.getY() + this.center.getY());

		// get the angle between x-axis and major-axis
		double major = this.majorAxisDirection.getLength();
//		double angle = MathUtils.getAngle(DXFConstants.DEFAULT_X_AXIS_VECTOR,
//				this.majorAxisDirection);
//
//		if (this.majorAxisDirection.getY() > 0) {
//			angle = -1 * angle;
//		}
//
//		if (angle != 0.0) {
//			start = MathUtils.rotatePointXY(start, this.center, angle);
//			end = MathUtils.rotatePointXY(end, this.center, angle);
//		}

		double angle = this.getRotationAngle();
		buf.append("M ");
		buf.append(start.getX());
		buf.append(' ');
		buf.append(start.getY());

		buf.append(" A ");
		buf.append(major);
		buf.append(' ');
		buf.append(major * this.ratio);
		buf.append(' ');

		// rotation value of the ellipse
		buf.append((-1.0 * Math.toDegrees(angle)));
		buf.append(' ');

		// the large-arc flag
		if ((this.endParameter - this.startParameter) > Math.PI) {
			buf.append(1);
		} else {
			buf.append(0);
		}
		buf.append(' ');

		// the sweep-flag always 0
		buf.append(" 1 ");

		// the endpoint
		buf.append(end.getX());
		buf.append(' ');
		buf.append(end.getY());

		return buf.toString();
	}

	public double getHalfMajorAxisLength() {
		return majorAxisDirection.getLength();
	}

	public Point getLocalStartPoint() {
		Point p = new Point();
		double major = getHalfMajorAxisLength();
		double minor = major * this.ratio;
		double x = major * Math.cos(this.startParameter);
		double y = minor * Math.sin(this.startParameter);
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

	public Point getLocalEndPoint() {
		Point p = new Point();
		double major = getHalfMajorAxisLength();
		double minor = major * this.ratio;
		double x = major * Math.cos(this.endParameter);
		double y = minor * Math.sin(this.endParameter);
		
		
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

	public double getRotationAngle() {

//		double major = majorAxisDirection.getLength();
//		Point end = MathUtils.getPointOfStraightLine(this.center,
//				majorAxisDirection, major);
//		Vector v = MathUtils.getVector(this.center, end);
//		double angle = MathUtils
//				.getAngle(DXFConstants.DEFAULT_X_AXIS_VECTOR, v);
//		return angle;
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