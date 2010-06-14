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
import org.kabeja.entities.Leader;
import org.kabeja.entities.util.DimensionStyle;
import org.kabeja.math.MathUtils;
import org.kabeja.math.Point3D;
import org.kabeja.math.TransformContext;
import org.kabeja.svg.SVGConstants;
import org.kabeja.svg.SVGUtils;
import org.kabeja.util.Constants;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


public class SVGLeaderGenerator extends AbstractSVGSAXGenerator {
    public void toSAX(ContentHandler handler, Map svgContext, DraftEntity entity,
        TransformContext transformContext) throws SAXException {
        Leader leader = (Leader) entity;

        if (!leader.isSplinePath()) {
           
            StringBuffer buf = new StringBuffer();
          

          for(Point3D p:leader.getCoordinates()) {
        	    if(buf.length()>0){
        	    	 buf.append(" L");
        	    }else{
        	    	  buf.append('M');
        	    } 	  
                buf.append(' ');
                buf.append(p.getX());
                buf.append(' ');
                buf.append(p.getY());

            }

            AttributesImpl attr = new AttributesImpl();
            super.setCommonAttributes(attr, svgContext, leader);
            SVGUtils.addAttribute(attr, "d", buf.toString());
            SVGUtils.emptyElement(handler, SVGConstants.SVG_PATH, attr);

            //the used DIMSTYLE
            DimensionStyle style = leader.getDocument()
                                            .getDimensionStyle(leader.getStyleNameID());

            if (leader.isArrowEnabled() && (style != null)) {
                //the arrow
                if (style.hasProperty(DimensionStyle.PROPERTY_DIMLDRBLK)) {
                    String blockID = style.getProperty(DimensionStyle.PROPERTY_DIMLDRBLK);

                    if (leader.getCoordinateCount() > 1) {
                        Point3D p1 = leader.getCoordinateAt(0);
                        Point3D p2 = leader.getCoordinateAt(1);
                        double length = MathUtils.distance(p1, p2);
                        double arrowLength = style.getDoubleProperty(DimensionStyle.PROPERTY_DIMASZ,
                                0.0) * style.getDoubleProperty(DimensionStyle.PROPERTY_DIMSCALE,
                                1.0);

                        if (length > (2 * arrowLength)) {
                            double angle = Math.toDegrees(MathUtils.getAngle(
                                        MathUtils.getVector(p1, p2),
                                        Constants.DEFAULT_X_AXIS_VECTOR));

                            attr = new AttributesImpl();

                            if (angle != 0.0) {
                                SVGUtils.addAttribute(attr, "transform",
                                    "rotate(" + angle + ")");
                            }

                            SVGUtils.startElement(handler,
                                SVGConstants.SVG_GROUP, attr);
                            attr = new AttributesImpl();
                            attr.addAttribute(SVGConstants.XMLNS_NAMESPACE,
                                "xlink", "xmlns:xlink", "CDATA",
                                SVGConstants.XLINK_NAMESPACE);

                            attr.addAttribute(SVGConstants.XLINK_NAMESPACE,
                                "href", "xlink:href", "CDATA",
                                "#" + SVGUtils.validateID(blockID));

                            SVGUtils.emptyElement(handler,
                                SVGConstants.SVG_USE, attr);

                            SVGUtils.endElement(handler, SVGConstants.SVG_GROUP);
                        }
                    }
                }
            }
        }
    }
}
