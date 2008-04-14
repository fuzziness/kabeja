package org.kabeja.parser;

import junit.framework.TestCase;

public class DXFValueTest extends TestCase {

	
	public void testParseDouble(){
		double d = 3.1314151617;
		DXFValue value = new DXFValue("  "+d+"   ");
		double r = value.getDoubleValue();
		assertEquals(d, r, 0.000001);
	}
	
	
	public void testParseInteger(){
		int i = 4;
		DXFValue value = new DXFValue("  "+i+"   ");
		int r = value.getIntegerValue();
		assertEquals(i, r);
	}

	public void testParseBoolean(){
		int i = 1;
		DXFValue value = new DXFValue("  "+i+"   ");
		boolean b = value.getBooleanValue();
		assertEquals(false, b);
	}

	
}
