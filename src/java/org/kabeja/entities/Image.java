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
 * Created on 28.06.2005
 *
 */
package org.kabeja.entities;

import java.util.ArrayList;
import java.util.List;

import org.kabeja.common.Type;
import org.kabeja.math.Bounds;
import org.kabeja.math.Point3D;
import org.kabeja.math.TransformContext;
import org.kabeja.objects.ImageDefObject;


/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 */
public class Image extends Entity {
    protected Point3D insertPoint = new Point3D();
    protected Point3D vectorV = new Point3D();
    protected Point3D vectorU = new Point3D();
    protected double imageSizeAlongU;
    protected double imageSizeAlongV;
    protected long imageDefID=-1;
    protected double brightness;
    protected double contrast;
    protected double fade;
    protected List<Point3D> clipBoundary = new ArrayList<Point3D>();
    protected boolean clipping = false;
    protected boolean rectangularClipping = false;
    protected boolean polygonalClipping = false;

   
    public Bounds getBounds() {
        Bounds b = new Bounds();
        ImageDefObject imageDef = (ImageDefObject) this.doc.getObjectByID(this.getImageDefObjectID());

        if (imageDef != null) {
            b.addToBounds(this.insertPoint);
            b.addToBounds(insertPoint.getX() + imageSizeAlongU,
                insertPoint.getY() + imageSizeAlongV, this.insertPoint.getZ());
        } else {
            b.setValid(false);
        }

        return b;
    }

    public Type<Image> getType() {
        return Type.TYPE_IMAGE;
    }

    public Point3D getInsertPoint() {
        return insertPoint;
    }

    public void setInsertPoint(Point3D p) {
        this.insertPoint = p;
    }

    public void setImageDefObjectID(long id) {
        this.imageDefID = id;
    }

    public long getImageDefObjectID() {
        return this.imageDefID;
    }

    /**
     * @return Returns the imageSizeAlongU.
     */
    public double getImageSizeAlongU() {
        return imageSizeAlongU;
    }

    /**
     * @param imageSizeAlongU
     *            The imageSizeAlongU to set.
     */
    public void setImageSizeAlongU(double imageSizeAlongU) {
        this.imageSizeAlongU = imageSizeAlongU;
    }

    /**
     * @return Returns the imageSizeAlongV.
     */
    public double getImageSizeAlongV() {
        return imageSizeAlongV;
    }

    /**
     * @param imageSizeAlongV
     *            The imageSizeAlongV to set.
     */
    public void setImageSizeAlongV(double imageSizeAlongV) {
        this.imageSizeAlongV = imageSizeAlongV;
    }

    /**
     * @return Returns the vectorU.
     */
    public Point3D getVectorU() {
        return vectorU;
    }

    /**
     * @param vectorU
     *            The vectorU to set.
     */
    public void setVectorU(Point3D vectorU) {
        this.vectorU = vectorU;
    }

    /**
     * @return Returns the vectorV.
     */
    public Point3D getVectorV() {
        return vectorV;
    }

    /**
     * @param vectorV
     *            The vectorV to set.
     */
    public void setVectorV(Point3D vectorV) {
        this.vectorV = vectorV;
    }

    /**
     * @return Returns the brightness.
     */
    public double getBrightness() {
        return brightness;
    }

    /**
     * @param brightness
     *            The brightness to set.
     */
    public void setBrightness(double brightness) {
        this.brightness = brightness;
    }

    /**
     * @return Returns the clipping.
     */
    public boolean isClipping() {
        return clipping;
    }

    /**
     * @param clipping
     *            The clipping to set.
     */
    public void setClipping(boolean clipping) {
        this.clipping = clipping;
    }

    /**
     * @return Returns the contrast.
     */
    public double getContrast() {
        return contrast;
    }

    /**
     * @param contrast
     *            The contrast to set.
     */
    public void setContrast(double contrast) {
        this.contrast = contrast;
    }

    /**
     * @return Returns the fade.
     */
    public double getFade() {
        return fade;
    }

    /**
     * @param fade
     *            The fade to set.
     */
    public void setFade(double fade) {
        this.fade = fade;
    }

    /**
     * @return Returns the clipBoundary.
     */
    public List<Point3D> getClipBoundary() {
        return clipBoundary;
    }

    public void addClippingPoint(Point3D p) {
        clipBoundary.add(p);
    }

    /**
     * @return Returns the polygonalClipping.
     */
    public boolean isPolygonalClipping() {
        return polygonalClipping;
    }

    /**
     * @param polygonalClipping
     *            The polygonalClipping to set.
     */
    public void setPolygonalClipping(boolean polygonalClipping) {
        this.polygonalClipping = polygonalClipping;
        this.rectangularClipping = !polygonalClipping;
    }

    /**
     * @return Returns the rectangularClipping.
     */
    public boolean isRectangularClipping() {
        return rectangularClipping;
    }

    /**
     * @param rectangularClipping
     *            The rectangularClipping to set.
     */
    public void setRectangularClipping(boolean rectangularClipping) {
        this.rectangularClipping = rectangularClipping;
        this.polygonalClipping = !rectangularClipping;
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
