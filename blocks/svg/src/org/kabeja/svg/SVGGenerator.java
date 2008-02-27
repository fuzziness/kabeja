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
package org.kabeja.svg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.kabeja.dxf.Bounds;
import org.kabeja.dxf.DXFBlock;
import org.kabeja.dxf.DXFColor;
import org.kabeja.dxf.DXFConstants;
import org.kabeja.dxf.DXFEntity;
import org.kabeja.dxf.DXFLayer;
import org.kabeja.dxf.DXFLineType;
import org.kabeja.dxf.DXFStyle;
import org.kabeja.dxf.DXFVariable;
import org.kabeja.dxf.objects.DXFDictionary;
import org.kabeja.dxf.objects.DXFLayout;
import org.kabeja.math.TransformContext;
import org.kabeja.svg.generators.SVGStyleGenerator;
import org.kabeja.xml.AbstractSAXGenerator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


public class SVGGenerator extends AbstractSAXGenerator {
    public final static String PROPERTY_MARGIN = "margin";
    public final static String PROPERTY_STROKE_WIDTH = "stroke-width";
    public final static String PROPERTY_DOCUMENT_BOUNDS = "useBounds";

    /**
     * This property defines the way of calculation/setup the bounds of the
     * Document. Possible values are:
     * <ul>
     * <li>kabeja: Bounds calculate on the geometries. (default)</li>
     * <li>paperspace: extracts the values of the limits from paperspace</li>
     * <li>modelspace: extracts the values of the limits from modelspace</li>
     * </ul>
     */
    public final static String PROPERTY_DOCUMENT_BOUNDS_RULE = "bounds-rule";
    public final static int PROPERTY_DOCUMENT_BOUNDS_RULE_KABEJA = 1;
    public final static int PROPERTY_DOCUMENT_BOUNDS_RULE_PAPERSPACE = 2;
    public final static int PROPERTY_DOCUMENT_BOUNDS_RULE_MODELSPACE = 3;
    public final static String PROPERTY_DOCUMENT_BOUNDS_RULE_KABEJA_VALUE = "kabeja";
    public final static String PROPERTY_DOCUMENT_BOUNDS_RULE_PAPERSPACE_VALUE = "Paperspace";
    public final static String PROPERTY_DOCUMENT_BOUNDS_RULE_PAPERSPACE_LIMITS_VALUE =
        "Paperspace-Limits";
    public final static String PROPERTY_DOCUMENT_BOUNDS_RULE_MODELSPACE_VALUE = "Modelspace";
    public final static String PROPERTY_DOCUMENT_BOUNDS_RULE_MODELSPACE_LIMITS_VALUE =
        "Modelspace-Limits";
    public final static String PROPERTY_DOCUMENT_OUTPUT_STYLE = "output-style";
    public final static int PROPERTY_DOCUMENT_OUTPUT_STYLE_NOLAYOUT = 0;
    public final static int PROPERTY_DOCUMENT_OUTPUT_STYLE_LAYOUT = 1;
    public final static int PROPERTY_DOCUMENT_OUTPUT_STYLE_PLOTSETTING = 2;
    public final static String PROPERTY_DOCUMENT_OUTPUT_STYLE_LAYOUT_VALUE = "layout";
    public final static String PROPERTY_DOCUMENT_OUTPUT_STYLE_PLOTSETTING_VALUE = "plotsetting";
    public final static String PROPERTY_DOCUMENT_OUTPUT_STYLE_NAME = "output-style-name";
    public final static String PROPERTY_WIDTH = "width";
    public final static String PROPERTY_HEIGHT = "height";
    public final static String PROPERTY_OVERFLOW = "svg-overflow";
    public static final double DEFAULT_MARGIN_PERCENT = 0.0;
    public final static String SUPPORTED_SVG_VERSION = "1.0";
    private boolean overflow = true;
    private boolean useLimits = false;
    private int boundsRule = PROPERTY_DOCUMENT_BOUNDS_RULE_MODELSPACE;
    private int outputStyle = PROPERTY_DOCUMENT_OUTPUT_STYLE_NOLAYOUT;
    private String marginSettings;
    private String outputStyleName = DXFConstants.LAYOUT_DEFAULT_NAME;
    protected SVGSAXGeneratorManager manager;

