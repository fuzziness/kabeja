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
import org.kabeja.math.Point3D;
import org.kabeja.math.TransformContext;
import org.kabeja.math.Vector;


/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 */
public class Tolerance extends Entity {
    protected Point3D insertionPoint = new Point3D();
    protected String styleNameID = "";
    protected String text;
    protected Vector xaxisDirection = new Vector();


    public Bounds getBounds() {
        Bounds bounds = new Bounds();
        bounds.setValid(false);

        return bounds;
    }


    public Type<Tolerance> getType() {
      
        return Type.TYPE_TOLERANCE;
    }

    /**
     * @return Returns the insertionPoint.
     */
    public Point3D getInsertionPoint() {
        return insertionPoint;
    }

    /**
     * @param insertionPoint The insertionPoint to set.
     */
    public void setInsertionPoint(Point3D insertionPoint) {
        this.insertionPoint = insertionPoint;
    }

    /**
     * @return Returns the styleID.
     */
    public String getStyleID() {
        return styleNameID;
    }

    /**
     * @param styleNameID The styleID to set.
     */
    public void setStyleID(String styleNameID) {
        this.styleNameID = styleNameID;
    }

    /**
     * @return Returns the text.
     */
    public String getText() {
        return text;
    }

    /**
     * @param text The text to set.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return Returns the xaxisDirection.
     */
    public Vector getXaxisDirection() {
        return xaxisDirection;
    }

    /**
     * @param xaxisDirection The xaxisDirection to set.
     */
    public void setXaxisDirection(Vector xaxisDirection) {
        this.xaxisDirection = xaxisDirection;
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
