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

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.kabeja.batik.tools.AbstractSAXSerializer;
import org.kabeja.svg.SVGGenerator;
import org.kabeja.ui.ApplicationToolBar;
import org.kabeja.ui.Component;
import org.kabeja.ui.ServiceManager;
import org.kabeja.ui.Serviceable;
import org.kabeja.ui.impl.AbstractPropertiesEditor;

import de.miethxml.toolkit.gui.JGoodiesSeparator;
import de.miethxml.toolkit.ui.UIUtils;


public class OutputSettingsUI extends AbstractPropertiesEditor
    implements Serviceable, Component {
    private String[] papers = new String[] {
            "NoPapper", "A0", "A1", "A2", "A3", "A4", "A5", "A6", "Letter"
        };
    private String[] units = new String[] { "inch", "mm", "px" };
    private String[] layout = new String[] {
            SVGGenerator.PROPERTY_DOCUMENT_BOUNDS_RULE_MODELSPACE_VALUE,
            SVGGenerator.PROPERTY_DOCUMENT_BOUNDS_RULE_MODELSPACE_LIMITS_VALUE,
            SVGGenerator.PROPERTY_DOCUMENT_BOUNDS_RULE_PAPERSPACE_VALUE,
            SVGGenerator.PROPERTY_DOCUMENT_BOUNDS_RULE_PAPERSPACE_LIMITS_VALUE
        };

    // the ui stuff
    private JDialog dialog;
    protected JCheckBox customPaper;
    protected JTextField widthField;
    protected JTextField heightField;
    protected JComboBox unitSelection;
    protected JComboBox paperSelection;
    protected JComboBox layoutSelection;
    protected JRadioButton landscapeSelection;
    protected boolean initialized = false;

    public void setServiceManager(ServiceManager manager) {
        Object[] objects = manager.getServiceComponents(ApplicationToolBar.SERVICE);

        for (int i = 0; i < objects.length; i++) {
            ((ApplicationToolBar) objects[i]).addAction(new AbstractAction(
                    "Output Settings",
                    new ImageIcon(UIUtils.resourceToBytes(this.getClass(),
                            "/icons/paper_settings.gif"))) {
                    public void actionPerformed(ActionEvent e) {
                        showDialog();
                    }
                });
        }
    }

    protected void init() {
        if (!this.initialized) {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

            panel.add(new JGoodiesSeparator("Paper Size"),
                createConstraints(0, 0, 3, 1, GridBagConstraints.HORIZONTAL,
                    GridBagConstraints.CENTER, new Insets(0, 0, 8, 5)));

            panel.add(new JLabel("Paper"),
                createConstraints(0, 1, 1, 1, GridBagConstraints.NONE,
                    GridBagConstraints.EAST));
            paperSelection = new JComboBox(papers);
            panel.add(paperSelection,
                createConstraints(1, 1, 2, 1, GridBagConstraints.HORIZONTAL,
                    GridBagConstraints.WEST));

            panel.add(new JLabel("Custom Paper"),
                createConstraints(0, 2, 1, 1, GridBagConstraints.NONE,
                    GridBagConstraints.EAST, new Insets(8, 0, 1, 0)));

            customPaper = new JCheckBox();
            customPaper.addItemListener(new ItemListener() {
                    public void itemStateChanged(ItemEvent e) {
                        enableCustomPaper(e.getStateChange() == ItemEvent.SELECTED);
                    }
                });

            panel.add(customPaper,
                createConstraints(1, 2, 1, 1, GridBagConstraints.NONE,
                    GridBagConstraints.WEST, new Insets(8, 0, 1, 0)));

            panel.add(new JLabel("Unit"),
                createConstraints(0, 3, 1, 1, GridBagConstraints.NONE,
                    GridBagConstraints.EAST));
            unitSelection = new JComboBox(units);
            panel.add(unitSelection,
                createConstraints(1, 3, 2, 1, GridBagConstraints.HORIZONTAL,
                    GridBagConstraints.WEST));

            panel.add(new JLabel("Width"),
                createConstraints(0, 4, 1, 1, GridBagConstraints.NONE,
                    GridBagConstraints.EAST));
            widthField = new JTextField(20);

            panel.add(widthField,
                createConstraints(1, 4, 2, 1, GridBagConstraints.BOTH,
                    GridBagConstraints.WEST));

            panel.add(new JLabel("Height"),
                createConstraints(0, 5, 1, 1, GridBagConstraints.NONE,
                    GridBagConstraints.EAST));
            heightField = new JTextField(20);
            panel.add(heightField,
                createConstraints(1, 5, 2, 1, GridBagConstraints.HORIZONTAL,
                    GridBagConstraints.WEST));

            panel.add(new JGoodiesSeparator("Orientation"),
                createConstraints(0, 6, 3, 1, GridBagConstraints.HORIZONTAL,
                    GridBagConstraints.CENTER, new Insets(25, 0, 1, 5)));

            ButtonGroup group = new ButtonGroup();

            landscapeSelection = new JRadioButton("Landscape");
            group.add(landscapeSelection);
            panel.add(landscapeSelection,
                createConstraints(1, 7, 2, 1, GridBagConstraints.HORIZONTAL,
                    GridBagConstraints.WEST));

            JRadioButton b = new JRadioButton("Portrait");
            b.setSelected(true);
            group.add(b);
            panel.add(b,
                createConstraints(1, 8, 2, 1, GridBagConstraints.HORIZONTAL,
                    GridBagConstraints.WEST));

            panel.add(new JGoodiesSeparator("Layout"),
                createConstraints(0, 9, 3, 1, GridBagConstraints.HORIZONTAL,
                    GridBagConstraints.CENTER, new Insets(25, 0, 1, 5)));

            layoutSelection = new JComboBox(layout);
            panel.add(layoutSelection,
                createConstraints(1, 10, 2, 1, GridBagConstraints.HORIZONTAL,
                    GridBagConstraints.WEST));

            JButton button = new JButton("OK");
            button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    dialog.setVisible(false);
                                    updateProperties();
                                }
                            });
                    }
                });
            panel.add(button,
                createConstraints(2, 11, 1, 1, GridBagConstraints.NONE,
                    GridBagConstraints.EAST, new Insets(12, 0, 1, 0)));

            enableCustomPaper(false);
            this.dialog = new JDialog();
            this.dialog.setTitle("Paper Selection");
            this.dialog.getContentPane().add(panel, BorderLayout.NORTH);
            this.dialog.pack();
            this.initialized = true;
        }
    }

    public void showDialog() {
        this.init();
        this.dialog.setVisible(true);
    }

    protected GridBagConstraints createConstraints(int x, int y, int width,
        int height, int fill, int anchor) {
        return createConstraints(x, y, width, height, fill, anchor,
            new Insets(1, 0, 1, 0));
    }

    protected GridBagConstraints createConstraints(int x, int y, int width,
        int height, int fill, int anchor, Insets insets) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
        gbc.fill = fill;
        gbc.anchor = anchor;
        gbc.ipadx = 9;
        gbc.ipady = 6;

        if (fill != GridBagConstraints.NONE) {
            gbc.weightx = 100;
        }

        gbc.weighty = 0;
        gbc.insets = insets;

        return gbc;
    }

    protected void updateProperties() {
        if (this.customPaper.isSelected()) {
            // custom paper
            this.properties.put(AbstractSAXSerializer.PROPERTY_PAPER, "NoPaper");
            this.properties.put(AbstractSAXSerializer.PROPERTY_WIDTH,
                this.widthField.getText() + unitSelection.getSelectedItem());
            this.properties.put(AbstractSAXSerializer.PROPERTY_HEIGHT,
                this.heightField.getText() + unitSelection.getSelectedItem());
        } else {
            this.properties.put(AbstractSAXSerializer.PROPERTY_PAPER,
                this.paperSelection.getSelectedItem());
        }

        // the orientation
        if (this.landscapeSelection.isSelected()) {
            this.properties.put(AbstractSAXSerializer.PROPERTY_ORIENTATION,
                "landscape");
        } else {
            this.properties.put(AbstractSAXSerializer.PROPERTY_ORIENTATION,
                "portait");
        }

        this.properties.put(SVGGenerator.PROPERTY_DOCUMENT_BOUNDS_RULE,
            this.layoutSelection.getSelectedItem().toString());
        firePropertiesChangedEvent();
    }

    protected void enableCustomPaper(boolean b) {
        this.paperSelection.setEnabled(!b);
        this.unitSelection.setEnabled(b);
        this.widthField.setEnabled(b);
        this.heightField.setEnabled(b);
    }
}
