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
import org.kabeja.math.MathUtils;
import org.kabeja.math.ParametricPlane;
import org.kabeja.math.Point3D;
import org.kabeja.math.TransformContext;


/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 */
public class Solid extends Entity {
    protected Point3D point1 = new Point3D();
    protected Point3D point2 = new Point3D();
    protected Point3D point3 = new Point3D();
    protected Point3D point4 = new Point3D();

    public Solid() {
    }

    public Bounds getBounds() {
        Bounds bounds = new Bounds();
        ParametricPlane plane = new ParametricPlane(this.getExtrusion());
        bounds.addToBounds(plane.getPoint(point1));
        bounds.addToBounds(plane.getPoint(point2));
        bounds.addToBounds(plane.getPoint(point3));
        bounds.addToBounds(plane.getPoint(point4));

        return bounds;
    }

    /**
     * @return Returns the point1.
     */
    public Point3D getPoint1() {
        return point1;
    }

    /**
     * @param point1
     *            The point1 to set.
     */
    public void setPoint1(Point3D point1) {
        this.point1 = point1;
    }

    /**
     * @return Returns the point2.
     */
    public Point3D getPoint2() {
        return point2;
    }

    /**
     * @param point2
     *            The point2 to set.
     */
    public void setPoint2(Point3D point2) {
        this.point2 = point2;
    }

    /**
     * @return Returns the point3.
     */
    public Point3D getPoint3() {
        return point3;
    }

    /**
     * @param point3
     *            The point3 to set.
     */
    public void setPoint3(Point3D point3) {
        this.point3 = point3;
    }

    /**
     * @return Returns the point4.
     */
    public Point3D getPoint4() {
        return point4;
    }

    /**
     * @param point4
     *            The point4 to set.
     */
    public void setPoint4(Point3D point4) {
        this.point4 = point4;
    }

    public Type<?> getType() {
        return Type.TYPE_SOLID;
    }

    public double getLength() {
        double length = 0.0;
        length += MathUtils.distance(this.point1, this.point2);
        length += MathUtils.distance(this.point2, this.point4);
        length += MathUtils.distance(this.point4, this.point3);
        length += MathUtils.distance(this.point3, this.point1);

        return length;
    }
    
    

    
    public void transform(TransformContext context) {
        
       
       this.setPoint1(context.transform(this.getPoint1()));
       this.setPoint2(context.transform(this.getPoint2()));
       this.setPoint3(context.transform(this.getPoint3()));
       this.setPoint4(context.transform(this.getPoint4()));
        
       
    }
    
}
