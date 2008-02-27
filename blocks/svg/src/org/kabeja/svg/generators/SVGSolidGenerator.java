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
import org.kabeja.dxf.DXFSolid;
import org.kabeja.dxf.helpers.Point;
import org.kabeja.math.TransformContext;
import org.kabeja.svg.SVGConstants;
import org.kabeja.svg.SVGUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


public class SVGSolidGenerator extends AbstractSVGSAXGenerator {
    public void toSAX(ContentHandler handler, Map svgContext, DXFEntity entity,
        TransformContext transformContext) throws SAXException {
        DXFSolid solid = (DXFSolid) entity;
        AttributesImpl attr = new AttributesImpl();

        StringBuffer points = new StringBuffer();

        // the sequence p1->p2->p4->p3 is defined
        // by the DXF specs
        Point point1 = solid.getPoint1();

        points.append(point1.getX());
        points.append(",");
        points.append(point1.getY());
        points.append(" ");

        Point point2 = solid.getPoint2();
        points.append(point2.getX());
        points.append(",");
        points.append(point2.getY());
        points.append(" ");

        Point point4 = solid.getPoint4();
        points.append(point4.getX());
        points.append(",");
        points.append(point4.getY());
        points.append(" ");

        Point point3 = solid.getPoint3();
        points.append(point3.getX());
        points.append(",");
        points.append(point3.getY());
        points.append(" ");

        SVGUtils.addAttribute(attr, "points", points.toString());

        super.setCommonAttributes(attr, svgContext, solid);

        // if the fillmode attribute is non-zero the solid is filled
        if (solid.getDXFDocument().getDXFHeader().isFillMode()) {
            SVGUtils.addAttribute(attr, "fill", "currentColor");
        }

        SVGUtils.emptyElement(handler, SVGConstants.SVG_POLYGON, attr);
    }
}
