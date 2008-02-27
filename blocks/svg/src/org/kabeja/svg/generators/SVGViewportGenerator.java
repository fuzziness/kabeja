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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.kabeja.dxf.Bounds;
import org.kabeja.dxf.DXFColor;
import org.kabeja.dxf.DXFConstants;
import org.kabeja.dxf.DXFEntity;
import org.kabeja.dxf.DXFLayer;
import org.kabeja.dxf.DXFLineType;
import org.kabeja.dxf.DXFViewport;
import org.kabeja.dxf.helpers.Point;
import org.kabeja.math.TransformContext;
import org.kabeja.svg.SVGConstants;
import org.kabeja.svg.SVGContext;
import org.kabeja.svg.SVGGenerationException;
import org.kabeja.svg.SVGSAXGenerator;
import org.kabeja.svg.SVGSAXGeneratorManager;
import org.kabeja.svg.SVGUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


public class SVGViewportGenerator extends AbstractSVGSAXGenerator {
    private SVGSAXGeneratorManager manager;

    public void toSAX(ContentHandler handler, Map svgContext, DXFEntity entity,
        TransformContext transformContext) throws SAXException {
        DXFViewport viewport = (DXFViewport) entity;
        // make a copy of the context
        svgContext = new HashMap(svgContext);

        if (viewport.getViewportStatus() > 0) {
            Point center = viewport.getCenterPoint();
            AttributesImpl attr = new AttributesImpl();
            SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_X,
                "" +
                SVGUtils.formatNumberAttribute((center.getX() -
                    (viewport.getWidth() / 2))));
            SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_Y,
                "" +
                SVGUtils.formatNumberAttribute((center.getY() -
                    (viewport.getHeight() / 2))));
            SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_WIDTH,
                "" + SVGUtils.formatNumberAttribute(viewport.getWidth()));
            SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_HEIGHT,
                "" + SVGUtils.formatNumberAttribute(viewport.getHeight()));
            super.setCommonAttributes(attr, svgContext, entity);
            SVGUtils.emptyElement(handler, SVGConstants.SVG_RECTANLGE, attr);

            if (!viewport.isModelSpace() &&
                    !viewport.getViewportID().equals("1")) {
                attr = new AttributesImpl();
                // first the clip path
                SVGUtils.addAttribute(attr, SVGConstants.XML_ID,
                    SVGUtils.validateID(viewport.getID() + "_clip"));
                SVGUtils.startElement(handler, SVGConstants.SVG_CLIPPING_PATH,
                    attr);

                Bounds viewBounds = viewport.getModelspaceViewBounds();
                attr = new AttributesImpl();

                double w = viewBounds.getWidth() / 2;
                double h = viewBounds.getHeight() / 2;
                Point p = viewport.getViewCenterPoint();
                StringBuffer buf = new StringBuffer();
                buf.append('M');
                buf.append(' ');
                buf.append(SVGUtils.formatNumberAttribute((p.getX() - w)));
                buf.append(' ');
                buf.append(SVGUtils.formatNumberAttribute((p.getY() - h)));
                buf.append(' ');
                buf.append('L');
                buf.append(' ');
                buf.append(SVGUtils.formatNumberAttribute((p.getX() + w)));
                buf.append(' ');
                buf.append(SVGUtils.formatNumberAttribute((p.getY() - h)));
                buf.append(' ');

                buf.append('L');
                buf.append(' ');
                buf.append(SVGUtils.formatNumberAttribute((p.getX() + w)));
                buf.append(' ');
                buf.append(SVGUtils.formatNumberAttribute((p.getY() + h)));
                buf.append(' ');
                buf.append('L');
                buf.append(' ');
                buf.append(SVGUtils.formatNumberAttribute((p.getX() - w)));
                buf.append(' ');
                buf.append(SVGUtils.formatNumberAttribute((p.getY() + h)));
                buf.append(' ');
                buf.append('z');

                SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_VIEWBOX,
                    buf.toString());

                SVGUtils.addAttribute(attr, "d", buf.toString());
                SVGUtils.emptyElement(handler, SVGConstants.SVG_PATH, attr);

                SVGUtils.endElement(handler, SVGConstants.SVG_CLIPPING_PATH);

                // output the view to modelspace
                double zoomXP = viewport.getZoomXPFactor();
                this.manager = (SVGSAXGeneratorManager) svgContext.get(SVGContext.SVGSAXGENERATOR_MANAGER);

                attr = new AttributesImpl();
                SVGUtils.addAttribute(attr, SVGConstants.XML_ID,
                    viewport.getID());

                // the transform
                StringBuffer buff = new StringBuffer();

                buff.append("translate(");
                buff.append(' ');
                buff.append(viewport.getCenterPoint().getX());
                buff.append(' ');
                buff.append(viewport.getCenterPoint().getY());
                buff.append(')');

                buff.append("scale(");
                buff.append(zoomXP);
                buff.append(' ');
                buff.append(zoomXP);
                buff.append(')');

                buff.append(' ');

                buff.append("translate(");
                buff.append(' ');
                buff.append((-1 * viewport.getViewCenterPoint().getX()));
                buff.append(' ');
                buff.append((-1 * viewport.getViewCenterPoint().getY()));
                buff.append(')');

                SVGUtils.addAttribute(attr, "transform", buff.toString());

                // the stroke-width
                double width = 0;

                if (svgContext.containsKey(SVGContext.STROKE_WIDTH)) {
                    Double lw = (Double) svgContext.get(SVGContext.LAYER_STROKE_WIDTH);
                    width = lw.doubleValue() / zoomXP;
                } else {
                    width = (viewBounds.getWidth() + viewBounds.getHeight()) / 2 * SVGConstants.DEFAULT_STROKE_WIDTH_PERCENT;

                    double defaultSW = ((double) DXFConstants.ENVIRONMENT_VARIABLE_LWDEFAULT) / 100.0;

                    if (width > defaultSW) {
                        width = defaultSW;
                    }
                }

                SVGUtils.addAttribute(attr,
                    SVGConstants.SVG_ATTRIBUTE_STROKE_WITDH,
                    SVGUtils.formatNumberAttribute(width));
                svgContext.put(SVGContext.STROKE_WIDTH, new Double(width));

                // reference the clip path
                SVGUtils.addAttribute(attr,
                    SVGConstants.SVG_ATTRIBUTE_CLIP_PATH,
                    "url(#" + SVGUtils.validateID(viewport.getID() + "_clip") +
                    ")");

                SVGUtils.startElement(handler, SVGConstants.SVG_GROUP, attr);

                Iterator i = viewport.getDXFDocument().getDXFLayerIterator();

                while (i.hasNext()) {
                    DXFLayer layer = (DXFLayer) i.next();

                    if (!viewport.isFrozenLayer(layer.getName())) {
                        this.layerToSAX(handler, layer, svgContext, viewBounds,
                            viewport);
                    }
                }

                SVGUtils.endElement(handler, SVGConstants.SVG_GROUP);
            }
        }
    }

    protected void layerToSAX(ContentHandler handler, DXFLayer layer,
        Map context, Bounds viewBounds, DXFViewport viewport)
        throws SAXException {
        AttributesImpl attr = new AttributesImpl();

        SVGUtils.addAttribute(attr, SVGConstants.XML_ID,
            SVGUtils.validateID(layer.getName()));

        SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_COLOR,
            "rgb(" + DXFColor.getRGBString(Math.abs(layer.getColor())) + ")");
        SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_STROKE,
            SVGConstants.SVG_ATTRIBUTE_STROKE_VALUE_CURRENTCOLOR);

        SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_FILL,
            SVGConstants.SVG_ATTRIBUTE_FILL_VALUE_NONE);

        if (!layer.isVisible()) {
            SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_VISIBILITY,
                SVGConstants.SVG_ATTRIBUTE_VISIBILITY_VALUE_HIDDEN);
        }

        String lt = layer.getLineType();

        if (lt.length() > 0) {
            DXFLineType ltype = layer.getDXFDocument().getDXFLineType(lt);
            SVGUtils.addStrokeDashArrayAttribute(attr, ltype);
        }

        // the stroke-width
        int lineWeight = layer.getLineWeight();

        // the stroke-width
        Double lw = null;

        if ((lineWeight > 0) &&
                !context.containsKey(SVGContext.STROKE_WIDTH_IGNORE)) {
            lw = new Double(lineWeight);
            SVGUtils.addAttribute(attr,
                SVGConstants.SVG_ATTRIBUTE_STROKE_WITDH,
                SVGUtils.lineWeightToStrokeWidth(lineWeight));
        } else {
            lw = (Double) context.get(SVGContext.STROKE_WIDTH);
            SVGUtils.addAttribute(attr,
                SVGConstants.SVG_ATTRIBUTE_STROKE_WITDH,
                SVGUtils.formatNumberAttribute(lw.doubleValue()));
        }

        context.put(SVGContext.LAYER_STROKE_WIDTH, lw);

        SVGUtils.startElement(handler, SVGConstants.SVG_GROUP, attr);

        Iterator types = layer.getDXFEntityTypeIterator();

        while (types.hasNext()) {
            String type = (String) types.next();
            ArrayList list = (ArrayList) layer.getDXFEntities(type);

            Iterator i = list.iterator();

            try {
                SVGSAXGenerator gen = this.manager.getSVGGenerator(type);

                while (i.hasNext()) {
                    DXFEntity entity = (DXFEntity) i.next();
                    Bounds b = entity.getBounds();

                    // TODO only output the modelspace entities which are inside
                    // or partial the
                    // bounds
                    if (entity.isModelSpace() && b.contains(viewBounds)) {
                        gen.toSAX(handler, context, entity, null);
                    }
                }
            } catch (SVGGenerationException e) {
                e.printStackTrace();
            }
        }

        SVGUtils.endElement(handler, SVGConstants.SVG_GROUP);
    }
}
