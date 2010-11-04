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

import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.kabeja.common.Block;
import org.kabeja.common.Color;
import org.kabeja.common.DraftEntity;
import org.kabeja.common.Layer;
import org.kabeja.common.LineWidth;
import org.kabeja.common.Style;
import org.kabeja.common.Type;
import org.kabeja.common.Variable;
import org.kabeja.entities.util.Utils;
import org.kabeja.math.Bounds;
import org.kabeja.math.TransformContext;
import org.kabeja.objects.Dictionary;
import org.kabeja.objects.Layout;
import org.kabeja.svg.generators.SVGStyleGenerator;
import org.kabeja.util.Constants;
import org.kabeja.xml.AbstractSAXGenerator;
import org.kabeja.xml.XMLConstants;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class SVGGenerator extends AbstractSAXGenerator {
    public final static String PROPERTY_MARGIN = "margin";

    public final static String PROPERTY_STROKE_WIDTH = "stroke-width";

    public final static String PROPERTY_STROKE_WIDTH_TYPE = "stroke-width-type";

    public final static String PROPERTY_STROKE_WIDTH_TYPE_VALUE_PERCENT = "percent";

    public final static String PROPERTY_STROKE_WIDTH_TYPE_VALUE_LINEWEIGHT = "lineweight";

    public final static String PROPERTY_STROKE_WIDTH_TYPE_VALUE_LINEWIDTH = "linewidth";

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

    public final static String PROPERTY_DOCUMENT_BOUNDS_RULE_PAPERSPACE_LIMITS_VALUE = "Paperspace-Limits";

    public final static String PROPERTY_DOCUMENT_BOUNDS_RULE_MODELSPACE_VALUE = "Modelspace";

    public final static String PROPERTY_DOCUMENT_BOUNDS_RULE_MODELSPACE_LIMITS_VALUE = "Modelspace-Limits";

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

    private String outputStyleName = Constants.LAYOUT_DEFAULT_NAME;

    protected SVGSAXGeneratorManager manager;

    protected boolean useModelSpaceBlock = false;

    protected boolean usePaperSpaceBlock = false;

    protected void generate() throws SAXException {
        this.setupProperties();
        this.generateSAX();
        this.context = null;
    }

    protected void setupProperties() {

        if (this.context == null) {
            this.context = new HashMap<String,Object>();
        } 

        // setup the properties

        // the margin
        if (this.properties.containsKey(PROPERTY_MARGIN)) {
            this.marginSettings =  this.properties.get(PROPERTY_MARGIN);
        }

        if (this.properties.containsKey(PROPERTY_OVERFLOW)) {
            this.overflow = Boolean.valueOf(
                    this.properties.get(PROPERTY_OVERFLOW))
                    .booleanValue();
        }

        // parse the line width property
        if (this.properties.containsKey(PROPERTY_STROKE_WIDTH)) {
            LineWidth lw = new LineWidth();
            String strokeWidth =  this.properties
                    .get(PROPERTY_STROKE_WIDTH);

            lw.setValue(Double.parseDouble(strokeWidth));

            String linewidthType = this.properties
                    .get(PROPERTY_STROKE_WIDTH_TYPE);
            if (PROPERTY_STROKE_WIDTH_TYPE_VALUE_PERCENT.equals(linewidthType)) {
                lw.setType(LineWidth.TYPE_PERCENT);

            } else if (PROPERTY_STROKE_WIDTH_TYPE_VALUE_LINEWEIGHT
                    .equals(linewidthType)) {
                lw.setType(LineWidth.TYPE_LINE_WEIGHT);

            } else {
                lw.setType(LineWidth.TYPE_LINE_WIDTH);
            }
            this.context.put(SVGContext.LINE_WIDTH, lw);
            // set to ignore the draft stroke width
            // this.context.put(SVGContext.DRAFT_STROKE_WIDTH_IGNORE, "");
        }

        if (this.properties.containsKey(PROPERTY_DOCUMENT_BOUNDS_RULE)) {
            String value = ((String) this.properties
                    .get(PROPERTY_DOCUMENT_BOUNDS_RULE)).trim();

            if (value.equals(PROPERTY_DOCUMENT_BOUNDS_RULE_KABEJA_VALUE)) {
                // the new default is modelspace now
                this.boundsRule = PROPERTY_DOCUMENT_BOUNDS_RULE_MODELSPACE;
                this.useLimits = false;
            } else if (value
                    .equals(PROPERTY_DOCUMENT_BOUNDS_RULE_PAPERSPACE_VALUE)) {
                this.boundsRule = PROPERTY_DOCUMENT_BOUNDS_RULE_PAPERSPACE;
                this.useLimits = false;
            } else if (value
                    .equals(PROPERTY_DOCUMENT_BOUNDS_RULE_MODELSPACE_VALUE)) {
                this.boundsRule = PROPERTY_DOCUMENT_BOUNDS_RULE_MODELSPACE;
                this.useLimits = false;
            } else if (value
                    .equals(PROPERTY_DOCUMENT_BOUNDS_RULE_MODELSPACE_LIMITS_VALUE)) {
                this.boundsRule = PROPERTY_DOCUMENT_BOUNDS_RULE_MODELSPACE;
                this.useLimits = true;
            } else if (value
                    .equals(PROPERTY_DOCUMENT_BOUNDS_RULE_PAPERSPACE_LIMITS_VALUE)) {
                this.boundsRule = PROPERTY_DOCUMENT_BOUNDS_RULE_PAPERSPACE;
                this.useLimits = true;
            }
        }

        if (this.properties.containsKey(PROPERTY_DOCUMENT_OUTPUT_STYLE)) {
            String value = ((String) this.properties
                    .get(PROPERTY_DOCUMENT_OUTPUT_STYLE)).trim().toLowerCase();

            if (value.equals(PROPERTY_DOCUMENT_OUTPUT_STYLE_LAYOUT_VALUE)) {
                this.outputStyle = PROPERTY_DOCUMENT_OUTPUT_STYLE_LAYOUT;
            } else if (value
                    .equals(PROPERTY_DOCUMENT_OUTPUT_STYLE_PLOTSETTING_VALUE)) {
                this.outputStyle = PROPERTY_DOCUMENT_OUTPUT_STYLE_PLOTSETTING;
            }

            if (this.properties
                    .containsKey(PROPERTY_DOCUMENT_OUTPUT_STYLE_NAME)) {
                this.outputStyleName = ((String) this.properties
                        .get(PROPERTY_DOCUMENT_OUTPUT_STYLE_NAME)).trim();
            }
        }

        if (this.manager == null) {
            this.manager = new SVGSAXGeneratorManager();
        }

        this.context.put(SVGContext.SVGSAXGENERATOR_MANAGER, manager);

        // setup some flags
        this.useModelSpaceBlock = false;
        this.usePaperSpaceBlock = false;
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
                Dictionary dict = (Dictionary) this.doc
                        .getRootDictionary().getObjectByName(
                                Constants.DICTIONARY_KEY_LAYOUT);

                if (dict != null) {
                    Layout layout = (Layout) dict
                            .getObjectByName(this.outputStyleName);

                    if (layout != null) {
                        Bounds paper = layout.getLimits();

                        // get the units of the paper
                        String units = "";

                        switch (layout.getPaperUnit()) {
                        case Constants.PAPER_UNIT_INCH:
                            units = "in";

                            break;

                        case Constants.PAPER_UNIT_MILLIMETER:
                            units = "mm";

                            break;

                        case Constants.PAPER_UNIT_PIXEL:
                            units = "px";

                            break;
                        }

                        if (paper.isValid() && (paper.getWidth() > 0)
                                && (paper.getHeight() > 0)) {
                            SVGUtils.addAttribute(attr,
                                    SVGConstants.SVG_ATTRIBUTE_HEIGHT, ""
                                            + paper.getHeight() + units);
                            SVGUtils.addAttribute(attr,
                                    SVGConstants.SVG_ATTRIBUTE_WIDTH, ""
                                            + paper.getWidth() + units);
                        }

                        // check for the bounds
                        // Note this value could be false
                        Bounds b = layout.getExtent();

                        if (b.isValid() && (b.getWidth() > 0)
                                && (b.getHeight() > 0)) {
                            bounds = b;
                        }
                    }
                }
            }

            // add the viewport
            // with margin
            // this is important otherwise in most cases
            // the SVG-Viewer will not show the content
            viewport = SVGUtils.formatNumberAttribute(bounds.getMinimumX())
                    + " "
                    + SVGUtils
                            .formatNumberAttribute((-1 * bounds.getMaximumY()))
                    + "  " + SVGUtils.formatNumberAttribute(bounds.getWidth())
                    + " " + SVGUtils.formatNumberAttribute(bounds.getHeight());

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

            // add Kabeja namespace for additional kabeja output
            attr.addAttribute(SVGConstants.XMLNS_NAMESPACE,
                    XMLConstants.KABEJA_NAMESPACE_PREFIX, "xmlns:"
                            + XMLConstants.KABEJA_NAMESPACE_PREFIX, "CDATA",
                    XMLConstants.KABEJA_NAMESPACE);

            SVGUtils.startElement(this.handler, SVGConstants.SVG_ROOT, attr);

            // the blocks as symbol in the defs-section of SVG
            attr = new AttributesImpl();
            SVGUtils.startElement(this.handler, SVGConstants.SVG_DEFS, attr);

            // set the context
            context.put(SVGContext.DRAFT_BOUNDS, bounds);

            double dotLength = 0.0;

            if (bounds.getWidth() > bounds.getHeight()) {
                dotLength = bounds.getHeight()
                        * SVGConstants.DEFAULT_STROKE_WIDTH_PERCENT;
            } else {
                dotLength = bounds.getWidth()
                        * SVGConstants.DEFAULT_STROKE_WIDTH_PERCENT;
            }

            context.put(SVGContext.DOT_LENGTH, new Double(dotLength));

           for(Block block :doc.getBlocks()){
                this.blockToSAX(block, null);
            }

            // maybe there is a fontdescription available from DXFStyle
           for(Style style:doc.getStyles()){
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
            if (this.context.containsKey(SVGContext.LINE_WIDTH)) {
                // the user has setup a stroke-width
                SVGUtils
                        .addAttribute(
                                attr,
                                SVGConstants.SVG_ATTRIBUTE_STROKE_WITDH,
                                SVGUtils
                                        .lineWidthToStrokeWidth((LineWidth) this.context
                                                .get(SVGContext.LINE_WIDTH)));
            } else {
                double sw = (bounds.getWidth() + bounds.getHeight()) / 2
                        * SVGConstants.DEFAULT_STROKE_WIDTH_PERCENT;
                double defaultSW = ((double) Constants.ENVIRONMENT_VARIABLE_LWDEFAULT) / 100.0;

                if (sw > defaultSW) {
                    sw = defaultSW;
                }

                LineWidth lw = new LineWidth();
                lw.setType(LineWidth.TYPE_LINE_WIDTH);
                lw.setValue(sw);

                SVGUtils.addAttribute(attr,
                        SVGConstants.SVG_ATTRIBUTE_STROKE_WITDH, SVGUtils
                                .lineWidthToStrokeWidth(lw));

                this.context.put(SVGContext.LINE_WIDTH, lw);
            }

            SVGUtils.startElement(handler, SVGConstants.SVG_GROUP, attr);

            if (this.useModelSpaceBlock) {
                Block block = this.doc
                        .getBlock(Constants.BLOCK_MODELSPACE);
                this.blockToSAX(block, null);
            } else if (this.usePaperSpaceBlock) {
                Block block = this.doc
                        .getBlock(Constants.BLOCK_PAPERSPACE);
                this.blockToSAX(block, null);
            } else {

                // the layers as container g-elements
                Iterator<Layer> i = Utils.sortedLayersByZIndexIterator(this.doc
                        .getLayers().iterator());
                while (i.hasNext()) {
                    Layer layer = (Layer) i.next();
                    if (this.boundsRule == PROPERTY_DOCUMENT_BOUNDS_RULE_PAPERSPACE) {
                        // out put only the paper space maybe with views to
                        // model space
                        this.layerToSAX(layer, false);
                    } else {
                        // output only the model space -> the default
                        this.layerToSAX(layer, true);
                    }
                }
            }

            SVGUtils.endElement(handler, SVGConstants.SVG_GROUP);
            SVGUtils.endElement(handler, SVGConstants.SVG_ROOT);
            handler.endDocument();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    protected void blockToSAX(Block block, TransformContext transformContext)
            throws SAXException {
        AttributesImpl attr = new AttributesImpl();
        // SVGUtils.addAttribute(attr, SVGConstants.XML_ID, SVGUtils
        // .validateID(block.getName()));
        SVGUtils.addAttribute(attr, SVGConstants.XML_ID, SVGUtils
                .toValidateID(block.getID()));

        SVGUtils.startElement(handler, SVGConstants.SVG_GROUP, attr);

     for( DraftEntity entity:block.getEntities()){

            try {
                SVGSAXGenerator gen = manager.getSVGGenerator(entity.getType().getHandle());
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
                            margin[i] = (Double.parseDouble(m) / 100)
                                    * bounds.getHeight();
                        } else {
                            margin[i] = (Double.parseDouble(m) / 100)
                                    * bounds.getWidth();
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

            if (this.doc.getHeader().hasVariable(
                    Constants.HEADER_VARIABLE_PEXTMAX)
                    && this.doc.getHeader().hasVariable(
                            Constants.HEADER_VARIABLE_PEXTMIN) && useLimits) {
                Variable min = this.doc.getHeader().getVariable(
                        Constants.HEADER_VARIABLE_PEXTMIN);
                Variable max = this.doc.getHeader().getVariable(
                        Constants.HEADER_VARIABLE_PEXTMAX);

                bounds.setMinimumX(min.getDoubleValue("10"));
                bounds.setMinimumY(min.getDoubleValue("20"));
                bounds.setMaximumX(max.getDoubleValue("10"));
                bounds.setMaximumY(max.getDoubleValue("20"));
            }

            if ((!bounds.isValid() || (bounds.getWidth() == 0.0) || (bounds
                    .getHeight() == 0.0))
                    && this.doc.getHeader().hasVariable(
                            Constants.HEADER_VARIABLE_PLIMMIN)
                    && this.doc.getHeader().hasVariable(
                            Constants.HEADER_VARIABLE_PLIMMAX) && useLimits) {
                Variable min = this.doc.getHeader().getVariable(
                        Constants.HEADER_VARIABLE_PLIMMIN);
                Variable max = this.doc.getHeader().getVariable(
                        Constants.HEADER_VARIABLE_PLIMMAX);

                bounds.setMinimumX(min.getDoubleValue("10"));
                bounds.setMinimumY(min.getDoubleValue("20"));
                bounds.setMaximumX(max.getDoubleValue("10"));
                bounds.setMaximumY(max.getDoubleValue("20"));
            }

            if (!bounds.isValid() || (bounds.getWidth() == 0.0)
                    || (bounds.getHeight() == 0.0)) {
                // get bounds only from paper space entities
                bounds = this.doc.getBounds(false);
            }
        } else if (this.boundsRule == PROPERTY_DOCUMENT_BOUNDS_RULE_MODELSPACE) {
            // first the user based limits of the modelspace
            bounds = new Bounds();

            if (this.doc.getHeader().hasVariable(
                    Constants.HEADER_VARIABLE_EXTMIN)
                    && this.doc.getHeader().hasVariable(
                            Constants.HEADER_VARIABLE_EXTMAX) && useLimits) {
                Variable min = this.doc.getHeader().getVariable(
                        Constants.HEADER_VARIABLE_EXTMIN);
                Variable max = this.doc.getHeader().getVariable(
                        Constants.HEADER_VARIABLE_EXTMAX);

                bounds.setMinimumX(min.getDoubleValue("10"));
                bounds.setMinimumY(min.getDoubleValue("20"));
                bounds.setMaximumX(max.getDoubleValue("10"));
                bounds.setMaximumY(max.getDoubleValue("20"));
            }

            if ((!bounds.isValid() || (bounds.getWidth() == 0.0) || (bounds
                    .getHeight() == 0.0))
                    && this.doc.getHeader().hasVariable(
                            Constants.HEADER_VARIABLE_LIMMIN)
                    && this.doc.getHeader().hasVariable(
                            Constants.HEADER_VARIABLE_LIMMAX) && useLimits) {
                Variable min = this.doc.getHeader().getVariable(
                        Constants.HEADER_VARIABLE_LIMMIN);
                Variable max = this.doc.getHeader().getVariable(
                        Constants.HEADER_VARIABLE_LIMMAX);

                bounds.setMinimumX(min.getDoubleValue("10"));
                bounds.setMinimumY(min.getDoubleValue("20"));
                bounds.setMaximumX(max.getDoubleValue("10"));
                bounds.setMaximumY(max.getDoubleValue("20"));
            }

            if (!bounds.isValid() || (bounds.getWidth() == 0.0)
                    || (bounds.getHeight() == 0.0)) {
                // get bounds only from model space entities
                bounds = this.doc.getBounds(true);
            }
        }

        if ((bounds == null) || !bounds.isValid() || (bounds.getWidth() == 0.0)
                || (bounds.getHeight() == 0.0)) {
            if (this.boundsRule == PROPERTY_DOCUMENT_BOUNDS_RULE_PAPERSPACE) {
                bounds = this.doc.getBounds(true);
                this.boundsRule = PROPERTY_DOCUMENT_BOUNDS_RULE_MODELSPACE;
            } else {
                bounds = this.doc.getBounds(false);
                this.boundsRule = PROPERTY_DOCUMENT_BOUNDS_RULE_PAPERSPACE;
            }
        }

        // check for model space block
        if (!bounds.isValid()) {
            Block block = this.doc
                    .getBlock(Constants.BLOCK_MODELSPACE);
            if (block != null) {
                bounds = block.getBounds();
                this.useModelSpaceBlock = true;
            }
        }

        // check for the paper space block
        if (!bounds.isValid()) {
            Block block = this.doc
                    .getBlock(Constants.BLOCK_PAPERSPACE);
            if (block != null) {
                bounds = block.getBounds();
                this.usePaperSpaceBlock = true;
            }
        }

        // last check if nothing is correct
        // this happens on empty drafts
        // or only drafts with blocks
        if (!bounds.isValid()) {
            bounds.addToBounds(0, 0, 0);
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

    protected void layerToSAX(Layer layer, boolean onModelspace)
            throws SAXException {
        AttributesImpl attr = new AttributesImpl();

        attr.addAttribute(XMLConstants.KABEJA_NAMESPACE,
                XMLConstants.KABEJA_ATTRIBUTE_LAYER_NAME,
                XMLConstants.KABEJA_QNAME_ATTRIBUTE_LAYER_NAME, "CDATA", layer
                        .getName());

        // the layer name may not be a valid ID, so we omit this part now
        // SVGUtils.addAttribute(attr, SVGConstants.XML_ID, SVGUtils
        // .validateID(layer.getName()));

        SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_COLOR,
                Color.getRGBString(Math.abs(layer.getColor())));
        SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_STROKE,
                SVGConstants.SVG_ATTRIBUTE_VALUE_CURRENTCOLOR);

        SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_FILL,
                SVGConstants.SVG_ATTRIBUTE_FILL_VALUE_NONE);

        if (!layer.isVisible() && onModelspace) {
            SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_VISIBILITY,
                    SVGConstants.SVG_ATTRIBUTE_VISIBILITY_VALUE_HIDDEN);
        }

      
        SVGUtils.addStrokeDashArrayAttribute(attr, layer.getLineType());
       

        // the stroke-width
        int lineWeight = layer.getLineWeight();

        // the stroke-width
        LineWidth lw = new LineWidth();
        if ((lineWeight > 0)
                && !context.containsKey(SVGContext.DRAFT_STROKE_WIDTH_IGNORE)) {
            lw.setType(LineWidth.TYPE_LINE_WEIGHT);
            lw.setValue(lineWeight);

        } else {
            lw = (LineWidth) context.get(SVGContext.LINE_WIDTH);
        }
        SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_STROKE_WITDH,
                SVGUtils.lineWidthToStrokeWidth(lw));
        context.put(SVGContext.LAYER_STROKE_WIDTH, lw);

        SVGUtils.startElement(handler, SVGConstants.SVG_GROUP, attr);
        SVGUtils.startElement(handler, SVGConstants.SVG_TITLE,
                new AttributesImpl());
        SVGUtils.characters(handler, layer.getName());
        SVGUtils.endElement(handler, SVGConstants.SVG_TITLE);
    for(  Type<? extends DraftEntity> type:layer.getEntityTypes()){
         

            try {
                SVGSAXGenerator gen = this.manager.getSVGGenerator(type.getHandle());
             for(  DraftEntity entity:layer.getEntitiesByType(type)){
                    boolean v = entity.isVisibile();
                    entity.setVisibile(!layer.isFrozen());

                    if (!onModelspace) {
                        entity.setVisibile(layer.isVisible());
                    }

                    if ((onModelspace && entity.isModelSpace())
                            || (!onModelspace && !entity.isModelSpace())) {
                        gen.toSAX(handler, context, entity, null);
                    }

                    // restore back the flag
                    entity.setVisibile(v);
                }
            } catch (SVGGenerationException e) {
                e.printStackTrace();
            }
        }

        SVGUtils.endElement(handler, SVGConstants.SVG_GROUP);
    }
}
