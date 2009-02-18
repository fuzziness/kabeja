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
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;

import javax.swing.JFrame;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.kabeja.dxf.DXFDocument;
import org.kabeja.dxf.DXFEntity;
import org.kabeja.parser.Parser;
import org.kabeja.parser.ParserBuilder;
import org.kabeja.svg.action.LayoutSwitchAction;
import org.kabeja.svg.tools.DXFSAXDocumentFactory;
import org.kabeja.svg.ui.SVGViewUIComponent;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.svg.SVGDocument;

import dk.abj.svg.action.HighlightAction;


public class Demo {
    /**
     * @param args
     */
	
	 static JSVGCanvas canvas;
    public static void main(String[] args) {
        Parser p = ParserBuilder.createDefaultParser();

        try {
            p.parse("/home/simon/Desktop/sample.DXF");

            
            
            DXFDocument doc = p.getDocument();
            
            
            
            
            DXFSAXDocumentFactory factory = new DXFSAXDocumentFactory();
//            SVGDocument svgDoc = factory.createDocument(doc,new HashMap());
//       
//            
//            UserAgentAdapter userAgent = new UserAgentAdapter();
//            BridgeContext ctx = new BridgeContext(userAgent);
//            ctx.setDynamicState(BridgeContext.DYNAMIC); 
//            GVTBuilder builder = new GVTBuilder();
//           
//            GraphicsNode node = builder.build(ctx, svgDoc);
//
//			DOMImplementation impl =
//			      GenericDOMImplementation.getDOMImplementation();
//			  String svgNS = "http://www.w3.org/2000/svg";
//			  Document myFactory = impl.createDocument(svgNS, "svg", null);
//
//			  SVGGraphics2D g2d = new SVGGraphics2D(myFactory);
//			  Writer out = new OutputStreamWriter(new FileOutputStream("/home/simon/Desktop/foo.svg"), "UTF-8");
//			
//			node.paint(g2d);
//			g2d.stream(out, false);
            

            

    
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
    
    
    public void doIt(DXFDocument doc){
   

            try{
          
            
            DXFSAXDocumentFactory factory = new DXFSAXDocumentFactory();
            SVGDocument svgDoc = factory.createDocument(doc,new HashMap());
       
            
            UserAgentAdapter userAgent = new UserAgentAdapter();
            BridgeContext ctx = new BridgeContext(userAgent);
            ctx.setDynamicState(BridgeContext.DYNAMIC); 
            GVTBuilder builder = new GVTBuilder();
           
            GraphicsNode node = builder.build(ctx, svgDoc);

			DOMImplementation impl =
			      GenericDOMImplementation.getDOMImplementation();
			  String svgNS = "http://www.w3.org/2000/svg";
			  Document myFactory = impl.createDocument(svgNS, "svg", null);

			  SVGGraphics2D g2d = new SVGGraphics2D(myFactory);
			  Writer out = new OutputStreamWriter(new FileOutputStream("/home/simon/Desktop/foo.svg"), "UTF-8");
			
			node.paint(g2d);
			g2d.stream(out, false);
            

	

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public class SvgConcreteTranscoder extends SVGAbstractTranscoder
    {

        public GraphicsNode getGraphicsNode()
        {
            return root;
        }

    }
}
