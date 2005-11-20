/*
 * Created on 17.10.2005
 *
 */
package org.kabeja.dxf;

import java.util.Map;

import org.kabeja.dxf.helpers.DXFUtils;
import org.kabeja.dxf.helpers.Point;
import org.kabeja.svg.SVGConstants;
import org.kabeja.svg.SVGContext;
import org.kabeja.svg.SVGUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 */
public class DXFXLine extends DXFRay{






	/* (non-Javadoc)
	 * @see de.miethxml.kabeja.dxf.DXFEntity#getBounds()
	 */
	public Bounds getBounds() {
		//the xline is a infinite straight line
		//so we omit the bounds

		bounds.setValid(false);
		return bounds;
	}
	/* (non-Javadoc)
	 * @see de.miethxml.kabeja.dxf.DXFEntity#getType()
	 */
	public String getType() {

		return DXFConstants.ENTITY_TYPE_XLINE;
	}
	/* (non-Javadoc)
	 * @see de.miethxml.kabeja.svg.SVGGenerator#toSAX(org.xml.sax.ContentHandler, java.util.Map)
	 */
	public void toSAX(ContentHandler handler, Map svgContext)
			throws SAXException {
		Bounds b = (Bounds)svgContext.get(SVGContext.DRAFT_BOUNDS);
		//we will create a line, which goes over or to the end of the draft bounds
		double t = 1.2*Math.sqrt(Math.pow(b.getHeight(),2)+Math.pow(b.getWidth(),2));

		Point end = DXFUtils.getPointFromParameterizedLine(basePoint,direction,t);
        Point start = DXFUtils.getPointFromParameterizedLine(basePoint,direction,(-1*t));

		AttributesImpl atts = new AttributesImpl();
		SVGUtils.addAttribute(atts,"x1",""+start.getX());
		SVGUtils.addAttribute(atts,"y1",""+start.getY());
		SVGUtils.addAttribute(atts,"x2",""+end.getX());
		SVGUtils.addAttribute(atts,"y2",""+end.getY());
        super.setCommonAttributes(atts);

        SVGUtils.emptyElement(handler,SVGConstants.SVG_LINE,atts);

	}
}
