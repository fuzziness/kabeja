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
package org.kabeja.svg;

import java.util.Map;

import org.kabeja.xml.AbstractSAXFilter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class StyleAttributeFilter extends AbstractSAXFilter {

	
	public final static String PROPERTY_DEFAULT_COLOR="style.default.color";
	
	protected boolean colorFound=false;

	protected String colorProperty = "currentColor";
	protected String color=colorProperty;
	protected int indent=0;
	protected int colorIndent=0;
	protected boolean defSection=false;
	
	
	public void startElement(String uri, String localName, String name,
			Attributes atts) throws SAXException {
		AttributesImpl attributes = new AttributesImpl();
		StringBuffer buf = new StringBuffer();
		boolean fill=false;

		
		indent++;
		for (int i = 0; i < atts.getLength(); i++) {
			if (atts.getLocalName(i).equals(SVGConstants.SVG_ATTRIBUTE_FILL)) {
              fill =true;
			}  else if (atts.getLocalName(i).equals(
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
				colorFound=true;
				this.colorIndent=indent;
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
				this.color ="#"+rs+gs+bs;
				index=0;
				
				}
			}
		}

		
		
		buf.append(SVGConstants.SVG_ATTRIBUTE_STROKE_OPACITY);
		buf.append(':');
		buf.append(1);
		buf.append(';');
		

		if(colorFound){
			buf.append(SVGConstants.SVG_ATTRIBUTE_STROKE);
			buf.append(':');
			buf.append(this.color);
			buf.append(';');
		}
		if(fill || localName.equals(SVGConstants.SVG_TEXT) || localName.equals(SVGConstants.SVG_TSPAN) ){
			buf.append(SVGConstants.SVG_ATTRIBUTE_FILL);
			buf.append(':');
			buf.append(this.color);
			buf.append(';');
			buf.append(SVGConstants.SVG_ATTRIBUTE_FILL_OPASITY);
			buf.append(':');
			buf.append(1);
			buf.append(';');

		}else{
			buf.append(SVGConstants.SVG_ATTRIBUTE_FILL);
			buf.append(':');
			buf.append(SVGConstants.SVG_ATTRIBUTE_FILL_VALUE_NONE);
			buf.append(';');
		
		}
		if (buf.length() > 0) {
			SVGUtils.addAttribute(attributes, SVGConstants.SVG_ATTRIBUTE_STYLE,
					buf.toString());
		}
		super.startElement(uri, localName, name, attributes);
	}


	public void endElement(String uri, String localName, String name)
			throws SAXException {
		if(this.indent==this.colorIndent){
			colorFound=false;
			this.color = colorProperty;
		}

		
		this.indent--;
		super.endElement(uri, localName, name);
	}


	public void setProperties(Map properties) {
		
		super.setProperties(properties);
		if(properties.containsKey(PROPERTY_DEFAULT_COLOR)){
			this.colorProperty=(String)properties.get(PROPERTY_DEFAULT_COLOR);
			this.color = this.colorProperty;
		}
	}

	
	
}
