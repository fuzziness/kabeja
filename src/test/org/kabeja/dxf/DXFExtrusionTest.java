package org.kabeja.dxf;

import org.kabeja.dxf.helpers.Point;
import org.kabeja.dxf.helpers.Vector;

import junit.framework.TestCase;

public class DXFExtrusionTest extends TestCase {
	double DELTA = 0.0000000001;

	public void testLineExtrusion() {
		DXFLine line = new DXFLine();
		line.setStartPoint(new Point(0, 0, 0));
		line.setEndPoint(new Point(100, 100, 0));
		line.setThickness(10.0);

		DXFExtrusion e = line.getExtrusion();
		Point p1 = e.extrudePoint(line.getStartPoint(), line.getThickness());
		Point p2 = e.extrudePoint(line.getEndPoint(), line.getThickness());
		assertEquals(10.0, p1.getZ(), DELTA);
		assertEquals(10.0, p2.getZ(), DELTA);
	}

	public void testLinePlaneExtrusion() {
		DXFLine line = new DXFLine();
		line.setStartPoint(new Point(0, 0, 0));
		line.setEndPoint(new Point(100, 100, 0));
		line.setThickness(10.0);

		DXFExtrusion e = line.getExtrusion();
		Vector v1 = e.getDirectionX();
		Vector v2 = e.getDirectionY();

		assertEquals(1.0, v1.getX(), DELTA);
		assertEquals(0.0, v1.getY(), DELTA);
		assertEquals(0.0, v1.getZ(), DELTA);
		assertEquals(0.0, v2.getX(), DELTA);
		assertEquals(1.0, v2.getY(), DELTA);
		assertEquals(0.0, v2.getZ(), DELTA);

	}
}
