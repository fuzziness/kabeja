/*
   Copyright 2008 Simon Mieth

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
package org.kabeja.ui;

import java.awt.Dimension;
import java.io.FileOutputStream;
import java.util.HashMap;

import javax.swing.JFrame;

import org.kabeja.dxf.DXFDocument;
import org.kabeja.dxf.DXFEntity;
import org.kabeja.parser.Parser;
import org.kabeja.parser.ParserBuilder;
import org.kabeja.svg.SVGGenerator;
import org.kabeja.svg.action.LayoutSwitchAction;
import org.kabeja.svg.ui.SVGViewUIComponent;
import org.kabeja.xml.SAXPrettyOutputter;

import dk.abj.svg.action.HighlightAction;


public class Demo {
    /**
     * @param args
     */
    public static void main(String[] args) {
        Parser p = ParserBuilder.createDefaultParser();

        try {
            p.parse("/home/simon/Desktop/Kabeja-Repo/Drafts/dxf/examples/my/D1.dxf");

            DXFDocument doc = p.getDocument();
//            DXFDocument doc = new DXFDocument();
//            SVGGenerator gen = new SVGGenerator();
//            SAXPrettyOutputter printer = new SAXPrettyOutputter();
//            printer.setOutput(new FileOutputStream("/home/simon/Desktop/muh.svg"));
//            gen.generate(doc, printer, new HashMap());
            DXFEntity e = doc.getDXFEntityByID("181");


            SVGViewUIComponent ui = new SVGViewUIComponent();
            ui.addAction(new HighlightAction("GG"));
            ui.addAction(new LayoutSwitchAction());

            JFrame f = new JFrame("Demo");
            f.add(ui.getView());
            f.setSize(new Dimension(640, 480));
            f.setVisible(true);
            ui.showDXFDocument(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
