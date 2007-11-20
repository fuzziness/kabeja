package org.kabeja.svg.generators;

import java.util.Map;

import org.kabeja.dxf.DXFCircle;
import org.kabeja.dxf.DXFEntity;
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
		SVGUtils.addAttribute(attr, "cx", SVGUtils.formatNumberAttribute(circle
				.getCenterPoint().getX()));
		SVGUtils.addAttribute(attr, "cy", SVGUtils.formatNumberAttribute(circle
				.getCenterPoint().getY()));
		SVGUtils.addAttribute(attr, "r", SVGUtils.formatNumberAttribute(circle
				.getRadius()));

		super.setCommonAttributes(attr, svgContext, circle);

		SVGUtils.emptyElement(handler, SVGConstants.SVG_CIRCLE, attr);

	}

}
