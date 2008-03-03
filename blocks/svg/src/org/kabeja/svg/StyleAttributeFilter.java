package org.kabeja.svg;

import org.kabeja.xml.AbstractSAXFilter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class StyleAttributeFilter extends AbstractSAXFilter {

	public void startElement(String uri, String localName, String name,
			Attributes atts) throws SAXException {
		AttributesImpl attributes = new AttributesImpl();
		StringBuffer buf = new StringBuffer();
		boolean colorFound=false;
		String color="currentColor";
		for (int i = 0; i < atts.getLength(); i++) {
			if (atts.getLocalName(i).equals(SVGConstants.SVG_ATTRIBUTE_FILL)) {
				buf.append(SVGConstants.SVG_ATTRIBUTE_FILL);
				buf.append(':');
				buf.append(atts.getValue(i));
				buf.append(';');
			} else if (atts.getLocalName(i).equals(
					SVGConstants.SVG_ATTRIBUTE_STROKE)) {
				colorFound=true;
//				buf.append(SVGConstants.SVG_ATTRIBUTE_STROKE);
//				buf.append(':');
//				buf.append(atts.getValue(i));
//				buf.append(';');
			} else if (atts.getLocalName(i).equals(
					SVGConstants.SVG_ATTRIBUTE_STROKE_WITDH)) {
				buf.append(SVGConstants.SVG_ATTRIBUTE_STROKE_WITDH);
				buf.append(':');
				buf.append(atts.getValue(i));
				buf.append(';');
			} else {
				attributes.addAttribute(atts.getURI(i), atts.getLocalName(i),
						atts.getQName(i), atts.getType(i), atts.getValue(i));
			}
			if (atts.getLocalName(i).equals(
					SVGConstants.SVG_ATTRIBUTE_COLOR)) {
				String value = (atts.getValue(i)).trim();
				if(value.startsWith("rgb(")){
					
				//convert to hex
				int index = 4;
				int end = value.indexOf(',');
				int r = Integer.parseInt(value.substring(index,end));
				index=end+1;
				end =value.indexOf(',',index);
				int g = Integer.parseInt(value.substring(index,end));
				index = end+1;
				int b = Integer.parseInt(value.substring(index,value.length()-1));
			
				String rs = Integer.toHexString(r);
				if(rs.length()==1){
					rs="0"+rs;
				}
				String gs = Integer.toHexString(g);
				if(gs.length()==1){
					gs="0"+gs;
				}
				String bs = Integer.toHexString(b);
				if(bs.length()==1){
					bs="0"+bs;
				}
				color ="#"+rs+gs+bs;
				index=0;
				}
			}
		}

		if(colorFound){
			buf.append(SVGConstants.SVG_ATTRIBUTE_STROKE);
			buf.append(':');
			buf.append(color);
			buf.append(';');
		}
		
		if (buf.length() > 0) {
			SVGUtils.addAttribute(attributes, SVGConstants.SVG_ATTRIBUTE_STYLE,
					buf.toString());
		}
		super.startElement(uri, localName, name, attributes);
	}

}
