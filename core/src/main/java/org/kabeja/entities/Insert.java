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

package org.kabeja.entities;

import java.util.ArrayList;
import java.util.List;

import org.kabeja.common.Block;
import org.kabeja.common.Type;
import org.kabeja.math.Bounds;
import org.kabeja.math.Point3D;
import org.kabeja.math.TransformContext;


/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 */
public class Insert extends Entity {
    private Point3D insertPoint = new Point3D();
    private double scale_x = 1.0;
    private double scale_y = 1.0;
    private double scale_z = 1.0;
    private double rotate = 0;
    private int rows = 1;
    private int columns = 1;
    private double row_spacing = 0;
    private double column_spacing = 0;
    private String blockName = "";
    private List<Attrib> attributes = new ArrayList<Attrib>();

    /**
     *
     */
    public Insert() {
    }

   
    public Bounds getBounds() {
        Bounds bounds = new Bounds();

        // extrusion.calculateExtrusion();
        // get the Block bounds
        Block block = this.getBlock();
        Bounds b = block.getBounds();

        if (!b.isValid()) {
            bounds.setValid(false);

            return bounds;
        }

        Point3D blkPoint = block.getReferencePoint();

        // Translate to origin and scale
        bounds.setMaximumX((b.getMaximumX() - blkPoint.getX()) * scale_x);
        bounds.setMinimumX((b.getMinimumX() - blkPoint.getX()) * scale_x);
        bounds.setMaximumY((b.getMaximumY() - blkPoint.getY()) * scale_y);
        bounds.setMinimumY((b.getMinimumY() - blkPoint.getY()) * scale_y);

        // Rotate the Bounds
        if (rotate != 0) {
            Point3D p1 = rotatePoint(bounds.getMaximumX(), bounds.getMaximumY());
            Point3D p2 = rotatePoint(bounds.getMaximumX(), bounds.getMinimumY());
            Point3D p3 = rotatePoint(bounds.getMinimumX(), bounds.getMaximumY());
            Point3D p4 = rotatePoint(bounds.getMinimumX(), bounds.getMinimumY());

            // we have now 4 Points
            // and we have to check all if they are the new maximum/minimum
            // start with a empty bounds
            bounds = new Bounds();

            bounds.addToBounds(p1);
            bounds.addToBounds(p2);
            bounds.addToBounds(p3);
            bounds.addToBounds(p4);
        }

        // translate to the InsertPoint
        bounds.setMaximumX(bounds.getMaximumX() + insertPoint.getX());
        bounds.setMinimumX(bounds.getMinimumX() + insertPoint.getX());
        bounds.setMaximumY(bounds.getMaximumY() + insertPoint.getY());
        bounds.setMinimumY(bounds.getMinimumY() + insertPoint.getY());

        // add the space from columns and rows
        double width = (columns - 1) * column_spacing;
        double height = (rows - 1) * row_spacing;

        if (width >= 0) {
            bounds.setMinimumX(bounds.getMinimumX() - width);
        } else {
            bounds.setMaximumX(bounds.getMaximumX() - width);
        }

        if (height >= 0) {
            bounds.setMinimumY(bounds.getMinimumY() - height);
        } else {
            bounds.setMaximumY(bounds.getMaximumY() - height);
        }

        return bounds;
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
     * @return Returns the rows count.
     */
    public int getRows() {
        return rows;
    }

    /**
     * @param rows
     *            The rows count to set.
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

    private Point3D rotatePoint(double x, double y) {
        double phi = Math.toRadians(rotate);
        Point3D point = new Point3D();

        // the rotation matrix
        point.setX((x * Math.cos(phi)) - (y * Math.sin(phi)));
        point.setY((x * Math.sin(phi)) + (y * Math.cos(phi)));

        return point;
    }

    public Type<Insert> getType() {
        return Type.TYPE_INSERT;
    }

    public double getLength() {
        return this.doc.getBlock(this.blockName).getLength();
    }
    
    
    public boolean hasAttributes(){
    	return this.attributes.size()>0;
    }
    
/**
 * Add a associated block attribute
 * @param attrib
 */
    public void addAttribute(Attrib attrib){
    	this.attributes.add(attrib);
    }
    /**
     * Get the associated block attributes count
     * @return
     */
    public int getAttributeCount(){
    	return this.attributes.size();
    }
    
    
    /**
     * Get the associated block attribute with given index
     * @param index of the associated attribute
     * @return the associated attribute for the given index or null
     */
    public Attrib getAttribute(int index){
    	return (Attrib)this.attributes.get(index);
    }
    
    
    public List<Attrib> getAttributes(){
    	return this.attributes;
    }
    
    
    
    

    /**
     * @return the insertPoint
     */
    public Point3D getInsertPoint() {
        return insertPoint;
    }

    /**
     * @param insertPoint the insertPoint to set
     */
    public void setInsertPoint(Point3D insertPoint) {
        this.insertPoint = insertPoint;
    }

    
    
    public Block getBlock(){
        return doc.getBlock(getBlockName());
    }
    
       
    /**
     * @return the blockName
     */
    public String getBlockName() {
        return blockName;
    }

    /**
     * @param blockName the blockName to set
     */
    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }
    
    /**
     * Not implemented yet
     */
    
    public void transform(TransformContext context) {
      
        this.setInsertPoint(context.transform(this.getInsertPoint()));
        
    }
    
}
