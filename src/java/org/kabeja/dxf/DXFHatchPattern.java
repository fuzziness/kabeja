/*
 Copyright 2005 Simon Mieth

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package org.kabeja.dxf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kabeja.dxf.helpers.HatchLineFamily;
import org.kabeja.dxf.helpers.HatchLineIterator;
import org.kabeja.dxf.helpers.HatchLineSegment;
import org.kabeja.dxf.helpers.Point;
import org.kabeja.svg.SVGConstants;
import org.kabeja.svg.SVGSAXGenerator;
import org.kabeja.svg.SVGUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * This class represent a single line family of a hatch pattern set.
 * 
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth </a>
 * 
 */
public class DXFHatchPattern implements SVGSAXGenerator {
	private static int idCount = 0;

	private String id = null;

	private List patterns = new ArrayList();

	private DXFHatch hatch;

	/**
	 * @return Returns the id.
	 */
	public String getID() {
		if (this.id == null) {
			this.id = "HATCH_PATTERN_ID_" + DXFHatchPattern.idCount;
			DXFHatchPattern.idCount++;
		}

		return id;
	}

	/**
	 * @param id
	 *            The id to set.
	 */
	public void setID(String id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kabeja.svg.SVGGenerator#toSAX(org.xml.sax.ContentHandler,
	 *      java.util.Map)
	 */
	public void toSAX(ContentHandler handler, Map svgContext)
			throws SAXException {
		if (!this.hatch.isSolid()) {
			// we have to create a tile with all lines

			Bounds bounds = this.hatch.getBounds();
			double dotLength = (bounds.getWidth() + bounds.getHeight()) / 2 * 0.002;

			AttributesImpl attr = new AttributesImpl();

			Iterator i = patterns.iterator();

			while (i.hasNext()) {
				HatchLineFamily pattern = (HatchLineFamily) i.next();

				attr = new AttributesImpl();
				SVGUtils.addAttribute(attr, "d", this.getSVGPath(bounds,
						pattern, dotLength));

				SVGUtils.emptyElement(handler, SVGConstants.SVG_PATH, attr);

			}

		}
	}

	public void addLineFamily(HatchLineFamily pattern) {
		patterns.add(pattern);
	}

	public Iterator getLineFamilyIterator() {
		return patterns.iterator();
	}

	/**
	 * The associated hatch for this pattern.
	 * 
	 * @return Returns the hatch.
	 */
	public DXFHatch getDXFHatch() {
		return this.hatch;
	}

	/**
	 * The associated hatch for this pattern.
	 * 
	 * @param hatch
	 *            The hatch to set.
	 */
	public void setHatch(DXFHatch hatch) {
		this.hatch = hatch;
	}

	/**
	 * Creates a SVG path of the used line family and fill the complete given
	 * bounds.
	 * 
	 * @param b -
	 *            The bounds of the DXFHatch
	 * @param pattern
	 *            the pattern of the line family
	 * @param dotlength
	 * @return
	 */

	protected String getSVGPath(Bounds b, HatchLineFamily pattern,
			double dotlength) {
		StringBuffer buf = new StringBuffer();

		Iterator li = new HatchLineIterator(this.hatch, pattern);

		while (li.hasNext()) {

			HatchLineSegment segment = (HatchLineSegment) li.next();
			// double angle = Math.toRadians(pattern.getRotationAngle());
			Point startPoint = segment.getStartPoint();
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
				Point p = segment.getPointAt(segment.getLength());
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
					Point p = segment.getPointAt(length);
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

	/**
	 * 
	 * @return the count of the used line families
	 */

	public int getLineFamilyCount() {
		return this.patterns.size();
	}
}
