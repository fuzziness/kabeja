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
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.gvt.GVTTreeRendererAdapter;
import org.apache.batik.swing.gvt.GVTTreeRendererEvent;
import org.apache.batik.swing.gvt.Interactor;
import org.apache.batik.swing.svg.GVTTreeBuilderAdapter;
import org.apache.batik.swing.svg.GVTTreeBuilderEvent;
import org.apache.batik.swing.svg.SVGDocumentLoaderAdapter;
import org.apache.batik.swing.svg.SVGDocumentLoaderEvent;
import org.kabeja.dxf.DXFDocument;
import org.kabeja.svg.SVGGenerator;
import org.kabeja.svg.action.CanvasUpdateRunnable;
import org.kabeja.svg.action.CustomActionView;
import org.kabeja.svg.action.DXFDocumentAction;
import org.kabeja.svg.action.GroupAction;
import org.kabeja.svg.action.GroupActionUnSelector;
import org.kabeja.svg.action.JSVGCanvasAction;
import org.kabeja.svg.action.SVGDocumentAction;
import org.kabeja.svg.action.ViewerAction;
import org.kabeja.svg.tools.DXFSAXDocumentFactory;
import org.kabeja.ui.DXFDocumentViewComponent;
import org.kabeja.ui.JToggleButtonGroup;
import org.kabeja.ui.PropertiesEditor;
import org.kabeja.ui.PropertiesListener;
import org.kabeja.ui.ServiceManager;
import org.kabeja.ui.Serviceable;
import org.kabeja.ui.UIException;
import org.kabeja.ui.event.DXFDocumentChangeEventProvider;
import org.kabeja.ui.event.DXFDocumentChangeListener;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGMatrix;


