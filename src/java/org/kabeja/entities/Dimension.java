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
/*
 * Created on Jan 4, 2005
 *
 *
 */
package org.kabeja.entities;

import org.kabeja.common.Block;
import org.kabeja.common.Type;
import org.kabeja.entities.util.DimensionStyle;
import org.kabeja.math.Bounds;
import org.kabeja.math.Point3D;
import org.kabeja.math.TransformContext;


/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth </a>
 *
 *
 *
 */
public class Dimension extends Entity {
    protected final int TYPE_LINEAR = 0;
    protected final int TYPE_ALIGNMENT = 1;
    protected final int TYPE_4POINT = 2;
    protected final int TYPE_DIAMETER = 3;
    protected final int TYPE_RADIAL = 4;
    protected final int TYPE_3POINT_ANGLE = 5;
    protected final int TYPE_COORDINATES = 6;
    protected int dimType;
    protected Point3D referencePoint = new Point3D();
    protected Point3D textPoint = new Point3D();
    protected Point3D insertPoint = new Point3D();
    protected Point3D referencePoint3 = new Point3D();
    protected Point3D referencePoint4 = new Point3D();
    protected Point3D referencePoint5 = new Point3D();
    protected Point3D referencePoint6 = new Point3D();
    protected int attechmentLocation;
    protected boolean exactTextLineSpacing = false;
    protected double rotate = 0;
    protected double horizontalDirection = 0;
    protected String dimensionStyle = "";
    protected String dimensionText = "";
    protected String dimensionBlock = "";
    protected int dimensionArea = 0;
    protected double textRotation = 0.0;
    protected double dimensionRotation = 0.0;
    protected double inclinationHelpLine = 0.0;
    protected double leadingLineLength = 0.0;
    protected double horizontalAlign = 0.0;

    public Dimension() {
    }

    /**
     * @return Returns the attechmentLocation.
     */
    public int getAttechmentLocation() {
        return attechmentLocation;
    }

    /**
     * @param attechmentLocation
     *            The attechmentLocation to set.
     */
    public void setAttechmentLocation(int attechmentLocation) {
        this.attechmentLocation = attechmentLocation;
    }

    /**
     * @return Returns the dimensionStyle.
     */
    public String getDimensionStyleID() {
        return dimensionStyle;
    }

    /**
     * @param dimensionStyle
     *            The dimensionStyle to set.
     */
    public void setDimensionStyleID(String dimensionStyle) {
        this.dimensionStyle = dimensionStyle;
    }

    /**
     * @return Returns the exactTextLineSpacing.
     */
    public boolean isExactTextLineSpacing() {
        return exactTextLineSpacing;
    }

    /**
     * @param exactTextLineSpacing
     *            The exactTextLineSpacing to set.
     */
    public void setExactTextLineSpacing(boolean exactTextLineSpacing) {
        this.exactTextLineSpacing = exactTextLineSpacing;
    }

    /**
     * @return Returns the horizontalDirection.
     */
    public double getHorizontalDirection() {
        return horizontalDirection;
    }

    /**
     * @param horizontalDirection
     *            The horizontalDirection to set.
     */
    public void setHorizontalDirection(double horizontalDirection) {
        this.horizontalDirection = horizontalDirection;
    }

    /**
     * @return Returns the insertPoint.
     */
    public Point3D getInsertPoint() {
        return insertPoint;
    }

    /**
     * @param insertPoint
     *            The insertPoint to set.
     */
    public void setInsertPoint(Point3D insertPoint) {
        this.insertPoint = insertPoint;
    }

    /**
     * @return Returns the referencePoint.
     */
    public Point3D getReferencePoint() {
        return referencePoint;
    }

    /**
     * @param referencePoint
     *            The referencePoint to set.
     */
    public void setReferencePoint(Point3D referencePoint) {
        this.referencePoint = referencePoint;
    }

    /**
     * @return Returns the referencePoint3.
     */
    public Point3D getReferencePoint3() {
        return referencePoint3;
    }

    /**
     * @param referencePoint3
     *            The referencePoint3 to set.
     */
    public void setReferencePoint3(Point3D referencePoint3) {
        this.referencePoint3 = referencePoint3;
    }

    /**
     * @return Returns the referencePoint4.
     */
    public Point3D getReferencePoint4() {
        return referencePoint4;
    }

