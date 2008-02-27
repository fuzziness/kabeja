/*
 * Created on Jun 28, 2004
 *
 */
package org.kabeja.svg;

import java.io.File;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Iterator;

import org.kabeja.dxf.DXFLineType;
import org.kabeja.dxf.helpers.StyledTextParagraph;
import org.kabeja.dxf.helpers.TextDocument;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


/**
 *
 *
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 *
 */
public class SVGUtils {
    public static String DEFAUL_ATTRIBUTE_TYPE = "CDATA";
    public static String DEFAULT_ID_NAME_PREFIX = "ID_";
    private static DecimalFormat format;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        format = new DecimalFormat("###.###################################",
                symbols);
    }

    public static void startElement(ContentHandler handler, String element,
        Attributes attr) throws SAXException {
        // handler
        // .startElement(SVGConstants.SVG_NAMESPACE,
        // element,SVGConstants.SVG_PREFIX+":"+element,
        // attr);
        handler.startElement(SVGConstants.SVG_NAMESPACE, element, element, attr);
    }

    public static void endElement(ContentHandler handler, String element)
        throws SAXException {
        // handler.endElement(SVGConstants.SVG_NAMESPACE,
        // element,SVGConstants.SVG_PREFIX+":"+element);
        handler.endElement(SVGConstants.SVG_NAMESPACE, element, element);
    }

    public static void addAttribute(AttributesImpl attr, String name,
        String value) {
        // attr.addAttribute(SVGConstants.SVG_NAMESPACE, name,
        // SVGConstants.SVG_PREFIX+":"+name , DEFAUL_ATTRIBUTE_TYPE,
        // value);
        attr.addAttribute("", name, name, DEFAUL_ATTRIBUTE_TYPE, value);
    }

    public static void characters(ContentHandler handler, String text)
        throws SAXException {
        char[] data = text.toCharArray();
        handler.characters(data, 0, data.length);
    }

    public static void emptyElement(ContentHandler handler, String element,
        Attributes attr) throws SAXException {
        startElement(handler, element, attr);
        endElement(handler, element);
    }

    public static void addStrokeDashArrayAttribute(AttributesImpl attr,
        DXFLineType ltype) {
        addStrokeDashArrayAttribute(attr, ltype, 1.0);
    }

    public static void addStrokeDashArrayAttribute(AttributesImpl attr,
        DXFLineType ltype, double scale) {
        if (ltype != null) {
            double[] pattern = ltype.getPattern();

            if (pattern.length > 0) {
                StringBuffer buf = new StringBuffer();

                for (int i = 0; i < pattern.length; i++) {
                    if (pattern[i] != 0.0) {
                        buf.append(format.format(Math.abs((pattern[i] * scale))));
                    } else {
                        // that means a dot
                        buf.append("0.05%");
                    }

                    buf.append(", ");
                }

                buf.deleteCharAt(buf.length() - 2);

                SVGUtils.addAttribute(attr,
                    SVGConstants.SVG_ATTRIBUTE_STROKE_DASHARRAY, buf.toString());
            }
        }
    }

    /**
     * SVG is XML and needs valid id attributes look at <a
     * href="http://www.w3.org/TR/2000/REC-xml-20001006#sec-common-syn">XML-Recommandation
     * </a>.
     *
     * @param id
     * @return a id which should be valid, but may be not unique.
     */
    public static String validateID(String id) {
        if (id.length() > 0) {
            StringBuffer buf = new StringBuffer();
            char first = id.charAt(0);

            if (!Character.isLetter(first) && (first != '_') && (first != ':')) {
                buf.append(DEFAULT_ID_NAME_PREFIX);
            }

            for (int i = 0; i < id.length(); i++) {
                char c = id.charAt(i);

                // TODO we have to allow here CombinigChar and Extender too
                if (Character.isLetter(c) || Character.isDigit(c) ||
                        (c == '-') || (c == '_') || (c == '.') || (c == ':')) {
                    buf.append(c);
                } else {
                    // normally we have to check all id to garante it will be a
                    // unique,
                    // but we convert the current char to a int with "_"-prefix
                    buf.append("_#");
                    buf.append((int) c);
                    buf.append('_');
                }
            }

            return buf.toString();
        } else {
            return id;
        }
    }

    /**
     * This will reverse a validated ID back to DXF id.
     *
     * @param id
     * @return
     */
    public static String reverseID(String id) {
        if (id.length() > 0) {
            StringBuffer buf = new StringBuffer();
            boolean marker = false;
            boolean start = false;
            StringBuffer number = new StringBuffer();

            for (int i = 0; i < id.length(); i++) {
                char c = id.charAt(i);

                if (c == '_') {
                    if (marker) {
                        if (number.length() > 0) {
                            int x = Integer.parseInt(number.toString());
                            buf.append((char) x);

                            start = false;
                            number.delete(0, number.length());
                        } else {
                            buf.append('_');
                            buf.append(c);
                        }

                        marker = false;
                    } else {
                        marker = true;
                    }
                } else if (marker) {
                    if (Character.isDigit(c) && start) {
                        number.append(c);
                    } else if (c != '#') {
                        marker = false;
                        buf.append('_');
                        buf.append(c);
                    } else {
                        // is #
                        start = true;
                    }
                } else {
                    buf.append(c);
                }
            }

            if (marker) {
                // we forgot a last single '_'
                buf.append('_');
            }

            if (buf.toString().startsWith(DEFAULT_ID_NAME_PREFIX)) {
                buf.delete(0, DEFAULT_ID_NAME_PREFIX.length());
            }

            return buf.toString();
        } else {
            return id;
        }
    }

    public static void textDocumentToSAX(ContentHandler handler,
        TextDocument doc) throws SAXException {
        Iterator i = doc.getStyledParagraphIterator();

        while (i.hasNext()) {
            StyledTextParagraph para = (StyledTextParagraph) i.next();
            styledTextToSAX(handler, para);
        }
    }

    /**
     *
     * @param handler
     * @param para
     *            the StyledTextParagraph^
     * @throws SAXException
     */
    public static void styledTextToSAX(ContentHandler handler,
        StyledTextParagraph para) throws SAXException {
        AttributesImpl atts = new AttributesImpl();
        String decoration = "";

        if (para.isUnderline()) {
            decoration += "underline ";
        }

        if (para.isOverline()) {
            decoration += "overline ";
        }

        if (decoration.length() > 0) {
            SVGUtils.addAttribute(atts,
                SVGConstants.SVG_ATTRIBUTE_TEXT_DECORATION, decoration);
        }

        if (para.getLineIndex() > 0) {
            SVGUtils.addAttribute(atts, "dy", "1.3em");
        }

        if (para.isNewline()) {
            SVGUtils.addAttribute(atts, "x", "" + para.getInsertPoint().getX());
            para.setNewline(false);
        }

        if (para.getValign() == StyledTextParagraph.VERTICAL_ALIGNMENT_TOP) {
            SVGUtils.addAttribute(atts,
                SVGConstants.SVG_ATTRIBUTE_TEXT_BASELINE_SHIFT, "-100%");
        } else if (para.getValign() == StyledTextParagraph.VERTICAL_ALIGNMENT_BOTTOM) {
            SVGUtils.addAttribute(atts,
                SVGConstants.SVG_ATTRIBUTE_TEXT_BASELINE_SHIFT, "sub");
        } else if (para.getValign() == StyledTextParagraph.VERTICAL_ALIGNMENT_CENTER) {
            SVGUtils.addAttribute(atts,
                SVGConstants.SVG_ATTRIBUTE_TEXT_BASELINE_SHIFT, "-40%");
        }

        if (para.getWidth() > 0.0) {
            SVGUtils.addAttribute(atts, SVGConstants.SVG_ATTRIBUTE_TEXT_LENGTH,
                "" + para.getWidth());
        }

        if (para.isBold()) {
            SVGUtils.addAttribute(atts, SVGConstants.SVG_ATTRIBUTE_FONT_WEIGHT,
                "bold");
        }

        if (para.isItalic()) {
            SVGUtils.addAttribute(atts, SVGConstants.SVG_ATTRIBUTE_FONT_STYLE,
                "italic");
        }

        if (para.getFont().length() > 0) {
            SVGUtils.addAttribute(atts, SVGConstants.SVG_ATTRIBUTE_FONT_FAMILY,
                para.getFont());
        }

        if (para.getFontHeight() > 0) {
            SVGUtils.addAttribute(atts, SVGConstants.SVG_ATTRIBUTE_FONT_SIZE,
                "" + formatNumberAttribute(para.getFontHeight()));
        }

        SVGUtils.startElement(handler, SVGConstants.SVG_TSPAN, atts);
        SVGUtils.characters(handler, para.getText());
        SVGUtils.endElement(handler, SVGConstants.SVG_TSPAN);
    }

    public static String formatNumberAttribute(double v) {
        return format.format(v);
    }

    public static String fileToURI(File file) {
        StringBuffer buf = new StringBuffer();

        try {
            buf.append("file://");

            char[] c = file.toURL().toExternalForm().toCharArray();

            for (int i = 5; i < c.length; i++) {
                if (Character.isWhitespace(c[i])) {
                    buf.append("%20");
                } else {
                    buf.append(c[i]);
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return buf.toString();
    }

    public static String pathToURI(String path) {
        StringBuffer buf = new StringBuffer();

        char[] c = path.toCharArray();

        if (c.length > 0) {
            buf.append("file://");

            if (c[0] != '/') {
                buf.append('/');
            }

            for (int i = 0; i < c.length; i++) {
                if (Character.isWhitespace(c[i])) {
                    buf.append("%20");
                } else if (c[i] == '\\') {
                    buf.append('/');
                } else {
                    buf.append(c[i]);
                }
            }
        }

        return buf.toString();
    }

    public static String lineWeightToStrokeWidth(int lineWeight) {
        double w = (double) lineWeight / 100.0;

        return "" + w + "mm";
    }
}