    protected void generate() throws SAXException {
        this.setupProperties();
        this.generateSAX();
        this.context = null;
    }

    protected void setupProperties() {
        if (this.context == null) {
            this.context = new HashMap();
        } else {
            //copy setup from context to 
            //properties
            Iterator i = this.context.keySet().iterator();

            while (i.hasNext()) {
                String key = (String) i.next();
                this.properties.put(key, this.context.get(key));
            }
        }

        // setup the properties

        // the margin
        if (this.properties.containsKey(PROPERTY_MARGIN)) {
            this.marginSettings = (String) this.properties.get(PROPERTY_MARGIN);
        }

        if (this.properties.containsKey(PROPERTY_OVERFLOW)) {
            this.overflow = Boolean.valueOf((String) this.properties.get(
                        PROPERTY_OVERFLOW)).booleanValue();
        }

        if (this.properties.containsKey(PROPERTY_STROKE_WIDTH)) {
            this.context.put(SVGContext.STROKE_WIDTH,
                this.properties.get(PROPERTY_STROKE_WIDTH));
            // set to ignore the draft stroke width
            this.context.put(SVGContext.STROKE_WIDTH_IGNORE, "");
        }

        if (this.properties.containsKey(PROPERTY_DOCUMENT_BOUNDS_RULE)) {
            String value = ((String) this.properties.get(PROPERTY_DOCUMENT_BOUNDS_RULE)).trim();

            if (value.equals(PROPERTY_DOCUMENT_BOUNDS_RULE_KABEJA_VALUE)) {
                //the new default is modelspace now
                this.boundsRule = PROPERTY_DOCUMENT_BOUNDS_RULE_MODELSPACE;
                this.useLimits = false;
            } else if (value.equals(
                        PROPERTY_DOCUMENT_BOUNDS_RULE_PAPERSPACE_VALUE)) {
                this.boundsRule = PROPERTY_DOCUMENT_BOUNDS_RULE_PAPERSPACE;
                this.useLimits = false;
            } else if (value.equals(
                        PROPERTY_DOCUMENT_BOUNDS_RULE_MODELSPACE_VALUE)) {
                this.boundsRule = PROPERTY_DOCUMENT_BOUNDS_RULE_MODELSPACE;
                this.useLimits = false;
            } else if (value.equals(
                        PROPERTY_DOCUMENT_BOUNDS_RULE_MODELSPACE_LIMITS_VALUE)) {
                this.boundsRule = PROPERTY_DOCUMENT_BOUNDS_RULE_MODELSPACE;
                this.useLimits = true;
            } else if (value.equals(
                        PROPERTY_DOCUMENT_BOUNDS_RULE_PAPERSPACE_LIMITS_VALUE)) {
                this.boundsRule = PROPERTY_DOCUMENT_BOUNDS_RULE_PAPERSPACE;
                this.useLimits = true;
            }
        }

        if (this.properties.containsKey(PROPERTY_DOCUMENT_OUTPUT_STYLE)) {
            String value = ((String) this.properties
                            .get(PROPERTY_DOCUMENT_OUTPUT_STYLE)).trim()
                            .toLowerCase();

            if (value.equals(PROPERTY_DOCUMENT_OUTPUT_STYLE_LAYOUT_VALUE)) {
                this.outputStyle = PROPERTY_DOCUMENT_OUTPUT_STYLE_LAYOUT;
            } else if (value.equals(
                        PROPERTY_DOCUMENT_OUTPUT_STYLE_PLOTSETTING_VALUE)) {
                this.outputStyle = PROPERTY_DOCUMENT_OUTPUT_STYLE_PLOTSETTING;
            }

            if (this.properties.containsKey(PROPERTY_DOCUMENT_OUTPUT_STYLE_NAME)) {
                this.outputStyleName = ((String) this.properties.get(PROPERTY_DOCUMENT_OUTPUT_STYLE_NAME)).trim();
            }
        }

        if (this.manager == null) {
            this.manager = new SVGSAXGeneratorManager();
        }

        this.context.put(SVGContext.SVGSAXGENERATOR_MANAGER, manager);
    }

