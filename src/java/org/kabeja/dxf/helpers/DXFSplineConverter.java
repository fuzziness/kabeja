package org.kabeja.dxf.helpers;

import java.util.ArrayList;
import java.util.Iterator;

import org.kabeja.dxf.DXFPolyline;
import org.kabeja.dxf.DXFSpline;
import org.kabeja.dxf.DXFVertex;
import org.kabeja.math.NURBS;
import org.kabeja.math.NURBSFixedNTELSPointIterator;

public class DXFSplineConverter {



	public static DXFPolyline toDXFPolyline(DXFSpline spline) {
		DXFPolyline p = new DXFPolyline();

		if (spline.getDegree() > 0 && spline.getKnots().length > 0) {

			Iterator pi = new NURBSFixedNTELSPointIterator(toNurbs(spline), 30);
			while (pi.hasNext()) {
				p.addVertex(new DXFVertex((Point) pi.next()));
			}
		} else {

			// the curve is the controlpoint polygon
			Iterator i = spline.getSplinePointIterator();
			ArrayList list = new ArrayList();
			while (i.hasNext()) {
				SplinePoint sp = (SplinePoint) i.next();

				if (sp.isControlPoint()) {
					p.addVertex(new DXFVertex(sp));
				}
			}
			
			

		}

		if (spline.isClosed()) {
			p.setFlags(1);
		}

		return p;
	}

	public static NURBS toNurbs(DXFSpline spline) {
		Iterator i = spline.getSplinePointIterator();
		ArrayList list = new ArrayList();
		while (i.hasNext()) {
			SplinePoint sp = (SplinePoint) i.next();

			if (sp.isControlPoint()) {
				list.add((Point) sp);
			}
		}
		NURBS n = new NURBS((Point[]) list.toArray(new Point[list.size()]),
				spline.getKnots(), spline.getWeights(), spline.getDegree());
		n.setClosed(spline.isClosed());
		return n;
	}
}
