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
import org.kabeja.svg.SVGUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 */
public class DXFSolid extends DXFEntity {
    protected Point point1 = new Point();
    protected Point point2 = new Point();
    protected Point point3 = new Point();
    protected Point point4 = new Point();

    public DXFSolid() {
    }

    public Bounds getBounds() {
        Bounds bounds = new Bounds();

        bounds.addToBounds(point1);
        bounds.addToBounds(point2);
        bounds.addToBounds(point3);
        bounds.addToBounds(point4);

        return bounds;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.kabeja.svg.SVGGenerator#toSAX(org.xml.sax.ContentHandler)
     */
    public void toSAX(ContentHandler handler, Map svgContext)
        throws SAXException {
        // output as polygon
        AttributesImpl attr = new AttributesImpl();

        StringBuffer points = new StringBuffer();

        // the sequence p1->p2->p4->p3 is defined
        // by the DXF specs
        points.append(point1.getX());
        points.append(",");
        points.append(point1.getY());
        points.append(" ");

        points.append(point2.getX());
        points.append(",");
        points.append(point2.getY());
        points.append(" ");

        points.append(point4.getX());
        points.append(",");
        points.append(point4.getY());
        points.append(" ");

        points.append(point3.getX());
        points.append(",");
        points.append(point3.getY());
        points.append(" ");

        SVGUtils.addAttribute(attr, "points", points.toString());

        super.setCommonAttributes(attr);

        // if the fillmode attribute is non-zero the solid is filled
        if (doc.getDXFHeader().isFillMode()) {
            SVGUtils.addAttribute(attr, "fill", "currentColor");
        }

        SVGUtils.emptyElement(handler, SVGConstants.SVG_POLYGON, attr);
    }

    /**
     * @return Returns the point1.
     */
    public Point getPoint1() {
        return point1;
    }

    /**
     * @param point1
     *            The point1 to set.
     */
    public void setPoint1(Point point1) {
        this.point1 = point1;
    }

    /**
     * @return Returns the point2.
     */
    public Point getPoint2() {
        return point2;
    }

    /**
     * @param point2
     *            The point2 to set.
     */
    public void setPoint2(Point point2) {
        this.point2 = point2;
    }

    /**
     * @return Returns the point3.
     */
    public Point getPoint3() {
        return point3;
    }

    /**
     * @param point3
     *            The point3 to set.
     */
    public void setPoint3(Point point3) {
        this.point3 = point3;
    }

    /**
     * @return Returns the point4.
     */
    public Point getPoint4() {
        return point4;
    }

    /**
     * @param point4
     *            The point4 to set.
     */
    public void setPoint4(Point point4) {
        this.point4 = point4;
    }

    public String getType() {
        return DXFConstants.ENTITY_TYPE_SOLID;
    }

	public double getLength() {
		double length=0.0;
		length+=MathUtils.distance(this.point1,this.point2);
		length+=MathUtils.distance(this.point2,this.point4);
		length+=MathUtils.distance(this.point4,this.point3);
		length+=MathUtils.distance(this.point3,this.point1);
		return length;
	}
    
    
}
