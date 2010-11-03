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
import org.kabeja.math.Point3D;
import org.kabeja.math.TransformContext;


/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 *
 */
public class Line extends Entity {
	
    private Point3D start;
    private Point3D end;

    public Line() {
        start = new Point3D();
        end = new Point3D();
    }

    public void setProperty(int groupcode, String value) {
    }

    public void setStartPoint(Point3D start) {
        this.start = start;
    }

    /**
     * @return Returns the end.
     */
    public Point3D getEndPoint() {
        return end;
    }

    /**
     * @param end
     *            The end to set.
     */
    public void setEndPoint(Point3D end) {
        this.end = end;
    }

    /**
     * @return Returns the start.
     */
    public Point3D getStartPoint() {
        return start;
    }

    public Bounds getBounds() {
        Bounds bounds = new Bounds();
        bounds.addToBounds(this.end);
        bounds.addToBounds(this.start);

        return bounds;
    }

    public Type<Line> getType() {
        return Type.TYPE_LINE;
    }

    public double getLength() {
        return MathUtils.distance(this.start, this.end);
    }
    

    
    public void transform(TransformContext context) {   
        this.setStartPoint(context.transform(this.getStartPoint()));
        this.setEndPoint(context.transform(this.getEndPoint()));
    }
}
