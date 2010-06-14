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

package org.kabeja.dxf.parser;

import org.kabeja.DraftDocument;
import org.kabeja.common.Header;
import org.kabeja.common.Variable;


/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 */
public class DXFHeaderSectionHandler implements DXFSectionHandler {
    public final static int VARIABLE_CODE = 9;
    private final String sectionKey = "HEADER";
    private DraftDocument doc;
    private Variable variable = null;
    private String mode;

    /* (non-Javadoc)
     * @see org.dxf2svg.parser.SectionHandler#getSectionKey()
     */
    public String getSectionKey() {
        return sectionKey;
    }

    /* (non-Javadoc)
     * @see org.dxf2svg.parser.SectionHandler#setDXFDocument(org.dxf2svg.xml.DXFDocument)
     */
    public void setDocument(DraftDocument doc) {
        this.doc = doc;
    }

    /* (non-Javadoc)
     * @see org.dxf2svg.parser.SectionHandler#parseGroup(int, java.lang.String)
     */
    public void parseGroup(int groupCode, DXFValue value) {
        if (groupCode == VARIABLE_CODE) {
            variable = new Variable(value.getValue());
            doc.getHeader().setVariable(variable);
        } else {
            //handle the current mode
            parse(groupCode, value);
        }
    }

    private void parse(int code, DXFValue value) {
        variable.setValue("" + code, value.getValue());
    }

    /* (non-Javadoc)
     * @see org.dxf2svg.parser.SectionHandler#endParsing()
     */
    public void endSection() {
    }

    /* (non-Javadoc)
     * @see org.dxf2svg.parser.SectionHandler#startParsing()
     */
    public void startSection() {
        doc.setHeader(new Header());
    }

    /* (non-Javadoc)
     * @see de.miethxml.kabeja.parser.Handler#releaseDXFDocument()
     */
    public void releaseDocument() {
        this.doc = null;
    }
}
