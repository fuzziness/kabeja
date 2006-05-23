package org.kabeja.xml;

import java.util.Map;

import org.kabeja.dxf.DXFDocument;
import org.xml.sax.ContentHandler;

public interface SAXGenerator {

	public void setProperties(Map properties);
	
	public void generate(DXFDocument doc,ContentHandler handler);
	
}
