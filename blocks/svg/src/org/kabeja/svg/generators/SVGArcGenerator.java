package org.kabeja.svg.generators;

import java.util.Map;

import org.kabeja.dxf.DXFArc;
import org.kabeja.dxf.DXFEntity;
import org.kabeja.dxf.helpers.Point;
import org.kabeja.math.TransformContext;
import org.kabeja.svg.SVGConstants;
import org.kabeja.svg.SVGPathBoundaryGenerator;
import org.kabeja.svg.SVGUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class SVGArcGenerator extends AbstractSVGSAXGenerator implements
		SVGPathBoundaryGenerator {

	public void toSAX(ContentHandler handler, Map svgContext, DXFEntity entity,
			TransformContext transformContext) throws SAXException {
		DXFArc arc = (DXFArc) entity;
		AttributesImpl attr = new AttributesImpl();

		SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_PATH,
				getSVGPath(arc));

		super.setCommonAttributes(attr, svgContext, arc);
		SVGUtils.emptyElement(handler, SVGConstants.SVG_PATH, attr);

	}

	public String getSVGPath(DXFEntity entity) {

		DXFArc arc = (DXFArc) entity;
		if (arc.isCounterClockwise()) {

		} else {

		}

		Point p;

		StringBuffer path = new StringBuffer();

		p = arc.getStartPoint();

		double radius = arc.getRadius();
		path.append("M ");

		path.append(p.getX());
		path.append(' ');
		path.append(p.getY());
		path.append(" A ");
		path.append(radius);
		path.append(' ');
		path.append(radius);
		// x-axis rotation -> always no rotation
		path.append(" 0");


		
		double diff = arc.getTotalAngle();

		// the large-arc-flag
		if (diff > 180) {
			path.append(" 1 ");
		} else {
			path.append(" 0 ");
		}

		if (!arc.isCounterClockwise()) {
			// the sweep-flag
			path.append(" 1 ");

		} else {
			// sweep flag 0
			path.append(" 0 ");
		}

		
		double angle = arc.getEndAngle();
		//handling of only for hatch boundary 
		if(arc.isCounterClockwise()){
			angle=-1*angle;
		}

		p = arc.getPoint(angle);
		
		path.append(' ');
		path.append(p.getX());
		path.append(' ');
		path.append(p.getY());

		return path.toString();
	}

}
