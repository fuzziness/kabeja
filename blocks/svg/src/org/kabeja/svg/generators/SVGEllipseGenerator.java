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

import org.kabeja.dxf.DXFEllipse;
import org.kabeja.dxf.DXFEntity;
import org.kabeja.dxf.helpers.Point;
import org.kabeja.math.TransformContext;
import org.kabeja.svg.SVGConstants;
import org.kabeja.svg.SVGPathBoundaryGenerator;
import org.kabeja.svg.SVGUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


public class SVGEllipseGenerator extends AbstractSVGSAXGenerator
    implements SVGPathBoundaryGenerator {
    public void toSAX(ContentHandler handler, Map svgContext, DXFEntity entity,
        TransformContext transformContext) throws SAXException {
        DXFEllipse ellipse = (DXFEllipse) entity;

        AttributesImpl attr = new AttributesImpl();
        super.setCommonAttributes(attr, svgContext, ellipse);

        if ((ellipse.getStartParameter() == DXFEllipse.DEFAULT_START_PARAMETER) &&
                (ellipse.getEndParameter() == DXFEllipse.DEFAULT_END_PARAMETER)) {
            SVGUtils.addAttribute(attr, "cx",
                SVGUtils.formatNumberAttribute(ellipse.getCenterPoint().getX()));
            SVGUtils.addAttribute(attr, "cy",
                SVGUtils.formatNumberAttribute(ellipse.getCenterPoint().getY()));

            double major = ellipse.getHalfMajorAxisLength();
            double minor = ellipse.getRatio() * major;
            SVGUtils.addAttribute(attr, "rx",
                SVGUtils.formatNumberAttribute(major));
            SVGUtils.addAttribute(attr, "ry",
                SVGUtils.formatNumberAttribute(minor));

            // chek for rotation
            double angle = ellipse.getRotationAngle();

            if (angle != 0.0) {
                StringBuffer buf = new StringBuffer();
                buf.append("rotate(");
                buf.append(SVGUtils.formatNumberAttribute(Math.toDegrees(angle)));
                buf.append(' ');
                buf.append(SVGUtils.formatNumberAttribute(
                        ellipse.getCenterPoint().getX()));
                buf.append(' ');
                buf.append(SVGUtils.formatNumberAttribute(
                        ellipse.getCenterPoint().getY()));
                buf.append(')');
                SVGUtils.addAttribute(attr, "transform", buf.toString());
            }

            // SVGUtils.addAttribute(attr, "fill", "none");
            SVGUtils.emptyElement(handler, SVGConstants.SVG_ELLIPSE, attr);
        } else {
            SVGUtils.addAttribute(attr, "d", getSVGPath(ellipse));
            SVGUtils.emptyElement(handler, SVGConstants.SVG_PATH, attr);
        }
    }

    public String getSVGPath(DXFEntity entity) {
        DXFEllipse ellipse = (DXFEllipse) entity;

        StringBuffer buf = new StringBuffer();

        Point start = ellipse.getPointAt(ellipse.getStartParameter());
        Point end = ellipse.getPointAt(ellipse.getEndParameter());

        buf.append("M ");
        buf.append(SVGUtils.formatNumberAttribute(start.getX()));
        buf.append(' ');
        buf.append(SVGUtils.formatNumberAttribute(start.getY()));

        // get the angle between x-axis and major-axis
        double major = ellipse.getMajorAxisDirection().getLength();
        double angle = ellipse.getRotationAngle();

        buf.append(" A ");
        buf.append(SVGUtils.formatNumberAttribute(major));
        buf.append(' ');
        buf.append(SVGUtils.formatNumberAttribute(major * ellipse.getRatio()));
        buf.append(' ');
        // rotation value of the ellipse
        buf.append(SVGUtils.formatNumberAttribute(Math.toDegrees(angle)));

        if ((ellipse.getStartParameter() == DXFEllipse.DEFAULT_START_PARAMETER) &&
                (ellipse.getEndParameter() == DXFEllipse.DEFAULT_END_PARAMETER)) {
            // drawing a full ellipse -> from start point to half
            // and then back

            // the large-arc flag and the sweep-flag always 1
            buf.append(" 1 1 ");

            buf.append(SVGUtils.formatNumberAttribute(end.getX()));
            buf.append(' ');
            buf.append(SVGUtils.formatNumberAttribute(end.getY()));

            buf.append(" A ");
            buf.append(SVGUtils.formatNumberAttribute(major));
            buf.append(' ');
            buf.append(SVGUtils.formatNumberAttribute(
                    major * ellipse.getRatio()));
            buf.append(' ');

            // rotation value of the ellipse
            buf.append(SVGUtils.formatNumberAttribute(Math.toDegrees(angle)));

            buf.append(" 1 1 ");

            buf.append(SVGUtils.formatNumberAttribute(start.getX()));
            buf.append(' ');
            buf.append(SVGUtils.formatNumberAttribute(start.getY()));

            // buf.append(" z ");
        } else {
            //TODO check the correct flags 
            //
            buf.append(' ');

            // the large-arc flag
            if ((ellipse.getEndParameter() - ellipse.getStartParameter()) >= Math.PI) {
                buf.append(1);
            } else {
                buf.append(0);
            }

            buf.append(' ');

            // the sweep-flag always 1
            buf.append(" 1 ");

            buf.append(SVGUtils.formatNumberAttribute(end.getX()));
            buf.append(' ');
            buf.append(SVGUtils.formatNumberAttribute(end.getY()));
        }

        return buf.toString();
    }
}
