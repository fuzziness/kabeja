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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kabeja.dxf.DXFEntity;
import org.kabeja.dxf.DXFPolyline;
import org.kabeja.dxf.DXFVertex;
import org.kabeja.dxf.helpers.Point;
import org.kabeja.dxf.helpers.PolylineSegment;
import org.kabeja.math.MathUtils;
import org.kabeja.math.TransformContext;
import org.kabeja.svg.SVGConstants;
import org.kabeja.svg.SVGPathBoundaryGenerator;
import org.kabeja.svg.SVGUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


public class SVGPolylineGenerator extends AbstractSVGSAXGenerator
    implements SVGPathBoundaryGenerator {
    public void toSAX(ContentHandler handler, Map svgContext, DXFEntity entity,
        TransformContext transformContext) throws SAXException {
        // the polyline will emit as a svg:path
        // note the dxf polyline has more
        // option as the svg:polyline
        DXFPolyline pline = (DXFPolyline) entity;

        if (pline.getVertexCount() > 0) {
            if (pline.is3DPolygonMesh()) {
                meshToSAX(handler, svgContext, pline);
            } else if (pline.isPolyfaceMesh()) {
                polyfaceToSAX(handler, svgContext, pline);
            } else if (pline.isCurveFitVerticesAdded()) {
                // splineFitToSAX(handler, svgContext);
            } else if (pline.isSplineFitVerticesAdded()) {
                splineFitToSAX(handler, svgContext, pline);
            } else if (pline.is3DPolygon()) {
                splineFitToSAX(handler, svgContext, pline);
            } else {
                polylineToSAX(handler, svgContext, pline);
            }
        }
    }

    protected void polylineToSAX(ContentHandler handler, Map svgContext,
        DXFPolyline pline) throws SAXException {
        AttributesImpl attr = new AttributesImpl();

        if ((pline.getStartWidth() != pline.getEndWidth()) ||
                !pline.isConstantWidth()) {
            // handle the different width
            polylinePartToSAX(handler, svgContext, pline);
        } else {
            StringBuffer d = new StringBuffer();
            DXFVertex last;
            DXFVertex first;
            Iterator i = pline.getVertexIterator();
            first = last = (DXFVertex) i.next();
            d.append("M ");
            d.append(SVGUtils.formatNumberAttribute(last.getX()));
            d.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
            d.append(SVGUtils.formatNumberAttribute(last.getY()));
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

            SVGUtils.addAttribute(attr, "d", d.toString());
            super.setCommonAttributes(attr, svgContext, pline);

            if (pline.getStartWidth() > 0.0) {
                SVGUtils.addAttribute(attr,
                    SVGConstants.SVG_ATTRIBUTE_STROKE_WITDH,
                    SVGUtils.formatNumberAttribute(pline.getStartWidth()));
            }

            SVGUtils.emptyElement(handler, SVGConstants.SVG_PATH, attr);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.kabeja.dxf.helpers.HatchBoundaryElement#getSVGPath()
     */
    public String getSVGPath(DXFEntity entity) {
        // create the path
        DXFPolyline pline = (DXFPolyline) entity;
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

    protected void polylinePartToSAX(ContentHandler handler, Map svgContext,
        DXFPolyline pline) throws SAXException {
        // output as group
        AttributesImpl attr = new AttributesImpl();
        super.setCommonAttributes(attr, svgContext, pline);
        SVGUtils.startElement(handler, SVGConstants.SVG_GROUP, attr);

        String oldID = pline.getID();
        PolylineSegment segment = null;

        // boolean bulged = false;
        boolean process = true;
        DXFVertex start = pline.getVertex(0);
        DXFVertex end = pline.getVertex(1);
        segment = new PolylineSegment(start, end, pline);

        int i = 1;

        while (i < pline.getVertexCount()) {
            // we need the next vertex to get the right endpoints
            PolylineSegment next = null;

            if ((i + 1) < pline.getVertexCount()) {
                process = false;

                DXFVertex nextStart = end;
                end = pline.getVertex(i + 1);
                next = new PolylineSegment(nextStart, end, pline);

                if (next.isBulged()) {
                    segment.setPoint3(next.getPoint2());
                    segment.setPoint4(next.getPoint1());
                } else {
                    segment.connect(next);
                }
            }

            StringBuffer d = new StringBuffer();
            d.append("M ");

            if (segment.isBulged()) {
                // first the line from 1->2
                d.append(segment.getPoint1().getX());
                d.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
                d.append(segment.getPoint1().getY());
                d.append(" L ");
                d.append(segment.getPoint2().getX());
                d.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
                d.append(segment.getPoint2().getY());

                // next the arc from 2->3
                double r = 0;

                if (segment.getBulge() > 0) {
                    r = segment.getInnerRadius();
                } else {
                    r = segment.getOuterRadius();
                }

                d.append(" A ");
                d.append(SVGUtils.formatNumberAttribute(r));
                d.append(' ');
                d.append(SVGUtils.formatNumberAttribute(r));
                //the x axis rotation
                d.append(" 0 ");

                if (Math.abs(segment.getBulgeHeight()) > Math.abs(
                            segment.getRadius())) {
                    // large Arc-flag
                    d.append(" 1 ");
                } else {
                    d.append(" 0 ");
                }

                // if the bulge > 0 the center point is on the left side
                // if the bulge < 0 the center point is ont the right side
                if (segment.getBulge() > 0) {
                    // the sweep-flag
                    d.append(" 0 ");
                } else {
                    d.append(" 1 ");
                }

                d.append(segment.getPoint3().getX());
                d.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
                d.append(segment.getPoint3().getY());

                // next the line from 3->4
                d.append(" L ");
                d.append(segment.getPoint4().getX());
                d.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
                d.append(segment.getPoint4().getY());

                // next the arc from 4->1
                r = 0;

                if (segment.getBulge() > 0) {
                    r = segment.getInnerRadius();
                } else {
                    r = segment.getOuterRadius();
                }

                d.append(" A ");
                d.append(SVGUtils.formatNumberAttribute(r));
                d.append(' ');
                d.append(SVGUtils.formatNumberAttribute(r));
                //the x axis rotation
                d.append(" 0 ");

                if (Math.abs(segment.getBulgeHeight()) > Math.abs(
                            segment.getRadius())) {
                    // large Arc-flag
                    d.append(" 1 ");
                } else {
                    d.append(" 0 ");
                }

                // if the bulge > 0 the center point is on the left side
                // if the bulge < 0 the center point is ont the right side
                if (segment.getBulge() > 0) {
                    // the sweep-flag
                    d.append(" 0 ");
                } else {
                    d.append(" 1 ");
                }

                d.append(segment.getPoint1().getX());
                d.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
                d.append(segment.getPoint1().getY());
                // and finally close the path
                d.append(" Z");
            } else {
                // ok output the trapezium
                // from p1 -> p2 -> p3 -> p4 and close
                d.append(segment.getPoint1().getX());
                d.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
                d.append(segment.getPoint1().getY());
                d.append(" L ");

                d.append(segment.getPoint2().getX());
                d.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
                d.append(segment.getPoint2().getY());
                d.append(" L ");

                d.append(segment.getPoint3().getX());
                d.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
                d.append(segment.getPoint3().getY());
                d.append(" L ");

                d.append(segment.getPoint4().getX());
                d.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
                d.append(segment.getPoint4().getY());
                d.append(" Z");
            }

            // output
            attr = new AttributesImpl();
            pline.setID(pline.getID() + "__" + i);
            super.setCommonAttributes(attr, svgContext, pline);

            if (pline.getDXFDocument().getDXFHeader().isFillMode()) {
                SVGUtils.addAttribute(attr, "fill", "currentColor");
            }

            SVGUtils.addAttribute(attr, "d", d.toString());

            // output now
            SVGUtils.emptyElement(handler, SVGConstants.SVG_PATH, attr);

            if (!process) {
                segment = next;
            }

            i++;
        }

        SVGUtils.endElement(handler, SVGConstants.SVG_GROUP);
        pline.setID(oldID);
    }

    protected void splineFitToSAX(ContentHandler handler, Map svgContext,
        DXFPolyline pline) throws SAXException {
        // TODO we will first use the approximation of the spline
        // and later take e deeper look at SVG-bezier curves /DXF b-splines
        StringBuffer d = new StringBuffer();

        Iterator i = pline.getVertexIterator();
        DXFVertex last = (DXFVertex) i.next();
        d.append("M " + last.getX() + " " + last.getY() + " ");

        while (i.hasNext()) {
            DXFVertex vertex = (DXFVertex) i.next();

            if (vertex.is2DSplineApproximationVertex()) {
                d.append("L ");
                d.append(vertex.getX());
                d.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
                d.append(vertex.getY());
                d.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
            }
        }

        AttributesImpl attr = new AttributesImpl();
        super.setCommonAttributes(attr, svgContext, pline);

        SVGUtils.addAttribute(attr, "d", d.toString());

        // output now
        SVGUtils.emptyElement(handler, SVGConstants.SVG_PATH, attr);
    }

    protected void singleEdgeToSAX(ContentHandler handler, DXFVertex start,
        DXFVertex end, Map svgContext, DXFPolyline pline)
        throws SAXException {
        AttributesImpl attr = new AttributesImpl();
        super.setCommonAttributes(attr, svgContext, pline);

        StringBuffer d = new StringBuffer();
        d.append("M ");
        d.append(start.getX());
        d.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
        d.append(start.getY());
        d.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);

        d.append(getVertexPath(start, end, pline));

        SVGUtils.addAttribute(attr, "d", d.toString());

        if (start.getStartWidth() > 0.0) {
            SVGUtils.addAttribute(attr,
                SVGConstants.SVG_ATTRIBUTE_STROKE_WITDH,
                "" + start.getStartWidth());
        }

        SVGUtils.emptyElement(handler, SVGConstants.SVG_PATH, attr);
    }

    protected void polyfaceToSAX(ContentHandler handler, Map svgContext,
        DXFPolyline pline) throws SAXException {
        Iterator i = pline.getVertexIterator();
        StringBuffer buf = new StringBuffer();

        while (i.hasNext()) {
            DXFVertex v = (DXFVertex) i.next();

            if (v.isFaceRecord()) {
                DXFVertex v1 = pline.getPolyFaceMeshVertex(v.getPolyFaceMeshVertex0());
                DXFVertex v2 = pline.getPolyFaceMeshVertex(v.getPolyFaceMeshVertex1());
                DXFVertex v3 = pline.getPolyFaceMeshVertex(v.getPolyFaceMeshVertex2());
                DXFVertex v4 = pline.getPolyFaceMeshVertex(v.getPolyFaceMeshVertex3());

                if (v.isPolyFaceEdge0Visible() &&
                        (v.getPolyFaceMeshVertex0() != 0)) {
                    addEdgeToPath(v1, v2, buf);
                }

                if (v.isPolyFaceEdge1Visible() &&
                        (v.getPolyFaceMeshVertex1() != 0)) {
                    addEdgeToPath(v2, v3, buf);
                }

                if (v.isPolyFaceEdge2Visible() &&
                        (v.getPolyFaceMeshVertex2() != 0)) {
                    addEdgeToPath(v3, v4, buf);
                }

                if (v.isPolyFaceEdge3Visible() &&
                        (v.getPolyFaceMeshVertex3() != 0)) {
                    addEdgeToPath(v4, v1, buf);
                } else if ((v4 == null) && (v3 != null)) {
                    // triangle
                    addEdgeToPath(v3, v1, buf);
                }

                if (buf.length() > 0) {
                    AttributesImpl attr = new AttributesImpl();
                    SVGUtils.addAttribute(attr, "d", buf.toString());
                    super.setCommonAttributes(attr, svgContext, v);
                    // fillmode ????????
                    SVGUtils.emptyElement(handler, SVGConstants.SVG_PATH, attr);
                    buf.delete(0, buf.length());
                }
            }
        }

        if (buf.length() > 0) {
            AttributesImpl attr = new AttributesImpl();
            SVGUtils.addAttribute(attr, "d", buf.toString());
            super.setCommonAttributes(attr, svgContext, pline);
            SVGUtils.emptyElement(handler, SVGConstants.SVG_PATH, attr);
        }
    }

    protected void addEdgeToPath(DXFVertex start, DXFVertex end,
        StringBuffer buf) {
        buf.append('M');
        buf.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
        buf.append(start.getX());
        buf.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
        buf.append(start.getY());
        buf.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);

        if (end != null) {
            buf.append('L');
            buf.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
            buf.append(end.getX());
            buf.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
            buf.append(end.getY());
            buf.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
        }
    }

    protected void meshToSAX(ContentHandler handler, Map svgContext,
        DXFPolyline pline) throws SAXException {
        // TODO check first the points and put the output together
        StringBuffer d = new StringBuffer();

        if (pline.isSimpleMesh()) {
            int rows = pline.getRows();
            d = new StringBuffer();

            Point[][] points = new Point[pline.getRows()][pline.getColumns()];
            Iterator it = pline.getVertexIterator();

            // create a line for each row
            for (int i = 0; i < pline.getRows(); i++) {
                d.append("M ");

                for (int x = 0; x < pline.getColumns(); x++) {
                    DXFVertex v = (DXFVertex) it.next();
                    points[i][x] = v.getPoint();
                    d.append(v.getX());
                    d.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
                    d.append(v.getY());

                    if (x < (pline.getColumns() - 1)) {
                        d.append(" L ");
                    }
                }

                if (pline.isClosedMeshNDirection()) {
                    d.append('L');
                    d.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
                    d.append(points[i][0].getX());
                    d.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
                    d.append(points[i][0].getY());
                    d.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
                }
            }

            // create a line for each column
            for (int i = 0; i < pline.getColumns(); i++) {
                d.append(" M ");

                for (int x = 0; x < pline.getRows(); x++) {
                    d.append(points[x][i].getX());
                    d.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
                    d.append(points[x][i].getY());

                    if (x < (pline.getRows() - 1)) {
                        d.append(" L ");
                    }
                }

                if (pline.isClosedMeshMDirection()) {
                    d.append('L');
                    d.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
                    d.append(points[0][i].getX());
                    d.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
                    d.append(points[0][i].getY());
                    d.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
                }
            }
        } else {
            Point[][] points = new Point[pline.getSurefaceDensityRows()][pline.getSurefaceDensityColumns()];
            Iterator vi = pline.getVertexIterator();
            List appVertices = new ArrayList();

            while (vi.hasNext()) {
                DXFVertex v = (DXFVertex) vi.next();

                if (v.isMeshApproximationVertex()) {
                    appVertices.add(v);
                }
            }

            Iterator it = appVertices.iterator();

            // create a line for each row
            for (int i = 0; i < pline.getSurefaceDensityRows(); i++) {
                d.append("M ");

                for (int x = 0; x < pline.getSurefaceDensityColumns(); x++) {
                    DXFVertex v = (DXFVertex) it.next();
                    points[i][x] = v.getPoint();
                    d.append(v.getX());
                    d.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
                    d.append(v.getY());

                    if (x < (pline.getSurefaceDensityColumns() - 1)) {
                        d.append(" L ");
                    }
                }

                if (pline.isClosedMeshNDirection()) {
                    d.append('L');
                    d.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
                    d.append(points[i][0].getX());
                    d.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
                    d.append(points[i][0].getY());
                    d.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
                }
            }

            // create a line for each column
            for (int i = 0; i < pline.getSurefaceDensityColumns(); i++) {
                d.append(" M ");

                for (int x = 0; x < pline.getSurefaceDensityRows(); x++) {
                    d.append(points[x][i].getX());
                    d.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
                    d.append(points[x][i].getY());

                    if (x < (pline.getSurefaceDensityRows() - 1)) {
                        d.append(" L ");
                    }
                }

                if (pline.isClosedMeshMDirection()) {
                    d.append('L');
                    d.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
                    d.append(points[0][i].getX());
                    d.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
                    d.append(points[0][i].getY());
                    d.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
                }
            }
        }

        AttributesImpl attr = new AttributesImpl();
        SVGUtils.addAttribute(attr, "d", d.toString());
        super.setCommonAttributes(attr, svgContext, pline);
        SVGUtils.emptyElement(handler, SVGConstants.SVG_PATH, attr);
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
