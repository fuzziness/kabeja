/*
 * Created on 29.09.2005
 *
 */
package org.kabeja.dxf;

import java.util.Map;

import org.kabeja.dxf.helpers.MathUtils;
import org.kabeja.dxf.helpers.Point;
import org.kabeja.svg.SVGConstants;
import org.kabeja.svg.SVGUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * @author simon
 * 
 */
public class DXF3DFace extends DXFSolid {

	 public String getType() {
	        // TODO Auto-generated method stub
	        return DXFConstants.ENTITY_TYPE_3DFACE;
	    }
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.miethxml.kabeja.svg.SVGGenerator#toSAX(org.xml.sax.ContentHandler)
	 */
	public void toSAX(ContentHandler handler, Map svgContext)
			throws SAXException {

		// all edges are visible
		if ((doc.getDXFHeader().hasVariable("$SPLFRAME") && doc.getDXFHeader()
				.getVariable("$SPLFRAME").getIntegerValue("70") == 1)
				|| getFlags() == 0) {

			AttributesImpl attr = new AttributesImpl();

			StringBuffer points = new StringBuffer();

			points.append(point1.getX());
			points.append(",");
			points.append(point1.getY());
			points.append(" ");

			points.append(point2.getX());
			points.append(",");
			points.append(point2.getY());
			points.append(" ");

			points.append(point3.getX());
			points.append(",");
			points.append(point3.getY());
			points.append(" ");

			points.append(point4.getX());
			points.append(",");
			points.append(point4.getY());
			points.append(" ");

			SVGUtils.addAttribute(attr, "points", points.toString());

			super.setCommonAttributes(attr, svgContext);

			SVGUtils.emptyElement(handler, SVGConstants.SVG_POLYGON, attr);

		} else {
			// draw only visible edges
			// bit 0 edge 1->2
			// bit 1 edge 2->3
			// bit 2 edge 3->4
			// bit 3 edge 4->1
			// if a bit is not set, the edge
			// is visible

			int flag = getFlags();

			if ((flag & 1) == 0) {
				edgeToSAX(handler, getPoint1(), getPoint2(), svgContext);

			}
			if ((flag & 2) == 0) {
				edgeToSAX(handler, getPoint2(), getPoint3(), svgContext);

			}
			if ((flag & 4) == 0) {
				edgeToSAX(handler, getPoint3(), getPoint4(), svgContext);

			}
			if ((flag & 8) == 0) {
				edgeToSAX(handler, getPoint4(), getPoint1(), svgContext);
			}

		}

	}

	protected void edgeToSAX(ContentHandler handler, Point p1, Point p2, Map svgContext)
			throws SAXException {
		AttributesImpl attr = new AttributesImpl();
		// set the attributes
		SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_X1, "" + p1.getX());

		double value = p1.getY();
		SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_Y1, "" + value);

		SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_X2, "" + p2.getX());

		value = p2.getY();

		SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_Y2, "" + value);
		super.setCommonAttributes(attr, svgContext);
		SVGUtils.emptyElement(handler, SVGConstants.SVG_LINE, attr);
	}

	public double getLength() {

		double length = 0.0;
		int flag = this.getFlags();

		if ((flag & 1) == 0) {
			length += MathUtils.distance(this.getPoint1(), this.getPoint2());

		}
		if ((flag & 2) == 0) {
			length += MathUtils.distance(this.getPoint2(), this.getPoint3());

		}
		if ((flag & 4) == 0) {
			length += MathUtils.distance(this.getPoint3(), this.getPoint4());

		}
		if ((flag & 8) == 0) {
			length += MathUtils.distance(this.getPoint4(), this.getPoint1());

		}

		return length;
	}

}
