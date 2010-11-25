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

import java.util.Iterator;
import java.util.Map;

import org.kabeja.dxf.DXFConstants;
import org.kabeja.dxf.DXFEntity;
import org.kabeja.dxf.DXFPolyline;
import org.kabeja.dxf.DXFSpline;
import org.kabeja.dxf.DXFVertex;
import org.kabeja.dxf.helpers.DXFSplineConverter;
import org.kabeja.math.MathUtils;
import org.kabeja.math.TransformContext;
import org.kabeja.svg.SVGConstants;
import org.kabeja.svg.SVGContext;
import org.kabeja.svg.SVGGenerationException;
import org.kabeja.svg.SVGPathBoundaryGenerator;
import org.kabeja.svg.SVGSAXGenerator;
import org.kabeja.svg.SVGSAXGeneratorManager;
import org.kabeja.svg.SVGUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;


public class SVGSplineGenerator extends AbstractSVGSAXGenerator
    implements SVGPathBoundaryGenerator {
    public void toSAX(ContentHandler handler, Map svgContext, DXFEntity entity,
        TransformContext transformContext) throws SAXException {
        DXFSpline spline = (DXFSpline) entity;
        DXFPolyline pline = DXFSplineConverter.toDXFPolyline(spline);
        pline.setID(spline.getID());

        SVGSAXGeneratorManager manager = (SVGSAXGeneratorManager) svgContext.get(SVGContext.SVGSAXGENERATOR_MANAGER);

        try {
            SVGSAXGenerator gen = manager.getSVGGenerator(DXFConstants.ENTITY_TYPE_POLYLINE);
            gen.toSAX(handler, svgContext, pline, transformContext);
        } catch (SVGGenerationException e) {
            throw new SAXException(e);
        }
    }

    public String getSVGPath(DXFEntity entity) {
        //use Polyline for now 
        DXFSpline spline = (DXFSpline) entity;
        DXFPolyline pline = DXFSplineConverter.toDXFPolyline(spline);
        StringBuffer d = new StringBuffer();

        DXFVertex last;
        DXFVertex first;

        Iterator i = pline.getVertexIterator();
        first = last = (DXFVertex) i.next();
        d.append("M ");
        d.append(last.getX());
        d.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
        d.append(last.getY());
        d.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);

        while (i.hasNext()) {
            DXFVertex end = (DXFVertex) i.next();
            d.append(getVertexPath(last, end, pline));
            last = end;
        }

        // bit coded values
        if (pline.isClosed()) {
            if (last.getBulge() != 0) {
                d.append(getVertexPath(last, first, pline));
            }

            d.append(" z");
        }

        return d.toString();
    }

    protected String getVertexPath(DXFVertex start, DXFVertex end,
        DXFPolyline pline) {
        StringBuffer d = new StringBuffer();

        if (start.getBulge() != 0) {
            // from the DXF-Specs.
            double l = MathUtils.distance(start.getPoint(), end.getPoint());

            // do nothing if the points are the same
            if (l > 0.0) {
                double r = pline.getRadius(Math.abs(start.getBulge()), l);
                double h = (start.getBulge() * l) / 2;

                // converting to an elipse with the same rx=ry
                d.append("A ");
                d.append(SVGUtils.formatNumberAttribute(r));
                d.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
                d.append(SVGUtils.formatNumberAttribute(r));
                d.append(" 0");

                if (Math.abs(start.getBulge()) > 1.0) {
                    // large Arc-flag
                    d.append(" 1 ");
                } else {
                    d.append(" 0 ");
                }

                // if the bulge > 0 the center point is on the left side
                // if the bulge < 0 the center point is ont the right side
                if (start.getBulge() < 0) {
                    // the sweep-flag
                    d.append(" 0 ");
                } else {
                    d.append(" 1 ");
                }

                d.append(end.getX());
                d.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
                d.append(end.getY());
                d.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
            }
        } else {
            d.append("L ");
            d.append(SVGUtils.formatNumberAttribute(end.getX()));
            d.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
            d.append(SVGUtils.formatNumberAttribute(end.getY()));
            d.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
        }

        return d.toString();
    }
}