    /**
     * @param referencePoint4
     *            The referencePoint4 to set.
     */
    public void setReferencePoint4(Point3D referencePoint4) {
        this.referencePoint4 = referencePoint4;
    }

    /**
     * @return Returns the referencePoint5.
     */
    public Point3D getReferencePoint5() {
        return referencePoint5;
    }

    /**
     * @param referencePoint5
     *            The referencePoint5 to set.
     */
    public void setReferencePoint5(Point3D referencePoint5) {
        this.referencePoint5 = referencePoint5;
    }

    /**
     * @return Returns the referencePoint6.
     */
    public Point3D getReferencePoint6() {
        return referencePoint6;
    }

    /**
     * @param referencePoint6
     *            The referencePoint6 to set.
     */
    public void setReferencePoint6(Point3D referencePoint6) {
        this.referencePoint6 = referencePoint6;
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
     * @return Returns the textPoint.
     */
    public Point3D getTextPoint() {
        return textPoint;
    }

    /**
     * @param textPoint
     *            The textPoint to set.
     */
    public void setTextPoint(Point3D textPoint) {
        this.textPoint = textPoint;
    }

    /**
     * @return Returns the type.
     */
    public int getDimensionType() {
        return dimType;
    }

    /**
     * @param type
     *            The type to set.
     */
    public void setDimensionType(int type) {
        this.dimType = type;
    }

    public double getDimensionRotation() {
        return dimensionRotation;
    }

    public void setDimensionRotation(double dimensionRotation) {
        this.dimensionRotation = dimensionRotation;
    }

    public String getDimensionText() {
        return dimensionText;
    }

    public void setDimensionText(String dimensionText) {
        this.dimensionText = dimensionText;
    }

    public double getHorizontalAlign() {
        return horizontalAlign;
    }

    public void setHorizontalAlign(double horizontalAlign) {
        this.horizontalAlign = horizontalAlign;
    }

    public double getInclinationHelpLine() {
        return inclinationHelpLine;
    }

    public void setInclinationHelpLine(double inclinationHelpLine) {
        this.inclinationHelpLine = inclinationHelpLine;
    }

    public double getLeadingLineLength() {
        return leadingLineLength;
    }

    public void setLeadingLineLength(double leadingLineLength) {
        this.leadingLineLength = leadingLineLength;
    }

    public double getTextRotation() {
        return textRotation;
    }

    public void setTextRotation(double textRotation) {
        this.textRotation = textRotation;
    }

    public String getDimensionBlock() {
        return dimensionBlock;
    }

    public void setDimensionBlock(String dimensionBlock) {
        this.dimensionBlock = dimensionBlock;
    }

    public int getDimensionArea() {
        return dimensionArea;
    }

    public void setDimensionArea(int dimensionArea) {
        this.dimensionArea = dimensionArea;
    }

    public Bounds getBounds() {
        // TODO add real bounds
        Bounds bounds = new Bounds();

        if (this.doc.getBlock(this.dimensionBlock) != null) {
            Block block = doc.getBlock(this.getDimensionBlock());
            Bounds b = block.getBounds();
            Point3D refPoint = block.getReferencePoint();

            if (b.isValid()) {
                // Translate to origin
                bounds.setMaximumX((b.getMaximumX() - refPoint.getX()));
                bounds.setMinimumX((b.getMinimumX() - refPoint.getX()));
                bounds.setMaximumY((b.getMaximumY() - refPoint.getY()));
                bounds.setMinimumY((b.getMinimumY() - refPoint.getY()));

                // translate to the InsertPoint
                bounds.setMaximumX(bounds.getMaximumX() +
                    this.insertPoint.getX());
                bounds.setMinimumX(bounds.getMinimumX() +
                    this.insertPoint.getX());
                bounds.setMaximumY(bounds.getMaximumY() +
                    this.insertPoint.getY());
                bounds.setMinimumY(bounds.getMinimumY() +
                    this.insertPoint.getY());
                ;
            }
        } else {
            bounds.setValid(false);
        }

        return bounds;
    }

    public DimensionStyle getDimensionStyle() {
        return doc.getDimensionStyle(getDimensionStyleID());
    }

    public Type<Dimension> getType() {
        return Type.TYPE_DIMENSTION;
    }

    public double getLength() {
        return 0;
    }
    
    /**
     * Not implemented yet
     */
    
    public void transform(TransformContext context) {
      
    }
}
