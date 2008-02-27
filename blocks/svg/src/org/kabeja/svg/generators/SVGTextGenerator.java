/*
   Copyright 2008 Simon Mieth

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
package org.kabeja.svg.generators;

import java.util.Map;

import org.kabeja.dxf.Bounds;
import org.kabeja.dxf.DXFEntity;
import org.kabeja.dxf.DXFStyle;
import org.kabeja.dxf.DXFText;
import org.kabeja.dxf.helpers.Point;
import org.kabeja.math.TransformContext;
import org.kabeja.svg.SVGConstants;
import org.kabeja.svg.SVGContext;
import org.kabeja.svg.SVGUtils;
import org.kabeja.tools.FontManager;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


public class SVGTextGenerator extends AbstractSVGSAXGenerator {
    public void toSAX(ContentHandler handler, Map svgContext, DXFEntity entity,
        TransformContext transformContext) throws SAXException {
        DXFText text = (DXFText) entity;

        AttributesImpl attr = new AttributesImpl();
        Point alignmentPoint = text.calculateAlignmentPoint();

        double height = text.getHeight();

        if (height == 0.0) {
            height = ((Bounds) svgContext.get(SVGContext.DRAFT_BOUNDS)).getHeight() * 0.005;
        }

        SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_FONT_SIZE,
            SVGUtils.formatNumberAttribute(height));

        // if there is a SVG font, we will use it
        if (text.getDXFDocument().getDXFStyle(text.getTextStyle()) != null) {
            DXFStyle style = text.getDXFDocument()
                                 .getDXFStyle(text.getTextStyle());
            FontManager manager = FontManager.getInstance();
            String fontID = null;

            if (manager.hasFontDescription(style.getBigFontFile())) {
                fontID = style.getBigFontFile();
            } else if (manager.hasFontDescription(style.getFontFile())) {
                fontID = style.getFontFile();
            }

            if (fontID != null) {
                fontID = fontID.toLowerCase();

                if (fontID.endsWith(".shx")) {
                    fontID = fontID.substring(0, fontID.indexOf(".shx"));
                }

                SVGUtils.addAttribute(attr,
                    SVGConstants.SVG_ATTRIBUTE_FONT_FAMILY, fontID);
            }
        } else {
            // Do we need to set a default?
            // SVGUtils.addAttribute(attr,SVGConstants.SVG_ATTRIBUTE_FONT_FAMILY,"simplex");
        }

        // set the alignment
        if (!text.isUpsideDown()) {
            switch (text.getAlign()) {
            case 0:
                SVGUtils.addAttribute(attr,
                    SVGConstants.SVG_ATTRIBUTE_TEXT_ANCHOR, "start");

                break;

            case 1:
                SVGUtils.addAttribute(attr,
                    SVGConstants.SVG_ATTRIBUTE_TEXT_ANCHOR, "middle");

                break;

            case 2:
                SVGUtils.addAttribute(attr,
                    SVGConstants.SVG_ATTRIBUTE_TEXT_ANCHOR, "end");

                break;

            case 3:
                SVGUtils.addAttribute(attr,
                    SVGConstants.SVG_ATTRIBUTE_TEXT_ANCHOR, "end");

                break;

            case 4:
                SVGUtils.addAttribute(attr,
                    SVGConstants.SVG_ATTRIBUTE_TEXT_ANCHOR, "middle");

                break;

            case 5:
                SVGUtils.addAttribute(attr,
                    SVGConstants.SVG_ATTRIBUTE_TEXT_ANCHOR, "end");

                break;

            default:
                SVGUtils.addAttribute(attr,
                    SVGConstants.SVG_ATTRIBUTE_TEXT_ANCHOR, "start");

                break;
            }
        }

        // in order to get the right text-view
        StringBuffer transform = new StringBuffer();

        if (!text.isBackward()) {
            SVGUtils.addAttribute(attr, "writing-mode", "lr-tb");
        } else {
            //SVGUtils.addAttribute(attr, "writing-mode", "rl");
            transform.append("matrix(-1 0 0 1  ");
            transform.append(2 * alignmentPoint.getX());
            transform.append(" 0)");
        }

        if (!text.isUpsideDown()) {
            transform.append("matrix(1 0 0 -1 0 ");
            transform.append(2 * alignmentPoint.getY());
            transform.append(')');
        }

        // rotation
        if (text.getRotation() != 0.0) {
            transform.append(" rotate(");
            transform.append((-1 * text.getRotation()));
            transform.append(' ');
            transform.append(alignmentPoint.getX());
            transform.append(' ');
            transform.append(alignmentPoint.getY());
            transform.append(' ');
            transform.append(')');
        }

        if (text.getObliqueAngle() != 0.0) {
            transform.append(" skewX(");
            transform.append((-1 * text.getObliqueAngle()));
            transform.append(" )");

            transform.append(" translate( ");
            transform.append(alignmentPoint.getY() * Math.tan(Math.toRadians(
                        1 * text.getObliqueAngle())));
            transform.append(')');
        }

        SVGUtils.addAttribute(attr, "transform", transform.toString());

        SVGUtils.addAttribute(attr, "x", "" + alignmentPoint.getX());
        SVGUtils.addAttribute(attr, "y", "" + alignmentPoint.getY());

        SVGUtils.addAttribute(attr, "fill", "currentColor");
        super.setCommonAttributes(attr, svgContext, text);
        SVGUtils.startElement(handler, SVGConstants.SVG_TEXT, attr);
        SVGUtils.textDocumentToSAX(handler, text.getTextDocument());
        SVGUtils.endElement(handler, SVGConstants.SVG_TEXT);
    }
}
