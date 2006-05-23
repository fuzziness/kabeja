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

import org.kabeja.dxf.helpers.HatchLinePattern;
import org.kabeja.dxf.helpers.Point;
import org.kabeja.svg.SVGConstants;
import org.kabeja.svg.SVGGenerator;
import org.kabeja.svg.SVGUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth </a>
 *
 */
public class DXFHatchPattern implements SVGGenerator {
    private static int idCount = 0;

    private String id = null;

    private List patterns = new ArrayList();

    private DXFHatch hatch;

    /**
     * @return Returns the id.
     */
    public String getId() {
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
    public void setId(String id) {
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
            double dotLength = (bounds.getWidth() + bounds.getHeight()) / 2 * 0.0005;

            AttributesImpl attr = new AttributesImpl();
            SVGUtils.addAttribute(attr, SVGConstants.XML_ID, getId());
            SVGUtils.addAttribute(attr, "x", "" + bounds.getMinimumX());
            SVGUtils.addAttribute(attr, "y", "" + bounds.getMinimumY());
            SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_WIDTH, ""
                    + bounds.getWidth());
            SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_HEIGHT, ""
                    + bounds.getHeight());

            SVGUtils.addAttribute(attr,
                    SVGConstants.SVG_ATTRIBUTE_PATTERN_UNITS, "userSpaceOnUse");
            SVGUtils.startElement(handler, SVGConstants.SVG_PATTERN, attr);
            attr = new AttributesImpl();
            SVGUtils.startElement(handler, SVGConstants.SVG_TITLE, attr);
            SVGUtils.characters(handler, this.hatch.getName());
            SVGUtils.endElement(handler, SVGConstants.SVG_TITLE);

            // we convert the pattern as one path element
           // StringBuffer buf = new StringBuffer();
            Iterator i = patterns.iterator();

            // System.out.println("PATTERN:\nName:" + hatch.getName());
            while (i.hasNext()) {
                HatchLinePattern pattern = (HatchLinePattern) i.next();

                attr = new AttributesImpl();
                SVGUtils.addAttribute(attr, "d", createLineType(bounds,
                        pattern, dotLength));
                SVGUtils.addAttribute(attr, "stroke", "red");
                SVGUtils.addAttribute(attr, "stroke-width", "0.01%");
                SVGUtils.emptyElement(handler, SVGConstants.SVG_PATH, attr);
            }
            SVGUtils.endElement(handler, SVGConstants.SVG_PATTERN);
        }
    }

    public void addLinePattern(HatchLinePattern pattern) {
        patterns.add(pattern);
    }

    public Iterator getLinePatternIterator() {
        return patterns.iterator();
    }

    /**
     * The associated hatch for this pattern.
     *
     * @return Returns the hatch.
     */
    public DXFHatch getHatch() {
        return hatch;
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

    protected String createLineType(Bounds b, HatchLinePattern pattern,
            double dotlength) {
        StringBuffer buf = new StringBuffer();
        Point base = pattern.getBasePoint();
        Point offset = pattern.getOffsetPoint();
        b.debug();
        System.out.println("base:" + base);
        System.out.println("baseX:" + pattern.getBaseX() + "         baseY:"
                + pattern.getBaseY());
        System.out.println("offset:" + offset);
        System.out.println("offsetX:" + pattern.getOffsetX()
                + "         offsetY:" + pattern.getOffsetY());

        double startx = 0;
        if (base.getX() < b.getMinimumX()) {
            startx = base.getX()
                    + Math.abs(offset.getX())
                    * Math.floor(Math.abs((b.getMinimumX() - base.getX())
                            / offset.getX()));
        } else {
            startx = base.getX()
                    - Math.abs(offset.getX())
                    * Math.ceil(Math.abs((b.getMinimumX() - base.getX())
                            / offset.getX()));
        }

        double starty = 0;
        if (base.getY() < b.getMinimumY()) {
            starty = base.getY()
                    + Math.abs(offset.getY())
                    * Math.floor(Math.abs((b.getMinimumY() - base.getY())
                            / offset.getY()));
        } else {
            starty = base.getY()
                    - Math.abs(offset.getY())
                    * Math.ceil(Math.abs((b.getMinimumY() - base.getY())
                            / offset.getY()));
        }

        double angle = pattern.getRotationAngle();

        System.out.println("angle:" + pattern.getRotationAngle() + " fix="
                + angle);
        System.out.println("startx:" + startx + "    starty:" + starty);
        angle = Math.toRadians(angle);
        double y = starty;
        double x = startx;
        double[] dashes = pattern.getPattern();
        if (dashes.length > 0) {
            while (startx < b.getMaximumX() && angle < 90) {
                y = starty;
                x = startx;

                buf.append('M');
                buf.append(' ');
                buf.append(x);
                buf.append(' ');
                buf.append(y);
                buf.append(' ');
                while (y < b.getMaximumY() && x < b.getMaximumX()) {

                    for (int i = 0; i < dashes.length; i++) {

                        x += Math.cos(angle) * Math.abs(dashes[i]);
                        y += Math.sin(angle) * Math.abs(dashes[i]);
                        if (dashes[i] > 0) {
                            buf.append('L');
                            buf.append(' ');
                            buf.append(x);
                            buf.append(' ');
                            buf.append(y);
                            buf.append(' ');
                        } else if (dashes[i] < 0) {
                            buf.append('M');
                            buf.append(' ');
                            buf.append(x);
                            buf.append(' ');
                            buf.append(y);
                            buf.append(' ');
                        } else {
                            // a dot
                            buf.append('l');
                            buf.append(' ');
                            buf.append(dotlength);
                            buf.append(' ');
                            buf.append(dotlength);
                            buf.append(' ');
                        }

                    }
                }

                startx += Math.abs(offset.getX());

            }
        } else {
            // draw a solid line
            while(startx<b.getMaximumX()){

                buf.append('M');
                buf.append(' ');
                buf.append(startx);
                buf.append(' ');
                buf.append(starty);
                buf.append(' ');

                 x =1/Math.tan(angle)*b.getHeight()+startx;

                 buf.append('L');
                 buf.append(' ');
                 buf.append(x);
                 buf.append(' ');
                 buf.append(b.getMaximumY());
                 buf.append(' ');
                 startx += Math.abs(offset.getX());
            }
        }
        System.out.println("pattern length:" + buf.length());
        return buf.toString();
    }
}
