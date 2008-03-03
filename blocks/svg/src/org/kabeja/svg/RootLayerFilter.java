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
package org.kabeja.svg;

import org.kabeja.xml.AbstractSAXFilter;
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
    private String strokeWidth = "";

    public void endElement(String uri, String localName, String qName)
        throws SAXException {
        if (this.inDraftSection) {
            if (uri.equals(SVGConstants.SVG_NAMESPACE) &&
                    localName.equals(SVGConstants.SVG_GROUP)) {
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
  
        if (uri.equals(SVGConstants.SVG_NAMESPACE) &&
                localName.equals(SVGConstants.SVG_GROUP)) {
            String id = atts.getValue(SVGConstants.XML_ID);

            if (((id != null) && id.equals("draft")) || this.inDraftSection) {
      
            	switch (groupDepth) {
                case 0:
                    // the root group
                    this.transformValue = atts.getValue(SVGConstants.SVG_ATTRIBUTE_TRANSFORM);
                    this.inDraftSection = true;
                    this.strokeWidth = atts.getValue(SVGConstants.SVG_ATTRIBUTE_STROKE_WITDH);

                    break;

                case 1:

                    AttributesImpl attributes = new AttributesImpl(atts);

                    if (attributes.getIndex(
                                SVGConstants.SVG_ATTRIBUTE_TRANSFORM) != -1) {
                        attributes.setAttribute(attributes.getIndex(
                                SVGConstants.SVG_ATTRIBUTE_TRANSFORM), "",
                            SVGConstants.SVG_ATTRIBUTE_TRANSFORM,
                            SVGConstants.SVG_ATTRIBUTE_TRANSFORM,
                            SVGUtils.DEFAUL_ATTRIBUTE_TYPE,
                            this.transformValue + " " +
                            attributes.getValue(
                                SVGConstants.SVG_ATTRIBUTE_TRANSFORM));
                    } else {
                        attributes.addAttribute("",
                            SVGConstants.SVG_ATTRIBUTE_TRANSFORM,
                            SVGConstants.SVG_ATTRIBUTE_TRANSFORM,
                            SVGUtils.DEFAUL_ATTRIBUTE_TYPE, this.transformValue);
                    }

                    if (attributes.getIndex(
                                SVGConstants.SVG_ATTRIBUTE_STROKE_WITDH) == -1) {
                        attributes.addAttribute("",
                            SVGConstants.SVG_ATTRIBUTE_STROKE_WITDH,
                            SVGConstants.SVG_ATTRIBUTE_STROKE_WITDH,
                            SVGUtils.DEFAUL_ATTRIBUTE_TYPE, this.strokeWidth);
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
