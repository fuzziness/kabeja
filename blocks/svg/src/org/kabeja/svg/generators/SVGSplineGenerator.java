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

import java.util.Iterator;
import java.util.Map;

import org.kabeja.common.DraftEntity;
import org.kabeja.entities.Polyline;
import org.kabeja.entities.Spline;
import org.kabeja.entities.Vertex;
import org.kabeja.math.MathUtils;
import org.kabeja.math.SplineConverter;
import org.kabeja.math.TransformContext;
import org.kabeja.svg.SVGConstants;
import org.kabeja.svg.SVGContext;
import org.kabeja.svg.SVGGenerationException;
import org.kabeja.svg.SVGPathBoundaryGenerator;
import org.kabeja.svg.SVGSAXGenerator;
import org.kabeja.svg.SVGSAXGeneratorManager;
import org.kabeja.svg.SVGUtils;
import org.kabeja.util.Constants;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;


public class SVGSplineGenerator extends AbstractSVGSAXGenerator
    implements SVGPathBoundaryGenerator {
    public void toSAX(ContentHandler handler, Map svgContext, DraftEntity entity,
        TransformContext transformContext) throws SAXException {
        Spline spline = (Spline) entity;
        Polyline pline = SplineConverter.toPolyline(spline);
       //TODO  pline.setID(spline.getID());

        SVGSAXGeneratorManager manager = (SVGSAXGeneratorManager) svgContext.get(SVGContext.SVGSAXGENERATOR_MANAGER);

        try {
            SVGSAXGenerator gen = manager.getSVGGenerator(Constants.ENTITY_TYPE_POLYLINE);
            gen.toSAX(handler, svgContext, pline, transformContext);
        } catch (SVGGenerationException e) {
            throw new SAXException(e);
        }
    }

    public String getSVGPath(DraftEntity entity) {
        //use Polyline for now 
        Spline spline = (Spline) entity;
        Polyline pline = SplineConverter.toPolyline(spline);
        StringBuffer d = new StringBuffer();

        Vertex last;
        Vertex first;

        Iterator<Vertex> i = pline.getVertices().iterator();
        first = last = (Vertex) i.next();
        d.append("M ");
        d.append(last.getPoint().getX());
        d.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
        d.append(last.getPoint().getY());
        d.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);

        while (i.hasNext()) {
            Vertex end = (Vertex) i.next();
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

    protected String getVertexPath(Vertex start, Vertex end,
        Polyline pline) {
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

                d.append(end.getPoint().getX());
                d.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
                d.append(end.getPoint().getY());
                d.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
            }
        } else {
            d.append("L ");
            d.append(SVGUtils.formatNumberAttribute(end.getPoint().getX()));
            d.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
            d.append(SVGUtils.formatNumberAttribute(end.getPoint().getY()));
            d.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
        }

        return d.toString();
    }
}
