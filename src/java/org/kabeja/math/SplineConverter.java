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

import java.util.ArrayList;
import java.util.Iterator;

import org.kabeja.entities.Polyline;
import org.kabeja.entities.Spline;
import org.kabeja.entities.Vertex;
import org.kabeja.entities.util.SplinePoint;


public class SplineConverter {
    public static Polyline toPolyline(Spline spline) {
        Polyline p = new Polyline();
        p.setDocument(spline.getDocument());

        if ((spline.getDegree() > 0) && (spline.getKnots().length > 0)) {
            Iterator<Point3D> pi = new NURBSFixedNTELSPointIterator(toNurbs(spline), 30);

            while (pi.hasNext()) {
                p.addVertex(new Vertex((Point3D) pi.next()));
            }
        } else {
            // the curve is the controlpoint polygon
          
            for(SplinePoint sp:spline.getSplinePoints()){
                if (sp.isControlPoint()) {
                    p.addVertex(new Vertex(sp));
                }
            }
        }

        if (spline.isClosed()) {
            p.setFlags(1);
        }

        return p;
    }

    public static NURBS toNurbs(Spline spline) {
    
        ArrayList<Point3D> list = new ArrayList<Point3D>();

       for(SplinePoint sp:spline.getSplinePoints()){
            if (sp.isControlPoint()) {
                list.add(sp);
            }
        }

        NURBS n = new NURBS((Point3D[]) list.toArray(new Point3D[list.size()]),
                spline.getKnots(), spline.getWeights(), spline.getDegree());
        n.setClosed(spline.isClosed());

        return n;
    }
}
