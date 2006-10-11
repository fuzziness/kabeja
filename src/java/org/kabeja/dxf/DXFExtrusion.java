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
package org.kabeja.dxf;

import org.kabeja.dxf.helpers.MathUtils;
import org.kabeja.dxf.helpers.Point;
import org.kabeja.dxf.helpers.Vector;


/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 *
 *
 */
public class DXFExtrusion {
    private final static double v = 1.0 / 64.0;
    protected Vector n = new Vector(0.0, 0.0, 1.0);
    protected Vector x;
    protected Vector y;
   

    public double getX() {
        return n.getX();
    }

    public void setX(double x) {
        n.setX(x);
    }

    public double getY() {
        return n.getY();
    }

    public void setY(double y) {
        n.setY(y);
    }

    public double getZ() {
        return n.getZ();
    }

    public void setZ(double z) {
        n.setZ(z);
    }

    public void calculateExtrusion() {
      

        if ((Math.abs(n.getX()) < v) && (Math.abs(n.getY()) < v)) {
            x = MathUtils.crossProduct(DXFConstants.DEFAULT_Y_AXIS_VECTOR, n);
        } else {
            x = MathUtils.crossProduct(DXFConstants.DEFAULT_Z_AXIS_VECTOR, n);
        }

        y = MathUtils.crossProduct(x, n);
    }

    public Point extrudePoint(Point basePoint, double elevation) {
        return MathUtils.getPointOfStraightLine(basePoint, this.n, elevation);
    }

    public Vector getNormal() {
        return n;
    }
}
