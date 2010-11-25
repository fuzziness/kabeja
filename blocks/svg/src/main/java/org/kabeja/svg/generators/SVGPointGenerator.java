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
import org.kabeja.dxf.DXFPoint;
import org.kabeja.math.TransformContext;
import org.kabeja.svg.SVGConstants;
import org.kabeja.svg.SVGUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


public class SVGPointGenerator extends AbstractSVGSAXGenerator {
    public void toSAX(ContentHandler handler, Map svgContext, DXFEntity entity,
        TransformContext transformContext) throws SAXException {
        DXFPoint point = (DXFPoint) entity;
        AttributesImpl attr = new AttributesImpl();
        SVGUtils.addAttribute(attr, "cx",
            SVGUtils.formatNumberAttribute(point.getX()));
        SVGUtils.addAttribute(attr, "cy",
            SVGUtils.formatNumberAttribute(point.getY()));
        SVGUtils.addAttribute(attr, "r", "" + 0.001);
        super.setCommonAttributes(attr, svgContext, point);
        SVGUtils.emptyElement(handler, SVGConstants.SVG_CIRCLE, attr);
    }
}
