package org.kabeja.parser.dxf.filter;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.kabeja.dxf.DXFDocument;
import org.kabeja.parser.DXFParser;
import org.kabeja.parser.ParserBuilder;
import org.kabeja.svg.SVGGenerator;
import org.kabeja.xml.SAXGenerator;
import org.kabeja.xml.SAXPrettyOutputter;
import org.kabeja.xml.SAXSerializer;

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
			SAXGenerator gen = new SVGGenerator();
			SAXSerializer out = new SAXPrettyOutputter();
			out.setOutput(new FileOutputStream(args[2]));
			gen.generate(doc, out);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
