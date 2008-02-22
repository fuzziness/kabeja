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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.gvt.GVTTreeRendererAdapter;
import org.apache.batik.swing.gvt.GVTTreeRendererEvent;
import org.apache.batik.swing.svg.GVTTreeBuilderAdapter;
import org.apache.batik.swing.svg.GVTTreeBuilderEvent;
import org.apache.batik.swing.svg.SVGDocumentLoaderAdapter;
import org.apache.batik.swing.svg.SVGDocumentLoaderEvent;
import org.kabeja.dxf.DXFDocument;
import org.kabeja.processing.ProcessingManager;
import org.kabeja.svg.SVGGenerator;
import org.kabeja.svg.tools.DXFSAXDocumentFactory;
import org.kabeja.ui.DXFDocumentViewComponent;
import org.kabeja.ui.PropertiesEditor;
import org.kabeja.ui.ServiceManager;
import org.kabeja.ui.Serviceable;
import org.kabeja.ui.UIException;
import org.kabeja.ui.event.DXFDocumentChangeEventProvider;
import org.kabeja.ui.event.DXFDocumentChangeListener;
import org.w3c.dom.svg.SVGDocument;

public class SVGViewUIComponent extends org.kabeja.ui.impl.AbstractPropertiesEditor implements DXFDocumentViewComponent,Serviceable,DXFDocumentChangeListener{

	protected boolean initialized = false;
	protected JSVGCanvas canvas;
	protected String title = "SVGView";
	protected JLabel infoLabel;
	protected JPanel parentPanel;
	protected CardLayout cards;
	protected List actions = new ArrayList();
	String[] data = new String[] { "Modelspace", "Paperspace", "Mixed" };
	protected DXFDocument doc;
	protected JComboBox switchViewBox;

	public String getTitle() {
		return title;
	}

	public JComponent getView() {
		if (!this.initialized) {

			this.cards = new CardLayout();
			this.parentPanel = new JPanel(cards);

			this.parentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0,
					0));
			this.infoLabel = new JLabel("DXF2SVGViewer", JLabel.CENTER) {
				
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

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
			// toolbar
			JToolBar toolbar = new JToolBar();
			toolbar.add(createViewSwitchBox());
			
			panel.add(toolbar, BorderLayout.NORTH);
			this.canvas = new JSVGCanvas();
			panel.add(canvas, BorderLayout.CENTER);

			this.canvas.setDoubleBuffered(true);
			this.canvas.setDoubleBufferedRendering(true);
			this.canvas.setDocumentState(JSVGCanvas.ALWAYS_DYNAMIC);

			this.canvas
					.addSVGDocumentLoaderListener(new SVGDocumentLoaderAdapter() {
						public void documentLoadingStarted(
								SVGDocumentLoaderEvent e) {
							infoLabel.setText("Loading ...");
						}

						public void documentLoadingCompleted(
								SVGDocumentLoaderEvent e) {
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

			this.canvas
					.addGVTTreeRendererListener(new GVTTreeRendererAdapter() {
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


	public void showDXFDocument(DXFDocument doc) throws UIException {
		this.doc = doc;
		this.properties.clear();
		//this will invoke the view
		this.switchViewBox.setSelectedIndex(-1);
		this.switchViewBox.setSelectedIndex(0);
	}
	
	
	protected void updateView(DXFDocument doc)throws UIException{
		try {
			
			this.infoLabel.setText("Starting ...");
			this.infoLabel.repaint();
			this.cards.show(this.parentPanel, "info");
			DXFSAXDocumentFactory factory = new DXFSAXDocumentFactory();
			SVGDocument svgDoc = factory.createDocument(doc, this.properties);
			this.canvas.setSVGDocument(svgDoc);

		} catch (Exception e) {

			throw new UIException(e);
		}
	}

	public JComboBox createViewSwitchBox() {

		this.switchViewBox = new JComboBox(data);
		this.switchViewBox.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {

				if (e.getStateChange() == ItemEvent.SELECTED) {
					String key = (String) e.getItem();
					if (key.equals(data[0])) {
						properties
						.put(SVGGenerator.PROPERTY_DOCUMENT_BOUNDS_RULE,
								SVGGenerator.PROPERTY_DOCUMENT_BOUNDS_RULE_MODELSPACE_VALUE);
						
					} else if (key.equals(data[1])) {
						properties
								.put(SVGGenerator.PROPERTY_DOCUMENT_BOUNDS_RULE,
										SVGGenerator.PROPERTY_DOCUMENT_BOUNDS_RULE_PAPERSPACE_VALUE);
					} else if (key.equals(data[2])) {
						properties
						.put(SVGGenerator.PROPERTY_DOCUMENT_BOUNDS_RULE,SVGGenerator.PROPERTY_DOCUMENT_BOUNDS_RULE_KABEJA_VALUE);
					}
					SwingUtilities.invokeLater(new Runnable() {

						public void run() {
							if (doc != null) {
								try {
									firePropertiesChangedEvent();
									updateView(doc);
									
								} catch (UIException e) {
				     			  e.printStackTrace();
								}
							}
						}

					});

				}

			}

		});

		return this.switchViewBox;
	}

	public void setServiceManager(ServiceManager manager) {
	   Object[] obj = manager.getServiceComponents(DXFDocumentChangeEventProvider.SERVICE);
		for(int i=0;i<obj.length;i++){
			((DXFDocumentChangeEventProvider)obj[i]).addDXFDocumentChangeListener(this);
		}
	}

	public void changed(DXFDocument doc) {
		 try {
			 System.out.println("got event");
			this.updateView(doc);
		} catch (UIException e) {
			e.printStackTrace();
		}
		
	}



}
