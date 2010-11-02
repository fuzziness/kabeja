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
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;


public class AboutAction extends AbstractAction {
    JDialog dialog;

    public AboutAction() {
        super.putValue(NAME, Messages.getString("menuitem.about"));
        super.putValue(SHORT_DESCRIPTION, Messages.getString("menuitem.about"));
    }

    public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    showDialog();
                }
            });
    }

    public void showDialog() {
        dialog = new JDialog();
        dialog.setTitle(Messages.getString("menuitem.about"));

        JLabel l = new JLabel(Messages.getString("AboutDialog.title")) {
                private static final long serialVersionUID = 1L;

                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                    super.paintComponent(g);
                    g.setColor(Color.DARK_GRAY);
                }
            };

        l.setOpaque(true);
        l.setBackground(Color.WHITE);
        l.setForeground(Color.BLACK);
        l.setFont(new Font("Sans", Font.BOLD, 36));
        l.setBorder(new BottomLineBorder());

        dialog.add(l, BorderLayout.NORTH);

        JPanel b = new JPanel(new GridLayout(3, 1));
        b.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        l = new JLabel(Messages.getString("AboutDialog.description"));
        l.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        b.add(l);
        l = new JLabel(Messages.getString("AboutDialog.version"));
        l.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        b.add(l);
        l = new JLabel(Messages.getString("AboutDialog.website"));
        l.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        b.add(l);

        dialog.add(b, BorderLayout.CENTER);

        JPanel p2 = new JPanel();
        JButton close = new JButton(Messages.getString("button.close"));
        p2.add(close);
        dialog.add(p2, "South");

        close.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    dialog.setVisible(false);
                    dialog.dispose();
                    dialog = null;
                }
            });

        dialog.setLocation(350, 50);
        dialog.pack();
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        AboutAction a = new AboutAction();
        a.showDialog();
    }

    public class BottomLineBorder implements Border {
        private Insets i = new Insets(10, 10, 10, 1);
   


 
        public boolean isBorderOpaque() {
            return false;
        }

        public void paintBorder(Component c, Graphics g, int x, int y,
            int width, int height) {
            g.setColor(Color.DARK_GRAY);
            g.drawLine(x, (y + height) - 1, x + width, (y + height) - 1);
        }

        public Insets getBorderInsets(Component c) {
            return i;
        }
    }
}
