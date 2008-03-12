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
package org.kabeja.ui.impl;

import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.kabeja.dxf.DXFDocument;
import org.kabeja.parser.DXFParser;
import org.kabeja.parser.Parser;
import org.kabeja.parser.ParserBuilder;
import org.kabeja.processing.ProcessPipeline;
import org.kabeja.processing.ProcessingManager;
import org.kabeja.ui.ApplicationMenuBar;
import org.kabeja.ui.ApplicationToolBar;
import org.kabeja.ui.DXFDocumentViewComponent;
import org.kabeja.ui.PropertiesEditor;
import org.kabeja.ui.PropertiesListener;
import org.kabeja.ui.ServiceManager;
import org.kabeja.ui.Serviceable;
import org.kabeja.ui.UIException;
import org.kabeja.ui.ViewComponent;
import org.kabeja.ui.event.DXFDocumentChangeEventProvider;
import org.kabeja.ui.event.DXFDocumentChangeListener;

import de.miethxml.toolkit.ui.PanelFactory;
import de.miethxml.toolkit.ui.UIUtils;


public class ProcessingRunViewComponent implements ViewComponent, Serviceable,
    ActionListener, PropertiesListener, DXFDocumentChangeEventProvider {
    protected JTabbedPane tabbedPane;
    protected JComponent view;
    protected JPanel pipelinePanel;
    protected boolean locked = false;
    protected boolean initialized = false;
    protected List viewComponents = new ArrayList();
    protected String processingPipeline;
    protected JTextArea logView;
    protected ProcessingManager manager;
    protected String baseDir = "./samples/dxf";
    protected File sourceFile;
    protected DXFDocument doc;
    protected boolean autogenerateOutput = false;
    protected Map properties = new HashMap();
    protected ArrayList listeners = new ArrayList();

    public String getTitle() {
        return "Run Processing";
    }

    public JComponent getView() {
        this.initialize();

        return this.view;
    }

    protected void initialize() {
        if (!this.initialized) {
            JPanel panel = PanelFactory.createTitledPanel(new JPanel(),
            		Messages.getString("ProcessingRunViewComponent.processing.pipeline"),
                    new ImageIcon(UIUtils.resourceToBytes(this.getClass(),
                            "/icons/project.gif")));
            pipelinePanel = new JPanel(new GridLayout(0, 1, 0, 3));

            pipelinePanel.setBorder(BorderFactory.createEmptyBorder(4, 5, 4, 5));

            JScrollPane scroll = new JScrollPane(pipelinePanel);
            scroll.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            panel.add(scroll);

            JSplitPane sp = PanelFactory.createOneTouchSplitPane();
            sp.setLeftComponent(panel);
            tabbedPane = new JTabbedPane();
            tabbedPane.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

            sp.setRightComponent(tabbedPane);
            sp.setDividerLocation(150);

            JSplitPane sp2 = PanelFactory.createOneTouchSplitPane(JSplitPane.VERTICAL_SPLIT);
            sp2.setTopComponent(sp);

            this.logView = new JTextArea();
            scroll = new JScrollPane(this.logView);
            scroll.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

            JPanel p = PanelFactory.createTitledPanel(scroll,Messages.getString("ProcessingRunViewComponent.processing.output"));
            sp2.setBottomComponent(p);
            sp2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            sp2.setDividerLocation(400);
            this.view = sp2;

            DXFFileTransferHandler handler = new DXFFileTransferHandler(this);
            this.view.setTransferHandler(handler);
            this.initialized = true;
        }
    }

    public void setProcessingManager(ProcessingManager manager) {
        this.manager = manager;
        this.initialize();
        // setup the pipelines
        pipelinePanel.removeAll();

        Iterator i = manager.getProcessPipelines().keySet().iterator();

        while (i.hasNext()) {
            String pipelineName = (String) i.next();
            ProcessPipeline p = manager.getProcessPipeline(pipelineName);
            JButton button = new JButton(pipelineName);
            button.setActionCommand(pipelineName);
            button.addActionListener(this);
            button.setToolTipText(p.getDescription());
            pipelinePanel.add(button);
        }

        pipelinePanel.repaint();
    }

    public void setServiceManager(ServiceManager manager) {
        Object[] objects = manager.getServiceComponents(ApplicationToolBar.SERVICE);

        Action action = new AbstractAction(Messages.getString("ProcessingRunViewComponent.open.draft"),
                new ImageIcon(UIUtils.resourceToBytes(this.getClass(),
                        "/icons/open.gif"))) {
                private static final long serialVersionUID = 1L;

                public void actionPerformed(ActionEvent e) {
                    Runnable r = new Runnable() {
                            public void run() {
                                chooseInput();
                            }
                        };

                    Thread t = new Thread(r);
                    t.start();
                }
            };

        // we set the action to all toolbars if there are more then on
        for (int i = 0; i < objects.length; i++) {
            ((ApplicationToolBar) objects[i]).addAction(action);
        }

        objects = manager.getServiceComponents(PropertiesEditor.SERVICE);

        for (int i = 0; i < objects.length; i++) {
            ((PropertiesEditor) objects[i]).addPropertiesListener(this);
        }

        // view menu
        ApplicationMenuBar mbar = ((ApplicationMenuBar) manager.getServiceComponents(ApplicationMenuBar.SERVICE)[0]);
        JMenu viewMenu = new JMenu(Messages.getString("ProcessingRunViewComponent.process.view.menuitem"));

        if (!mbar.hasMenu(ApplicationMenuBar.MENU_ID_VIEW)) {
            mbar.setMenu(ApplicationMenuBar.MENU_ID_VIEW, new JMenu(Messages.getString("menu.view")));
        }

        objects = manager.getServiceComponents(DXFDocumentViewComponent.SERVICE);

        for (int i = 0; i < objects.length; i++) {
            ViewControl c = new ViewControl((DXFDocumentViewComponent) objects[i]);
            this.viewComponents.add(c);

            JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(c.getTitle());
            menuItem.setSelected(c.isEnabled());
            menuItem.addItemListener(c);
            viewMenu.add(menuItem);
        }

        mbar.setJMenuItem(ApplicationMenuBar.MENU_ID_VIEW, viewMenu);
        mbar.setAction(ApplicationMenuBar.MENU_ID_FILE, action);
    }

    public void actionPerformed(ActionEvent e) {
        if (!this.locked) {
            this.processingPipeline = e.getActionCommand();
            this.locked = true;

            Runnable r = new Runnable() {
                    public void run() {
                        process();
                    }
                };

            Thread t = new Thread(r);
            t.start();
        }
    }

    protected void process() {
        try {
            if (this.sourceFile.isDirectory()) {
                File[] files = this.sourceFile.listFiles();
                Parser parser = ParserBuilder.createDefaultParser();

                // we have to parse every file
                for (int i = 0; i < files.length; i++) {
                    String ext = files[i].getAbsolutePath();

                    int index = ext.lastIndexOf(".");

                    if ((index + 1) < ext.length()) {
                        ext = ext.substring(ext.lastIndexOf(".") + 1);

                        if (parser.supportedExtension(ext)) {
                            this.parseFile(files[i], parser);
                            this.processFile(this.doc, files[i]);
                        }
                    }
                }

                // file already parsed
            } else if (this.sourceFile.isFile()) {
                this.processFile(this.doc, this.sourceFile);
            }
        } catch (Exception e) {
            this.logException(e);
        }

        this.locked = false;
    }

    protected void processFile(DXFDocument doc, File f) {
        try {
            this.logView.append(Messages.getString("ProcessingRunViewComponent.log.processing") + f.getAbsolutePath() + "\n");

            File out = null;

            if (this.autogenerateOutput) {
                this.manager.getProcessPipeline(this.processingPipeline);

                String suffix = this.manager.getProcessPipeline(this.processingPipeline)
                                            .getSAXSerializer().getSuffix();
                String n = f.getAbsolutePath();
                n = n.substring(0, n.lastIndexOf('.')) + "." + suffix;
                out = new File(n);
            } else {
                JFileChooser fc = new JFileChooser(this.baseDir);
                int value = fc.showSaveDialog(null);

                if (value == JFileChooser.APPROVE_OPTION) {
                    out = fc.getSelectedFile();
                }
            }

            if (out != null) {
                this.manager.process(doc, this.properties,
                    this.processingPipeline, new FileOutputStream(out));
                this.log(Messages.getString("ProcessingRunViewComponent.log.finished") + out.getAbsolutePath() + "\n");
            } else {
                this.log("No output set, do nothing.\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.logException(e);
        }
    }

    protected void chooseInput() {
        JFileChooser fc = new JFileChooser(this.baseDir);
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        JCheckBox cb = new JCheckBox();
        cb.setSelected(true);

        JPanel p = new JPanel(new FlowLayout());
        p.add(cb);
        p.add(new JLabel(Messages.getString("ProcessingRunViewComponent.file.dialog.autogenerate")));
        fc.setAccessory(p);

        int value = fc.showOpenDialog(null);

        if (value == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            this.autogenerateOutput = cb.isSelected();

            if (file.isFile()) {
                this.baseDir = file.getParent();
                this.processInput(file);
            } else if (file.isDirectory()) {
                this.baseDir = file.getAbsolutePath();
                this.log(Messages.getString("ProcessingRunViewComponent.log.select.directory")+ file.getAbsolutePath() + "\n");
                this.log("No preview\n");
            }
        }
    }

    protected void parseFile(File f, Parser parser) throws Exception {
        this.sourceFile = f;
        this.logView.append(Messages.getString("ProcessingRunViewComponent.log.parsing") + f.getAbsolutePath() + "\n");
        parser.parse(new FileInputStream(f), DXFParser.DEFAULT_ENCODING);
        this.doc = parser.getDocument();
    }

    protected void propagateDXFDocument(DXFDocument doc)
        throws Exception {
        Iterator i = this.viewComponents.iterator();

        while (i.hasNext()) {
            ViewControl c = (ViewControl) i.next();

            if (c.isEnabled()) {
                c.getDXFDocumentViewComponent().showDXFDocument(doc);
            }
        }
    }

    protected void logException(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        this.log(sw.toString());
    }

    protected void log(String msg) {
        this.logView.append(msg);
        this.logView.setCaretPosition(this.logView.getDocument().getLength());
    }

    public void propertiesChanged(Map props) {
        // copy changed properties to my properties
        Iterator i = props.keySet().iterator();

        while (i.hasNext()) {
            String key = (String) i.next();
            this.properties.put(key, props.get(key));
        }
    }

    public void addDXFDocumentChangeListener(DXFDocumentChangeListener listener) {
        this.listeners.add(listener);
    }

    public void removeDXFDocumentChangeListener(
        DXFDocumentChangeListener listener) {
        this.listeners.remove(listener);
    }

    protected void fireDXFDocumentChangeEvent() throws Exception {
        Iterator i = ((ArrayList) this.listeners.clone()).iterator();

        while (i.hasNext()) {
            DXFDocumentChangeListener l = (DXFDocumentChangeListener) i.next();

            if (!(l instanceof DXFDocumentViewComponent)) {
                // avoid double event
                l.changed(this.doc);
            }
        }

        this.propagateDXFDocument(this.doc);
    }

    public void processInput(File file) {
        Parser parser = ParserBuilder.createDefaultParser();

        try {
            this.parseFile(file, parser);
            this.fireDXFDocumentChangeEvent();
        } catch (Exception e) {
            e.printStackTrace();
            this.logException(e);
        }
    }

    private class ViewControl implements ItemListener {
        private JComponent view;
        private String title;
        private DXFDocumentViewComponent component;
        private int index;

        ViewControl(DXFDocumentViewComponent component) {
            this.component = component;
            this.view = component.getView();
            this.index = tabbedPane.getTabCount();
            tabbedPane.add(component.getTitle(), view);
        }

        public boolean isEnabled() {
            return this.view.isEnabled();
        }

        public void setEnabled(boolean b) {
            this.view.setEnabled(b);
            this.enableChildren(b, this.view);
            tabbedPane.setEnabledAt(this.index, b);

            if (b && (doc != null)) {
                try {
                    this.component.showDXFDocument(doc);
                } catch (UIException e) {
                    e.printStackTrace();
                }
            }
        }

        public DXFDocumentViewComponent getDXFDocumentViewComponent() {
            return this.component;
        }

        public String getTitle() {
            return this.component.getTitle();
        }

        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            setEnabled(true);
                        }
                    });
            } else {
                SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            setEnabled(false);
                        }
                    });
            }
        }

        protected void enableChildren(boolean b, Container c) {
            for (int i = 0; i < c.getComponentCount(); i++) {
                Component comp = c.getComponent(i);
                comp.setEnabled(b);

                if (comp instanceof Container) {
                    enableChildren(b, (Container) comp);
                }
            }
        }
    }
}
