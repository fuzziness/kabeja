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
import java.util.Iterator;
import java.util.List;
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
public class DXFLeader extends DXFEntity {
    protected String styleName = "";
    protected double arrowHeadSize = 0.0;
    protected double textGap;
    protected double scaleFactor;
    protected double textWidth;
    protected double textHeight;
    protected List coordinates = new ArrayList();
    protected int pathType = 0;
    protected int creationType = 0;
    protected int hooklineDirecton = 0;
    protected boolean hookline = false;
    protected Point horizontalDirection = new Point();
    protected Point lastOffsetText = new Point();
    protected Point lastOffsetInsertion = new Point();
    protected boolean arrowEnabled = false;
    protected String textID = "";

    /**
     * @return Returns the textID.
     */
    public String getTextID() {
        return textID;
    }

    /**
     * @param textID
     *            The textID to set.
     */
    public void setTextID(String textID) {
        this.textID = textID;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kabeja.dxf.DXFEntity#getBounds()
     */
    public Bounds getBounds() {
        bounds.setValid(false);

        return bounds;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kabeja.dxf.DXFEntity#getType()
     */
    public String getType() {
        return DXFConstants.ENTITY_TYPE_LEADER;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kabeja.svg.SVGGenerator#toSAX(org.xml.sax.ContentHandler,
     *      java.util.Map)
     */
    public void toSAX(ContentHandler handler, Map svgContext)
        throws SAXException {
        // TODO handle spline
        if (!isSplinePath()) {
            Iterator i = this.coordinates.iterator();
            StringBuffer buf = new StringBuffer();
            buf.append('M');

            while (i.hasNext()) {
                buf.append(' ');

                Point p = (Point) i.next();
                buf.append(p.getX());
                buf.append(' ');
                buf.append(p.getY());

                if (i.hasNext()) {
                    buf.append(" L");
                }
            }


            AttributesImpl attr = new AttributesImpl();
            super.setCommonAttributes(attr, svgContext);
            SVGUtils.addAttribute(attr, "d", buf.toString());
            SVGUtils.emptyElement(handler, SVGConstants.SVG_PATH, attr);


            //the used DIMSTYLE
            DXFDimensionStyle style = this.doc.getDXFDimensionStyle(this.styleName);

            if (isArrowEnabled() && style != null) {
                //the arrow
                if (style.hasProperty(DXFDimensionStyle.PROPERTY_DIMLDRBLK)) {
                    String blockID = style.getProperty(DXFDimensionStyle.PROPERTY_DIMLDRBLK);

                    if (this.coordinates.size() > 1) {
                        Point p1 = (Point) this.coordinates.get(0);
                        Point p2 = (Point) this.coordinates.get(0);
                        double length = MathUtils.distance(p1, p2);
                        double arrowLength = style.getDoubleProperty(DXFDimensionStyle.PROPERTY_DIMASZ,
                                0.0) * style.getDoubleProperty(DXFDimensionStyle.PROPERTY_DIMSCALE,
                                1.0);

                        if (length > (2 * arrowLength)) {
                            double angle = Math.toDegrees(MathUtils.getAngle(
                                        MathUtils.getVector(p1, p2),
                                        DXFConstants.DEFAULT_X_AXIS_VECTOR));

                            attr = new AttributesImpl();

                            if (angle != 0.0) {
                                SVGUtils.addAttribute(attr, "transform",
                                    "rotate(" + angle + ")");
                            }

                            SVGUtils.startElement(handler,
                                SVGConstants.SVG_GROUP, attr);
                            attr = new AttributesImpl();
                        	attr.addAttribute(SVGConstants.XMLNS_NAMESPACE, "xlink", "xmlns:xlink", "CDATA",
            	                    SVGConstants.XLINK_NAMESPACE);

                            attr.addAttribute(SVGConstants.XLINK_NAMESPACE,
                                "href", "xlink:href", "CDATA",
                                "#" + SVGUtils.validateID(blockID));

                            SVGUtils.emptyElement(handler,
                                SVGConstants.SVG_USE, attr);

                            SVGUtils.endElement(handler, SVGConstants.SVG_GROUP);
                        }
                    }
                }
            }
        }
    }

    /**
     * @return Returns the arrowHeadSize.
     */
    public double getArrowHeadSize() {
        return arrowHeadSize;
    }

    /**
     * @param arrowHeadSize
     *            The arrowHeadSize to set.
     */
    public void setArrowHeadSize(double arrowHeadSize) {
        this.arrowHeadSize = arrowHeadSize;
    }

    /**
     * @return Returns the creationType.
     */
    public int getCreationType() {
        return creationType;
    }

    /**
     * @param creationType
     *            The creationType to set.
     */
    public void setCreationType(int creationType) {
        this.creationType = creationType;
    }

    /**
     * @return Returns the hookline.
     */
    public boolean isHookline() {
        return hookline;
    }

    /**
     * @param hookline
     *            The hookline to set.
     */
    public void setHookline(boolean hookline) {
        this.hookline = hookline;
    }

    /**
     * @return Returns the hooklineDirecton.
     */
    public int getHooklineDirecton() {
        return hooklineDirecton;
    }

    /**
     * @param hooklineDirecton
     *            The hooklineDirecton to set.
     */
    public void setHooklineDirecton(int hooklineDirecton) {
        this.hooklineDirecton = hooklineDirecton;
    }

    /**
     * @return Returns the horizontalDirection.
     */
    public Point getHorizontalDirection() {
        return horizontalDirection;
    }

    /**
     * @param horizontalDirection
     *            The horizontalDirection to set.
     */
    public void setHorizontalDirection(Point horizontalDirection) {
        this.horizontalDirection = horizontalDirection;
    }

    /**
     * @return Returns the lastOffsetInsertion.
     */
    public Point getLastOffsetInsertion() {
        return lastOffsetInsertion;
    }

    /**
     * @param lastOffsetInsertion
     *            The lastOffsetInsertion to set.
     */
    public void setLastOffsetInsertion(Point lastOffsetInsertion) {
        this.lastOffsetInsertion = lastOffsetInsertion;
    }

    /**
     * @return Returns the lastOffsetText.
     */
    public Point getLastOffsetText() {
        return lastOffsetText;
    }

    /**
     * @param lastOffsetText
     *            The lastOffsetText to set.
     */
    public void setLastOffsetText(Point lastOffsetText) {
        this.lastOffsetText = lastOffsetText;
    }

    /**
     * @return Returns the pathType.
     */
    public int getPathType() {
        return pathType;
    }

    /**
     * @param pathType
     *            The pathType to set.
     */
    public void setPathType(int pathType) {
        this.pathType = pathType;
    }

    /**
     * @return Returns the scaleFactor.
     */
    public double getScaleFactor() {
        return scaleFactor;
    }

    /**
     * @param scaleFactor
     *            The scaleFactor to set.
     */
    public void setScaleFactor(double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    /**
     * @return Returns the styleName.
     */
    public String getStyleNameID() {
        return styleName;
    }

    /**
     * @param styleName
     *            The styleName to set.
     */
    public void setStyleNameID(String styleName) {
        this.styleName = styleName;
    }

    /**
     * @return Returns the textGap.
     */
    public double getTextGap() {
        return textGap;
    }

    /**
     * @param textGap
     *            The textGap to set.
     */
    public void setTextGap(double textGap) {
        this.textGap = textGap;
    }

    /**
     * @return Returns the textHeight.
     */
    public double getTextHeight() {
        return textHeight;
    }

    /**
     * @param textHeight
     *            The textHeight to set.
     */
    public void setTextHeight(double textHeight) {
        this.textHeight = textHeight;
    }

    /**
     * @return Returns the textWidth.
     */
    public double getTextWidth() {
        return textWidth;
    }

    /**
     * @param textWidth
     *            The textWidth to set.
     */
    public void setTextWidth(double textWidth) {
        this.textWidth = textWidth;
    }

    public void addCoordinate(Point vertex) {
        coordinates.add(vertex);
    }

    /**
     * @return Returns the arrowEnabled.
     */
    public boolean isArrowEnabled() {
        return arrowEnabled;
    }

    /**
     * @param arrowEnabled
     *            The arrowEnabled to set.
     */
    public void setArrowEnabled(boolean arrowEnabled) {
        this.arrowEnabled = arrowEnabled;
    }

    public boolean isSplinePath() {
        return this.pathType == 1;
    }

	public double getLength() {
		// TODO Auto-generated method stub
		return 0;
	}
    
    
}
