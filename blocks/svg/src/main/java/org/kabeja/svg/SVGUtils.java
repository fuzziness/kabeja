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
 * Created on Jun 28, 2004
 *
 */
package org.kabeja.svg;

import java.io.File;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Iterator;

import org.kabeja.common.LineType;
import org.kabeja.common.LineWidth;
import org.kabeja.entities.util.StyledTextParagraph;
import org.kabeja.entities.util.TextDocument;
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
	public final static String DEFAUL_ATTRIBUTE_TYPE = "CDATA";
	public final static String DEFAULT_ID_NAME_PREFIX = "ID";
	public final static char DEFAULT_CONVERT_MARKER_CHAR = '_';
	private static DecimalFormat format;

	static {
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		format = new DecimalFormat("###.###################################", symbols);
	}

	public static void startElement(ContentHandler handler, String element, Attributes attr) throws SAXException {
		// handler
		// .startElement(SVGConstants.SVG_NAMESPACE,
		// element,SVGConstants.SVG_PREFIX+":"+element,
		// attr);
		handler.startElement(SVGConstants.SVG_NAMESPACE, element, element, attr);
	}

	public static void endElement(ContentHandler handler, String element) throws SAXException {
		// handler.endElement(SVGConstants.SVG_NAMESPACE,
		// element,SVGConstants.SVG_PREFIX+":"+element);
		handler.endElement(SVGConstants.SVG_NAMESPACE, element, element);
	}

	public static void addAttribute(AttributesImpl attr, String name, String value) {

		// we have remove to override
		int index = attr.getIndex(name);
		if (index > -1) {
			attr.removeAttribute(index);
		}
		attr.addAttribute("", name, name, DEFAUL_ATTRIBUTE_TYPE, value);
	}

	public static void characters(ContentHandler handler, String text) throws SAXException {
		char[] data = text.toCharArray();
		handler.characters(data, 0, data.length);
	}

	public static void emptyElement(ContentHandler handler, String element, Attributes attr) throws SAXException {
		startElement(handler, element, attr);
		endElement(handler, element);
	}

	public static void addStrokeDashArrayAttribute(AttributesImpl attr, LineType ltype) {
		addStrokeDashArrayAttribute(attr, ltype, 1.0);
	}

	public static void addStrokeDashArrayAttribute(AttributesImpl attr, LineType ltype, double scale) {
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

				SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_STROKE_DASHARRAY, buf.toString());
			}
		}
	}

	/**
	 * SVG is XML and needs valid id attributes look at <a
	 * href="http://www.w3.org/TR/2000/REC-xml-20001006#sec-common-syn"
	 * >XML-Recommandation </a>.
	 * 
	 * @param id
	 * @return a id which should be valid, but may be not unique.
	 */
	public static String toValidateID(long id) {
		return DEFAULT_ID_NAME_PREFIX + id;
	}

	/**
	 * SVG is XML and needs valid id attributes look at <a
	 * href="http://www.w3.org/TR/2000/REC-xml-20001006#sec-common-syn"
	 * >XML-Recommandation </a>.
	 * 
	 * @param id
	 * @return a id which should be valid, but may be not unique.
	 */
	public static String validateID(String id) {
		if (id.length() > 0) {
			StringBuffer buf = new StringBuffer();
			char first = id.charAt(0);

			if (!Character.isLetter(first) && (first != ':')) {
				buf.append(DEFAULT_ID_NAME_PREFIX);
			}

			for (int i = 0; i < id.length(); i++) {
				char c = id.charAt(i);

				// TODO we have to allow here CombinigChar and Extender too
				if (Character.isLetter(c) || Character.isDigit(c) || (c == '-') || (c == '.') || (c == ':')) {
					buf.append(c);
				} else {
					// normally we have to check all id to guarantee it will be
					// a
					// unique,
					// but we convert the current char to a integer with
					// "_"-prefix and "_" suffix
					buf.append(DEFAULT_CONVERT_MARKER_CHAR);
					buf.append((int) c);
					buf.append(DEFAULT_CONVERT_MARKER_CHAR);
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
	public static long reverseID(String id) {
		if (id.length() > 0) {
			if (id.startsWith(DEFAULT_ID_NAME_PREFIX)) {
				try {
					return Long.parseLong(id.substring(DEFAULT_ID_NAME_PREFIX.length() - 1));
				} catch (Exception e) {
					// no dxf generated id
					e.printStackTrace();

				}
			}

		}
		return -1;
	}

	public static void textDocumentToSAX(ContentHandler handler, TextDocument doc) throws SAXException {
		for (StyledTextParagraph para : doc.getStyledParagraphs()) {
			styledTextToSAX(handler, para);
		}
	}

	/**
	 * 
	 * @param handler
	 * @param para
	 *            the StyledTextParagraph
	 * @throws SAXException
	 */
	public static void styledTextToSAX(ContentHandler handler, StyledTextParagraph para) throws SAXException {
		AttributesImpl atts = new AttributesImpl();
		String decoration = "";

		if (para.isUnderline()) {
			decoration += "underline ";
		}

		if (para.isOverline()) {
			decoration += "overline ";
		}

		if (decoration.length() > 0) {
			SVGUtils.addAttribute(atts, SVGConstants.SVG_ATTRIBUTE_TEXT_DECORATION, decoration);
		}

		if (para.getLineIndex() > 0) {
			SVGUtils.addAttribute(atts, "dy", "1.3em");
		}

		if (para.isNewline()) {
			SVGUtils.addAttribute(atts, "x", "" + para.getInsertPoint().getX());
			para.setNewline(false);
		}

		if (para.getValign() == StyledTextParagraph.VERTICAL_ALIGNMENT_TOP) {
			SVGUtils.addAttribute(atts, SVGConstants.SVG_ATTRIBUTE_TEXT_BASELINE_SHIFT, "-100%");
		} else if (para.getValign() == StyledTextParagraph.VERTICAL_ALIGNMENT_BOTTOM) {
			SVGUtils.addAttribute(atts, SVGConstants.SVG_ATTRIBUTE_TEXT_BASELINE_SHIFT, "sub");
		} else if (para.getValign() == StyledTextParagraph.VERTICAL_ALIGNMENT_CENTER) {
			SVGUtils.addAttribute(atts, SVGConstants.SVG_ATTRIBUTE_TEXT_BASELINE_SHIFT, "-40%");
		}

		if (para.getWidth() > 0.0) {
			SVGUtils.addAttribute(atts, SVGConstants.SVG_ATTRIBUTE_TEXT_LENGTH, "" + para.getWidth());
		}

		if (para.isBold()) {
			SVGUtils.addAttribute(atts, SVGConstants.SVG_ATTRIBUTE_FONT_WEIGHT, "bold");
		}

		if (para.isItalic()) {
			SVGUtils.addAttribute(atts, SVGConstants.SVG_ATTRIBUTE_FONT_STYLE, "italic");
		}

		if (para.getFont().length() > 0) {
			SVGUtils.addAttribute(atts, SVGConstants.SVG_ATTRIBUTE_FONT_FAMILY, para.getFont());
		}

		if (para.getFontHeight() > 0) {
			SVGUtils.addAttribute(atts, SVGConstants.SVG_ATTRIBUTE_FONT_SIZE, "" + formatNumberAttribute(para.getFontHeight()));
		}

		atts.addAttribute(SVGConstants.XML_NAMESPACE, "space", "xml:space", "CDATA", "preserve");

		SVGUtils.startElement(handler, SVGConstants.SVG_TSPAN, atts);
		SVGUtils.characters(handler, para.getText());
		SVGUtils.endElement(handler, SVGConstants.SVG_TSPAN);
	}

	public static String formatNumberAttribute(double v) {
		return format.format((float) v);
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

		return "" + w + " mm";
	}

	public static String lineWidthToStrokeWidth(LineWidth lw) {
		switch (lw.getType()) {
		case LineWidth.TYPE_LINE_WEIGHT:
			double w = lw.getValue() / 100.0;
			return formatNumberAttribute(w) + " mm";

		case LineWidth.TYPE_LINE_WIDTH:
			return formatNumberAttribute(lw.getValue());

		case LineWidth.TYPE_PERCENT:
			return formatNumberAttribute(lw.getValue()) + "%";
		default:
			return "0.02%";

		}
	}
}
