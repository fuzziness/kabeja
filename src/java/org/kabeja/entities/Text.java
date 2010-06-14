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

import org.kabeja.DraftDocument;
import org.kabeja.common.DraftEntity;
import org.kabeja.common.Type;
import org.kabeja.entities.util.TextDocument;
import org.kabeja.entities.util.TextParser;
import org.kabeja.entities.util.Utils;
import org.kabeja.math.Bounds;
import org.kabeja.math.Point3D;
import org.kabeja.math.TransformContext;

/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 * 
 */
public class Text extends Entity {
    public static final double DEFAULT_FONT_SIZE = 8;

    public static final int VALIGN_BASELINE = 0;

    public static final int VALIGN_BOTTOM = 1;

    public static final int VALIGN_CENTER = 2;

    public static final int VALIGN_TOP = 3;

    public static final int ALIGN_LEFT = 0;

    public static final int ALIGN_CENTER = 1;

    public static final int ALIGN_RIGHT = 2;

    public static final int ALIGN_ALIGNED = 3;

    public static final int ALIGN_MIDDLE = 4;

    public static final int ALIGN_FIT = 5;

    protected double rotation = 0.0;

    protected double height = 0.0;

    protected double scale_x = 1.0;

    protected double oblique_angle = 0.0;

    protected double align_x = 0.0;

    protected double align_y = 0.0;

    protected double align_z = 0.0;
    
 

    // the horizontal align
    protected int align = 0;

    // the vertical align
    protected int valign = 0;

    protected String text = "";

    protected String textStyle = "";

    protected Point3D p = new Point3D();

    protected Point3D alignmentPoint = new Point3D();

    protected Point3D align_p2;
    
    private final static int BOOLEAN_BIT_ALIGNMENTPOINTSET=10;
  
//    private final static int BOOLEAN_BIT_BACKWARD=11;
//    private final static int BOOLEAN_BIT_UPSIDEDOWN=12;
//    private final static int BOOLEAN_BIT_TOP=13;
//    private final static int BOOLEAN_BIT_BOTTOM=14;
//    private final static int BOOLEAN_VERTICAL_CENTER=15;

    

   

    

    protected TextDocument textDoc = new TextDocument();

    protected int textGenerationFlag = 0;



    public void setDocument(DraftDocument doc) {
        super.setDocument(doc);
    }


    public Bounds getBounds() {
        Bounds bounds = new Bounds();

        double tl = getTextDocument().getText().length();

        if (tl > 0) {
            // TODO calculate the real values
            Point3D p = calculateAlignmentPoint();
            bounds.addToBounds(p);

            double h = getHeight();

            double w = tl * h * 0.6;

            if (this.isBackward()) {
                w = -1 * w;
            }

            // we set the horizontal width
            switch (this.align) {
            case 0:
                bounds.addToBounds(p.getX() + w, p.getY(), p.getZ());

                break;

            case 1:
                bounds.addToBounds(p.getX() + (w / 2), p.getY(), p.getZ());
                bounds.addToBounds(p.getX() - (w / 2), p.getY(), p.getZ());

                break;

            case 2:
                bounds.addToBounds(p.getX() - w, p.getY(), p.getZ());

                break;

            case 3:
                bounds.addToBounds(p.getX() - w, p.getY(), p.getZ());

                break;

            case 4:
                bounds.addToBounds(p.getX() + (w / 2), p.getY(), p.getZ());
                bounds.addToBounds(p.getX() - (w / 2), p.getY(), p.getZ());

                break;

            case 5:
                bounds.addToBounds(p.getX() + (w / 2), p.getY(), p.getZ());
                bounds.addToBounds(p.getX() - (w / 2), p.getY(), p.getZ());

                break;
            }

            // set the vertical height
            switch (this.valign) {
            case VALIGN_BASELINE:
                bounds.addToBounds(p.getX(), p.getY() + (h * 0.75), p.getZ());

                break;

            case VALIGN_BOTTOM:
                bounds.addToBounds(p.getX(), p.getY() + h, p.getZ());

                break;

            case VALIGN_CENTER:
                bounds.addToBounds(p.getX(), p.getY() + (h * 0.5), p.getZ());
                bounds.addToBounds(p.getX(), p.getY() - (h * 0.5), p.getZ());

                break;

            case VALIGN_TOP:
                bounds.addToBounds(p.getX(), p.getY() - h, p.getZ());

                break;
            }
        } else {
            bounds.setValid(false);
        }

        return bounds;
    }

    /**
     * @return Returns the align.
     */
    public int getAlign() {
        return align;
    }

    /**
     * @param align
     *            The align to set.
     */
    public void setAlign(int align) {
        this.align = align;
    }


    /**
     * @return Returns the height.
     */
    public double getHeight() {
        if (height != 0.0) {
            return height;
        } else if (doc.getStyle(this.textStyle) != null) {
            return doc.getStyle(this.textStyle).getTextHeight();
        } else {
            return 0.0;
        }
    }

