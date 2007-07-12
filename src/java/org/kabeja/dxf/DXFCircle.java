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

import java.util.Map;

import org.kabeja.dxf.helpers.Point;
import org.kabeja.svg.SVGConstants;
import org.kabeja.svg.SVGUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 * 
 */
public class DXFCircle extends DXFEntity {
	private Point center;

	private double radius;

	/**
	 * 
	 */
	public DXFCircle() {
	}

	public double getRadius() {
		return radius;
	}

	/**
	 * @param radius
	 *            The radius to set.
	 */
	public void setRadius(double radius) {
		this.radius = radius;
	}

	public void setCenterPoint(Point p) {
		this.center = p;
	}

	public Point getCenterPoint() {
		return center;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dxf2svg.dxf.DXFEntity#toSAX(org.xml.sax.ContentHandler)
	 */
	public void toSAX(ContentHandler handler, Map svgContext)
			throws SAXException {
		AttributesImpl attr = new AttributesImpl();
		SVGUtils.addAttribute(attr, "cx", "" + this.center.getX());
		SVGUtils.addAttribute(attr, "cy", "" + this.center.getY());
		SVGUtils.addAttribute(attr, "r", "" + this.radius);

		super.setCommonAttributes(attr, svgContext);

		SVGUtils.emptyElement(handler, SVGConstants.SVG_CIRCLE, attr);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dxf2svg.dxf.DXFEntity#updateViewPort()
	 */
	public Bounds getBounds() {

		Bounds bounds = new Bounds();
		bounds.setMaximumX(center.getX() + radius);
		bounds.setMinimumX(center.getX() - radius);
		bounds.setMaximumY(center.getY() + radius);
		bounds.setMinimumY(center.getY() - radius);

		return bounds;
	}

	public String getType() {
		return DXFConstants.ENTITY_TYPE_CIRCLE;
	}

	public double getLength() {

		return 2 * Math.PI * this.radius;
	}

}
