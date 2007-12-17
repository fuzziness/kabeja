/*
   Copyright 2007 Simon Mieth

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
package org.kabeja.svg.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.gvt.GVTTreeRendererAdapter;
import org.apache.batik.swing.gvt.GVTTreeRendererEvent;
import org.apache.batik.swing.svg.GVTTreeBuilderAdapter;
import org.apache.batik.swing.svg.GVTTreeBuilderEvent;
import org.apache.batik.swing.svg.SVGDocumentLoaderAdapter;
import org.apache.batik.swing.svg.SVGDocumentLoaderEvent;
import org.kabeja.dxf.DXFDocument;
import org.kabeja.processing.ProcessingManager;
import org.kabeja.svg.tools.DXFSAXDocumentFactory;
import org.kabeja.ui.DXFDocumentViewComponent;
import org.kabeja.ui.UIException;
import org.w3c.dom.svg.SVGDocument;
import org.xml.sax.SAXException;

public class SVGViewUIComponent implements DXFDocumentViewComponent{

	
	protected boolean initialized=false;
	protected JSVGCanvas canvas;
	protected String title="SVGView";
	protected JLabel infoLabel;
	protected JPanel parentPanel;
	protected CardLayout cards;
	protected List actions = new ArrayList();
	
	public String getTitle() {	
		return title;
	}

	public JComponent getView() {
		if(!this.initialized){
	
			
			this.cards = new CardLayout();
			this.parentPanel = new JPanel(cards);
			
			this.parentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
			this.infoLabel = new JLabel("DXF2SVGViewer", JLabel.CENTER) {
				protected void paintComponent(Graphics g) {
					Graphics2D g2 = (Graphics2D) g;
					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
							RenderingHints.VALUE_ANTIALIAS_ON);
					super.paintComponent(g);
				}
			};
			this.infoLabel.setOpaque(true);
			this.infoLabel.setBackground(new Color(96, 96, 96));
			this.infoLabel.setForeground(Color.WHITE);
			this.infoLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
			
			this.parentPanel.add(this.infoLabel, "info");
			
			JPanel panel = new JPanel(new BorderLayout());
			//toolbar
			JToolBar toolbar = new JToolBar();
			toolbar.add(new JButton("Nothing"));
			panel.add(toolbar,BorderLayout.NORTH);
			this.canvas = new JSVGCanvas();
			panel.add(canvas,BorderLayout.CENTER);
			
			
			this.canvas.setDoubleBuffered(true);
			this.canvas.setDoubleBufferedRendering(true);
			this.canvas.setDocumentState(JSVGCanvas.ALWAYS_DYNAMIC);

			this.canvas.addSVGDocumentLoaderListener(new SVGDocumentLoaderAdapter() {
				public void documentLoadingStarted(SVGDocumentLoaderEvent e) {
					infoLabel.setText("Loading ...");
				}

				public void documentLoadingCompleted(SVGDocumentLoaderEvent e) {
					infoLabel.setText("Draft loaded");
				}
			});

			this.canvas.addGVTTreeBuilderListener(new GVTTreeBuilderAdapter() {
				public void gvtBuildStarted(GVTTreeBuilderEvent e) {
					infoLabel.setText("Building draft ...");
				}

				public void gvtBuildCompleted(GVTTreeBuilderEvent e) {
					infoLabel.setText("Finished building");

				}
			});

			this.canvas.addGVTTreeRendererListener(new GVTTreeRendererAdapter() {
				public void gvtRenderingPrepare(GVTTreeRendererEvent e) {
					infoLabel.setText("Rendering draft ...");
				}

				public void gvtRenderingCompleted(GVTTreeRendererEvent e) {
					infoLabel.setText("");
					cards.show(parentPanel, "view");
				}
			});
			this.parentPanel.add(panel, "view");
		
		}
		return parentPanel;
	}

	public void setProcessingManager(ProcessingManager manager) {
		
		
	}

	public void showDXFDocument(DXFDocument doc) throws UIException {
		try {
			this.infoLabel.setText("Starting ...");
			this.infoLabel.repaint();
			this.cards.show(this.parentPanel, "info");
			DXFSAXDocumentFactory factory = new DXFSAXDocumentFactory();
			SVGDocument svgDoc = factory.createDocument(doc);
			this.canvas.setSVGDocument(svgDoc);
			
		} catch (Exception e) {
			
			throw new UIException(e);
		}
	}

}
