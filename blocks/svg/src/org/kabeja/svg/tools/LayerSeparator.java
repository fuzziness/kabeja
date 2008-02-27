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
package org.kabeja.svg.tools;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.kabeja.dxf.Bounds;
import org.kabeja.dxf.DXFDocument;
import org.kabeja.dxf.DXFHeader;
import org.kabeja.dxf.DXFLayer;
import org.kabeja.dxf.DXFVariable;
import org.kabeja.parser.DXFParser;
import org.kabeja.parser.Parser;
import org.kabeja.parser.ParserBuilder;
import org.kabeja.svg.SVGConstants;
import org.kabeja.svg.SVGGenerator;
import org.kabeja.xml.SAXGenerator;
import org.kabeja.xml.SAXPrettyOutputter;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 *
 */
public class LayerSeparator {
    public static void main(String[] args) {
        LayerSeparator split = new LayerSeparator();

        if (args.length >= 2) {
            split.processFile(args[0], args[1]);
        } else {
            split.processFile(args[0], null);
        }
    }

    public void processFile(String source, String result) {
        if (result == null) {
            result = source.substring(0,
                    source.toLowerCase().lastIndexOf(".dxf"));
        }

        Parser parser = ParserBuilder.createDefaultParser();

        try {
            parser.parse(new FileInputStream(source), DXFParser.DEFAULT_ENCODING);

            DXFDocument doc = parser.getDocument();
            splitLayers(doc, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void splitLayers(DXFDocument doc, String basename) {
        Iterator i = doc.getDXFLayerIterator();

        ArrayList layers = new ArrayList();

        // remove all layers from the doc
        Bounds b = doc.getBounds();

        while (i.hasNext()) {
            DXFLayer l = (DXFLayer) i.next();
            layers.add(l);
            i.remove();
        }

        //set fixed bounds
        DXFHeader h = doc.getDXFHeader();
        DXFVariable v = new DXFVariable("$PLIMMIN");
        v.setValue("10", "" + b.getMinimumX());
        v.setValue("20", "" + b.getMinimumY());
        h.setVariable(v);

        v = new DXFVariable("$PLIMMAX");
        v.setValue("10", "" + b.getMaximumX());
        v.setValue("20", "" + b.getMaximumY());
        h.setVariable(v);

        System.out.println(layers.size() + " layers to separate.");
        i = layers.iterator();

        int count = 0;

        while (i.hasNext()) {
            DXFLayer l = (DXFLayer) i.next();
            doc.addDXFLayer(l);
            count++;
            System.out.println("Generate:" + basename + count + ".svg");
            output(doc, basename + count + ".svg");
            doc.removeDXFLayer(l.getName());
        }
    }

    public void output(DXFDocument doc, String file) {
        try {
            SAXPrettyOutputter writer = new SAXPrettyOutputter(new FileOutputStream(
                        file), SAXPrettyOutputter.DEFAULT_ENCODING);
            writer.setDTD(SVGConstants.SVG_DTD_1_0);

            SAXGenerator svgGenerator = new SVGGenerator();
            svgGenerator.generate(doc, writer, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
