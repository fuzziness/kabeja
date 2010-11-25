/*
   Copyright 2005 Simon Mieth

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
package org.kabeja.svg.dimension;

import java.util.Map;

import org.kabeja.dxf.DXFDimension;
import org.kabeja.dxf.DXFEntity;
import org.kabeja.math.TransformContext;
import org.kabeja.svg.SVGConstants;
import org.kabeja.svg.SVGUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 *
 */
public class LinearDimensionOutputter extends AbstractDimensionOutputter {
    public LinearDimensionOutputter(DXFDimension dim) {
        super(dim);
    }

    public void output(ContentHandler handler, Map svgContext)
        throws SAXException {
        if (dim.getDimensionBlock().length() > 0) {
            AttributesImpl attr = new AttributesImpl();
            StringBuffer buf = new StringBuffer();

            buf.append("translate(");
            buf.append((dim.getInsertPoint().getX()));
            buf.append(" ");
            buf.append((dim.getInsertPoint().getY()));
            buf.append(")");

            SVGUtils.addAttribute(attr, "transform", buf.toString());

            setCommonAttributes(attr, svgContext, dim);

            SVGUtils.startElement(handler, SVGConstants.SVG_GROUP, attr);
            attr = new AttributesImpl();
            attr.addAttribute(SVGConstants.XMLNS_NAMESPACE, "xlink",
                "xmlns:xlink", "CDATA", SVGConstants.XLINK_NAMESPACE);
            attr.addAttribute(SVGConstants.XLINK_NAMESPACE, "href",
                "xlink:href", "CDATA",
                "#" + SVGUtils.validateID(dim.getDimensionBlock()));

            SVGUtils.emptyElement(handler, SVGConstants.SVG_USE, attr);

            SVGUtils.endElement(handler, SVGConstants.SVG_GROUP);
        }
    }

    public void toSAX(ContentHandler handler, Map svgContext, DXFEntity entity,
        TransformContext transformContext) throws SAXException {
        output(handler, svgContext);
    }
}
