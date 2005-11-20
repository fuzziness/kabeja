/*
   Copyright 2005 Simon Mieth

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
package org.kabeja.dxf;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 */
public class DXFRegion extends DXFEntity {
    protected List acisData = new ArrayList();

    /* (non-Javadoc)
     * @see de.miethxml.kabeja.dxf.DXFEntity#getBounds()
     */
    public Bounds getBounds() {
        bounds.setValid(false);

        return bounds;
    }

    /* (non-Javadoc)
     * @see de.miethxml.kabeja.dxf.DXFEntity#getType()
     */
    public String getType() {
        // TODO Auto-generated method stub
        return DXFConstants.ENTITY_TYPE_REGION;
    }

    /* (non-Javadoc)
     * @see de.miethxml.kabeja.svg.SVGGenerator#toSAX(org.xml.sax.ContentHandler, java.util.Map)
     */
    public void toSAX(ContentHandler handler, Map svgContext)
        throws SAXException {
        // no output
    }

    /**
     * The ACIS commands as a list of lines
     * @return the list
     */
    public List getACISDATA() {
        return acisData;
    }

    public void appendACISDATA(String data) {
        acisData.add(data);
    }
}
