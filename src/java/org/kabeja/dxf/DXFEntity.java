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

import java.util.Map;

import org.kabeja.svg.SVGConstants;
import org.kabeja.svg.SVGContext;
import org.kabeja.svg.SVGFragmentGenerator;
import org.kabeja.svg.SVGUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 *
 */
public abstract class DXFEntity implements SVGFragmentGenerator {
    public static String TYPE = "";
    protected DXFDocument doc;
    protected String id = "";
    protected String layerID = "";
    protected boolean visibile = true;
    protected String lineType = "";
    protected int flags = 0;
    protected Bounds bounds = new Bounds();
    protected boolean block = false;
    protected double linetypeScaleFactor = 1.0;
    protected int color = 0;
    protected byte[] colorRGB;
    protected int lineWeight;
    protected double transparency;
    protected double thickness = 0.0;
    protected DXFExtrusion extrusion = new DXFExtrusion();
    protected boolean modelSpace = false;

    public DXFEntity() {
    }

    public void setDXFDocument(DXFDocument doc) {
        this.doc = doc;
    }

    public DXFDocument getDXFDocument() {
        return this.doc;
    }

    /**
     * Gives the name of the layer, which containts the entity.
     * @return the name of the layer
     */
    
    public String getLayerName() {
        return this.layerID;
    }

    
    /**
     * Set the name of the layer, which containts the entity.
     * @return the name of the layer
     */
    
    public void setLayerName(String id) {
        this.layerID = id;
    }

    public abstract void toSAX(ContentHandler handler, Map svgContext)
        throws SAXException;

    public abstract Bounds getBounds();

    /**
     * @return Returns the lineType.
     */
    public String getLineType() {
        return lineType;
    }

    /**
     * @param lineType
     *            The lineType to set.
     */
    public void setLineType(String lineType) {
        this.lineType = lineType;
    }

    /**
     * @return Returns the visibile.
     */
    public boolean isVisibile() {
        return visibile;
    }

    /**
     * @param visibile
     *            The visibile to set.
     */
    public void setVisibile(boolean visibile) {
        this.visibile = visibile;
    }

    /**
     * @return Returns the flags.
     */
    public int getFlags() {
        return flags;
    }

    /**
     * @param flags
     *            The flags to set.
     */
    public void setFlags(int flags) {
        this.flags = flags;
    }

    public void setBlockEntity(boolean b) {
        this.block = b;
    }

    public boolean isBlockEntity() {
        return block;
    }

    public void setExtrusion(DXFExtrusion extrusion) {
        this.extrusion = extrusion;
    }

    public DXFExtrusion getExtrusion() {
        return extrusion;
    }

    public double getLinetypeScaleFactor() {
        return linetypeScaleFactor;
    }

    public void setLinetypeScaleFactor(double linetypeScaleFactor) {
        this.linetypeScaleFactor = linetypeScaleFactor;
    }

    protected void setCommonAttributes(AttributesImpl atts, Map context) {
    
    	// a negative color indicates the layer is off
        if (!isVisibile()) {
            // we calculate the bounds self so they must not
            // rendered from the SVG-Renderer.
            // If they should be in the rendering-tree change
            // this to visible=hidden
            SVGUtils.addAttribute(atts, SVGConstants.SVG_ATTRIBUTE_DISPLAY, SVGConstants.SVG_ATTRIBUTE_DISPLAY_VALUE_NONE);
        }

        // color 256 indicates color by layer
        if ((this.color != 0) && (this.color != 256)) {
            SVGUtils.addAttribute(atts, SVGConstants.SVG_ATTRIBUTE_COLOR,
                "rgb(" + DXFColor.getRGBString(getColor()) + ")");
            SVGUtils.addAttribute(atts, SVGConstants.SVG_ATTRIBUTE_STROKE, SVGConstants.SVG_ATTRIBUTE_STROKE_VALUE_CURRENTCOLOR);
        }

        if (this.getID().length() > 0) {
            SVGUtils.addAttribute(atts, SVGConstants.XML_ID, SVGUtils.validateID(this.getID()));
        }

        if(this.lineWeight>0 && !context.containsKey(SVGContext.STROKE_WIDTH_IGNORE)){
        	SVGUtils.addAttribute(atts, SVGConstants.SVG_ATTRIBUTE_STROKE_WITDH, SVGUtils.lineWeightToStrokeWidth(this.lineWeight));
        }
        
        double gscale = this.doc.getDXFHeader().getLinetypeScale();

        if ((this.lineType.length() > 0) &&
                !"CONTINUOUS".equals( this.lineType ) &&
                !"BYBLOCK".equals( this.lineType ) &&
                !"BYLAYER".equals( this.lineType ) && !isOmitLineType()) {
            DXFLineType ltype = doc.getDXFLineType(this.lineType);

            gscale = gscale * this.linetypeScaleFactor;

            SVGUtils.addStrokeDashArrayAttribute(atts, ltype, gscale);
        } else if (!isOmitLineType()) {
            // get the linetype from layer
            DXFLineType ltype = doc.getDXFLineType(doc.getDXFLayer(this.layerID)
                                                      .getLineType());

            if (ltype != null) {
                gscale = gscale * this.linetypeScaleFactor;
                SVGUtils.addStrokeDashArrayAttribute(atts, ltype,
                    (this.linetypeScaleFactor * gscale));
            } else if (isOmitLineType()) {
                SVGUtils.addAttribute(atts,
                    SVGConstants.SVG_ATTRIBUTE_STROKE_DASHARRAY, "");
            }
        }
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public byte[] getColorRGB() {
        return colorRGB;
    }

    public void setColorRGB(byte[] colorRGB) {
        this.colorRGB = colorRGB;
    }

    public int getLineWeight() {
        return lineWeight;
    }

    public void setLineWeight(int lineWeight) {
        this.lineWeight = lineWeight;
    }

    public double getTransparency() {
        return transparency;
    }

    public void setTransparency(double transparency) {
        this.transparency = transparency;
    }

    public void setID(String id) {
        this.id = id;
    }

    public String getID() {
        return id;
    }

    public void setExtrusionX(double x) {
        extrusion.setX(x);
    }

    public void setExtrusionY(double y) {
        extrusion.setY(y);
    }

    public void setExtrusionZ(double z) {
        extrusion.setZ(z);
    }

    public abstract String getType();

    /**
     * The thickness reflects the height of the entity.
     *
     *
     * @return Returns the thickness.
     */
    public double getThickness() {
        return thickness;
    }

    /**
     * @param thickness
     *            The thickness /height of the entity to set.
     */
    public void setThickness(double thickness) {
        this.thickness = thickness;
    }

    protected boolean isOmitLineType() {
        return false;
    }

    /**
     * @return Returns the modelSpace.
     */
    public boolean isModelSpace() {
        return modelSpace;
    }

    /**
     * @param modelSpace The modelSpace to set.
     */
    public void setModelSpace(boolean modelSpace) {
        this.modelSpace = modelSpace;
    }
    
    /**
     * Returns the length of the entity or 0 if the entity has no length
     * @return
     */
    
    
    public abstract double getLength();
}
