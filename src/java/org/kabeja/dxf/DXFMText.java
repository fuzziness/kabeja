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

import org.kabeja.dxf.helpers.DXFTextParser;
import org.kabeja.dxf.helpers.DXFUtils;
import org.kabeja.dxf.helpers.Point;
import org.kabeja.dxf.helpers.TextDocument;
import org.kabeja.svg.SVGConstants;
import org.kabeja.svg.SVGContext;
import org.kabeja.svg.SVGUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 */
public class DXFMText extends DXFText {
    public static final int ATTACHMENT_TOP = 1;
    public static final int ATTACHMENT_TOP_CENTER = 2;
    public static final int ATTACHMENT_TOP_RIGHT = 3;
    public static final int ATTACHMENT_MIDDLE_LEFT = 4;
    public static final int ATTACHMENT_MIDDLE_CENTER = 5;
    public static final int ATTACHMENT_MIDDLE_RIGHT = 6;
    public static final int ATTACHMENT_BOTTOM_LEFT = 7;
    public static final int ATTACHMENT_BOTTOM_CENTER = 8;
    public static final int ATTACHMENT_BOTTOM_RIGHT = 9;
    private int attachmentpoint = 1;
    private double refwidth = 0.0;
    private double refheight = 0.0;

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.kabeja.dxf.DXFText#processText(java.lang.String,
     *      org.xml.sax.ContentHandler)
     */
    public void setAttachmentPoint(int value) {
        this.attachmentpoint = value;
    }

    public void setReferenceWidth(double width) {
        this.refwidth = width;
    }

    public double getReferenceWidth() {
        return this.refwidth;
    }

    public void setReferenceHeight(double height) {
        this.refheight = height;
    }

    public double getReferenceHeight() {
        return this.refheight;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.kabeja.svg.SVGGenerator#toSAX(org.xml.sax.ContentHandler)
     */
    public void toSAX(ContentHandler handler, Map svgContext)
        throws SAXException {
        AttributesImpl attr = new AttributesImpl();

        Point alignmentPoint = new Point(p.getX(), p.getY(), p.getZ());

        boolean notUpsideDown = true;
        DXFStyle style = null;

        if ((textStyle.length() > 0) &&
                ((style = doc.getDXFStyle(this.textStyle)) != null)) {
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
            switch (attachmentpoint) {
            case ATTACHMENT_TOP:
                SVGUtils.addAttribute(attr,
                    SVGConstants.SVG_ATTRIBUTE_TEXT_ANCHOR, "start");
                top = true;

                break;

            case ATTACHMENT_TOP_CENTER:
                SVGUtils.addAttribute(attr,
                    SVGConstants.SVG_ATTRIBUTE_TEXT_ANCHOR, "middle");
                top = true;

                break;

            case ATTACHMENT_TOP_RIGHT:
                SVGUtils.addAttribute(attr,
                    SVGConstants.SVG_ATTRIBUTE_TEXT_ANCHOR, "end");
                top = true;

                break;

            case ATTACHMENT_MIDDLE_LEFT:
                SVGUtils.addAttribute(attr,
                    SVGConstants.SVG_ATTRIBUTE_TEXT_ANCHOR, "start");
                SVGUtils.addAttribute(attr,
                    SVGConstants.SVG_ATTRIBUTE_TEXT_ALIGNMENT_BASELINE, "middle");

                break;

            case ATTACHMENT_MIDDLE_CENTER:
                SVGUtils.addAttribute(attr,
                    SVGConstants.SVG_ATTRIBUTE_TEXT_ANCHOR, "middle");
                SVGUtils.addAttribute(attr,
                    SVGConstants.SVG_ATTRIBUTE_TEXT_ALIGNMENT_BASELINE, "middle");

                break;

            case ATTACHMENT_MIDDLE_RIGHT:
                SVGUtils.addAttribute(attr,
                    SVGConstants.SVG_ATTRIBUTE_TEXT_ANCHOR, "end");
                SVGUtils.addAttribute(attr,
                    SVGConstants.SVG_ATTRIBUTE_TEXT_ALIGNMENT_BASELINE, "middle");

                break;

            case ATTACHMENT_BOTTOM_LEFT:
                SVGUtils.addAttribute(attr,
                    SVGConstants.SVG_ATTRIBUTE_TEXT_ANCHOR, "start");
                bottom = true;

                break;

            case ATTACHMENT_BOTTOM_CENTER:
                SVGUtils.addAttribute(attr,
                    SVGConstants.SVG_ATTRIBUTE_TEXT_ANCHOR, "middle");

                break;

            case ATTACHMENT_BOTTOM_RIGHT:
                SVGUtils.addAttribute(attr,
                    SVGConstants.SVG_ATTRIBUTE_TEXT_ANCHOR, "end");

                break;

            default:
                SVGUtils.addAttribute(attr,
                    SVGConstants.SVG_ATTRIBUTE_TEXT_ANCHOR, "start");

                break;
            }
        }

        SVGUtils.addAttribute(attr, "x", "" + alignmentPoint.getX());
        SVGUtils.addAttribute(attr, "y",
            "" + doc.translateY(alignmentPoint.getY()));

        // given text-entity-height
        double height = getHeight();

        if (height >= Double.MAX_VALUE) {
            height = ((Bounds) svgContext.get(SVGContext.DRAFT_BOUNDS)).getHeight() * 0.005;
        }

        SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_FONT_SIZE,
            SVGUtils.formatNumberAttribute(height));

        // in order to get the right text-view
        StringBuffer transform = new StringBuffer();




        if (!isUpsideDown()) {
            transform.append("matrix(1 0 0 -1 0 ");
            transform.append(2 * alignmentPoint.getY());
            transform.append(')');
        }





        // rotation
        double angle = getRotation();

        if (angle != 0.0) {
            transform.append(" rotate(");
            transform.append((-1 * angle));
            transform.append(' ');
            transform.append(alignmentPoint.getX());
            transform.append(' ');
            transform.append(alignmentPoint.getY());
            transform.append(" )");
        }

        if (this.oblique_angle != 0.0) {
            transform.append(" skewX(");
            transform.append(-1*this.oblique_angle);
            transform.append(')');

            transform.append(" translate( ");
            transform.append(alignmentPoint.getY()*Math.tan(Math.toRadians(1*this.oblique_angle)));
            transform.append(')');
         }


        SVGUtils.addAttribute(attr, "transform", transform.toString());

        if (refwidth > 0.0) {
            SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_TEXT_LENGTH,
                "" + refwidth);
        }



        SVGUtils.addAttribute(attr, "fill", "currentColor");
        super.setCommonAttributes(attr, svgContext);
        SVGUtils.startElement(handler, SVGConstants.SVG_TEXT, attr);
        SVGUtils.textDocumentToSAX(handler, getTextDocument());
        SVGUtils.endElement(handler, SVGConstants.SVG_TEXT);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.kabeja.dxf.DXFEntity#getType()
     */
    public String getType() {
        return DXFConstants.ENTITY_TYPE_MTEXT;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.kabeja.dxf.DXFText#getRotation()
     */
    public double getRotation() {
        if (rotation != 0.0) {
            return rotation;
        } else if ((align_p1.getX() != 0.0) || (align_p1.getY() != 0.0) ||
                (align_p1.getZ() != 0.0)) {
            // the align point as direction vector here
            // calculate the angle between the x-axis and the direction-vector
            double[] x = { align_p1.getX(), align_p1.getY(), align_p1.getZ() };
            double v = align_p1.getX() / DXFUtils.vectorValue(x);
            v = Math.toDegrees(Math.acos(v));

            return v;
        }

        // same as 0.0
        return rotation;
    }

    public TextDocument getTextDocument() {
        return this.textDoc;
    }

    public void setText(String text) {
        this.text = text;

        this.textDoc = DXFTextParser.parseDXFMText(this);
    }

    public int getAlignment() {
        return attachmentpoint;
    }

    protected boolean isOmitLineType() {
        return true;
    }

    /* (non-Javadoc)
     * @see org.kabeja.dxf.DXFEntity#getBounds()
     */
    public Bounds getBounds() {
        int l = this.textDoc.getMaximumLineLength();

        if (l > 0) {
            this.bounds.addToBounds(this.p);

            double h = getHeight();

            if (h == 0.0) {
                h = getReferenceHeight();
            }

            double w = l * 0.7 * h;
            h *= this.textDoc.getLineCount();

            switch (this.attachmentpoint) {
            case ATTACHMENT_BOTTOM_CENTER:
                bounds.addToBounds(this.p.getX() + (w / 2), this.p.getY() + h);
                bounds.addToBounds(this.p.getX() - (w / 2), this.p.getY() + h);

                break;

            case ATTACHMENT_BOTTOM_LEFT:
                bounds.addToBounds(this.p.getX() + w, this.p.getY() + h);

                break;

            case ATTACHMENT_BOTTOM_RIGHT:
                bounds.addToBounds(this.p.getX() - w, this.p.getY() + h);

                break;

            case ATTACHMENT_MIDDLE_CENTER:
                bounds.addToBounds(this.p.getX() + (w / 2),
                    this.p.getY() + (h / 2));
                bounds.addToBounds(this.p.getX() - (w / 2),
                    this.p.getY() - (h / 2));

                break;

            case ATTACHMENT_MIDDLE_LEFT:
                bounds.addToBounds(this.p.getX() + w, this.p.getY() + (h / 2));
                bounds.addToBounds(this.p.getX() + w, this.p.getY() - (h / 2));

                break;

            case ATTACHMENT_MIDDLE_RIGHT:
                bounds.addToBounds(this.p.getX() - w, this.p.getY() + (h / 2));
                bounds.addToBounds(this.p.getX() - w, this.p.getY() - (h / 2));

                break;

            case ATTACHMENT_TOP:
                bounds.addToBounds(this.p.getX() + w, this.p.getY() - h);

                break;

            case ATTACHMENT_TOP_CENTER:
                bounds.addToBounds(this.p.getX() + (w / 2), this.p.getY() - h);
                bounds.addToBounds(this.p.getX() - (w / 2), this.p.getY() - h);

                break;

            case ATTACHMENT_TOP_RIGHT:
                bounds.addToBounds(this.p.getX() - w, this.p.getY() - h);

                break;
            }
        } else {
            bounds.setValid(false);
        }

        return bounds;
    }
}
