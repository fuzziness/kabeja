package org.kabeja.svg.generators;

import java.util.Map;

import org.kabeja.dxf.DXFBlock;
import org.kabeja.dxf.DXFDimension;
import org.kabeja.dxf.DXFEntity;
import org.kabeja.dxf.helpers.Point;
import org.kabeja.math.TransformContext;
import org.kabeja.svg.SVGConstants;
import org.kabeja.svg.SVGUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class SVGDimensionGenerator extends AbstractSVGSAXGenerator {

	public void toSAX(ContentHandler handler, Map svgContext, DXFEntity entity,
			TransformContext transformContext) throws SAXException {
		DXFDimension dimension = (DXFDimension) entity;

		if (dimension.getDXFDocument().getDXFBlock(
				dimension.getDimensionBlock()) != null) {
			DXFBlock block = dimension.getDXFDocument().getDXFBlock(dimension.getDimensionBlock());
			AttributesImpl attr = new AttributesImpl();
			StringBuffer buf = new StringBuffer();

			buf.append("translate(");
			buf.append((dimension.getInsertPoint().getX()));
			buf.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
			buf.append((dimension.getInsertPoint().getY()));
			buf.append(")");

			  Point referencePoint = block.getReferencePoint();
            if ((referencePoint.getX() != 0.0) || (referencePoint.getY() != 0.0)) {
                buf.append(" translate(");
                buf.append(SVGUtils.formatNumberAttribute(referencePoint.getX()));
                buf.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
                buf.append(SVGUtils.formatNumberAttribute(referencePoint.getY()));
                buf.append(")");
            }
			SVGUtils.addAttribute(attr, "transform", buf.toString());

			super.setCommonAttributes(attr, svgContext, dimension);

			SVGUtils.startElement(handler, SVGConstants.SVG_GROUP, attr);
			attr = new AttributesImpl();

			attr.addAttribute(SVGConstants.XMLNS_NAMESPACE, "xlink",
					"xmlns:xlink", "CDATA", SVGConstants.XLINK_NAMESPACE);
			attr.addAttribute(SVGConstants.XLINK_NAMESPACE, "href",
					"xlink:href", "CDATA", "#"
							+ SVGUtils
									.validateID(dimension.getDimensionBlock()));

			SVGUtils.emptyElement(handler, SVGConstants.SVG_USE, attr);

			SVGUtils.endElement(handler, SVGConstants.SVG_GROUP);
		}
		// else if (dimType == TYPE_LINEAR || dimType == 32 || dimType == 160) {
		// LinearDimensionOutputter out = new LinearDimensionOutputter(
		// this);
		// out.output(handler);
		// }
	}

}
