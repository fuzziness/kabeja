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
package org.kabeja.dxf;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kabeja.dxf.objects.DXFObject;
import org.kabeja.svg.SVGConstants;
import org.kabeja.svg.SVGContext;
import org.kabeja.svg.SVGSAXGenerator;
import org.kabeja.svg.SVGUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 *
 */
public class DXFDocument implements SVGSAXGenerator {
    public static String PROPERTY_ENCODING = "encoding";
    public static final double DEFAULT_MARGIN = 5;
    private Hashtable layers = new Hashtable();
    private Hashtable blocks = new Hashtable();
    private HashMap lineTypes = new HashMap();
    private HashMap dimensionStyles = new HashMap();
    private HashMap textStyles = new HashMap();

    // the user coordinate systems
    private Hashtable ucs = new Hashtable();
    private Hashtable properties = new Hashtable();
    private List viewports = new ArrayList();
    private Bounds bounds = new Bounds();
    private double margin;
    private DXFHeader header = new DXFHeader();
    private HashMap objects = new HashMap();
    private HashMap patterns = new HashMap();
    private List views = new ArrayList();

    public DXFDocument() {
        // the defalut layer
        DXFLayer defaultLayer = new DXFLayer();
        defaultLayer.setDXFDocument(this);
        defaultLayer.setName(DXFConstants.DEFAULT_LAYER);
        layers.put(DXFConstants.DEFAULT_LAYER, defaultLayer);
        margin = DEFAULT_MARGIN;
    }

    public void addDXFLayer(DXFLayer layer) {
        layers.put(layer.getName(), layer);
    }

    /**
     *
     * @param key
     *            The layer id
     * @return the layer or if not found the default layer (layer "0")
     */
    public DXFLayer getDXFLayer(String key) {
        if (layers.containsKey(key)) {
            return (DXFLayer) layers.get(key);
        }

        // retun the default layer
        return (DXFLayer) layers.get(DXFConstants.DEFAULT_LAYER);
    }

    /**
     *
     * @return the Iterator over all DXFLayer of this document
     */
    public Iterator getDXFLayerIterator() {
        return layers.values().iterator();
    }

    public void addDXFLineType(DXFLineType ltype) {
        lineTypes.put(ltype.getName(), ltype);
    }

    public DXFLineType getDXFLineType(String name) {
        return (DXFLineType) lineTypes.get(name);
    }

    /**
     *
     * @return the iterator over all DXFLineTypes
     */
    public Iterator getDXFLineTypeIterator() {
        return lineTypes.values().iterator();
    }

    public void toSAX(ContentHandler handler, Map svgContext) {
        if (null == svgContext) {
            svgContext = new HashMap();
        }

        generateSAX(handler, svgContext);
    }

    public void addDXFEntity(DXFEntity entity) {
        entity.setDXFDocument(this);
        getDXFLayer(entity.getLayerName()).addDXFEntity(entity);
    }

    public void addDXFBlock(DXFBlock block) {
        block.setDXFDocument(this);
        blocks.put(block.getName(), block);
    }

    public DXFBlock getDXFBlock(String name) {
        return (DXFBlock) blocks.get(name);
    }

    /**
     *
     * @return the iterator over all DXFBlocks
     */
    public Iterator getDXFBlockIterator() {
        return blocks.values().iterator();
    }

    public void setProperty(String key, String value) {
        properties.put(key, value);
    }

    public String getProperty(String key) {
        if (properties.contains(key)) {
            return (String) properties.get(key);
        }

        return null;
    }

    public boolean hasProperty(String key) {
        return properties.containsKey(key);
    }

