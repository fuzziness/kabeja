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

package org.kabeja.util;

import org.kabeja.common.LineType;
import org.kabeja.entities.MLine;
import org.kabeja.entities.Polyline;
import org.kabeja.entities.Vertex;
import org.kabeja.entities.util.MLineSegment;
import org.kabeja.entities.util.MLineSegmentElement;
import org.kabeja.math.MathUtils;
import org.kabeja.math.ParametricLine;
import org.kabeja.math.Point3D;
import org.kabeja.math.Vector;
import org.kabeja.objects.MLineStyle;
import org.kabeja.objects.MLineStyleElement;


public class MLineConverter {
	
    public static Polyline[] toPolyline(MLine mline) {
        MLineStyle style = (MLineStyle) mline.getDocument()
                                                   .getObjectByID(mline.getMLineStyleID());


        // initialize polylines
        Polyline[] pl = new Polyline[style.getMLineStyleLElementCount()];

        for (int x = 0; x < pl.length; x++) {
            MLineStyleElement se = style.getMLineStyleLElement(x);
            pl[x] = new Polyline();
            pl[x].setDocument(mline.getDocument());
           
            LineType ltype = mline.getDocument().getLineType(se.getLineType()); 
            if(ltype != null && !se.getLineType().equals("BYLAYER")){
               pl[x].setLineType(ltype);
            }
            
            pl[x].setColor(se.getLineColor());

            if (mline.isClosed()) {
                pl[x].setFlags(1);
            }
        }

        Vector v = new Vector();
        
        ParametricLine l = new ParametricLine();
        ParametricLine miter = new ParametricLine();

        for (int i = 0; i < mline.getMLineSegmentCount(); i++) {
            MLineSegment seg = mline.getMLineSegment(i);

            v = seg.getDirection();
            Vector  d = seg.getMiterDirection();
            miter.setStartPoint(seg.getStartPoint());
            miter.setDirectionVector(d);

            for (int x = 0; x < seg.getMLineSegmentElementCount(); x++) {
                MLineSegmentElement segEl = seg.getMLineSegmentElement(x);
                double[] le = segEl.getLengthParameters();
                Point3D s = miter.getPointAt(le[0]);
                l.setStartPoint(s);
                l.setDirectionVector(v);
                pl[x].addVertex(new Vertex(l.getPointAt(le[1])));
            }
        }

        if (style.hasEndRoundCaps()) {
            Point3D p1 = pl[0].getVertex(pl[0].getVertexCount() - 1).getPoint();
            Point3D p2 = pl[pl.length - 1].getVertex(pl[pl.length - 1].getVertexCount() -
                    1).getPoint();
            Vector v1 = MathUtils.getVector(p1, p2);
            double distance = v1.getLength();
            double r = distance / 2;

            double length = Math.sqrt(2) * r;
            double h = r - (Math.sqrt(0.5) * r);
            double bulge = (h * 2) / length;
            v1.normalize();

            ParametricLine line = new ParametricLine(p1, v1);
            Point3D center = line.getPointAt(r);
            line.setStartPoint(center);

            v.normalize();
            line.setDirectionVector(v);
            center = line.getPointAt(r);

            pl[0].getVertex(pl[0].getVertexCount() - 1).setBulge(-1 * bulge);
            pl[0].addVertex(new Vertex(center));

            pl[pl.length - 1].getVertex(pl[pl.length - 1].getVertexCount() - 1)
                             .setBulge(bulge);
            pl[pl.length - 1].addVertex(new Vertex(center));
        }

        return pl;
    }
}
