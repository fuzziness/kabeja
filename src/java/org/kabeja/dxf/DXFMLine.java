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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kabeja.dxf.helpers.Point;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;


/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 */
public class DXFMLine extends DXFEntity {
    protected double scale = 1.0;
    protected Point startPoint = new Point();
    private List vertices = new ArrayList();
    private List directionVectors = new ArrayList();
    protected String mLineStyleID = "";

    /* (non-Javadoc)
     * @see de.miethxml.kabeja.svg.SVGGenerator#toSAX(org.xml.sax.ContentHandler)
     */
    public void toSAX(ContentHandler handler, Map svgContext)
        throws SAXException {
    }

    /* (non-Javadoc)
     * @see de.miethxml.kabeja.dxf.DXFEntity#getBounds()
     */
    public Bounds getBounds() {
        Bounds b = new Bounds();
        b.setValid(false);

        return b;
    }

    /* (non-Javadoc)
     * @see de.miethxml.kabeja.dxf.DXFEntity#getType()
     */
    public String getType() {
        return DXFConstants.ENTITY_TYPE_MLINE;
    }

	public double getLength() {
		// TODO Auto-generated method stub
		return 0;
	}
    
    
}
