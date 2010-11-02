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

import org.kabeja.common.Type;
import org.kabeja.math.Bounds;
import org.kabeja.math.TransformContext;


/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 */
public class Point extends Entity {
	
    protected org.kabeja.math.Point3D p = new org.kabeja.math.Point3D();
    protected double angle=0.0;


    public Point(double x, double y, double z) {
    	this(new  org.kabeja.math.Point3D(x,y,z));
    }

    public Point(org.kabeja.math.Point3D p) {
    	super();
        this.p = p;
    }
    
    public Point(){
        this(new  org.kabeja.math.Point3D());
    }

    /**
     * @return Returns the x.
     */
    public double getX() {
        return this.p.getX();
    }

    /**
     * @param x
     *            The x to set.
     */
    public void setX(double x) {
        this.p.setX(x);
    }

    /**
     * @return Returns the y.
     */
    public double getY() {
        return this.p.getY();
    }

    /**
     * @param y
     *            The y to set.
     */
    public void setY(double y) {
        this.p.setY(y);
    }

    /**
     * @return Returns the z.
     */
    public double getZ() {
        return this.p.getZ();
    }

    /**
     * @param z
     *            The z to set.
     */
    public void setZ(double z) {
        this.p.setZ(z);
    }

    public Bounds getBounds() {
        Bounds bounds = new Bounds();
        bounds.addToBounds(p);

        return bounds;
    }

    public Type<Point> getType() {
        return Type.TYPE_POINT;
    }

    public  org.kabeja.math.Point3D getPoint() {
        return this.p;
    }

    public void setPoint(org.kabeja.math.Point3D p) {
        this.p = p;
    }

    public double getLength() {
        // a point has no length
        return 0;
    }

    /**
     * @return the angle
     */
    public double getAngle() {
        return angle;
    }

    /**
     * @param angle the angle to set
     */
    public void setAngle(double angle) {
        this.angle = angle;
    }

    /**
     * Not implemented yet
     */
    
    public void transform(TransformContext context) {
        
    }
   
}
