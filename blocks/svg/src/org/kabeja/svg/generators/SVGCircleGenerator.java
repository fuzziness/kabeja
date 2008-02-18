package org.kabeja.svg.generators;

import java.util.Map;

import org.kabeja.dxf.DXFCircle;
import org.kabeja.dxf.DXFEntity;
import org.kabeja.dxf.helpers.Point;
import org.kabeja.math.ParametricPlane;
import org.kabeja.math.TransformContext;
import org.kabeja.svg.SVGConstants;
import org.kabeja.svg.SVGUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class SVGCircleGenerator extends AbstractSVGSAXGenerator {

	public void toSAX(ContentHandler handler, Map svgContext, DXFEntity entity,
			TransformContext transformContext) throws SAXException {
		DXFCircle circle = (DXFCircle) entity;
		AttributesImpl attr = new AttributesImpl();
		ParametricPlane plane = new ParametricPlane(circle.getExtrusion());
		Point center = circle.getCenterPoint();
		
		Point p = plane.getPoint(center.getX(), center.getY());
		
		SVGUtils.addAttribute(attr, "cx", SVGUtils.formatNumberAttribute(p.getX()));
		SVGUtils.addAttribute(attr, "cy", SVGUtils.formatNumberAttribute(p.getY()));
		SVGUtils.addAttribute(attr, "r", SVGUtils.formatNumberAttribute(circle
				.getRadius()));

		super.setCommonAttributes(attr, svgContext, circle);

		SVGUtils.emptyElement(handler, SVGConstants.SVG_CIRCLE, attr);

	}

}
