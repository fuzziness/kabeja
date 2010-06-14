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

import java.util.ArrayList;
import java.util.List;

import org.kabeja.common.Type;
import org.kabeja.entities.util.MLineSegment;
import org.kabeja.math.Bounds;
import org.kabeja.math.Point3D;
import org.kabeja.math.TransformContext;
import org.kabeja.util.MLineConverter;


/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 */
public class MLine extends Entity {
    public final static int JUSTIFICATION_TOP = 0;
    public final static int JUSTIFICATION_ZERO = 1;
    public final static int JUSTIFICATION_BOTTOM = 2;
    protected double scale = 1.0;
    protected Point3D startPoint = new Point3D();
    protected List<MLineSegment> mlineSegments = new ArrayList<MLineSegment>();
    protected int lineCount = 0;
    protected int justification = 0;
    protected long mLineStyleID =-1;
    protected String mLineStyleName = "";


    public Bounds getBounds() {
        Bounds b = new Bounds();
        Polyline[] pl = this.toPolylines();

        for (int i = 0; i < pl.length; i++) {
            b.addToBounds(pl[i].getBounds());
        }

        // b.setValid(false);
        return b;
    }

    public Type<MLine> getType() {
        return Type.TYPE_MLINE;
    }

    public double getLength() {
        //TODO convert  mline -> polyline only  after changes
        Polyline[] pl = toPolylines();
        double l = 0;

        for (int i = 0; i < pl.length; i++) {
            l += pl[i].getLength();
        }

        return l;
    }

    public void addMLineSegement(MLineSegment seg) {
        this.mlineSegments.add(seg);
    }

    public int getMLineSegmentCount() {
        return this.mlineSegments.size();
    }

    public MLineSegment getMLineSegment(int index) {
        return  this.mlineSegments.get(index);
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public Point3D getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(Point3D startPoint) {
        this.startPoint = startPoint;
    }

    public int getLineCount() {
        return lineCount;
    }

    public void setLineCount(int lineCount) {
        this.lineCount = lineCount;
    }

    public long getMLineStyleID() {
        return mLineStyleID;
    }

    public void setMLineStyleID(long lineStyleID) {
        mLineStyleID = lineStyleID;
    }

    public int getJustification() {
        return justification;
    }

    public void setJustification(int justification) {
        this.justification = justification;
    }

    public String getMLineStyleName() {
        return mLineStyleName;
    }

    public void setMLineStyleName(String lineStyleName) {
        mLineStyleName = lineStyleName;
    }

    public Polyline[] toPolylines() {
        return MLineConverter.toPolyline(this);
    }

    public boolean isClosed() {
        return (this.flags & 2) == 2;
    }
    
    /**
     * Not implemented yet
     */
    
    public void transform(TransformContext context) {
       
    }
}