    /**
     * @param height
     *            The height to set.
     */
    public void setHeight(double height) {
        this.height = height;
    }

    /**
     * @return Returns the oblique_angle.
     */
    public double getObliqueAngle() {
        return oblique_angle;
    }

    /**
     * @param oblique_angle
     *            The oblique_angle to set.
     */
    public void setObliqueAngle(double oblique_angle) {
        this.oblique_angle = oblique_angle;
    }

    /**
     * @return Returns the rotation.
     */
    public double getRotation() {
        return rotation;
    }

    /**
     * @param rotation
     *            The rotation to set.
     */
    public void setRotation(double rotation) {
        this.rotation = rotation;
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
     * @return Returns the text.
     */
    public String getText() {
        return text;
    }

    /**
     * @param text
     *            The text to set.
     */
    public void setText(String text) {
        this.text = text;
        this.textDoc = TextParser.parseText(this);
    }

    /**
     * @return Returns the textStyle.
     */
    public String getTextStyle() {
        return textStyle;
    }

    /**
     * @param textStyle
     *            The textStyle to set.
     */
    public void setTextStyle(String textStyle) {
        this.textStyle = textStyle;
    }

    /**
     * @return Returns the valign.
     */
    public int getValign() {
        return valign;
    }

    /**
     * @param valign
     *            The valign to set.
     */
    public void setValign(int valign) {
        this.valign = valign;
    }


    public boolean isBackward() {
        return Utils.isBitEnabled(textGenerationFlag, 3);
    }

    public void setBackward(boolean backward) {
        if (backward) {
            Utils.enableBit(textGenerationFlag, 3);
        }
    }

    public boolean isUpsideDown() {
        return Utils.isBitEnabled(textGenerationFlag, 2);
    }

    public void setUpsideDown(boolean upsideDown) {
        if (upsideDown) {
            Utils.enableBit(textGenerationFlag, 2);
        }
    }

    public Type<? extends DraftEntity> getType() {
        return Type.TYPE_TEXT;
    }

    /**
     * @return Returns the alignmentPointSet.
     */
    public boolean isAlignmentPointSet() {
        return this.isBitEnabled(BOOLEAN_BIT_ALIGNMENTPOINTSET);
    }

    /**
     * @param alignmentPointSet
     *            The alignmentPointSet to set.
     */
    public void setAlignmentPoint(boolean alignmentPoint) {
      this.setBit(BOOLEAN_BIT_ALIGNMENTPOINTSET, alignmentPoint);
    }
    
    
    public void setAligmnentPoint(Point3D alignmentPoint){
        this.alignmentPoint=alignmentPoint;
    }

    public TextDocument getTextDocument() {
        return this.textDoc;
    }

   public void setInsertPoint(Point3D insertPoint){
       this.p=insertPoint;
   }
    
    public Point3D getInsertPoint() {
        return p;
    }

    public Point3D getAlignmentPoint() {
        return alignmentPoint;
    }

    public Point3D calculateAlignmentPoint() {
        Point3D aPoint = new Point3D(p.getX(), p.getY(), p.getZ());

        if (!isUpsideDown()) {
            switch (align) {
            case 1:

                if (isAlignmentPointSet()) {
                    aPoint.setX(alignmentPoint.getX());
                }

                break;

            case 2:

                if (isAlignmentPointSet()) {
                    aPoint.setX(alignmentPoint .getX());
                }

                break;

            case 3:

                if (isAlignmentPointSet()) {
                    aPoint.setX(alignmentPoint.getX());
                }

                break;

            case 4:

                if (isAlignmentPointSet()) {
                    aPoint.setX(alignmentPoint.getX());
                }

                break;

            case 5:

                if (isAlignmentPointSet()) {
                    aPoint.setX(alignmentPoint.getX());
                }

                break;
            }

            if (isAlignmentPointSet()) {
                aPoint.setY(alignmentPoint .getY());
            }
        }

        return aPoint;
    }

    public boolean isOmitLineType() {
        return true;
    }

    public double getLength() {
        return 0;
    }

    /**
     * @return the textGenerationFlag
     */
    public int getTextGenerationFlag() {
        return textGenerationFlag;
    }

    /**
     * @param textGenerationFlag
     *            the textGenerationFlag to set
     */
    public void setTextGenerationFlag(int textGenerationFlag) {
        this.textGenerationFlag = textGenerationFlag;
    }
    
    /**
     * Not implemented yet
     * @throws CloneNotSupportedException 
     */
    
    public void transform(TransformContext context) {

            this.setInsertPoint(context.transform(this.getInsertPoint()));
            if(this.isAlignmentPointSet()){
               this.setAligmnentPoint(context.transform(this.getAlignmentPoint()));
            }
      
    }
    
    
    
    
}
