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

import org.kabeja.dxf.DXFEntity;
import org.kabeja.dxf.DXFLine;
import org.kabeja.dxf.helpers.Point;
import org.kabeja.math.TransformContext;
import org.kabeja.svg.SVGConstants;
import org.kabeja.svg.SVGPathBoundaryGenerator;
import org.kabeja.svg.SVGUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


public class SVGLineGenerator extends AbstractSVGSAXGenerator
    implements SVGPathBoundaryGenerator {
    public void toSAX(ContentHandler handler, Map svgContext, DXFEntity entity,
        TransformContext transformContext) throws SAXException {
        DXFLine line = (DXFLine) entity;
        AttributesImpl attr = new AttributesImpl();

        // set the attributes
        SVGUtils.addAttribute(attr, "x1",
            SVGUtils.formatNumberAttribute(line.getStartPoint().getX()));
        SVGUtils.addAttribute(attr, "y1",
            SVGUtils.formatNumberAttribute(line.getStartPoint().getY()));
        SVGUtils.addAttribute(attr, "x2",
            SVGUtils.formatNumberAttribute(line.getEndPoint().getX()));
        SVGUtils.addAttribute(attr, "y2",
            SVGUtils.formatNumberAttribute(line.getEndPoint().getY()));
        super.setCommonAttributes(attr, svgContext, line);
        SVGUtils.emptyElement(handler, SVGConstants.SVG_LINE, attr);
    }

    public String getSVGPath(DXFEntity entity) {
        DXFLine line = (DXFLine) entity;
        Point start = line.getStartPoint();
        Point end = line.getEndPoint();
        StringBuffer buf = new StringBuffer();
        buf.append("M ");
        buf.append(SVGUtils.formatNumberAttribute(start.getX()));
        buf.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
        buf.append(SVGUtils.formatNumberAttribute(start.getY()));
        buf.append(" L ");
        buf.append(SVGUtils.formatNumberAttribute(end.getX()));
        buf.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
        buf.append(SVGUtils.formatNumberAttribute(end.getY()));

        return buf.toString();
    }
}
