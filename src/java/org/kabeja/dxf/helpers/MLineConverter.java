package org.kabeja.dxf.helpers;

import org.kabeja.dxf.DXFMLine;
import org.kabeja.dxf.DXFPolyline;
import org.kabeja.dxf.DXFVertex;
import org.kabeja.dxf.objects.DXFMLineStyle;
import org.kabeja.dxf.objects.DXFMLineStyleElement;

public class MLineConverter {

	public static DXFPolyline[] toDXFPolyline(DXFMLine mline) {
	
		
		DXFMLineStyle style = (DXFMLineStyle) mline.getDXFDocument()
				.getDXFObject(mline.getMLineStyleID());
		// style
		// .sortDXFMLineStyleElements(new
		// DXFMLineStyleElementDistanceComparator());

		// initialize polylines
		DXFPolyline[] pl = new DXFPolyline[style
				.getDXFMLineStyleLElementCount()];
		for (int x = 0; x < pl.length; x++) {
			DXFMLineStyleElement se = style.getDXFMLineStyleLElement(x);
			pl[x] = new DXFPolyline();
			pl[x].setDXFDocument(mline.getDXFDocument());
			pl[x].setLineType(se.getLineType());
			pl[x].setColor(se.getLineColor());
			if (mline.isClosed()) {
				pl[x].setFlags(1);
			}
		}
		Vector v = new Vector();
		Vector d = new Vector();
		ParametricLine l = new ParametricLine();
		ParametricLine miter = new ParametricLine();
		for (int i = 0; i < mline.getDXFMLineSegmentCount(); i++) {
			DXFMLineSegment seg =  mline.getDXFMLineSegment(i);

			v = seg.getDirection();
			d = seg.getMiterDirection();
			miter.setStartPoint(seg.getStartPoint());
			miter.setDirectionVector(d);
			for (int x = 0; x < seg.getDXFMLineSegmentElementCount(); x++) {
				DXFMLineSegmentElement segEl = seg.getDXFMLineSegmentElement(x);
				double[] le = segEl.getLengthParameters();
				Point s = miter.getPointAt(le[0]);
				l.setStartPoint(s);
				l.setDirectionVector(v);
				pl[x].addVertex(new DXFVertex(l.getPointAt(le[1])));

			}

		}
		if (style.hasEndRoundCaps()) {

			Point p1 = pl[0].getVertex(pl[0].getVertexCount() - 1).getPoint();
			Point p2 = pl[pl.length - 1].getVertex(
					pl[pl.length - 1].getVertexCount() - 1).getPoint();
			Vector v1 = MathUtils.getVector(p1, p2);
			double distance = v1.getLength();
			double r = distance / 2;

			double length = Math.sqrt(2) * r;
			double h = r - Math.sqrt(0.5) * r;
			double bulge = h * 2 / length;
			v1.normalize();
			ParametricLine line = new ParametricLine(p1, v1);
			Point center = line.getPointAt(r);
			line.setStartPoint(center);

			v.normalize();
			line.setDirectionVector(v);
			center = line.getPointAt(r);

			pl[0].getVertex(pl[0].getVertexCount() - 1).setBulge(-1 * bulge);
			pl[0].addVertex(new DXFVertex(center));

			pl[pl.length - 1].getVertex(pl[pl.length - 1].getVertexCount() - 1)
					.setBulge(bulge);
			pl[pl.length - 1].addVertex(new DXFVertex(center));

		}

		return pl;
	}
}
