/*
 * Created on Jun 28, 2004
 *
 */
package org.kabeja.dxf;

import java.util.Map;

import org.kabeja.dxf.helpers.MathUtils;
import org.kabeja.dxf.helpers.Point;
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
public class DXFArc extends DXFEntity implements SVGPathBoundaryElement {

	private Point center;

	private double radius;

	private double start_angle;

	private double end_angle;

	public DXFArc() {

		center = new Point();

	}

	/**
	 * @return Returns the end_angle.
	 */
	public double getEndAngle() {
		return end_angle;
	}

	/**
	 * @param end_angle
	 *            The end_angle to set.
	 */
	public void setEndAngle(double end_angle) {
		this.end_angle = end_angle;
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

	/**
	 * @return Returns the start_angle.
	 */
	public double getStartAngle() {
		return start_angle;
	}

	/**
	 * @param start_angle
	 *            The start_angle to set.
	 */
	public void setStartAngle(double start_angle) {
		this.start_angle = start_angle;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.dxf2svg.dxf.DXFEntity#toSAX(org.xml.sax.ContentHandler)
	 */
	public void toSAX(ContentHandler handler, Map svgContext)
			throws SAXException {

		// model as a Path with a ellipse-arc element
		// calulate the startpoint

		AttributesImpl attr = new AttributesImpl();

		SVGUtils.addAttribute(attr, "d", getSVGPath());

		super.setCommonAttributes(attr);
		SVGUtils.emptyElement(handler, SVGConstants.SVG_PATH, attr);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.dxf2svg.dxf.DXFEntity#updateViewPort()
	 */
	public Bounds getBounds() {

		Bounds bounds = new Bounds();
       Point start = getStartPoint();
       Point end = getEndPoint();
		bounds.addToBounds(start);
		bounds.addToBounds(end);


        int startQ = MathUtils.getQuadrant(start,center);
        int endQ =  MathUtils.getQuadrant(end,center);

        // if the bulge < 0 the center point is on the left side
        // if the bulge > 0 the center point is ont the right side



        if (endQ < startQ) {
            endQ += 4;
        }

        while (endQ > startQ) {
            switch (startQ) {
            case 0:
                bounds.addToBounds(center.getX(), center.getY() + radius);
                break;
            case 1:
                bounds.addToBounds(center.getX() - radius, center.getY());
                break;
            case 2:
                bounds.addToBounds(center.getX(), center.getY() - radius);
                break;
            case 3:
                bounds.addToBounds(center.getX() + radius,center.getY());
                endQ -= 4;
                startQ -= 4;
                break;
            }
            startQ++;

        }





		// which circle-quadrants ?
//		int start_q = 0;
//		int end_q = 0;
//
//		if (start_angle >= 360) {
//
//			double v = Math.floor(start_angle / 360);
//
//			start_q = (int) Math.floor((start_angle - 360 * v) / 90);
//		} else {
//			start_q = (int) Math.floor(start_angle / 90);
//		}
//		if (end_angle >= 360) {
//
//			double v = Math.floor(end_angle / 360);
//
//			end_q = (int) Math.floor((end_angle - 360 * v) / 90);
//		} else {
//			end_q = (int) Math.floor(end_angle / 90);
//		}
//
//		int count = 0;
//		// going counterclockwise through all quadrants
//		while (end_q != start_q) {
//
//			if (start_q < 1) {
//				bounds.addToBounds(center.getX(), center.getY() + radius);
//				start_q += 1;
//			} else if (start_q < 2) {
//				bounds.addToBounds(center.getX() - radius, center.getY());
//				start_q += 1;
//			} else if (start_q < 3) {
//
//				bounds.addToBounds(center.getX(), center.getY() - radius);
//				start_q += 1;
//			} else if (start_q < 4) {
//
//				bounds.addToBounds(center.getX() + radius, center.getY());
//				start_q -= 3;
//			}
//
//		}

		return bounds;
	}

	public void setCenterPoint(Point p) {
		this.center = p;
	}

	public Point getCenterPoint() {
		return center;
	}

	public Point getStartPoint() {
		Point p = new Point();

		double x = radius * Math.cos(Math.toRadians(start_angle));
		p.setX(center.getX() + x);

		double y = radius * Math.sin(Math.toRadians(start_angle));
		p.setY(center.getY() + y);

		return p;
	}

	public Point getEndPoint() {

		Point p = new Point();

		double x = radius * Math.cos(Math.toRadians(end_angle));
		p.setX(center.getX() + x);

		double y = radius * Math.sin(Math.toRadians(end_angle));
		p.setY(center.getY() + y);

		return p;
	}

	/**
	 *
	 */

	public String getType() {
		return DXFConstants.ENTITY_TYPE_ARC;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.kabeja.dxf.helpers.HatchBoundaryEdge#getSVGPath()
	 */
	public String getSVGPath() {
		Point p;

		StringBuffer path = new StringBuffer();

		p = this.getStartPoint();

		path.append("M ");
		path.append(p.getX());
		path.append(' ');
		path.append(p.getY());
		path.append(" A ");
		path.append(radius);
		path.append(' ');
		path.append(radius);
		path.append(" 0");

		double diff = this.getTotalAngle();


		// the large-arc-flag
		if (diff > 180) {
			path.append(" 1 ");
		} else {
			path.append(" 0 ");
		}

		if (Math.abs(diff) > 0) {
			// the sweep-flag
			path.append(" 1 ");

		} else {
			// sweep flag 0 
			//funny here we would never come
			//here
			path.append(" 0 ");
		}

		p = this.getEndPoint();
		path.append(' ');
		path.append(p.getX());
		path.append(' ');
		path.append(p.getY());

		return path.toString();
	}

	public double getLength() {
		double alpha = this.getTotalAngle();	
		return (alpha*Math.PI*this.radius)/180.0;
	}
	
	public double getTotalAngle(){
		if(this.end_angle<this.start_angle){
			return (360+this.end_angle)-this.start_angle;
		}else{
		 return Math.abs(this.end_angle-this.start_angle);
		}
	}
	
	
	public double getChordLength(){
		double s = 2*this.radius*Math.sin(Math.toRadians(this.getTotalAngle()/2));
		return s;
	}
	
}