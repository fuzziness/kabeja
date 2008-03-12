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

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.kabeja.processing.ProcessingManager;
import org.kabeja.ui.Application;
import org.kabeja.ui.ApplicationMenuBar;
import org.kabeja.ui.ApplicationToolBar;
import org.kabeja.ui.ProcessingUIComponent;
import org.kabeja.ui.ServiceManager;
import org.kabeja.ui.Serviceable;
import org.kabeja.ui.Startable;
import org.kabeja.ui.ViewComponent;

import de.miethxml.toolkit.ui.SelectorComponent;


public class ProcessingUI implements Serviceable, Startable,
    ProcessingUIComponent, ApplicationToolBar, ApplicationMenuBar {
    protected ServiceManager serviceManager;
    protected ProcessingManager manager;
    protected JFrame frame;
    protected List components = new ArrayList();
    private JPanel mainPanel;
    private CardLayout mainContainer;
    private SelectorComponent selector;
    private JPanel panel;
    private JToolBar toolbar;
    private JMenuBar menubar;
    private Map menus = new HashMap();
    private Application application;

    public ProcessingUI() {
    }

    protected void initialize() {
        this.frame = new JFrame(Messages.getString("ProcessingUI.title")); //$NON-NLS-1$
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder());

        mainContainer = new CardLayout();
        mainPanel = new JPanel(mainContainer);

        // the toolbar
        toolbar = new JToolBar();
        toolbar.add(Box.createHorizontalGlue());

        selector = new SelectorComponent();
        toolbar.add(selector.getView());

        frame.getContentPane().add(toolbar, BorderLayout.NORTH);

        // the menubar
        this.menubar = new JMenuBar();
        this.frame.setJMenuBar(this.menubar);

        //Help and about 
        this.menubar.add(Box.createHorizontalGlue());
        JMenu menu = new JMenu(Messages.getString("ProcessingUI.menu.help")); //$NON-NLS-1$
        menu.add(new AboutAction());
        this.menubar.add(menu);
        this.menus.put(ApplicationMenuBar.MENU_ID_HELP, menu);
        
        menu = new JMenu(Messages.getString("ProcessingUI.menu.file")); //$NON-NLS-1$
        menu.add(new AbstractAction(Messages.getString(
                    "ProcessingUI.menu.file.exit")) {
                public void actionPerformed(ActionEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                shutdown();
                            }
                        });
                }
            });

       
        
        this.setMenu(ApplicationMenuBar.MENU_ID_FILE, menu);

 
      
        this.frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
        this.frame.setSize(700,620);
        this.frame.validate();
    }

    public void setVisible(boolean b) {
        if (b) {
            this.frame.setVisible(true);
        } else {
            this.frame.setVisible(false);

            // this.frame.dispose();
        }
    }

    public void addViewComponent(ViewComponent component) {
        int index = components.size();
        components.add(component);

        JComponent view = component.getView();

        if (mainPanel.getMinimumSize().getWidth() < view.getPreferredSize()
                                                            .getWidth()) {
            mainPanel.setPreferredSize(view.getPreferredSize());
        }

        mainPanel.add(view, "" + index); //$NON-NLS-1$

        AbstractAction action = new SwitchAction(component.getTitle(), index);

        // add to selector component
        selector.addAction(action);
    }

    public void setServiceManager(ServiceManager manager) {
        this.serviceManager = manager;

        org.kabeja.ui.Component[] objects = this.serviceManager.getServiceComponents(Application.SERVICE);
        this.application = (Application) objects[0];

        this.initialize();

        objects = this.serviceManager.getServiceComponents(ViewComponent.SERVICE);

        for (int i = 0; i < objects.length; i++) {
            this.addViewComponent((ViewComponent) objects[i]);
        }
    }

    public void setProcessingManager(ProcessingManager manager) {
        this.manager = manager;
    }

    public void start() {
        this.frame.setVisible(true);
    }

    public void stop() {
        this.frame.setVisible(false);
    }

    public void addAction(Action action) {
        JButton button = new JButton(action);
        button.setToolTipText(button.getText());
        button.setText("");
        this.addAction(button);
    }

    public void addAction(Component component) {
        if (this.toolbar.getComponentCount() > 1) {
            this.toolbar.add(component, this.toolbar.getComponentCount() - 2);
        } else {
            this.toolbar.add(component);
        }
    }

    public boolean hasMenu(String id) {
        return this.menus.containsKey(id);
    }

    public void setAction(String menuID, Action action) {
        this.setJMenuItem(menuID, new JMenuItem(action));
    }

    public void setJMenuItem(String menuID, JMenuItem item) {
        if (this.hasMenu(menuID)) {
            JMenu menu = (JMenu) this.menus.get(menuID);

            if (menuID.equals(ApplicationMenuBar.MENU_ID_FILE)) {
                menu.add(item, 0);
            } else {
                menu.add(item);
            }
        }
    }

    public void setMenu(String menuID, JMenu menu) {
        if (!this.hasMenu(menuID)) {
            this.menus.put(menuID, menu);
            //let the help menu at the end
            this.menubar.add(menu,this.menubar.getComponentCount()-2);
        }
    }

    protected void shutdown() {
        this.application.stop();
    }

    public class SwitchAction extends AbstractAction {
        int index;

        public SwitchAction(String label, int index) {
            super(label);
            this.index = index;
        }

        public void actionPerformed(ActionEvent e) {
            mainContainer.show(mainPanel, "" + this.index); //$NON-NLS-1$
        }
    }
}
