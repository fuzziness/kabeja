package org.kabeja.xml;

import java.util.Map;

import org.kabeja.dxf.DXFDocument;
import org.xml.sax.ContentHandler;
/**
 * This interface descripes a generator component,which emit convert the
 * DXFDocument to SAX-Event.
 * &lt;p&gt;
 * Lifecycle
 * &lt;/p&gt;
 * &lt;ol&gt;
 * &lt;li&gt;setProperties&lt;/li&gt;
 * &lt;li&gt;generate(DXFDocument doc,ConentHandler handler)&lt;/li&gt;
 * &lt;/ol&gt;
 *
 * @author simon.mieth
 *
 */
public interface SAXGenerator {

	public void setProperties(Map properties);

	public void generate(DXFDocument doc,ContentHandler handler);

}
