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
package org.kabeja.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JToggleButton;


public class JToggleButtonGroup implements ActionListener {
    protected List unselectorButtons = new ArrayList();
    protected boolean selected = false;
    protected JToggleButton selectedButton;

    public void actionPerformed(ActionEvent e) {
        if (this.unselectorButtons.contains(e.getSource()) && this.selected) {
            this.selected = false;
            this.selectedButton.setSelected(false);

            return;
        } else if (this.selected) {
            if ((this.selectedButton == e.getSource()) &&
                    this.selectedButton.isSelected()) {
                this.selected = false;
            } else {
                this.selectedButton.setSelected(false);
            }
        } else {
            this.selected = true;
        }

        this.selectedButton = (JToggleButton) e.getSource();
    }

    public void add(JToggleButton button) {
        button.setSelected(false);
        button.addActionListener(this);
    }

    public void addUnSelector(AbstractButton button) {
        this.unselectorButtons.add(button);
        button.addActionListener(this);
    }
}
