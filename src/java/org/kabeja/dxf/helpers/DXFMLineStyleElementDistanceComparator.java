package org.kabeja.dxf.helpers;

import java.util.Comparator;

import org.kabeja.dxf.objects.DXFMLineStyleElement;

public class DXFMLineStyleElementDistanceComparator implements Comparator {

	public int compare(Object arg0, Object arg1) {

		DXFMLineStyleElement el1 = (DXFMLineStyleElement) arg0;
		DXFMLineStyleElement el2 = (DXFMLineStyleElement) arg1;

		if (el1.getOffset() > el2.getOffset()) {
			return 1;
		} else if (el1.getOffset() < el2.getOffset()) {
			return -1;
		}
		
		return 0;
	}

}
