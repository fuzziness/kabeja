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
package org.kabeja.svg.action;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.kabeja.dxf.DXFDocument;
import org.kabeja.svg.SVGGenerator;
import org.kabeja.svg.ui.PopUpButton;
import org.kabeja.ui.impl.AbstractPropertiesEditor;

import de.miethxml.toolkit.ui.UIUtils;


public class LayoutSwitchAction extends AbstractPropertiesEditor
    implements ViewerAction, CustomActionView, DXFDocumentAction {
    PopUpButton button;
    JCheckBoxMenuItem defaultItem;

    public JComponent getView() {
        if (this.button == null) {
            ItemListener l = new ItemListener() {
                    public void itemStateChanged(ItemEvent e) {
                        if (e.getStateChange() == ItemEvent.SELECTED) {
                            properties.put(SVGGenerator.PROPERTY_DOCUMENT_BOUNDS_RULE,
                                ((JCheckBoxMenuItem) e.getItem()).getText());
                            SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        firePropertiesChangedEvent();
                                    }
                                });
                        }
                    }
                };

            JPopupMenu menu = new JPopupMenu();

            ButtonGroup group = new ButtonGroup();
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(SVGGenerator.PROPERTY_DOCUMENT_BOUNDS_RULE_MODELSPACE_VALUE);
            item.setIcon(new ImageIcon(UIUtils.resourceToBytes(
                        this.getClass(), "/icons/layout_ms.png")));

            item.addItemListener(l);
            group.add(item);
            menu.add(item);

            this.defaultItem = item;

            item = new JCheckBoxMenuItem(SVGGenerator.PROPERTY_DOCUMENT_BOUNDS_RULE_MODELSPACE_LIMITS_VALUE);
            item.addItemListener(l);
            item.setIcon(new ImageIcon(UIUtils.resourceToBytes(
                        this.getClass(), "/icons/layout_ms_l.png")));
            group.add(item);
            menu.add(item);
            item = new JCheckBoxMenuItem(SVGGenerator.PROPERTY_DOCUMENT_BOUNDS_RULE_PAPERSPACE_VALUE);
            item.addItemListener(l);
            item.setIcon(new ImageIcon(UIUtils.resourceToBytes(
                        this.getClass(), "/icons/layout_ps.png")));
            group.add(item);
            menu.add(item);
            item = new JCheckBoxMenuItem(SVGGenerator.PROPERTY_DOCUMENT_BOUNDS_RULE_PAPERSPACE_LIMITS_VALUE);
            item.addItemListener(l);
            item.setIcon(new ImageIcon(UIUtils.resourceToBytes(
                        this.getClass(), "/icons/layout_ps_l.png")));
            group.add(item);
            menu.add(item);
            this.button = new PopUpButton(menu, "");
            this.defaultItem.setSelected(true);
        }

        return this.button;
    }

    public void setDXFDocument(DXFDocument doc) {
        this.defaultItem.setSelected(true);
    }
}
