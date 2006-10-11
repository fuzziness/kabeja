package org.kabeja.xml;

import org.kabeja.svg.SVGConstants;
import org.kabeja.svg.SVGUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * This filter removes the SVG group "draft" which holds the complete drawing.
 * 
 * @author simon
 * 
 */

public class RootLayerFilter extends AbstractSAXFilter {
	private boolean inDraftSection = false;

	private int groupDepth = 0;

	private String transformValue = "";
	
	private String strokeWidth="";

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (this.inDraftSection) {
			if (uri.equals(SVGConstants.SVG_NAMESPACE)
					&& localName.equals(SVGConstants.SVG_GROUP)) {
				if (this.groupDepth == 1) {
					// we filter out the main group
				
					this.inDraftSection = false;
					return;
				}
				

				groupDepth--;
			}
		}
		super.endElement(uri, localName, qName);

	}

	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {

		if (uri.equals(SVGConstants.SVG_NAMESPACE)
				&& localName.equals(SVGConstants.SVG_GROUP)) {
			 String id =atts.getValue(SVGConstants.XML_ID);
			if (id !=null && id.equals("draft")
					|| this.inDraftSection) {

				switch (groupDepth) {
				case 0:
					// the root group
					this.transformValue = atts
							.getValue(SVGConstants.SVG_ATTRIBUTE_TRANSFORM);
					this.inDraftSection = true;
					this.strokeWidth = atts
					.getValue(SVGConstants.SVG_ATTRIBUTE_STROKE_WITDH);
					break;
				case 1:
					AttributesImpl attributes = new AttributesImpl(atts);
					if (attributes
							.getIndex(SVGConstants.SVG_ATTRIBUTE_TRANSFORM) != -1) {
						attributes
								.setAttribute(
										attributes
												.getIndex(SVGConstants.SVG_ATTRIBUTE_TRANSFORM),
										"",
										SVGConstants.SVG_ATTRIBUTE_TRANSFORM,
										SVGConstants.SVG_ATTRIBUTE_TRANSFORM,
										SVGUtils.DEFAUL_ATTRIBUTE_TYPE,
										this.transformValue
												+ " "
												+ attributes
														.getValue(SVGConstants.SVG_ATTRIBUTE_TRANSFORM));
					} else {
						attributes.addAttribute(

						"", SVGConstants.SVG_ATTRIBUTE_TRANSFORM,
								SVGConstants.SVG_ATTRIBUTE_TRANSFORM,
								SVGUtils.DEFAUL_ATTRIBUTE_TYPE,
								this.transformValue);
					}
					if (attributes
							.getIndex(SVGConstants.SVG_ATTRIBUTE_STROKE_WITDH) == -1) {
						attributes
								.addAttribute(
									
										"",
										SVGConstants.SVG_ATTRIBUTE_STROKE_WITDH,
										SVGConstants.SVG_ATTRIBUTE_STROKE_WITDH,
										SVGUtils.DEFAUL_ATTRIBUTE_TYPE,
										this.strokeWidth);
					} 
					
					
					super.startElement(uri, localName, qName, attributes);
					break;
				default:
					super.startElement(uri, localName, qName, atts);
					break;
				}
		
				this.groupDepth++;
				return;
			}
		}

		super.startElement(uri, localName, qName, atts);
	}

}
