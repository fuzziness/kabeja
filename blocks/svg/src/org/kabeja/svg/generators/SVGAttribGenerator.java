package org.kabeja.svg.generators;

import java.util.Map;

import org.kabeja.dxf.DXFAttrib;
import org.kabeja.dxf.DXFEntity;
import org.kabeja.math.TransformContext;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class SVGAttribGenerator extends SVGTextGenerator{

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.kabeja.svg.SVGGenerator#toSAX(org.xml.sax.ContentHandler)
     */
    public void toSAX(ContentHandler handler, Map svgContext, DXFEntity entity, TransformContext transformContext)
        throws SAXException {
    	
    	DXFAttrib attrib = (DXFAttrib)entity;
        switch (attrib.getFlags()) {
        case 1:
            attrib.setVisibile(false);

            break;

        case 4:

            // userinput
            attrib.setVisibile(false);

            break;
        }

        super.toSAX(handler, svgContext, attrib, transformContext);
    }
}
