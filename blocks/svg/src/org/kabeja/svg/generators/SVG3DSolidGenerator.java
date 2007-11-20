package org.kabeja.svg.generators;

import java.util.Map;

import org.kabeja.dxf.DXF3DSolid;
import org.kabeja.dxf.DXFEntity;
import org.kabeja.math.TransformContext;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class SVG3DSolidGenerator extends AbstractSVGSAXGenerator {

	public void toSAX(ContentHandler handler, Map svgContext, DXFEntity entity,
			TransformContext transformContext) throws SAXException {
		
            // DXF3DSolid solid = (DXF3DSolid)entity;
             
	}

}