    private void generateSAX() throws SAXException {
        try {
            this.handler.startDocument();

            AttributesImpl attr = new AttributesImpl();

            String viewport = "";
            Bounds bounds = this.getBounds();

            // set the height and width from properties or layout settings
            if (this.outputStyle == PROPERTY_DOCUMENT_OUTPUT_STYLE_NOLAYOUT) {
                if (this.properties.containsKey(PROPERTY_WIDTH)) {
                    SVGUtils.addAttribute(attr,
                        SVGConstants.SVG_ATTRIBUTE_WIDTH,
                        (String) this.properties.get(PROPERTY_WIDTH));
                }

                if (this.properties.containsKey(PROPERTY_HEIGHT)) {
                    SVGUtils.addAttribute(attr,
                        SVGConstants.SVG_ATTRIBUTE_HEIGHT,
                        (String) this.properties.get(PROPERTY_HEIGHT));
                }
            } else if (this.outputStyle == PROPERTY_DOCUMENT_OUTPUT_STYLE_LAYOUT) {
                // check for a layout and get the papersize
                DXFDictionary dict = (DXFDictionary) this.doc.getRootDXFDictionary()
                                                             .getDXFObjectByName(DXFConstants.DICTIONARY_KEY_LAYOUT);

                if (dict != null) {
                    DXFLayout layout = (DXFLayout) dict.getDXFObjectByName(this.outputStyleName);

                    if (layout != null) {
                        Bounds paper = layout.getLimits();

                        // get the units of the paper
                        String units = "";

                        switch (layout.getPaperUnit()) {
                        case DXFConstants.PAPER_UNIT_INCH:
                            units = "in";

                            break;

                        case DXFConstants.PAPER_UNIT_MILLIMETER:
                            units = "mm";

                            break;

                        case DXFConstants.PAPER_UNIT_PIXEL:
                            units = "px";

                            break;
                        }

                        if (paper.isValid() && (paper.getWidth() > 0) &&
                                (paper.getHeight() > 0)) {
                            SVGUtils.addAttribute(attr,
                                SVGConstants.SVG_ATTRIBUTE_HEIGHT,
                                "" + paper.getHeight() + units);
                            SVGUtils.addAttribute(attr,
                                SVGConstants.SVG_ATTRIBUTE_WIDTH,
                                "" + paper.getWidth() + units);
                        }

                        // check for the bounds
                        // Note this value could be false
                        Bounds b = layout.getExtent();

                        if (b.isValid() && (b.getWidth() > 0) &&
                                (b.getHeight() > 0)) {
                            bounds = b;
                        }
                    }
                }
            }

            // add the viewport
            // with margin
            // this is important otherwise in most cases
            // the SVG-Viewer will not show the content
            viewport = SVGUtils.formatNumberAttribute(bounds.getMinimumX()) +
                " " +
                SVGUtils.formatNumberAttribute((-1 * bounds.getMaximumY())) +
                "  " + SVGUtils.formatNumberAttribute(bounds.getWidth()) + " " +
                SVGUtils.formatNumberAttribute(bounds.getHeight());

            SVGUtils.addAttribute(attr, "viewBox", viewport);

            // set the default namespace
            SVGUtils.addAttribute(attr, "xmlns", SVGConstants.SVG_NAMESPACE);

            // the version of SVG we generate now
            SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_VERSION,
                SUPPORTED_SVG_VERSION);

            // the overflow
            if (this.overflow) {
                SVGUtils.addAttribute(attr,
                    SVGConstants.SVG_ATTRIBUTE_OVERFLOW,
                    SVGConstants.SVG_ATTRIBUTEVALUE_VISIBLE);
            }

            SVGUtils.startElement(this.handler, SVGConstants.SVG_ROOT, attr);

            // the blocks as symbol in the defs-section of SVG
            attr = new AttributesImpl();
            SVGUtils.startElement(this.handler, SVGConstants.SVG_DEFS, attr);

            // set the context
            context.put(SVGContext.DRAFT_BOUNDS, bounds);

            double dotLength = 0.0;

            if (bounds.getWidth() > bounds.getHeight()) {
                dotLength = bounds.getHeight() * SVGConstants.DEFAULT_STROKE_WIDTH_PERCENT;
            } else {
                dotLength = bounds.getWidth() * SVGConstants.DEFAULT_STROKE_WIDTH_PERCENT;
            }

            context.put(SVGContext.DOT_LENGTH, new Double(dotLength));

            Iterator i = this.doc.getDXFBlockIterator();

            while (i.hasNext()) {
                DXFBlock block = (DXFBlock) i.next();
                this.blockToSAX(block, null);
            }

            // maybe there is a fontdescription available from DXFStyle
            i = this.doc.getDXFStyleIterator();

            while (i.hasNext()) {
                DXFStyle style = (DXFStyle) i.next();
                SVGStyleGenerator.toSAX(handler, context, style);
            }

            SVGUtils.endElement(handler, SVGConstants.SVG_DEFS);

            // the draft
            attr = new AttributesImpl();
            SVGUtils.addAttribute(attr, SVGConstants.XML_ID, "draft");

            // the globale coordinate system transformation
            // note: DXF has the y-axis positiv from bottom to top
            // SVG has the y-axis positiv from top to bottom
            SVGUtils.addAttribute(attr, "transform", "matrix(1 0 0 -1 0 0)");

            // the stroke-width
            if (this.context.containsKey(SVGContext.STROKE_WIDTH)) {
                // the user has setup a stroke-width
                SVGUtils.addAttribute(attr,
                    SVGConstants.SVG_ATTRIBUTE_STROKE_WITDH,
                    "" + this.context.get(SVGContext.STROKE_WIDTH));
            } else {
                double sw = (bounds.getWidth() + bounds.getHeight()) / 2 * SVGConstants.DEFAULT_STROKE_WIDTH_PERCENT;
                double defaultSW = ((double) DXFConstants.ENVIRONMENT_VARIABLE_LWDEFAULT) / 100.0;

                if (sw > defaultSW) {
                    sw = defaultSW;
                }

                SVGUtils.addAttribute(attr,
                    SVGConstants.SVG_ATTRIBUTE_STROKE_WITDH,
                    SVGUtils.formatNumberAttribute(sw));
                this.context.put(SVGContext.STROKE_WIDTH, new Double(sw));
            }

            SVGUtils.startElement(handler, SVGConstants.SVG_GROUP, attr);

            // the layers as container g-elements
            i = this.doc.getDXFLayerIterator();

            while (i.hasNext()) {
                DXFLayer layer = (DXFLayer) i.next();

                if (this.boundsRule == PROPERTY_DOCUMENT_BOUNDS_RULE_PAPERSPACE) {
                    //out put only the paper space maybe with views to 
                    //model space
                    this.layerToSAX(layer, false);
                } else {
                    //output only the model space -> the default
                    this.layerToSAX(layer, true);
                }
            }

            SVGUtils.endElement(handler, SVGConstants.SVG_GROUP);
            SVGUtils.endElement(handler, SVGConstants.SVG_ROOT);
            handler.endDocument();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    protected void blockToSAX(DXFBlock block, TransformContext transformContext)
        throws SAXException {
        AttributesImpl attr = new AttributesImpl();
        SVGUtils.addAttribute(attr, SVGConstants.XML_ID,
            SVGUtils.validateID(block.getName()));

        SVGUtils.startElement(handler, SVGConstants.SVG_GROUP, attr);

        Iterator i = block.getDXFEntitiesIterator();

        while (i.hasNext()) {
            DXFEntity entity = (DXFEntity) i.next();

            try {
                SVGSAXGenerator gen = manager.getSVGGenerator(entity.getType());
                gen.toSAX(handler, this.context, entity, transformContext);
            } catch (SVGGenerationException e) {
                e.printStackTrace();
            }
        }

        SVGUtils.endElement(handler, SVGConstants.SVG_GROUP);
    }

    /**
     * Returns the margin-array where:
     * <ul>
     * <li>0 ->top margin</li>
     * <li>1 ->right margin</li>
     * <li>2 ->bottom margin</li>
     * <li>3 ->left margin</li>
     * </ul>
     *
     * @param bounds
     * @return
     */
    protected double[] getMargin(Bounds bounds) {
        double[] margin = new double[4];

        if (this.marginSettings != null) {
            StringTokenizer st = new StringTokenizer(this.marginSettings);
            int count = st.countTokens();

            switch (count) {
            case 4:

                for (int i = 0; i < count; i++) {
                    String m = st.nextToken().trim();

                    if (m.endsWith("%")) {
                        m = m.substring(0, m.length() - 1);

                        if ((i == 0) && (i == 2)) {
                            margin[i] = (Double.parseDouble(m) / 100) * bounds.getHeight();
                        } else {
                            margin[i] = (Double.parseDouble(m) / 100) * bounds.getWidth();
                        }
                    } else {
                        margin[i] = Double.parseDouble(m);
                    }
                }

                return margin;

            case 1:

                String m = st.nextToken().trim();

                if (m.endsWith("%")) {
                    m = m.substring(0, m.length() - 1);
                }

                margin[0] = Double.parseDouble(m);
                margin[1] = margin[2] = margin[3] = margin[0];

                return margin;
            }
        }

        margin[0] = bounds.getHeight() * (DEFAULT_MARGIN_PERCENT / 100);
        margin[2] = margin[0];
        margin[1] = bounds.getWidth() * (DEFAULT_MARGIN_PERCENT / 100);
        margin[3] = margin[1];

        return margin;
    }

    protected Bounds getBounds() {
        Bounds bounds = null;

        if (this.boundsRule == PROPERTY_DOCUMENT_BOUNDS_RULE_PAPERSPACE) {
            // first the user based limits of the paperspace
            bounds = new Bounds();

            if (this.doc.getDXFHeader()
                            .hasVariable(DXFConstants.HEADER_VARIABLE_PEXTMAX) &&
                    this.doc.getDXFHeader()
                                .hasVariable(DXFConstants.HEADER_VARIABLE_PEXTMIN) &&
                    useLimits) {
                DXFVariable min = this.doc.getDXFHeader()
                                          .getVariable(DXFConstants.HEADER_VARIABLE_PEXTMIN);
                DXFVariable max = this.doc.getDXFHeader()
                                          .getVariable(DXFConstants.HEADER_VARIABLE_PEXTMAX);

                bounds.setMinimumX(min.getDoubleValue("10"));
                bounds.setMinimumY(min.getDoubleValue("20"));
                bounds.setMaximumX(max.getDoubleValue("10"));
                bounds.setMaximumY(max.getDoubleValue("20"));
            }

            if ((!bounds.isValid() || (bounds.getWidth() == 0.0) ||
                    (bounds.getHeight() == 0.0)) &&
                    this.doc.getDXFHeader()
                                .hasVariable(DXFConstants.HEADER_VARIABLE_PLIMMIN) &&
                    this.doc.getDXFHeader()
                                .hasVariable(DXFConstants.HEADER_VARIABLE_PLIMMAX) &&
                    useLimits) {
                DXFVariable min = this.doc.getDXFHeader()
                                          .getVariable(DXFConstants.HEADER_VARIABLE_PLIMMIN);
                DXFVariable max = this.doc.getDXFHeader()
                                          .getVariable(DXFConstants.HEADER_VARIABLE_PLIMMAX);

                bounds.setMinimumX(min.getDoubleValue("10"));
                bounds.setMinimumY(min.getDoubleValue("20"));
                bounds.setMaximumX(max.getDoubleValue("10"));
                bounds.setMaximumY(max.getDoubleValue("20"));
            }

            if (!bounds.isValid() || (bounds.getWidth() == 0.0) ||
                    (bounds.getHeight() == 0.0)) {
                //get bounds only from paper space entities 
                bounds = this.doc.getBounds(false);
            }
        } else if (this.boundsRule == PROPERTY_DOCUMENT_BOUNDS_RULE_MODELSPACE) {
            // first the user based limits of the modelspace
            bounds = new Bounds();

            if (this.doc.getDXFHeader()
                            .hasVariable(DXFConstants.HEADER_VARIABLE_EXTMIN) &&
                    this.doc.getDXFHeader()
                                .hasVariable(DXFConstants.HEADER_VARIABLE_EXTMAX) &&
                    useLimits) {
                DXFVariable min = this.doc.getDXFHeader()
                                          .getVariable(DXFConstants.HEADER_VARIABLE_EXTMIN);
                DXFVariable max = this.doc.getDXFHeader()
                                          .getVariable(DXFConstants.HEADER_VARIABLE_EXTMAX);

                bounds.setMinimumX(min.getDoubleValue("10"));
                bounds.setMinimumY(min.getDoubleValue("20"));
                bounds.setMaximumX(max.getDoubleValue("10"));
                bounds.setMaximumY(max.getDoubleValue("20"));
            }

            if ((!bounds.isValid() || (bounds.getWidth() == 0.0) ||
                    (bounds.getHeight() == 0.0)) &&
                    this.doc.getDXFHeader()
                                .hasVariable(DXFConstants.HEADER_VARIABLE_LIMMIN) &&
                    this.doc.getDXFHeader()
                                .hasVariable(DXFConstants.HEADER_VARIABLE_LIMMAX) &&
                    useLimits) {
                DXFVariable min = this.doc.getDXFHeader()
                                          .getVariable(DXFConstants.HEADER_VARIABLE_LIMMIN);
                DXFVariable max = this.doc.getDXFHeader()
                                          .getVariable(DXFConstants.HEADER_VARIABLE_LIMMAX);

                bounds.setMinimumX(min.getDoubleValue("10"));
                bounds.setMinimumY(min.getDoubleValue("20"));
                bounds.setMaximumX(max.getDoubleValue("10"));
                bounds.setMaximumY(max.getDoubleValue("20"));
            }

            if (!bounds.isValid() || (bounds.getWidth() == 0.0) ||
                    (bounds.getHeight() == 0.0)) {
                //get bounds only from model space entities
                bounds = this.doc.getBounds(true);
            }
        }

        if ((bounds == null) || !bounds.isValid() ||
                (bounds.getWidth() == 0.0) || (bounds.getHeight() == 0.0)) {
            if (this.boundsRule == PROPERTY_DOCUMENT_BOUNDS_RULE_PAPERSPACE) {
                bounds = this.doc.getBounds(true);
                this.boundsRule = PROPERTY_DOCUMENT_BOUNDS_RULE_MODELSPACE;
            } else {
                bounds = this.doc.getBounds(false);
                this.boundsRule = PROPERTY_DOCUMENT_BOUNDS_RULE_PAPERSPACE;
            }
        }

        // set a margin
        double[] margin = this.getMargin(bounds);
        bounds.setMinimumX(bounds.getMinimumX() - margin[3]);
        bounds.setMaximumX(bounds.getMaximumX() + margin[1]);
        bounds.setMinimumY(bounds.getMinimumY() - margin[2]);
        bounds.setMaximumY(bounds.getMaximumY() + margin[0]);

        return bounds;
    }

    public void setSVGSAXGeneratorManager(SVGSAXGeneratorManager manager) {
        this.manager = manager;
    }

    protected void layerToSAX(DXFLayer layer, boolean onModelspace)
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

        if (!layer.isVisible() && onModelspace) {
            SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_VISIBILITY,
                SVGConstants.SVG_ATTRIBUTE_VISIBILITY_VALUE_HIDDEN);
        }

        String lt = layer.getLineType();

        if (lt.length() > 0) {
            DXFLineType ltype = doc.getDXFLineType(lt);
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

            try {
                SVGSAXGenerator gen = this.manager.getSVGGenerator(type);

                Iterator i = list.iterator();

                while (i.hasNext()) {
                    DXFEntity entity = (DXFEntity) i.next();
                    boolean v = entity.isVisibile();
                    entity.setVisibile(!layer.isFrozen());

                    if (!onModelspace) {
                        entity.setVisibile(layer.isVisible());
                    }

                    if ((onModelspace && entity.isModelSpace()) ||
                            (!onModelspace && !entity.isModelSpace())) {
                        gen.toSAX(handler, context, entity, null);
                    }

                    //restore back the flag
                    entity.setVisibile(v);
                }
            } catch (SVGGenerationException e) {
                e.printStackTrace();
            }
        }

        SVGUtils.endElement(handler, SVGConstants.SVG_GROUP);
    }
}
