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
package org.kabeja.entities;

import org.kabeja.common.Type;
import org.kabeja.math.Bounds;
import org.kabeja.math.ParametricPlane;
import org.kabeja.math.Point3D;
import org.kabeja.math.TransformContext;


/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 */
public class Circle extends Entity {
    private Point3D center;
    private double radius;

    /**
     *
     */
    public Circle() {
    }

    public double getRadius() {
        return radius;
    }

    /**
     * @param radius
     *            The radius to set.
     */
    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void setCenterPoint(Point3D p) {
        this.center = p;
    }

    public Point3D getCenterPoint() {
        return center;
    }

    public Bounds getBounds() {
        Bounds bounds = new Bounds();
        ParametricPlane plane = new ParametricPlane(this.getExtrusion());
        Point3D p = plane.getPoint(this.center.getX(), this.center.getY());
        bounds.setMaximumX(p.getX() + radius);
        bounds.setMinimumX(p.getX() - radius);
        bounds.setMaximumY(p.getY() + radius);
        bounds.setMinimumY(p.getY() - radius);

        return bounds;
    }

    public Type<Circle> getType() {
        return Type.TYPE_CIRCLE;
    }

    public double getLength() {
        return 2 * Math.PI * this.radius;
    }

    public Point3D getPointAt(double angle) {
        // the local part
        double x = this.radius * Math.cos(Math.toRadians(angle));
        double y = radius * Math.sin(Math.toRadians(angle));

        // the wcs part
        ParametricPlane plane = new ParametricPlane(this.getExtrusion());
        Point3D p = plane.getPoint(x + this.center.getX(), y +
                this.center.getY());

        return p;
    }
    
    /**
     * Not implemented yet
     */
    
    public void transform(TransformContext context) {
    
        this.setCenterPoint(context.transform(this.getCenterPoint()));
         //handle scale  in the right way
        // scaleX != scaleY != scaleZ --> ellipse
       
    }
    
}
