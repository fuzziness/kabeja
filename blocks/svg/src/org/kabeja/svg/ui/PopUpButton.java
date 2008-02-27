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
package org.kabeja.svg.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;


public class PopUpButton extends JButton {
    private JPopupMenu popupmenu;
    private int[] px = new int[3];
    private int[] py = new int[3];

    public PopUpButton(JPopupMenu menu, String text) {
        super(text);
        this.popupmenu = menu;
        //this.setUI(new BasicButtonUI());
        //this.setFocusable(false);
        // this.setBorder(new SmallTriangleBorder());
        this.setOpaque(false);
        this.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Component c = (Component) e.getSource();
                    popupmenu.show(c, 0, c.getHeight());
                }
            });

        ItemListener l = new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getItem();
                        setIcon(item.getIcon());
                    }
                }
            };

        for (int i = 0; i < menu.getComponentCount(); i++) {
            Component c = menu.getComponent(i);

            if (c instanceof JCheckBoxMenuItem) {
                JCheckBoxMenuItem item = (JCheckBoxMenuItem) c;
                item.addItemListener(l);
            }
        }
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Dimension dim = this.getSize();
        int[] px = new int[3];
        px[0] = (int) (dim.getWidth() - 7);
        py[0] = (int) (dim.getHeight() - 2);

        px[1] = px[0] + 5;
        py[1] = py[0];

        px[2] = px[1];
        py[2] = py[0] - 5;
        g.setColor(Color.BLACK);
        g.fillPolygon(px, py, 3);
    }
}
