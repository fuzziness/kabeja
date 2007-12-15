package org.kabeja.dxf;

import org.kabeja.dxf.helpers.Point;

import junit.framework.TestCase;

public class DXFExtrusionTest extends TestCase{

	
	
	public void testLineExtrusion(){
		DXFLine line = new DXFLine();
		line.setStartPoint(new Point(0,0,0));
		line.setEndPoint(new Point(100,100,0));
		line.setThickness(10.0);
		
		DXFExtrusion e = line.getExtrusion();
		Point p1 = e.extrudePoint(line.getStartPoint(), line.getThickness());
		Point p2 = e.extrudePoint(line.getEndPoint(), line.getThickness());
		assertEquals(10.0,p1.getZ(),0.00001);
		assertEquals(10.0,p2.getZ(),0.00001);
	}
}
