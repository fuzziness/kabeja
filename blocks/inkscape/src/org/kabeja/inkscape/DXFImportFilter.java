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

import java.util.HashMap;
import java.util.Map;

import org.kabeja.dxf.DXFDocument;
import org.kabeja.parser.DXFParseException;
import org.kabeja.parser.Parser;
import org.kabeja.parser.ParserBuilder;
import org.kabeja.processing.PolylineConverter;
import org.kabeja.processing.PostProcessor;
import org.kabeja.svg.SVGGenerator;
import org.kabeja.xml.ConsoleSerializer;
import org.kabeja.svg.FixedStrokeWidthFilter;
import org.kabeja.svg.RootLayerFilter;
import org.kabeja.xml.SAXFilter;
import org.kabeja.xml.SAXGenerator;
import org.kabeja.xml.SAXSerializer;

/**
 * This is a CLI wrapper for kabeja to imitate the behavior of the native
 * Inscape dxf2svg import filter
 */
public class DXFImportFilter {

	public void importFile(String file) {
		try {
			// parse the dxf file
			Parser parser = ParserBuilder.createDefaultParser();

			parser.parse(file);
			DXFDocument doc = parser.getDocument();

			Map noprops = new HashMap();
			
			//connect all entities, where possible
			PostProcessor pp = new PolylineConverter();
			pp.setProperties(noprops);
			pp.process(doc, noprops);
			// the processing and svg conversion
			SAXGenerator generator = new SVGGenerator();
			generator.setProperties(new HashMap());

			// fix problems width percent width values
			SAXFilter filter1 = new FixedStrokeWidthFilter();
			Map properties = new HashMap();
			properties.put(FixedStrokeWidthFilter.PROPERTY_FIXED_FONTSIZE,"false");
			filter1.setProperties(properties);

			
			//remove the root group
			SAXFilter filter2 = new RootLayerFilter();
			filter2.setProperties(noprops);
			
			//chain the filters
			filter1.setContentHandler(filter2);
			
			
			// output goes to stdout
			SAXSerializer serializer = new ConsoleSerializer();
			serializer.setOutput(null);
			serializer.setProperties(noprops);

			// setup the process pipeline
			// and start the generation

			filter2.setContentHandler(serializer);
			generator.generate(doc, filter1);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		if (args.length >= 1) {
			DXFImportFilter filter = new DXFImportFilter();
			filter.importFile(args[0]);
		}
	}

}
