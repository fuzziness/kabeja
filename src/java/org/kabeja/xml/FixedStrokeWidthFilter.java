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
package org.kabeja.xml;

import org.kabeja.svg.SVGConstants;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class FixedStrokeWidthFilter extends AbstractSAXFilter {

	public final static String PERCENT = "%";

	protected int strokeWidth;

	protected double strokeBase;

	protected boolean replace = true;

	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {

		AttributesImpl attsImpl = new AttributesImpl(atts);
		if (SVGConstants.SVG_ROOT.equals(localName)) {
			String viewBox = atts.getValue(SVGConstants.SVG_ATTRIBUTE_VIEWBOX);
			this.parseViewBox(viewBox);
		} else {
			String strokeWidth = atts
					.getValue(SVGConstants.SVG_ATTRIBUTE_STROKE_WITDH);
			if (strokeWidth != null && strokeWidth.length() > 0) {
				// we replace the stroke width with a fixed value
				attsImpl.setValue(attsImpl
						.getIndex(SVGConstants.SVG_ATTRIBUTE_STROKE_WITDH),
						this.createStrokeWidth(strokeWidth));
			}
			// we need to add "px" as length identify for the fonts
			if (SVGConstants.SVG_TEXT.equals(localName) || SVGConstants.SVG_TSPAN.equals(localName)) {
				int index = -1;
				if ((index = atts
						.getIndex(SVGConstants.SVG_ATTRIBUTE_FONT_SIZE)) != -1) {
					String fontSize = atts.getValue(index);
					attsImpl.setValue(attsImpl
							.getIndex(SVGConstants.SVG_ATTRIBUTE_FONT_SIZE),
							fontSize + "px");

				}
			}
		}

		super.startElement(uri, localName, qName, attsImpl);
	}

	protected void parseViewBox(String viewBox) {

		String[] data = viewBox.split("(\\s+)");

		if (data.length == 4) {
			double width = Math.abs(Double.parseDouble(data[2]));
			double height = Math.abs(Double.parseDouble(data[3]));

			this.strokeBase = Math.sqrt(Math.pow(width, 2)
					+ Math.pow(height, 2));
		}
	}

	public String createStrokeWidth(String strokeWidth) {
		String value = strokeWidth.trim();

		if (value.endsWith(PERCENT)) {

			double f = Double.parseDouble(strokeWidth.substring(0, value
					.length() - 1)) / 100;

			return "" + f * this.strokeBase;
		} else {
			return strokeWidth;
		}

	}

}
