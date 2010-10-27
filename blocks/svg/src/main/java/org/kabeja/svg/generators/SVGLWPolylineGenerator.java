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
import org.kabeja.entities.LW2DVertex;
import org.kabeja.entities.LWPolyline;
import org.kabeja.entities.util.LWPolylineSegment;
import org.kabeja.math.TransformContext;
import org.kabeja.svg.SVGConstants;
import org.kabeja.svg.SVGPathBoundaryGenerator;
import org.kabeja.svg.SVGUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;



public class SVGLWPolylineGenerator extends AbstractSVGSAXGenerator
implements SVGPathBoundaryGenerator {
    
    public String getSVGPath(DraftEntity entity) {
        // create the path
        LWPolyline pline = (LWPolyline) entity;
        StringBuffer d = new StringBuffer();

        LW2DVertex last;
        LW2DVertex  first;

        Iterator<LW2DVertex> i = pline.getVertices().iterator();
       first = last = i.next();
        d.append("M ");
        d.append(last.getX());
        d.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
        d.append(last.getY());
        d.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);

        while (i.hasNext()) {
            LW2DVertex end =  i.next();
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


    protected String getVertexPath(LW2DVertex start, LW2DVertex end,
        LWPolyline pline) {
        StringBuffer d = new StringBuffer();

        if (start.getBulge() != 0) {
            // from the DXF-Specs.
            double l = getLength(start, end);

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
    

    /**
     * Returns the distance between 2 Point3D
     *
     * @param start
     * @param end
     * @return the length between the two points
     */
    protected double getLength(LW2DVertex start, LW2DVertex end) {
        double value = Math.sqrt(Math.pow(end.getX() - start.getX(), 2) +
                Math.pow(end.getY() - start.getY(), 2));

        return value;
    }



	public void toSAX(ContentHandler handler, Map svgContext, DraftEntity entity,
			TransformContext transformContext) throws SAXException {
		 LWPolyline lwpolyline = (LWPolyline)entity;
		    AttributesImpl attr = new AttributesImpl();

            if ((lwpolyline.getStartWidth() != lwpolyline.getEndWidth()) ||
                    !lwpolyline.isConstantWidth()) {
                // handle the different width
                polylinePartToSAX(handler, svgContext, lwpolyline);
            } else {
                StringBuffer d = new StringBuffer();
                LW2DVertex last;
                LW2DVertex first;
                Iterator<LW2DVertex> i = lwpolyline.getVertices().iterator();
                first = last = i.next();
                d.append("M ");
                d.append(SVGUtils.formatNumberAttribute(last.getX()));
                d.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
                d.append(SVGUtils.formatNumberAttribute(last.getY()));
                d.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);

                while (i.hasNext()) {
                   LW2DVertex end = i.next();
                    d.append(getVertexPath(last, end, lwpolyline));
                    last = end;
                }

                // bit coded values
                if (lwpolyline.isClosed()) {
                    if (last.getBulge() != 0) {
                        d.append(getVertexPath(last, first, lwpolyline));
                    }

                    d.append(" z");
                }

                SVGUtils.addAttribute(attr, "d", d.toString());
                super.setCommonAttributes(attr, svgContext, lwpolyline);

                if (lwpolyline.getStartWidth() > 0.0) {
                    SVGUtils.addAttribute(attr,
                        SVGConstants.SVG_ATTRIBUTE_STROKE_WITDH,
                        SVGUtils.formatNumberAttribute(lwpolyline.getStartWidth()));
                }

                SVGUtils.emptyElement(handler, SVGConstants.SVG_PATH, attr);
            }
		
	}
	
	
	 protected void polylinePartToSAX(ContentHandler handler, Map svgContext,
		        LWPolyline pline) throws SAXException {
		        // output as group
		        AttributesImpl attr = new AttributesImpl();
		        super.setCommonAttributes(attr, svgContext, pline);
		        SVGUtils.startElement(handler, SVGConstants.SVG_GROUP, attr);

		       
		       LWPolylineSegment segment = null;

		        // boolean bulged = false;
		        boolean process = true;
		        LW2DVertex start = pline.getVertex(0);
		        LW2DVertex end = pline.getVertex(1);
		        segment = new LWPolylineSegment(start, end, pline);

		        int i = 1;

		        while (i < pline.getVertexCount()) {
		            // we need the next vertex to get the right endpoints
		            LWPolylineSegment next = null;

		            if ((i + 1) < pline.getVertexCount()) {
		                process = false;

		                LW2DVertex nextStart = end;
		                end = pline.getVertex(i + 1);
		                next = new LWPolylineSegment(nextStart, end, pline);

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
		         
		            super.setCommonAttributes(attr, svgContext, pline);

		            if (pline.getDocument().getHeader().isFillMode()) {
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
		       
		    }
    
}
