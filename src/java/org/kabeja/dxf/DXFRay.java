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

import org.kabeja.dxf.helpers.DXFUtils;
import org.kabeja.dxf.helpers.Point;
import org.kabeja.dxf.helpers.Vector;

import org.kabeja.svg.SVGConstants;
import org.kabeja.svg.SVGContext;
import org.kabeja.svg.SVGUtils;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.util.Map;


/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 */
public class DXFRay extends DXFEntity {
    protected Point basePoint = new Point();
    protected Vector direction = new Vector();

    /* (non-Javadoc)
     * @see de.miethxml.kabeja.dxf.DXFEntity#getBounds()
     */
    public Bounds getBounds() {
        // we will only add the base point
        //the end is infinite
        bounds.addToBounds(basePoint);

        return bounds;
    }

    /* (non-Javadoc)
     * @see de.miethxml.kabeja.dxf.DXFEntity#getType()
     */
    public String getType() {
        return DXFConstants.ENTITY_TYPE_RAY;
    }

    /* (non-Javadoc)
     * @see de.miethxml.kabeja.svg.SVGGenerator#toSAX(org.xml.sax.ContentHandler, java.util.Map)
     */
    public void toSAX(ContentHandler handler, Map svgContext)
        throws SAXException {
        Bounds b = (Bounds) svgContext.get(SVGContext.DRAFT_BOUNDS);

        //we will create a line, which goes over or to the end of the draft bounds
        double t = Math.sqrt(Math.pow(b.getHeight(), 2) +
                Math.pow(b.getWidth(), 2));

        Point end = DXFUtils.getPointFromParameterizedLine(basePoint,
                direction, t);

        AttributesImpl atts = new AttributesImpl();
        SVGUtils.addAttribute(atts, "x1", "" + basePoint.getX());
        SVGUtils.addAttribute(atts, "y1", "" + basePoint.getY());
        SVGUtils.addAttribute(atts, "x2", "" + end.getX());
        SVGUtils.addAttribute(atts, "y2", "" + end.getY());
        super.setCommonAttributes(atts);

        SVGUtils.emptyElement(handler, SVGConstants.SVG_LINE, atts);
    }

    /**
     * @return Returns the basePoint.
     */
    public Point getBasePoint() {
        return basePoint;
    }

    /**
     * @param basePoint The basePoint to set.
     */
    public void setBasePoint(Point basePoint) {
        this.basePoint = basePoint;
    }

    /**
     * @return Returns the direction.
     */
    public Vector getDirection() {
        return direction;
    }

    /**
     * @param direction The direction to set.
     */
    public void setDirection(Vector direction) {
        this.direction = direction;
    }
}
