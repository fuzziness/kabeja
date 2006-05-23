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
public class DXFInsert extends DXFEntity {
    private Point p;
    private double scale_x = 1.0;
    private double scale_y = 1.0;
    private double scale_z = 1.0;
    private double rotate = 0;
    private int rows = 1;
    private int columns = 1;
    private double row_spacing = 0;
    private double column_spacing = 0;
    private String blockID = "";

    /**
     *
     */
    public DXFInsert() {
        p = new Point();
        bounds = new Bounds();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.dxf2svg.dxf.DXFEntity#getBounds()
     */
    public Bounds getBounds() {
        Bounds insert = new Bounds();

        // extrusion.calculateExtrusion();
        // get the Block bounds
        DXFBlock block = doc.getDXFBlock(getBlockID());
        Bounds b = block.getBounds();

        if (!b.isValid()) {
            insert.setValid(false);

            return insert;
        }

        Point blkPoint = block.getReferencePoint();

        // Translate to origin and scale
        insert.setMaximumX((b.getMaximumX() - blkPoint.getX()) * scale_x);
        insert.setMinimumX((b.getMinimumX() - blkPoint.getX()) * scale_x);
        insert.setMaximumY((b.getMaximumY() - blkPoint.getY()) * scale_y);
        insert.setMinimumY((b.getMinimumY() - blkPoint.getY()) * scale_y);

        // Rotate the Bounds
        if (rotate != 0) {
            Point p1 = rotatePoint(insert.getMaximumX(), insert.getMaximumY());
            Point p2 = rotatePoint(insert.getMaximumX(), insert.getMinimumY());
            Point p3 = rotatePoint(insert.getMinimumX(), insert.getMaximumY());
            Point p4 = rotatePoint(insert.getMinimumX(), insert.getMinimumY());

            // we have now 4 Points
            // and we have to check all if they are the new maximum/minimum
            // start with a empty bounds
            insert = new Bounds();

            insert.addToBounds(p1);
            insert.addToBounds(p2);
            insert.addToBounds(p3);
            insert.addToBounds(p4);
        }

        // translate to the InsertPoint
        insert.setMaximumX(insert.getMaximumX() + p.getX());
        insert.setMinimumX(insert.getMinimumX() + p.getX());
        insert.setMaximumY(insert.getMaximumY() + p.getY());
        insert.setMinimumY(insert.getMinimumY() + p.getY());

        // add the space from columns and rows
        double width = (columns - 1) * column_spacing;
        double height = (rows - 1) * row_spacing;

        if (width >= 0) {
            insert.setMinimumX(insert.getMinimumX() - width);
        } else {
            insert.setMaximumX(insert.getMaximumX() - width);
        }

        if (height >= 0) {
            insert.setMinimumY(insert.getMinimumY() - height);
        } else {
            insert.setMaximumY(insert.getMaximumY() - height);
        }

       

        return insert;
    }

    /**
     * @return Returns the blockID.
     */
    public String getBlockID() {
        return blockID;
    }

    /**
     * @param blockID
     *            The blockID to set.
     */
    public void setBlockID(String blockID) {
        this.blockID = blockID;
    }

    /**
     * @return Returns the column_spacing.
     */
    public double getColumnSpacing() {
        return column_spacing;
    }

    /**
     * @param column_spacing
     *            The column_spacing to set.
     */
    public void setColumnSpacing(double column_spacing) {
        this.column_spacing = column_spacing;
    }

    /**
     * @return Returns the columns.
     */
    public int getColumns() {
        return columns;
    }

    /**
     * @param columns
     *            The columns to set.
     */
    public void setColumns(int columns) {
        this.columns = columns;
    }

    /**
     * @return Returns the p.
     */
    public Point getPoint() {
        return p;
    }

    /**
     * @param p
     *            The p to set.
     */
    public void setPoint(Point p) {
        this.p = p;
    }

    /**
     * @return Returns the rotate.
     */
    public double getRotate() {
        return rotate;
    }

    /**
     * @param rotate
     *            The rotate to set.
     */
    public void setRotate(double rotate) {
        this.rotate = rotate;
    }

    /**
     * @return Returns the row_spacing.
     */
    public double getRowSpacing() {
        return row_spacing;
    }

    /**
     * @param row_spacing
     *            The row_spacing to set.
     */
    public void setRowSpacing(double row_spacing) {
        this.row_spacing = row_spacing;
    }

    /**
     * @return Returns the rows.
     */
    public int getRows() {
        return rows;
    }

    /**
     * @param rows
     *            The rows to set.
     */
    public void setRows(int rows) {
        this.rows = rows;
    }

    /**
     * @return Returns the scale_x.
     */
    public double getScaleX() {
        return scale_x;
    }

    /**
     * @param scale_x
     *            The scale_x to set.
     */
    public void setScaleX(double scale_x) {
        this.scale_x = scale_x;
    }

    /**
     * @return Returns the scale_y.
     */
    public double getScaleY() {
        return scale_y;
    }

    /**
     * @param scale_y
     *            The scale_y to set.
     */
    public void setScaleY(double scale_y) {
        this.scale_y = scale_y;
    }

    /**
     * @return Returns the scale_z.
     */
    public double getScaleZ() {
        return scale_z;
    }

    /**
     * @param scale_z
     *            The scale_z to set.
     */
    public void setScaleZ(double scale_z) {
        this.scale_z = scale_z;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.dxf2svg.svg.SVGGenerator#toSAX(org.xml.sax.ContentHandler)
     */
    public void toSAX(ContentHandler handler, Map svgContext)
        throws SAXException {
        DXFBlock block = doc.getDXFBlock(getBlockID());

        StringBuffer buf = new StringBuffer();

        Point bp = block.getReferencePoint();

        // translate to the insert point all the rows and columns
        for (int column = 0; column < columns; column++) {
            for (int row = 0; row < rows; row++) {
                // translate to the insert point
                buf.append("translate(");
                buf.append((p.getX() - (column_spacing * column)));
                buf.append(" ");
                buf.append((p.getY() - (row_spacing * row)));
                buf.append(")");

                // then rotate
                if (rotate != 0) {
                    buf.append(" rotate(");
                    buf.append(rotate);
                    buf.append(")");
                }

                // then scale
                if ((scale_x != 1.0) || (scale_y != 1.0)) {
                    buf.append(" scale(");
                    buf.append(scale_x);
                    buf.append(" ");
                    buf.append(scale_y);
                    buf.append(")");
                }

                if ((bp.getX() != 0.0) || (bp.getY() != 0.0)) {
                    buf.append(" translate(");
                    buf.append(bp.getX());
                    buf.append(" ");
                    buf.append(bp.getY());
                    buf.append(")");
                }

                AttributesImpl attr = new AttributesImpl();
                SVGUtils.addAttribute(attr, "transform", buf.toString());

                // add common attributes
                super.setCommonAttributes(attr);

                // fix the scale of stroke-width
                if ((this.scale_x + this.scale_y) > 0) {
                    double width = 0.04 / (this.scale_x + this.scale_y);
                    SVGUtils.addAttribute(attr, "stroke-width",
                        "" + SVGUtils.formatNumberAttribute(width) + "%");
                }

                // SVGUtils.startElement(handler, SVGConstants.SVG_GROUP, attr);
                // attr = new AttributesImpl();
                attr.addAttribute("", "", "xmlns:xlink", "CDATA",
                    SVGConstants.XLINK_NAMESPACE);

                attr.addAttribute(SVGConstants.XLINK_NAMESPACE, "href",
                    "xlink:href", "CDATA",
                    "#" + SVGUtils.validateID(getBlockID()));

                SVGUtils.emptyElement(handler, SVGConstants.SVG_USE, attr);

                // SVGUtils.endElement(handler, SVGConstants.SVG_GROUP);
                buf.delete(0, buf.length());
            }
        }
    }

    private Point rotatePoint(double x, double y) {
        double phi = Math.toRadians(rotate);
        Point point = new Point();

        // the rotation matrix
        point.setX((x * Math.cos(phi)) - (y * Math.sin(phi)));
        point.setY((x * Math.sin(phi)) + (y * Math.cos(phi)));

        return point;
    }

    public String getType() {
        return DXFConstants.ENTITY_TYPE_INSERT;
    }

	public double getLength() {	
		return this.doc.getDXFBlock(this.blockID).getLength();
	}
    
	
}
