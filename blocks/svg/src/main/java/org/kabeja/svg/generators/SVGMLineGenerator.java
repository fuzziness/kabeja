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

import org.kabeja.common.Color;
import org.kabeja.common.DraftEntity;
import org.kabeja.entities.MLine;
import org.kabeja.entities.Polyline;
import org.kabeja.entities.util.Utils;
import org.kabeja.math.TransformContext;
import org.kabeja.objects.MLineStyle;
import org.kabeja.svg.SVGConstants;
import org.kabeja.svg.SVGContext;
import org.kabeja.svg.SVGGenerationException;
import org.kabeja.svg.SVGPathBoundaryGenerator;
import org.kabeja.svg.SVGSAXGenerator;
import org.kabeja.svg.SVGSAXGeneratorManager;
import org.kabeja.svg.SVGUtils;
import org.kabeja.util.Constants;
import org.kabeja.util.MLineConverter;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


public class SVGMLineGenerator extends AbstractSVGSAXGenerator {
    public void toSAX(ContentHandler handler, Map svgContext, DraftEntity entity,
        TransformContext transformContext) throws SAXException {
        MLine mline = (MLine) entity;

        Polyline[] pl = MLineConverter.toPolyline(mline);

        MLineStyle style = (MLineStyle) mline.getDocument()
                                                   .getObjectByID(mline.getMLineStyleID());
        SVGSAXGeneratorManager manager = (SVGSAXGeneratorManager) svgContext.get(SVGContext.SVGSAXGENERATOR_MANAGER);
        SVGPathBoundaryGenerator gen = manager.getSVGPathBoundaryGenerator(Constants.ENTITY_TYPE_POLYLINE);

        if (style.isFilled()) {
            // we create a filled polyline
            StringBuffer buf = new StringBuffer();
            Polyline p1 = pl[0];
            buf.append(gen.getSVGPath(p1));

            Polyline p2 = pl[pl.length - 1];
            Utils.reversePolyline(p2);

            String str = gen.getSVGPath(p2).trim();

            if (str.startsWith("M")) {
                buf.append(" L ");
                buf.append(str.substring(1));
            } else {
                buf.append(str);
            }

            buf.append(" z");

            AttributesImpl atts = new AttributesImpl();
            SVGUtils.addAttribute(atts, "d", buf.toString());
            SVGUtils.addAttribute(atts, SVGConstants.SVG_ATTRIBUTE_STROKE,
                "none");
            SVGUtils.addAttribute(atts, SVGConstants.SVG_ATTRIBUTE_FILL,
                Color.getRGBString(style.getFillColor()));
            SVGUtils.emptyElement(handler, SVGConstants.SVG_PATH, atts);
        }

        try {
            SVGSAXGenerator saxGenerator = manager.getSVGGenerator(Constants.ENTITY_TYPE_POLYLINE);

            for (int i = 0; i < pl.length; i++) {
                saxGenerator.toSAX(handler, svgContext, pl[i], transformContext);
            }
        } catch (SVGGenerationException e) {
            throw new SAXException(e);
        }
    }
}
