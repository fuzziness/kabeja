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

import java.util.Iterator;
import java.util.Map;

import org.kabeja.common.DraftEntity;
import org.kabeja.common.LineWidth;
import org.kabeja.entities.Hatch;
import org.kabeja.entities.util.HatchBoundaryLoop;
import org.kabeja.entities.util.HatchLineFamily;
import org.kabeja.entities.util.HatchLineIterator;
import org.kabeja.entities.util.HatchLineSegment;
import org.kabeja.entities.util.HatchPattern;
import org.kabeja.math.Bounds;
import org.kabeja.math.Point3D;
import org.kabeja.math.TransformContext;
import org.kabeja.svg.SVGConstants;
import org.kabeja.svg.SVGContext;
import org.kabeja.svg.SVGPathBoundaryGenerator;
import org.kabeja.svg.SVGSAXGeneratorManager;
import org.kabeja.svg.SVGUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class SVGHatchGenerator extends AbstractSVGSAXGenerator {
	public void toSAX(ContentHandler handler, Map svgContext, DraftEntity entity,
			TransformContext transformContext) throws SAXException {
		Hatch hatch = (Hatch) entity;
		SVGSAXGeneratorManager manager = (SVGSAXGeneratorManager) svgContext
				.get(SVGContext.SVGSAXGENERATOR_MANAGER);

		Bounds hatchBounds = hatch.getBounds();

		if (hatchBounds.isValid()) {
			AttributesImpl attr = new AttributesImpl();

			// the id
			if (hatch.isSolid()) {
				super.setCommonAttributes(attr, svgContext, hatch);

				SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_FILL,
						SVGConstants.SVG_ATTRIBUTE_VALUE_CURRENTCOLOR);
				SVGUtils.startElement(handler, SVGConstants.SVG_GROUP, attr);

				for (HatchBoundaryLoop loop : hatch.getBoundaryLoops()) {
					this.loopToSVGPath(handler, loop, manager);
				}

				SVGUtils.endElement(handler, SVGConstants.SVG_GROUP);
			} else {
				// we will draw a rectangle with the pattern and use then the
				// boundary path as clip-path
				attr = new AttributesImpl();
				SVGUtils.addAttribute(attr, SVGConstants.XML_ID, SVGUtils
						.toValidateID(hatch.getID()));

				boolean clipClipPath = false;

				if (hatch.getHatchStyle() < 2) {
					this.islandToClipPath(handler, hatch, manager);
					clipClipPath = true;
					SVGUtils.addAttribute(attr,
							SVGConstants.SVG_ATTRIBUTE_CLIP_PATH, "url(#"
									+ SVGUtils.toValidateID(hatch.getID())
									+ "-clip)");
				}

				SVGUtils.startElement(handler, SVGConstants.SVG_CLIPPING_PATH,
						attr);

				if (clipClipPath) {
					this.outermostToSVGPath(handler, hatch, manager);
				} else {
					for (HatchBoundaryLoop loop : hatch.getBoundaryLoops()) {
						this.loopToSVGPath(handler, loop, manager);
					}

				}

				SVGUtils.endElement(handler, SVGConstants.SVG_CLIPPING_PATH);

				HatchPattern pattern = hatch.getDocument().getHatchPattern(
						hatch.getHatchPatternID());

				attr = new AttributesImpl();
				SVGUtils.addAttribute(attr,
						SVGConstants.SVG_ATTRIBUTE_CLIP_PATH, "url(#"
								+ SVGUtils.toValidateID(hatch.getID()) + ")");
				SVGUtils.startElement(handler, SVGConstants.SVG_GROUP, attr);
				SVGUtils.startElement(handler, SVGConstants.SVG_TITLE,
						new AttributesImpl());
				SVGUtils.characters(handler, hatch.getName());
				SVGUtils.endElement(handler, SVGConstants.SVG_TITLE);
				convertHatchPatternToSAX(handler, svgContext, hatchBounds,
						hatch, transformContext, pattern);
				SVGUtils.endElement(handler, SVGConstants.SVG_GROUP);
			}
		}
	}

	protected void islandToClipPath(ContentHandler handler, Hatch hatch,
			SVGSAXGeneratorManager manager) throws SAXException {
		AttributesImpl attr = new AttributesImpl();
		SVGUtils.addAttribute(attr, SVGConstants.XML_ID, SVGUtils
				.toValidateID(hatch.getID())
				+ "-clip");
		SVGUtils.startElement(handler, SVGConstants.SVG_CLIPPING_PATH, attr);

		// we will draw a rectangle with the pattern and use then the
		// boundary path as clip-path

		// first the clip-path
		for (HatchBoundaryLoop loop : hatch.getBoundaryLoops()) {

			if (!loop.isOutermost()) {
				loopToSVGPath(handler, loop, manager);
			}
		}

		SVGUtils.endElement(handler, SVGConstants.SVG_CLIPPING_PATH);
	}

	protected void outermostToSVGPath(ContentHandler handler, Hatch hatch,
			SVGSAXGeneratorManager manager) throws SAXException {

		for (HatchBoundaryLoop loop : hatch.getBoundaryLoops()) {
			if (loop.isOutermost()) {
				loopToSVGPath(handler, loop, manager);
			}
		}
	}

	protected void loopToSVGPath(ContentHandler handler,
			HatchBoundaryLoop loop, SVGSAXGeneratorManager manager)
			throws SAXException {
		StringBuffer buf = new StringBuffer();
		Iterator i = loop.getBoundaryEdgesIterator();

		if (i.hasNext()) {
			DraftEntity entity = (DraftEntity) i.next();
			buf.append(' ');

			String d = manager.getSVGPathBoundaryGenerator(entity.getType().getHandle())
					.getSVGPath(entity);

			if (d.length() == 0) {
				return;
			}

			buf.append(d);
			buf.append(' ');

			while (i.hasNext()) {
				entity = (DraftEntity) i.next();

				SVGPathBoundaryGenerator part = manager
						.getSVGPathBoundaryGenerator(entity.getType().getHandle());
				buf.append(' ');
				d = removeStartPoint(part.getSVGPath(entity).trim());

				buf.append(d);
				buf.append(' ');
			}

			// every loop as single path
			if (d.length() > 0) {
				AttributesImpl attr = new AttributesImpl();
				SVGUtils.addAttribute(attr, "d", buf.toString());
				SVGUtils.emptyElement(handler, SVGConstants.SVG_PATH, attr);
			}
		}
	}

	protected String removeStartPoint(String svgPath) {
		if ((svgPath.length() > 0) && (svgPath.charAt(0) == 'M')) {
			boolean separator = false;
			int delemiterCount = 0;

			for (int i = 1; i < svgPath.length(); i++) {
				char c = svgPath.charAt(i);

				if (Character.isWhitespace(c) || (c == ',')) {
					separator = true;
				} else {
					if (separator && (delemiterCount == 2)) {
						return svgPath.substring(i - 1);
					} else if (separator) {
						delemiterCount++;
						separator = false;
					}
				}
			}
		}

		return svgPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kabeja.svg.SVGGenerator#toSAX(org.xml.sax.ContentHandler,
	 * java.util.Map)
	 */
	public void convertHatchPatternToSAX(ContentHandler handler, Map context,
			Bounds hatchBounds, Hatch hatch, TransformContext transformContext,
			HatchPattern p) throws SAXException {
		// we have to create a tile with all lines
		double dotLength = (hatchBounds.getWidth() + hatchBounds.getHeight()) / 2 * 0.002;

		AttributesImpl attr = new AttributesImpl();

		Iterator i = p.getLineFamilyIterator();

		// patterns.iterator();
		while (i.hasNext()) {
			HatchLineFamily pattern = (HatchLineFamily) i.next();

			attr = new AttributesImpl();

			if (context.containsKey(SVGContext.LAYER_STROKE_WIDTH)) {
				LineWidth lw = (LineWidth) context
						.get(SVGContext.LAYER_STROKE_WIDTH);
				SVGUtils.addAttribute(attr,
						SVGConstants.SVG_ATTRIBUTE_STROKE_WITDH, SVGUtils
								.lineWidthToStrokeWidth(lw));
			}

			SVGUtils.addAttribute(attr, "d", convertPatternToSVGPath(
					hatchBounds, hatch, pattern, dotLength));

			SVGUtils.emptyElement(handler, SVGConstants.SVG_PATH, attr);
		}
	}

	/**
	 * Creates a SVG path of the used line family and fill the complete given
	 * bounds.
	 * 
	 * @param b
	 *            - The bounds of the DXFHatch
	 * @param pattern
	 *            the pattern of the line family
	 * @param dotlength
	 * @return
	 */
	protected String convertPatternToSVGPath(Bounds b, Hatch hatch,
			HatchLineFamily pattern, double dotlength) {
		StringBuffer buf = new StringBuffer();

		Iterator<HatchLineSegment> li = new HatchLineIterator(hatch, pattern);

		while (li.hasNext()) {

			HatchLineSegment segment =  li.next();
			// double angle = Math.toRadians(pattern.getRotationAngle());
			Point3D startPoint = segment.getStartPoint();
			double x = startPoint.getX();
			double y = startPoint.getY();

			// the start Point of the line segment
			buf.append('M');
			buf.append(' ');
			buf.append(SVGUtils.formatNumberAttribute(x));
			buf.append(' ');
			buf.append(SVGUtils.formatNumberAttribute(y));
			buf.append(' ');

			if (segment.isSolid()) {
				Point3D p = segment.getPointAt(segment.getLength());
				buf.append('L');
				buf.append(' ');
				buf.append(SVGUtils.formatNumberAttribute(p.getX()));
				buf.append(' ');
				buf.append(SVGUtils.formatNumberAttribute(p.getY()));
				buf.append(' ');
			} else {
				double length = 0;

				while (segment.hasNext()) {
					double l = segment.next();
					length += Math.abs(l);

					Point3D p = segment.getPointAt(length);

					if (l > 0) {
						buf.append('L');
						buf.append(' ');
						buf.append(SVGUtils.formatNumberAttribute(p.getX()));
						buf.append(' ');
						buf.append(SVGUtils.formatNumberAttribute(p.getY()));
						buf.append(' ');
					} else if (l < 0) {
						buf.append('M');
						buf.append(' ');
						buf.append(SVGUtils.formatNumberAttribute(p.getX()));
						buf.append(' ');
						buf.append(SVGUtils.formatNumberAttribute(p.getY()));
						buf.append(' ');
					} else {
						// a dot
						buf.append('l');
						buf.append(' ');
						buf.append(SVGUtils.formatNumberAttribute(dotlength));
						buf.append(' ');
						buf.append(SVGUtils.formatNumberAttribute(dotlength));
						buf.append(' ');
					}
				}
			}
		}

		return buf.toString();
	}
}
