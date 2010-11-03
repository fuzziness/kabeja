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

import org.kabeja.common.DraftEntity;
import org.kabeja.common.Type;
import org.kabeja.math.Bounds;
import org.kabeja.math.Point3D;
import org.kabeja.math.TransformContext;
import org.kabeja.math.Vector;


/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 */
public class Ray extends Entity {
    protected Point3D basePoint = new Point3D();
    protected Vector direction = new Vector();


    public Bounds getBounds() {
        // we will only add the base point
        //the end is infinite
        Bounds bounds = new Bounds();
        bounds.addToBounds(basePoint);

        return bounds;
    }


    public Type<? extends DraftEntity> getType() {
        return Type.TYPE_RAY;
    }

    /**
     * @return Returns the basePoint.
     */
    public Point3D getBasePoint() {
        return basePoint;
    }

    /**
     * @param basePoint The basePoint to set.
     */
    public void setBasePoint(Point3D basePoint) {
        this.basePoint = basePoint;
    }

    /**
     * @return Returns the direction.
     */
    public Vector getDirection() {
        return direction;
    }

    /**
     * @param direction The direction to set.
     */
    public void setDirection(Vector direction) {
        this.direction = direction;
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
