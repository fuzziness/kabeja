/*
 * Created on Jul 15, 2004
 *
 */
package org.kabeja.dxf;

import java.util.Map;

import org.kabeja.dxf.helpers.DXFUtils;
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

	private double ratio = 1.0;

	private double startParameter = Double.POSITIVE_INFINITY;

	private double endParameter = Double.POSITIVE_INFINITY;

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
		if (startParameter == Double.POSITIVE_INFINITY
				&& endParameter == Double.POSITIVE_INFINITY) {

			SVGUtils.addAttribute(attr, "cx", "" + center.getX());
			SVGUtils.addAttribute(attr, "cy", ""
					+ doc.translateY(center.getY()));
			double major = DXFUtils.distance(center, majorAxisDirection);
			double minor = ratio * major;
			SVGUtils.addAttribute(attr, "rx", "" + major);
			SVGUtils.addAttribute(attr, "ry", "" + minor);
			// chek for rotation

			if (majorAxisDirection.getY() != 0.0) {
				double angle = Math.atan(1 / (majorAxisDirection.getY() / majorAxisDirection.getX()));
				SVGUtils.addAttribute(attr, "transform", "rotate(" + angle
						+ "  " + center.getX() + " " + center.getY() + ")");
			}
			double angle = this.getRotationAngle();
			if (angle != 0.0) {
				StringBuffer buf = new StringBuffer();
				buf.append("rotate(");
				buf.append((-1.0 * Math.toDegrees(angle)));
				buf.append(' ');
				buf.append(center.getX());
				buf.append(' ');
				buf.append(center.getY());
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

		double length = this.getMajorAxisLength();

		bounds.addToBounds(center.getX() + length, center.getY() + length);
		bounds.addToBounds(center.getX() - length, center.getY() - length);

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

		boolean override = false;
		if (this.startParameter == Double.POSITIVE_INFINITY
				&& this.endParameter == Double.POSITIVE_INFINITY) {
			this.startParameter = 0.0;
			this.endParameter = 2 * Math.PI;
			override = true;
		}

		StringBuffer buf = new StringBuffer();

		Point start = this.getLocalStartPoint();
		//translate to centerpoint
		start.setX(start.getX() + this.center.getX());
		start.setY(start.getY() + this.center.getY());

		Point end = this.getLocalEndPoint();
		//translate to centerpoint
		end.setX(end.getX() + this.center.getX());
		end.setY(end.getY() + this.center.getY());

		//get the angle between x-axis and major-axis
		double major = majorAxisDirection.getLength();
		double angle = MathUtils
				.getAngle(DXFConstants.DEFAULT_X_AXIS_VECTOR, this.majorAxisDirection);

		if(this.majorAxisDirection.getY()>0){
			angle = -1*angle;
		}



		if (angle != 0.0) {
			start = MathUtils.rotatePointXY(start, this.center, angle);
			end = MathUtils.rotatePointXY(end, this.center, angle);
		}

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
		 buf.append((-1.0*Math.toDegrees(this.getRotationAngle())));
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

		if (override) {
			this.startParameter = Double.POSITIVE_INFINITY;
			this.endParameter = Double.POSITIVE_INFINITY;
		}

		return buf.toString();
	}

	public double getMajorAxisLength() {
		return majorAxisDirection.getLength();
	}

	public Point getLocalStartPoint() {
		Point p = new Point();
		double major = getMajorAxisLength();
		double minor = major * this.ratio;
		p.setX(major * Math.cos(this.startParameter));
		p.setY(minor * Math.sin(this.startParameter));
		p.setZ(0.0);
		return p;
	}

	public Point getLocalEndPoint() {
		Point p = new Point();
		double major = getMajorAxisLength();
		double minor = major * this.ratio;
		p.setX(major * Math.cos(this.endParameter));
		p.setY(minor * Math.sin(this.endParameter));
		p.setZ(0.0);
		return p;
	}

	public double getRotationAngle() {

		double major = majorAxisDirection.getLength();
		Point end = MathUtils.getPointOfStraightLine(center,majorAxisDirection,major);
		Vector v = MathUtils
				.getVector(getCenterPoint(), end);
		double angle = MathUtils
				.getAngle(DXFConstants.DEFAULT_X_AXIS_VECTOR, v);
		return angle;
	}

}