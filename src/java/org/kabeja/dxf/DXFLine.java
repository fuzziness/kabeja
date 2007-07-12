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

import java.util.Map;

import org.kabeja.dxf.helpers.MathUtils;
import org.kabeja.dxf.helpers.Point;
import org.kabeja.svg.SVGConstants;
import org.kabeja.svg.SVGPathBoundaryElement;
import org.kabeja.svg.SVGUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 *
 */
public class DXFLine extends DXFEntity implements SVGPathBoundaryElement {
    private Point start;
    private Point end;

    public DXFLine() {
        start = new Point();
        end = new Point();
    }

    public void setProperty(int groupcode, String value) {
    }

    public void setStartPoint(Point start) {
        this.start = start;
    }

    /**
     * @return Returns the end.
     */
    public Point getEndPoint() {
        return end;
    }

    /**
     * @param end
     *            The end to set.
     */
    public void setEndPoint(Point end) {
        this.end = end;
    }

    /**
     * @return Returns the start.
     */
    public Point getStartPoint() {
        return start;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.dxf2svg.xml.DXFEntity#toSAX(org.xml.sax.ContentHandler)
     */
    public void toSAX(ContentHandler handler, Map svgContext)
        throws SAXException {
        AttributesImpl attr = new AttributesImpl();

        // set the attributes
        SVGUtils.addAttribute(attr, "x1", "" + this.getStartPoint().getX());
        SVGUtils.addAttribute(attr, "y1", "" + this.getStartPoint().getY());
        SVGUtils.addAttribute(attr, "x2", "" + this.getEndPoint().getX());
        SVGUtils.addAttribute(attr, "y2", "" + this.getEndPoint().getY());
        super.setCommonAttributes(attr, svgContext);
        SVGUtils.emptyElement(handler, SVGConstants.SVG_LINE, attr);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.dxf2svg.dxf.DXFEntity#updateViewPort()
     */
    public Bounds getBounds() {
        bounds.addToBounds(this.end.getX(), this.end.getY());
        bounds.addToBounds(this.start.getX(), this.start.getY());

        return bounds;
    }

    public String getType() {
        return DXFConstants.ENTITY_TYPE_LINE;
    }

    /* (non-Javadoc)
     * @see org.kabeja.dxf.helpers.HatchBoundaryEdge#getSVGPath()
     */
    public String getSVGPath() {
        StringBuffer buf = new StringBuffer();
        buf.append("M ");
        buf.append(start.getX());
        buf.append(' ');
        buf.append(start.getY());
        buf.append(" L ");
        buf.append(end.getX());
        buf.append(' ');
        buf.append(end.getY());

        return buf.toString();
    }

	public double getLength() {
		
		return MathUtils.distance(this.start,this.end);
	}
    
    
}
