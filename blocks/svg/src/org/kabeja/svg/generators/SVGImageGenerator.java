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
import org.kabeja.dxf.DXFImage;
import org.kabeja.dxf.helpers.Point;
import org.kabeja.dxf.objects.DXFImageDefObject;
import org.kabeja.math.TransformContext;
import org.kabeja.svg.SVGConstants;
import org.kabeja.svg.SVGUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


public class SVGImageGenerator extends AbstractSVGSAXGenerator {
    public void toSAX(ContentHandler handler, Map svgContext, DXFEntity entity,
        TransformContext transformContext) throws SAXException {
        DXFImage image = (DXFImage) entity;

        // get the image path from the referenced IMAGEDEF object
        DXFImageDefObject imageDef = (DXFImageDefObject) image.getDXFDocument()
                                                              .getDXFObjectByID(image.getImageDefObjectID());

        if (imageDef != null) {
            // TODO add clipping here with clipPath
            AttributesImpl attr = new AttributesImpl();
            super.setCommonAttributes(attr, svgContext, image);

            Point insertPoint = image.getInsertPoint();

            double imageSizeAlongU = image.getImageSizeAlongU();
            double imageSizeAlongV = image.getImageSizeAlongV();

            SVGUtils.addAttribute(attr, "x",
                SVGUtils.formatNumberAttribute(insertPoint.getX()));
            SVGUtils.addAttribute(attr, "y",
                SVGUtils.formatNumberAttribute(insertPoint.getY()));
            SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_WIDTH,
                SVGUtils.formatNumberAttribute(+imageSizeAlongU));
            SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_HEIGHT,
                SVGUtils.formatNumberAttribute(+imageSizeAlongV));

            // convert the file to uri
            attr.addAttribute(SVGConstants.XMLNS_NAMESPACE, "xlink",
                "xmlns:xlink", "CDATA", SVGConstants.XLINK_NAMESPACE);
            attr.addAttribute(SVGConstants.XLINK_NAMESPACE, "href",
                "xlink:href", "CDATA",
                SVGUtils.pathToURI(imageDef.getFilename()));

            // We have a main transformation on the complete draft.
            // So we need here the rotate of image to get the right
            // view back.
            StringBuffer transform = new StringBuffer();
            transform.append("rotate(180 ");
            transform.append(SVGUtils.formatNumberAttribute((insertPoint.getX() +
                    (imageSizeAlongU / 2))));
            transform.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
            transform.append(SVGUtils.formatNumberAttribute((insertPoint.getY() +
                    (imageSizeAlongV / 2))));
            transform.append(")");

            SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_TRANSFORM,
                transform.toString());
            SVGUtils.emptyElement(handler, SVGConstants.SVG_IMAGE, attr);
        }
    }
}
