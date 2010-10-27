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
import org.kabeja.entities.Solid;
import org.kabeja.math.ParametricPlane;
import org.kabeja.math.Point3D;
import org.kabeja.math.TransformContext;
import org.kabeja.svg.SVGConstants;
import org.kabeja.svg.SVGUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


public class SVGSolidGenerator extends AbstractSVGSAXGenerator {
    public void toSAX(ContentHandler handler, Map svgContext, DraftEntity entity,
        TransformContext transformContext) throws SAXException {
        Solid solid = (Solid) entity;
        AttributesImpl attr = new AttributesImpl();

        StringBuffer points = new StringBuffer();
        ParametricPlane plane = new ParametricPlane(solid.getExtrusion());
        // the sequence p1->p2->p4->p3 is defined
        // by the DXF specs
       addPointToBuffer(points,plane.getPoint(solid.getPoint1()));
       addPointToBuffer(points,plane.getPoint(solid.getPoint2()));
       addPointToBuffer(points,plane.getPoint(solid.getPoint4()));
       addPointToBuffer(points,plane.getPoint(solid.getPoint3()));


        SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_POINTS, points.toString());

        super.setCommonAttributes(attr, svgContext, solid);

        // if the fillmode attribute is non-zero the solid is filled
        if (solid.getDocument().getHeader().isFillMode()) {
            SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_FILL, SVGConstants.SVG_ATTRIBUTE_VALUE_CURRENTCOLOR);
        }

        SVGUtils.emptyElement(handler, SVGConstants.SVG_POLYGON, attr);
    }
    
    
    protected void addPointToBuffer(StringBuffer b,Point3D p){
        b.append(SVGUtils.formatNumberAttribute(p.getX()));
        b.append(SVGConstants.SVG_POLYGON_POINT_SEPARATOR);
        b.append(SVGUtils.formatNumberAttribute(p.getY()));
        b.append(SVGConstants.SVG_PATH_SEPARATOR);

    }
}
