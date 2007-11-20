package org.kabeja.svg.generators;

import java.util.Map;

import org.kabeja.dxf.DXFConstants;
import org.kabeja.dxf.DXFDimensionStyle;
import org.kabeja.dxf.DXFEntity;
import org.kabeja.dxf.DXFTolerance;
import org.kabeja.dxf.helpers.MathUtils;
import org.kabeja.math.TransformContext;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class SVGToleranceGenerator extends AbstractSVGSAXGenerator{

	public void toSAX(ContentHandler handler, Map svgContext, DXFEntity entity,
			TransformContext transformContext) throws SAXException {
	      //TODO implement the SVG tolerance generator
		
		DXFTolerance tolerance = (DXFTolerance)entity;
        DXFDimensionStyle style =  tolerance.getDXFDocument().getDXFDimensionStyle(tolerance.getStyleID());
 
        double angle = MathUtils.getAngle( tolerance.getXaxisDirection(),
                DXFConstants.DEFAULT_X_AXIS_VECTOR);
        double textHeight = style.getDoubleProperty(DXFDimensionStyle.PROPERTY_DIMTXT);
        double scale = style.getDoubleProperty(DXFDimensionStyle.PROPERTY_DIMSCALE,
                1.0);
        textHeight *= scale;
		
	}

}
