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
package org.kabeja.svg;

import java.util.Map;

import org.kabeja.xml.AbstractSAXFilter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


public class FixedStrokeWidthFilter extends AbstractSAXFilter {
    public final static String PROPERTY_FIXED_FONTSIZE = "fixed-fontsize";
    public final static String PERCENT = "%";
    protected int strokeWidth = 1;
    protected double strokeBase = 0;
    protected boolean replace = true;
    protected boolean fixFontsize = true;

    public void startElement(String uri, String localName, String qName,
        Attributes atts) throws SAXException {
        AttributesImpl attsImpl = new AttributesImpl(atts);

        if (SVGConstants.SVG_ROOT.equals(localName)) {
            String viewBox = atts.getValue(SVGConstants.SVG_ATTRIBUTE_VIEWBOX);
            this.parseViewBox(viewBox);
        } else if (localName.equals(SVGConstants.SVG_DEFS)) {
            // the defs section has already fixed values
            this.replace = false;
        } else if (this.replace) {
            String strokeWidth = atts.getValue(SVGConstants.SVG_ATTRIBUTE_STROKE_WITDH);

            if ((strokeWidth != null) && (strokeWidth.length() > 0)) {
                // we replace the stroke width with a fixed value
                attsImpl.setValue(attsImpl.getIndex(
                        SVGConstants.SVG_ATTRIBUTE_STROKE_WITDH),
                    this.createStrokeWidth(strokeWidth));
            }
        }

        // we need to add "px" as length identify for the fonts
        if ((SVGConstants.SVG_TEXT.equals(localName)) && this.fixFontsize) {
            int index = -1;

            if ((index = atts.getIndex(SVGConstants.SVG_ATTRIBUTE_FONT_SIZE)) != -1) {
                String fontSize = atts.getValue(index);
                attsImpl.setValue(index, "1px");

                double fixedSize = Double.parseDouble(fontSize) / 5;
                String scaleValue = " scale(" +
                    SVGUtils.formatNumberAttribute(fixedSize) + ") ";
                index = -1;

                if ((index = atts.getIndex(SVGConstants.SVG_ATTRIBUTE_TRANSFORM)) != -1) {
                    String transform = atts.getValue(index);
                    attsImpl.setValue(attsImpl.getIndex(
                            SVGConstants.SVG_ATTRIBUTE_TRANSFORM),
                        transform + scaleValue);
                } else {
                    attsImpl.addAttribute("",
                        SVGConstants.SVG_ATTRIBUTE_TRANSFORM,
                        SVGConstants.SVG_ATTRIBUTE_TRANSFORM,
                        SVGUtils.DEFAUL_ATTRIBUTE_TYPE, scaleValue);
                }

                //to avoid scaling of the  coordinats
                double x = Double.parseDouble(atts.getValue("x")) / fixedSize;
                attsImpl.setValue(atts.getIndex("x"), "" + x);

                double y = Double.parseDouble(atts.getValue("y")) / fixedSize;
                attsImpl.setValue(atts.getIndex("y"), "" + y);
            }
        }

        super.startElement(uri, localName, qName, attsImpl);
    }

    public void endElement(String uri, String localName, String qName)
        throws SAXException {
        if (localName.equals(SVGConstants.SVG_DEFS)) {
            this.replace = true;
        }

        super.endElement(uri, localName, qName);
    }

    protected void parseViewBox(String viewBox) {
        // parse the viewbox data, white separate list of numbers
        String[] data = viewBox.split("(\\s+)");

        if (data.length == 4) {
            double width = Math.abs(Double.parseDouble(data[2]));
            double height = Math.abs(Double.parseDouble(data[3]));

            this.strokeBase = Math.sqrt(Math.pow(width, 2) +
                    Math.pow(height, 2));
        }
    }

    public String createStrokeWidth(String strokeWidth) {
        String value = strokeWidth.trim();

        if (value.endsWith(PERCENT)) {
            double f = Double.parseDouble(strokeWidth.substring(0,
                        value.length() - 1)) / 100;

            return "" + (f * this.strokeBase);
        } else {
            return strokeWidth;
        }
    }

    public void setProperties(Map properties) {
        super.setProperties(properties);

        if (this.properties.containsKey(PROPERTY_FIXED_FONTSIZE)) {
            this.fixFontsize = Boolean.valueOf((String) this.properties.get(
                        PROPERTY_FIXED_FONTSIZE)).booleanValue();
        }
    }
}