public class SVGViewUIComponent implements DXFDocumentViewComponent,
    Serviceable, DXFDocumentChangeListener,
    org.kabeja.svg.action.CanvasUpdateManager, PropertiesListener {
    protected boolean initialized = false;
    protected JSVGCanvas canvas;
    protected String title = "SVGView";
    protected JLabel infoLabel;
    protected JPanel parentPanel;
    protected JPanel panel;
    protected CardLayout cards;
    protected List actions = new ArrayList();
    String[] data = new String[] {
            "Modelspace-Calculated", "Modelspace", "Paperspace Calculated",
            "Paperspace", "Mixed"
        };
    protected DXFDocument doc;
    protected JComboBox switchViewBox;
    protected JToolBar toolbar;
    protected Map properties = new HashMap();
    protected JLabel mousePosition;
    private DecimalFormat format;

    public String getTitle() {
        return title;
    }

    public JComponent getView() {
        if (!this.initialized) {
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setDecimalSeparator('.');
            format = new DecimalFormat("###.#####", symbols);
            this.cards = new CardLayout();

            this.parentPanel = new JPanel(new BorderLayout());
            this.panel = new JPanel(cards) {
                        public void setEnabled(boolean b) {
                            if (!b) {
                                canvas.setSVGDocument(null);
                            }
                        }
                    };
            this.parentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0,
                    0));
            this.infoLabel = new JLabel("DXF2SVGViewer", JLabel.CENTER) {
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

            panel.add(this.infoLabel, "info");

            this.mousePosition = new JLabel("SVG x: y:");
            this.mousePosition.setBorder(BorderFactory.createLoweredBevelBorder());
            this.parentPanel.add(this.mousePosition, BorderLayout.SOUTH);

            // toolbar
            this.toolbar = new JToolBar();
            this.parentPanel.add(toolbar, BorderLayout.NORTH);

            // the svg canvas
            this.canvas = new JSVGCanvas();

            this.canvas.setDoubleBuffered(true);
            this.canvas.setDoubleBufferedRendering(true);
            this.canvas.setDocumentState(JSVGCanvas.ALWAYS_DYNAMIC);

            this.canvas.addSVGDocumentLoaderListener(new SVGDocumentLoaderAdapter() {
                    public void documentLoadingStarted(SVGDocumentLoaderEvent e) {
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

            this.canvas.addGVTTreeRendererListener(new GVTTreeRendererAdapter() {
                    public void gvtRenderingPrepare(GVTTreeRendererEvent e) {
                        infoLabel.setText("Rendering draft ...");
                    }

                    public void gvtRenderingCompleted(GVTTreeRendererEvent e) {
                        infoLabel.setText("");
                        cards.show(panel, "view");
                    }
                });
            this.canvas.addMouseMotionListener(new MouseMotionAdapter() {
                    private int count = 3;

                    public void mouseMoved(MouseEvent e) {
                        if (count == 3) {
                            count = 0;

                            Point p = e.getPoint();

                            // setup the start and end positions
                            SVGMatrix matrix = canvas.getSVGDocument()
                                                     .getRootElement()
                                                     .getScreenCTM().inverse();
                            double x = (matrix.getA() * p.getX()) +
                                (matrix.getC() * p.getY()) + matrix.getE();
                            double y = (matrix.getB() * p.getX()) +
                                (matrix.getD() * p.getY()) + matrix.getF();
                            mousePosition.setText("SVG  x:" + format.format(x) +
                                "  y:" + format.format(y) + "   DXF x:" +
                                format.format(x) + "  y:" +
                                format.format(-1 * y));
                        } else {
                            count++;
                        }
                    }
                });

            panel.add(this.canvas, "view");
            this.parentPanel.add(panel, BorderLayout.CENTER);

            this.registerActions();
            this.initialized = true;
        }

        return this.parentPanel;
    }

    public void showDXFDocument(DXFDocument doc) throws UIException {
        this.doc = doc;
        this.properties.clear();

        // this will invoke the view
        //this.switchViewBox.setSelectedIndex(-1);
        //this.switchViewBox.setSelectedIndex(0);
        Iterator i = this.actions.iterator();

        while (i.hasNext()) {
            Object obj = i.next();

            if (obj instanceof DXFDocumentAction) {
                ((DXFDocumentAction) obj).setDXFDocument(doc);
            }
        }

        this.updateView(doc);
    }

    protected void updateView(DXFDocument doc) throws UIException {
        try {
            this.infoLabel.setText("Starting ...");
            this.infoLabel.repaint();
            this.cards.show(this.panel, "info");

            DXFSAXDocumentFactory factory = new DXFSAXDocumentFactory();
            SVGDocument svgDoc = factory.createDocument(doc, this.properties);
            this.setSVGDocument(svgDoc);
  
        } catch (Exception e) {
           
            this.infoLabel.setText("Error:"+e.getMessage());
            this.infoLabel.repaint();
            this.cards.show(this.panel, "info");
            throw new UIException(e);
        }
    }

    public void setServiceManager(ServiceManager manager) {
        Object[] obj = manager.getServiceComponents(DXFDocumentChangeEventProvider.SERVICE);

        for (int i = 0; i < obj.length; i++) {
            ((DXFDocumentChangeEventProvider) obj[i]).addDXFDocumentChangeListener(this);
        }

        // the actions
        obj = manager.getServiceComponents(ViewerAction.SERVICE);

        for (int i = 0; i < obj.length; i++) {
            this.addAction((ViewerAction) obj[i]);
        }

        this.registerActions();
    }

    public void changed(DXFDocument doc) {
        try {
            this.updateView(doc);
        } catch (UIException e) {
            e.printStackTrace();
        }
    }

    public void invokeAndWait(Runnable r) throws InterruptedException {
        this.canvas.getUpdateManager().getUpdateRunnableQueue().invokeAndWait(r);
    }

    public void invokeLater(Runnable r) throws InterruptedException {
        this.canvas.getUpdateManager().getUpdateRunnableQueue().invokeLater(r);
    }

    protected void registerActions() {
        this.toolbar.removeAll();

        Iterator i = this.actions.iterator();
        JToggleButtonGroup group = new JToggleButtonGroup();

        //
        //		this.canvas.setEnableImageZoomInteractor(false);
        //		this.canvas.setEnablePanInteractor(false);
        //		this.canvas.setEnableRotateInteractor(false);
        //		this.canvas.setEnableZoomInteractor(false);
        while (i.hasNext()) {
            ViewerAction action = (ViewerAction) i.next();

            if (action instanceof CustomActionView) {
                this.toolbar.add(((CustomActionView) action).getView());
            } else if (action instanceof Action) {
                if (action instanceof ItemListener) {
                    JToggleButton button = new JToggleButton((Action) action);

                    if (action instanceof GroupAction) {
                        group.add(button);
                    } else if (action instanceof GroupActionUnSelector) {
                        group.addUnSelector(button);
                    }

                    button.setText("");
                    button.addItemListener((ItemListener) action);
                    this.toolbar.add(button);
                } else {
                    this.toolbar.add((Action) action);
                }
            }

            if (action instanceof PropertiesEditor) {
                PropertiesEditor editor = (PropertiesEditor) action;
                editor.addPropertiesListener(this);
                editor.setProperties(this.properties);
            }

            if (action instanceof CanvasUpdateRunnable) {
                ((CanvasUpdateRunnable) action).setCanvasUpdateManager(this);
            }

            if (action instanceof Interactor) {
                this.canvas.getInteractors().add((Interactor) action);
            }

            if (action instanceof JSVGCanvasAction) {
                ((JSVGCanvasAction) action).setJSVGCanvas(this.canvas);
            }

            if ((this.doc != null) && action instanceof DXFDocumentAction) {
                ((DXFDocumentAction) action).setDXFDocument(this.doc);
            }
        }
    }

    protected void setSVGDocument(SVGDocument doc) {
        this.canvas.setSVGDocument(doc);

        Iterator i = this.actions.iterator();

        while (i.hasNext()) {
            ViewerAction action = (ViewerAction) i.next();

            if (action instanceof SVGDocumentAction) {
                ((SVGDocumentAction) action).setDocument(doc);
            }
        }
    }

    public void addAction(ViewerAction action) {
        this.actions.add(action);
    }

    public void propertiesChanged(Map properties) {
        if (properties.containsKey(SVGGenerator.PROPERTY_DOCUMENT_BOUNDS_RULE) &&
                (this.doc != null)) {
            this.properties.put(SVGGenerator.PROPERTY_DOCUMENT_BOUNDS_RULE,
                properties.get(SVGGenerator.PROPERTY_DOCUMENT_BOUNDS_RULE));

            try {
                this.updateView(this.doc);
            } catch (UIException e) {
                e.printStackTrace();
            }
        }
    }
}
