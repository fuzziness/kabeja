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
 Copyright 2008 Simon Mieth

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
package org.kabeja.inkscape.xml;


import org.kabeja.svg.SVGConstants;
import org.kabeja.xml.AbstractSAXFilter;
import org.kabeja.xml.XMLConstants;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * This SAXFilter adds Inkscape specific layer attributes to the SVG/DXF layer.
 * 
 * @author Simon Mieth
 * 
 */

public class SAXInkscapeLayerFilter extends AbstractSAXFilter {

	public final static String INKSCAPE_NAMESPACE = "http://www.inkscape.org/namespaces/inkscape";
	public final static String INKSCAPE_NAMESPACE_PREFIX = "inkscape";
	public final static String INKSCAPE_ATTRIBUTE_GROUPMODE = "groupmode";
	public final static String INKSCAPE_ATTRIBUTE_GROUPMODE_VALUE = "layer";
	public final static String INKSCAPE_ATTRIBUTE_LABEL = "label";
	public final static String KABEJA_QNAME_LAYER_NAME = XMLConstants.KABEJA_NAMESPACE_PREFIX+":"+XMLConstants.KABEJA_ATTRIBUTE_LAYER_NAME;
	protected int indent = 0;
	protected int mode = 0;

	public final static int MODE_DEFAULT = 0;
	public final static int MODE_DEF = 1;
	public final static int MODE_DRAFT_GROUP = 2;

	public void endElement(String uri, String localName, String name)
			throws SAXException {

		if (localName.equals(SVGConstants.SVG_GROUP)&& mode != MODE_DEF) {
			this.indent--;
		} else if (localName.equals(SVGConstants.SVG_DEFS)) {
			this.mode = MODE_DEFAULT;
		}
		super.endElement(uri, localName, name);
	}

	public void startDocument() throws SAXException {
		this.indent = 0;
		super.startDocument();
	}

	public void startElement(String uri, String localName, String name,
			Attributes atts) throws SAXException {
		Attributes attr = atts;
		if (localName.equals(SVGConstants.SVG_GROUP) && this.mode != MODE_DEF) {
			
			this.indent++;
			
			if (indent == 2 && this.mode == MODE_DRAFT_GROUP) {
				attr = this.addInkscapeLayerAttributes(atts);
			} else if (indent == 1
					&& atts.getIndex(SVGConstants.XML_ID)>-1 && atts.getValue(SVGConstants.XML_ID).equals("draft")) {
				this.mode = MODE_DRAFT_GROUP;
			} else if (indent == 1 && this.mode == MODE_DEFAULT) {
				attr = this.addInkscapeLayerAttributes(atts);
			}
		} else if (localName.equals(SVGConstants.SVG_DEFS)) {
			this.mode = MODE_DEF;
		}
		super.startElement(uri, localName, name, attr);
	}

	protected Attributes addInkscapeLayerAttributes(Attributes atts) {
		AttributesImpl attr = new AttributesImpl(atts);
		attr.addAttribute(SVGConstants.XMLNS_NAMESPACE, INKSCAPE_NAMESPACE_PREFIX ,
				"xmlns:"+INKSCAPE_NAMESPACE_PREFIX , "CDATA", INKSCAPE_NAMESPACE);
		attr.addAttribute(INKSCAPE_NAMESPACE, INKSCAPE_ATTRIBUTE_GROUPMODE,
				INKSCAPE_NAMESPACE_PREFIX + ':' + INKSCAPE_ATTRIBUTE_GROUPMODE,
				"CDATA", INKSCAPE_ATTRIBUTE_GROUPMODE_VALUE);
		attr.addAttribute(INKSCAPE_NAMESPACE, INKSCAPE_ATTRIBUTE_LABEL,
				INKSCAPE_NAMESPACE_PREFIX + ':' + INKSCAPE_ATTRIBUTE_LABEL,
				"CDATA", atts.getValue( KABEJA_QNAME_LAYER_NAME));

		return attr;
	}
}
