/*
 Copyright 2005 Simon Mieth

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package org.kabeja.inkscape;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.kabeja.batik.tools.AbstractSAXSerializer;
import org.kabeja.dxf.DXFDocument;
import org.kabeja.parser.Parser;
import org.kabeja.parser.ParserBuilder;
import org.kabeja.processing.PolylineConverter;
import org.kabeja.processing.PostProcessor;
import org.kabeja.svg.RootLayerFilter;
import org.kabeja.svg.SVGGenerator;
import org.kabeja.svg.StyleAttributeFilter;
import org.kabeja.xml.ConsoleSerializer;
import org.kabeja.xml.SAXFilter;
import org.kabeja.xml.SAXGenerator;
import org.kabeja.xml.SAXSerializer;

/**
 * This is a CLI wrapper for kabeja to imitate the behavior of the native
 * Inscape dxf2svg import filter
 */
public class DXFImportFilter {

	public void importFile(String[] args) {
		try {
			Map properties = null;
			String file = "";
			if (args.length > 1) {
				properties = parseParameters(args);
				file = args[args.length - 1];
			} else {
				properties = new HashMap();
				file = args[0];
				//properties = getFromUI();
			}
			// parse the dxf file
			Parser parser = ParserBuilder.createDefaultParser();

			parser.parse(file);

			DXFDocument doc = parser.getDocument();

			Map noprops = new HashMap();

			// connect all entities, where possible
			PostProcessor pp = new PolylineConverter();
			pp.setProperties(noprops);
			pp.process(doc, noprops);

			// the processing and svg conversion
			SAXGenerator generator = new SVGGenerator();
			generator.setProperties(properties);

			// remove the root group
			SAXFilter filter1 = new RootLayerFilter();
			filter1.setProperties(noprops);

			// fix problems width percent width values
			SAXFilter filter2 = new StyleAttributeFilter();

			filter2.setProperties(noprops);
			// chain the filters
			filter1.setContentHandler(filter2);

			// output goes to stdout
			SAXSerializer serializer = new ConsoleSerializer();
			serializer.setOutput(null);
			serializer.setProperties(noprops);

			// setup the process pipeline
			// and start the generation
			filter2.setContentHandler(serializer);
			generator.generate(doc, filter1, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		if (args.length >= 1) {
			DXFImportFilter filter = new DXFImportFilter();
			filter.importFile(args);
		}
	}

	protected Map parseParameters(String[] args) {
		Map map = new HashMap();
		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("--")) {
				int pos = args[i].indexOf('=');
				String param = args[i].substring(2, pos);
				String value = args[i].substring(pos + 1);
				map.put(param, value);
			}
		}

		return map;
	}

	protected Map getFromUI() {
		String[] layout = new String[] {
				SVGGenerator.PROPERTY_DOCUMENT_BOUNDS_RULE_MODELSPACE_VALUE,
				SVGGenerator.PROPERTY_DOCUMENT_BOUNDS_RULE_MODELSPACE_LIMITS_VALUE,
				SVGGenerator.PROPERTY_DOCUMENT_BOUNDS_RULE_PAPERSPACE_VALUE,
				SVGGenerator.PROPERTY_DOCUMENT_BOUNDS_RULE_PAPERSPACE_LIMITS_VALUE };
		Map map = new HashMap();

		int index = JOptionPane.showOptionDialog(null, "Choose Layout",
				"Layout Selection", JOptionPane.OK_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, layout, layout[0]);
		map.put(SVGGenerator.PROPERTY_DOCUMENT_BOUNDS_RULE, layout[index]);
		return map;
	}

}
