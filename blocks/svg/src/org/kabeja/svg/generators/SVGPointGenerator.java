package org.kabeja.svg.generators;

import java.util.Map;

import org.kabeja.dxf.DXFEntity;
import org.kabeja.dxf.DXFPoint;
import org.kabeja.math.TransformContext;
import org.kabeja.svg.SVGConstants;
import org.kabeja.svg.SVGUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class SVGPointGenerator extends AbstractSVGSAXGenerator {

	public void toSAX(ContentHandler handler, Map svgContext, DXFEntity entity,
			TransformContext transformContext) throws SAXException {
		DXFPoint point = (DXFPoint) entity;
		AttributesImpl attr = new AttributesImpl();
		SVGUtils.addAttribute(attr, "cx", SVGUtils.formatNumberAttribute(point
				.getX()));
		SVGUtils.addAttribute(attr, "cy", SVGUtils.formatNumberAttribute(point
				.getY()));
		SVGUtils.addAttribute(attr, "r", "" + 0.001);
		super.setCommonAttributes(attr, svgContext, point);
		SVGUtils.emptyElement(handler, SVGConstants.SVG_CIRCLE, attr);

	}

}
