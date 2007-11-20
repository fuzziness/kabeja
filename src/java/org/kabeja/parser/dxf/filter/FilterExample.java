package org.kabeja.parser.dxf.filter;

import java.util.HashMap;
import java.util.Map;

import org.kabeja.dxf.DXFDocument;
import org.kabeja.parser.DXFParser;
import org.kabeja.parser.ParserBuilder;

public class FilterExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		try {
			DXFParser parser = (DXFParser)ParserBuilder.createDefaultParser();
			//test 
			DXFStreamFilter filter = new DXFStreamLayerFilter();
			Map p = new HashMap();
			p.put("layers.include", args[0]);
			filter.setProperties(p);
			parser.addDXFStreamFilter(filter);
			parser.parse(args[1]);
			DXFDocument doc = parser.getDocument();		
			//do something with the doc
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}

}