    private void generateSAX(ContentHandler handler, Map context) {
        try {
            handler.startDocument();

            AttributesImpl attr = new AttributesImpl();

            // add the viewport
            // with margin
            // this is important otherwise in most cases
            // the SVG-Viewer will not show the content
            String viewport = "";
            Bounds bounds = getBounds();

            if (header.hasVariable("$PEXTMAX") &&
                    header.hasVariable("$PEXTMMIN")) {
                DXFVariable min = header.getVariable("$PEXTMIN");
                DXFVariable max = header.getVariable("$PEXTMAX");
                double x = min.getDoubleValue("10");
                double y = min.getDoubleValue("20");
                double max_y = max.getDoubleValue("20");
                double width = max.getDoubleValue("10") - x;
                double height = max_y - y;

                double boundsWidth = bounds.getWidth();

                //we set a limit here and use the bounds instead
                if ((width <= (boundsWidth * 2)) && (width > 0)) {
                    viewport = "" + x + " " + ((-1.0) * max_y) + " " +
                        Math.abs(width) + " " + Math.abs(height);
                }
            }

            if (viewport.length() == 0) {
                viewport = "" + (bounds.getMinimumX() - margin) + " " +
                    ((-1 * bounds.getMaximumY()) - margin) + "  " +
                    (bounds.getWidth() + (2 * margin)) + " " +
                    (bounds.getHeight() + (2 * margin));
            }

            SVGUtils.addAttribute(attr, "viewBox", viewport);

            //set the default namespace
            SVGUtils.addAttribute(attr, "xmlns", SVGConstants.SVG_NAMESPACE);

            SVGUtils.startElement(handler, SVGConstants.SVG_ROOT, attr);

            // the blocks as symbol in the defs-section of SVG
            attr = new AttributesImpl();
            SVGUtils.startElement(handler, SVGConstants.SVG_DEFS, attr);

            //set the context
            context.put(SVGContext.DRAFT_BOUNDS, bounds);

            double dotLength = 0.0;

            if (bounds.getWidth() > bounds.getHeight()) {
                dotLength = bounds.getHeight() * SVGConstants.DEFAULT_STROKE_WIDTH_PERCENT;
            } else {
                dotLength = bounds.getWidth() * SVGConstants.DEFAULT_STROKE_WIDTH_PERCENT;
            }

            context.put(SVGContext.DOT_LENGTH, new Double(dotLength));

            Enumeration e = blocks.elements();

            while (e.hasMoreElements()) {
                DXFBlock block = (DXFBlock) e.nextElement();
                block.toSAX(handler, context);
            }

            //maybe there is a fontdescription available from DXFStyle
            Iterator i = getDXFStyleIterator();

            while (i.hasNext()) {
                DXFStyle style = (DXFStyle) i.next();
                style.toSAX(handler, context);
            }

//            i = getDXFHatchPatternIterator();
//
//            while (i.hasNext()) {
//                DXFHatchPattern pattern = (DXFHatchPattern) i.next();
//                pattern.toSAX(handler, context);
//            }

            SVGUtils.endElement(handler, SVGConstants.SVG_DEFS);

            // the draft
            attr = new AttributesImpl();
            SVGUtils.addAttribute(attr, "id", "draft");

            // the globale coordinate system transformation
            // note: DXF has the y-axis positiv from bottom to top
            // SVG has the y-axis positiv from top to bottom
            SVGUtils.addAttribute(attr, "transform", "matrix(1 0 0 -1 0 0)");
            SVGUtils.addAttribute(attr, "stroke-width",
                "" + SVGConstants.DEFAULT_STROKE_WIDTH_PERCENT + '%');
            SVGUtils.startElement(handler, SVGConstants.SVG_GROUP, attr);

            // the layers as container g-elements
            e = layers.elements();

            while (e.hasMoreElements()) {
                DXFLayer layer = (DXFLayer) e.nextElement();
                layer.toSAX(handler, context);
            }

            SVGUtils.endElement(handler, SVGConstants.SVG_GROUP);
            SVGUtils.endElement(handler, SVGConstants.SVG_ROOT);
            handler.endDocument();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    public double translateX(double value) {
        return value;
    }

    public double translateY(double value) {
        // return bounds.getMaximumY() - value;
        return value;
    }

    public Bounds getBounds() {
        Enumeration e = layers.elements();

        while (e.hasMoreElements()) {
            DXFLayer layer = (DXFLayer) e.nextElement();
            Bounds b = layer.getBounds();

            if (b.isValid()) {
                bounds.addToBounds(b);
            }
        }

        return bounds;
    }

    public double getHeight() {
        return bounds.getHeight();
    }

    public double getWidth() {
        return bounds.getWidth();
    }

    public DXFHeader getDXFHeader() {
        return header;
    }

    public void setDXFHeader(DXFHeader header) {
        this.header = header;
    }

    public void addDXFDimensionStyle(DXFDimensionStyle style) {
        dimensionStyles.put(style.getName(), style);
    }

    public DXFDimensionStyle getDXFDimensionStyle(String name) {
        return (DXFDimensionStyle) dimensionStyles.get(name);
    }

    public Iterator getDXFDimensionStyleIterator() {
        return dimensionStyles.values().iterator();
    }

    public void addDXStyle(DXFStyle style) {
        textStyles.put(style.getName(), style);
    }

    public DXFStyle getDXFStyle(String name) {
        return (DXFStyle) textStyles.get(name);
    }

    public Iterator getDXFStyleIterator() {
        return textStyles.values().iterator();
    }

    public void removeDXFLayer(String id) {
        layers.remove(id);
    }

    public void addDXFViewport(DXFViewport viewport) {
        viewports.add(viewport);
    }

    public Iterator getDXFViewportIterator() {
        return viewports.iterator();
    }

    public void removeDXFViewport(DXFViewport viewport) {
        viewports.remove(viewport);
    }

    public void removeDXFViewport(int index) {
        viewports.remove(index);
    }

    public void addDXFView(DXFView view) {
        this.views.add(view);
    }

    public Iterator getDXFViewIterator() {
        return this.views.iterator();
    }

    public void addDXFObject(DXFObject obj) {
        HashMap type = null;

        if (objects.containsKey(obj.getObjectType())) {
            type = (HashMap) objects.get(obj.getObjectType());
        } else {
            type = new HashMap();
            objects.put(obj.getObjectType(), type);
        }

        type.put(obj.getHandleID(), obj);
    }

    public List getDXFObjectsByType(String type) {
        HashMap objecttypes = (HashMap) objects.get(type);
        List list = new ArrayList(objecttypes.values());

        return list;
    }

    
    public DXFObject getDXFObject(String type, String id) {
        HashMap objecttypes = (HashMap) objects.get(type);
        return (DXFObject) objecttypes.get(id);
    }

    
    /**
     * Adds a DXFHatchPattern to the document.
     * @param pattern
     */
    
    public void addDXFHatchPattern(DXFHatchPattern pattern) {
        this.patterns.put(pattern.getID(), pattern);
    }

    /**
     * 
     * @return java.util.Iterator over all DXFHatchPattern
     * of the document
     */
    
    public Iterator getDXFHatchPatternIterator() {
        return patterns.values().iterator();
    }
    
    /**
     * 
     * @param ID of the pattern (also called pattern name)
     * @return the DXFHatchPattern or null
     */
    public DXFHatchPattern getDXFHatchPattern(String id){
    	return (DXFHatchPattern)this.patterns.get(id);
    }
}
