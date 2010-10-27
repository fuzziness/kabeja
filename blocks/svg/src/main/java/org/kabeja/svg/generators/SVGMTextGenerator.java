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

import org.kabeja.common.DraftEntity;
import org.kabeja.common.Style;
import org.kabeja.entities.MText;
import org.kabeja.math.Bounds;
import org.kabeja.math.Point3D;
import org.kabeja.math.TransformContext;
import org.kabeja.svg.SVGConstants;
import org.kabeja.svg.SVGContext;
import org.kabeja.svg.SVGUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


public class SVGMTextGenerator extends AbstractSVGSAXGenerator {
    public void toSAX(ContentHandler handler, Map svgContext, DraftEntity entity,
        TransformContext transformContext) throws SAXException {
    

    	
    	MText mText = (MText) entity;

        AttributesImpl attr = new AttributesImpl();

        Point3D p = mText.getInsertPoint();
        Point3D alignmentPoint = new Point3D(p.getX(), p.getY(), p.getZ());

        boolean notUpsideDown = true;

        // boolean top=false;
        // boolean bottom=false;
        Style style = null;

        if ((mText.getTextStyle().length() > 0) &&
                ((style = mText.getDocument()
                                   .getStyle(mText.getTextStyle())) != null)) {
            if (style.isBackward()) {
                SVGUtils.addAttribute(attr, "writing-mode", "rl");
            } else {
                SVGUtils.addAttribute(attr, "writing-mode", "lr-tb");
            }

            if (style.isUpsideDown()) {
                notUpsideDown = false;
            }
        } else {
            SVGUtils.addAttribute(attr, "writing-mode", "lr-tb");
        }

        if (notUpsideDown) {
            switch (mText.getAlignment()) {
            case MText.ATTACHMENT_TOP_LEFT:
                SVGUtils.addAttribute(attr,
                    SVGConstants.SVG_ATTRIBUTE_TEXT_ANCHOR, "start");

                // top = true;
                break;

            case MText.ATTACHMENT_TOP_CENTER:
                SVGUtils.addAttribute(attr,
                    SVGConstants.SVG_ATTRIBUTE_TEXT_ANCHOR, "middle");

                // top = true;
                break;

            case MText.ATTACHMENT_TOP_RIGHT:
                SVGUtils.addAttribute(attr,
                    SVGConstants.SVG_ATTRIBUTE_TEXT_ANCHOR, "end");

                // top = true;
                break;

            case MText.ATTACHMENT_MIDDLE_LEFT:
                SVGUtils.addAttribute(attr,
                    SVGConstants.SVG_ATTRIBUTE_TEXT_ANCHOR, "start");
                SVGUtils.addAttribute(attr,
                    SVGConstants.SVG_ATTRIBUTE_TEXT_ALIGNMENT_BASELINE, "middle");

                break;

            case MText.ATTACHMENT_MIDDLE_CENTER:
                SVGUtils.addAttribute(attr,
                    SVGConstants.SVG_ATTRIBUTE_TEXT_ANCHOR, "middle");
                SVGUtils.addAttribute(attr,
                    SVGConstants.SVG_ATTRIBUTE_TEXT_ALIGNMENT_BASELINE, "middle");

                break;

            case MText.ATTACHMENT_MIDDLE_RIGHT:
                SVGUtils.addAttribute(attr,
                    SVGConstants.SVG_ATTRIBUTE_TEXT_ANCHOR, "end");
                SVGUtils.addAttribute(attr,
                    SVGConstants.SVG_ATTRIBUTE_TEXT_ALIGNMENT_BASELINE, "middle");

                break;

            case MText.ATTACHMENT_BOTTOM_LEFT:
                SVGUtils.addAttribute(attr,
                    SVGConstants.SVG_ATTRIBUTE_TEXT_ANCHOR, "start");

                // bottom = true;
                break;

            case MText.ATTACHMENT_BOTTOM_CENTER:
                SVGUtils.addAttribute(attr,
                    SVGConstants.SVG_ATTRIBUTE_TEXT_ANCHOR, "middle");

                break;

            case MText.ATTACHMENT_BOTTOM_RIGHT:
                SVGUtils.addAttribute(attr,
                    SVGConstants.SVG_ATTRIBUTE_TEXT_ANCHOR, "end");

                break;

            default:
                SVGUtils.addAttribute(attr,
                    SVGConstants.SVG_ATTRIBUTE_TEXT_ANCHOR, "start");

                break;
            }
        }
        
        SVGUtils.addAttribute(attr,SVGConstants.SVG_ATTRIBUTE_X , SVGUtils.formatNumberAttribute(alignmentPoint.getX()));
        SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_Y, SVGUtils.formatNumberAttribute(alignmentPoint.getY()));


        // given text-entity-height
        double height = mText.getHeight();

        if (height >= Double.MAX_VALUE) {
            height = ((Bounds) svgContext.get(SVGContext.DRAFT_BOUNDS)).getHeight() * 0.005;
        }

        SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_FONT_SIZE,
            SVGUtils.formatNumberAttribute(height));

        // in order to get the right text-view
        StringBuffer transform = new StringBuffer();

        if (!mText.isUpsideDown()) {
            transform.append("matrix(1 0 0 -1 0 ");
            transform.append(2 * alignmentPoint.getY());
            transform.append(')');
        }

        // rotation
        double angle = mText.getRotation();

        if (angle != 0.0) {
            transform.append(" rotate(");
            transform.append( SVGUtils.formatNumberAttribute((-1 * angle)));
            transform.append(' ');
            transform.append( SVGUtils.formatNumberAttribute(alignmentPoint.getX()));
            transform.append(' ');
            transform.append( SVGUtils.formatNumberAttribute(alignmentPoint.getY()));
            transform.append(" )");
        }

        if (mText.getObliqueAngle() != 0.0) {
            transform.append(" skewX(");
            transform.append( SVGUtils.formatNumberAttribute(-1 * mText.getObliqueAngle()));
            transform.append(')');

            transform.append(" translate( ");
            transform.append( SVGUtils.formatNumberAttribute(alignmentPoint.getY() * Math.tan(Math.toRadians(
                        1 * mText.getObliqueAngle()))));
            transform.append(')');
        }

        SVGUtils.addAttribute(attr, "transform", transform.toString());

        if (mText.getReferenceWidth() > 0.0) {
            SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_TEXT_LENGTH,
                "" + mText.getReferenceWidth());
        }

       
        super.setCommonTextAttributes(attr, svgContext, mText);
        SVGUtils.startElement(handler, SVGConstants.SVG_TEXT, attr);
        SVGUtils.textDocumentToSAX(handler, mText.getTextDocument());
        SVGUtils.endElement(handler, SVGConstants.SVG_TEXT);
    }
}
