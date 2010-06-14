/*******************************************************************************
 * Copyright 2010 Simon Mieth
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package org.kabeja.svg.generators;

import java.util.Map;

import org.kabeja.DraftDocument;
import org.kabeja.common.DraftEntity;
import org.kabeja.entities.Face3D;
import org.kabeja.math.Point3D;
import org.kabeja.math.TransformContext;
import org.kabeja.svg.SVGConstants;
import org.kabeja.svg.SVGUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class SVG3DFaceGenerator extends AbstractSVGSAXGenerator {
	public SVG3DFaceGenerator() {
		super();
	}

	public void toSAX(ContentHandler handler, Map svgContext, DraftEntity entity,
			TransformContext transformContext) throws SAXException {
		// all edges are visible
		Face3D face = (Face3D) entity;
		DraftDocument doc = face.getDocument();

		if ((doc.getHeader().hasVariable("$SPLFRAME") && (doc.getHeader()
				.getVariable("$SPLFRAME").getIntegerValue("70") == 1))
				|| (face.getFlags() == 0)) {
			AttributesImpl attr = new AttributesImpl();

			StringBuffer points = new StringBuffer();

			Point3D point1 = face.getPoint1();

			points.append(SVGUtils.formatNumberAttribute(point1.getX()));
			points.append(",");
			points.append(SVGUtils.formatNumberAttribute(point1.getY()));
			points.append(" ");

			Point3D point2 = face.getPoint2();

			points.append(SVGUtils.formatNumberAttribute(point2.getX()));
			points.append(",");
			points.append(SVGUtils.formatNumberAttribute(point2.getY()));
			points.append(" ");

			Point3D point3 = face.getPoint3();

			points.append(SVGUtils.formatNumberAttribute(point3.getX()));
			points.append(",");
			points.append(SVGUtils.formatNumberAttribute(point3.getY()));
			points.append(" ");

			Point3D point4 = face.getPoint4();

			points.append(SVGUtils.formatNumberAttribute(point4.getX()));
			points.append(",");
			points.append(SVGUtils.formatNumberAttribute(point4.getY()));
			points.append(" ");

			SVGUtils.addAttribute(attr, "points", points.toString());

			super.setCommonAttributes(attr, svgContext, face);

			SVGUtils.emptyElement(handler, SVGConstants.SVG_POLYGON, attr);
		} else {
			// draw only visible edges
			// bit 0 edge 1->2
			// bit 1 edge 2->3
			// bit 2 edge 3->4
			// bit 3 edge 4->1
			// if a bit is not set, the edge
			// is visible
			int flag = face.getFlags();

			if ((flag & 1) == 0) {
				edgeToSAX(handler, face.getPoint1(), face.getPoint2(),
						svgContext, face);
			}

			if ((flag & 2) == 0) {
				edgeToSAX(handler, face.getPoint2(), face.getPoint3(),
						svgContext, face);
			}

			if ((flag & 4) == 0) {
				edgeToSAX(handler, face.getPoint3(), face.getPoint4(),
						svgContext, face);
			}

			if ((flag & 8) == 0) {
				edgeToSAX(handler, face.getPoint4(), face.getPoint1(),
						svgContext, face);
			}
		}
	}

	protected void edgeToSAX(ContentHandler handler, Point3D p1, Point3D p2,
			Map svgContext, Face3D face) throws SAXException {
		AttributesImpl attr = new AttributesImpl();
		// set the attributes
		SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_X1, SVGUtils
				.formatNumberAttribute(p1.getX()));

		SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_Y1, SVGUtils
				.formatNumberAttribute(p1.getY()));

		SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_X2, SVGUtils
				.formatNumberAttribute(p2.getX()));

		SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_Y2, SVGUtils
				.formatNumberAttribute(p2.getY()));
		super.setCommonAttributes(attr, svgContext, face);
		SVGUtils.emptyElement(handler, SVGConstants.SVG_LINE, attr);
	}
}
