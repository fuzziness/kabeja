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

package org.kabeja.svg.dimension;

import org.kabeja.common.Variable;
import org.kabeja.entities.Dimension;
import org.kabeja.entities.util.DimensionStyle;
import org.kabeja.svg.SVGConstants;
import org.kabeja.svg.SVGUtils;
import org.kabeja.svg.generators.AbstractSVGSAXGenerator;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 *
 */
public abstract class AbstractDimensionOutputter extends AbstractSVGSAXGenerator {
    protected Dimension dim;

    public AbstractDimensionOutputter(Dimension dim) {
        this.dim = dim;
    }

    protected void outputText(ContentHandler handler) throws SAXException {
        if ((dim.getDimensionText().length() > 0) &&
                (dim.getDimensionText().indexOf("<>") < 0)) {
       
            AttributesImpl attr = new AttributesImpl();
            SVGUtils.addAttribute(attr, "x", "" + dim.getTextPoint().getX());
            SVGUtils.addAttribute(attr, "y", "" + dim.getTextPoint().getY());

            StringBuffer transform = new StringBuffer();

            transform.append("matrix(1 0 0 -1 0 ");
            transform.append(2 * dim.getTextPoint().getY());
            transform.append(")");

            // rotation
            if (dim.getTextRotation() != 0.0) {
                double rotation = 0.0;

                if (dim.getDimensionRotation() != 0.0) {
                    rotation = 360 - dim.getDimensionRotation();
                }

                rotation += dim.getTextRotation();
                transform.append(" rotate(");
                transform.append("" + (-1 * rotation));
                transform.append(" " + dim.getTextPoint().getX());
                transform.append(" " + dim.getTextPoint().getY() + " )");
            }

            SVGUtils.addAttribute(attr, "transform", transform.toString());

            //get the text height from style
            DimensionStyle style = dim.getDimensionStyle();

            if (style == null) {
                //next try  the standard style
                style = dim.getDocument().getDimensionStyle("STANDARD");
            }

            if ((style != null) &&
                    style.hasProperty(DimensionStyle.PROPERTY_DIMTXT)) {
                double height = style.getDoubleProperty(DimensionStyle.PROPERTY_DIMTXT);
               
                SVGUtils.addAttribute(attr, "font-size", "" + height);
            } else {
                //try from dxf header
                Variable var = dim.getDocument().getHeader()
                                     .getVariable("$DIMTXT");

                if ((var != null) && (var.getDoubleValue("40") != 0.0)) {
                    SVGUtils.addAttribute(attr, "font-size",
                        "" + var.getDoubleValue("40"));
                }
            }

            SVGUtils.addAttribute(attr, "writing-mode", "rl");
            SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_TEXT_ANCHOR,
                "middle");
            SVGUtils.addAttribute(attr,
                SVGConstants.SVG_ATTRIBUTE_TEXT_ALIGNMENT_BASELINE, "middle");
            SVGUtils.addAttribute(attr, "fill", "currentColor");
            SVGUtils.startElement(handler, SVGConstants.SVG_TEXT, attr);

            SVGUtils.characters(handler, dim.getDimensionText());
            SVGUtils.endElement(handler, SVGConstants.SVG_TEXT);
        }
    }
}
