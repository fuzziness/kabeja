package org.kabeja.svg.generators;

import java.util.Map;

import org.kabeja.dxf.DXFEntity;
import org.kabeja.dxf.DXFStyle;
import org.kabeja.math.TransformContext;
import org.kabeja.svg.SVGConstants;
import org.kabeja.svg.SVGUtils;
import org.kabeja.tools.FontManager;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class SVGStyleGenerator {
	 /*
     * (non-Javadoc)
     *
     * @see de.miethxml.kabeja.svg.SVGGenerator#toSAX(org.xml.sax.ContentHandler)
     */
    public static void toSAX(ContentHandler handler, Map svgContext, DXFStyle style)
        throws SAXException {
        FontManager manager = FontManager.getInstance();

        if (manager.hasFontDescription(style.getBigFontFile())) {
            generateSAXFontDescription(handler, style.getBigFontFile());
        } else if (manager.hasFontDescription(style.getFontFile())) {
            generateSAXFontDescription(handler, style.getFontFile());
        }
    }

    protected static void generateSAXFontDescription(ContentHandler handler,
        String font) throws SAXException {
        font = font.toLowerCase();

        if (font.endsWith(".shx")) {
            font = font.substring(0, font.indexOf(".shx"));
        }

        AttributesImpl attr = new AttributesImpl();
        SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_FONT_FAMILY, font);

        SVGUtils.startElement(handler, SVGConstants.SVG_FONT_FACE, attr);
        attr = new AttributesImpl();
        SVGUtils.startElement(handler, SVGConstants.SVG_FONT_FACE_SRC, attr);

        attr = new AttributesImpl();

        String url = FontManager.getInstance().getFontDescription(font) + "#" +
            font;
        attr.addAttribute("", "", "xmlns:xlink", "CDATA",
            SVGConstants.XLINK_NAMESPACE);
        attr.addAttribute(SVGConstants.XLINK_NAMESPACE, "href", "xlink:href",
            "CDATA", url);
        SVGUtils.emptyElement(handler, SVGConstants.SVG_FONT_FACE_URI, attr);
        SVGUtils.endElement(handler, SVGConstants.SVG_FONT_FACE_SRC);
        SVGUtils.endElement(handler, SVGConstants.SVG_FONT_FACE);
    }

}
