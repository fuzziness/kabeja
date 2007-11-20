package org.kabeja.svg.generators;

import java.util.Map;

import org.kabeja.dxf.DXFColor;
import org.kabeja.dxf.DXFDocument;
import org.kabeja.dxf.DXFEntity;
import org.kabeja.dxf.DXFLineType;
import org.kabeja.svg.SVGConstants;
import org.kabeja.svg.SVGContext;
import org.kabeja.svg.SVGSAXGenerator;
import org.kabeja.svg.SVGUtils;
import org.xml.sax.helpers.AttributesImpl;

public abstract class AbstractSVGSAXGenerator implements SVGSAXGenerator {

	
	public void setCommonAttributes(AttributesImpl atts,Map context,DXFEntity entity){
	    
    	// a negative color indicates the layer is off
        if (!entity.isVisibile()) {
            // we calculate the bounds self so they must not
            // rendered from the SVG-Renderer.
            // If they should be in the rendering-tree change
            // this to visible=hidden
            SVGUtils.addAttribute(atts, SVGConstants.SVG_ATTRIBUTE_DISPLAY, SVGConstants.SVG_ATTRIBUTE_DISPLAY_VALUE_NONE);
        }

        // color 256 indicates color by layer
        int color =entity.getColor();
        if ((color != 0) && (color != 256)) {
            SVGUtils.addAttribute(atts, SVGConstants.SVG_ATTRIBUTE_COLOR,
                "rgb(" + DXFColor.getRGBString(color) + ")");
            SVGUtils.addAttribute(atts, SVGConstants.SVG_ATTRIBUTE_STROKE, SVGConstants.SVG_ATTRIBUTE_STROKE_VALUE_CURRENTCOLOR);
        }

        if (entity.getID().length() > 0) {
            SVGUtils.addAttribute(atts, SVGConstants.XML_ID, SVGUtils.validateID(entity.getID()));
        }

        if(entity.getLineWeight()>0 && !context.containsKey(SVGContext.STROKE_WIDTH_IGNORE)){
        	SVGUtils.addAttribute(atts, SVGConstants.SVG_ATTRIBUTE_STROKE_WITDH, SVGUtils.lineWeightToStrokeWidth(entity.getLineWeight()));
        }
        
        DXFDocument doc = entity.getDXFDocument();
        
        double gscale = doc.getDXFHeader().getLinetypeScale();

        String lineType = entity.getLineType();
        if ((lineType.length() > 0) &&
                !"CONTINUOUS".equals( lineType ) &&
                !"BYBLOCK".equals( lineType ) &&
                !"BYLAYER".equals( lineType ) && !entity.isOmitLineType()) {
            DXFLineType ltype = doc.getDXFLineType(lineType);

            gscale = gscale * entity.getLinetypeScaleFactor();

            SVGUtils.addStrokeDashArrayAttribute(atts, ltype, gscale);
        } else if (!entity.isOmitLineType()) {
            // get the linetype from layer
            DXFLineType ltype = doc.getDXFLineType(doc.getDXFLayer(entity.getLayerName())
                                                      .getLineType());

            if (ltype != null) {
                gscale = gscale * entity.getLinetypeScaleFactor();
                SVGUtils.addStrokeDashArrayAttribute(atts, ltype,
                    (entity.getLinetypeScaleFactor() * gscale));
            } else if (entity.isOmitLineType()) {
                SVGUtils.addAttribute(atts,
                    SVGConstants.SVG_ATTRIBUTE_STROKE_DASHARRAY, "");
            }
        }
	}
}
