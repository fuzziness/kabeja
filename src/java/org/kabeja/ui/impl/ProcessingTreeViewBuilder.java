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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.tree.TreeModel;

import org.kabeja.processing.ProcessingManager;
import org.kabeja.ui.model.ProcessingTreeModelPresenter;

import de.miethxml.toolkit.ui.PanelFactory;
import de.miethxml.toolkit.ui.UIUtils;


public class ProcessingTreeViewBuilder {
    protected ProcessingManager manager;
    protected JTree tree;

    public ProcessingTreeViewBuilder(ProcessingManager manager) {
        this.manager = manager;
    }

    public javax.swing.JComponent getView() {
        JSplitPane sp = PanelFactory.createOneTouchSplitPane();

        tree = new JTree(((TreeModel) new ProcessingTreeModelPresenter(
                    this.manager)));
        tree.setEditable(true);
        tree.setDragEnabled(true);

        tree.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        tree.setRowHeight(0);

        JScrollPane scroll = new JScrollPane(tree);

        scroll.setBorder(BorderFactory.createEmptyBorder());

        JPanel panel = PanelFactory.createTitledPanel(scroll, "ProcessingTree",
                "Options", createBorderMenu());
        panel.setPreferredSize(new Dimension(150, 350));

        sp.setLeftComponent(panel);

        JPanel panel2 = PanelFactory.createTitledPanel(new JPanel(),
                "Property Editor");
        panel2.setPreferredSize(new Dimension(150, 70));
        sp.setRightComponent(panel2);

        return sp;
    }

    private JPopupMenu createBorderMenu() {
        JPopupMenu bordermenu = new JPopupMenu();
        JMenuItem item = new JMenuItem("Expand All",
                new ImageIcon(UIUtils.resourceToBytes(this.getClass(),
                        "/icons/expand_all.gif")));
        item.setMnemonic('E');
        item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int row = 0;

                    while (row < tree.getRowCount()) {
                        tree.expandRow(row);
                        row++;
                    }
                }
            });
        bordermenu.add(item);

        item = new JMenuItem("Collapse All",
                new ImageIcon(UIUtils.resourceToBytes(this.getClass(),
                        "/icons/collapse_all.gif")));
        item.setMnemonic('C');
        item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int row = tree.getRowCount() - 1;

                    while (row > 0) {
                        tree.collapseRow(row);
                        row--;
                    }
                }
            });
        bordermenu.add(item);

        return bordermenu;
    }
}
