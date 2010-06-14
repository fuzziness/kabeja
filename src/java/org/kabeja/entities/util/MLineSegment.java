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

package org.kabeja.entities.util;

import java.util.ArrayList;
import java.util.List;

import org.kabeja.math.Point3D;
import org.kabeja.math.Vector;


public class MLineSegment {
    protected Point3D startPoint = new Point3D();
    protected Vector direction = new Vector();
    protected Vector miterDirection = new Vector();
    protected List elements = new ArrayList();

    public Point3D getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(Point3D startPoint) {
        this.startPoint = startPoint;
    }

    public Vector getDirection() {
        return direction;
    }

    public void setDirection(Vector direction) {
        this.direction = direction;
    }

    public Vector getMiterDirection() {
        return miterDirection;
    }

    public void setMiterDirection(Vector miterDirection) {
        this.miterDirection = miterDirection;
    }

    public void addMLineSegmentElement(MLineSegmentElement el) {
        this.elements.add(el);
    }

    public int getMLineSegmentElementCount() {
        return this.elements.size();
    }

    public MLineSegmentElement getMLineSegmentElement(int index) {
        return (MLineSegmentElement) this.elements.get(index);
    }
}
