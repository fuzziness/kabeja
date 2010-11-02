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

package org.kabeja.math;



/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 */
public class Edge {
    protected Point3D startPoint = new Point3D();
    protected Point3D endPoint = new Point3D();

    /**
     * @return Returns the endPoint.
     */
    public Point3D getEndPoint() {
        return endPoint;
    }

    /**
     * @param endPoint The endPoint to set.
     */
    public void setEndPoint(Point3D endPoint) {
        this.endPoint = endPoint;
    }

    /**
     * @return Returns the startPoint.
     */
    public Point3D getStartPoint() {
        return startPoint;
    }

    /**
     * @param startPoint The startPoint to set.
     */
    public void setStartPoint(Point3D startPoint) {
        this.startPoint = startPoint;
    }

    public Point3D getIntersectionPoint(Edge e) {
        return null;
    }
}
