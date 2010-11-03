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

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import org.kabeja.processing.ProcessingManager;
import org.kabeja.tools.SAXProcessingManagerBuilder;


public class OpenProcessingAction extends AbstractAction {
    protected String baseDir = "";
    protected ServiceContainer container;

    public OpenProcessingAction(ServiceContainer container) {
        super(Messages.getString("OpenProcessingAction.menuitem"));
        putValue(SHORT_DESCRIPTION, Messages.getString("OpenProcessingAction.menuitem.description"));
        this.container = container;
    }

    public void actionPerformed(ActionEvent e) {
        Thread t = new Thread(new Runnable() {
                    public void run() {
                        openProcessing();
                    }
                });
        t.start();
    }

    protected void openProcessing() {
        JFileChooser fc = new JFileChooser(this.baseDir);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int value = fc.showOpenDialog(null);

        if (value == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();

            try {
                ProcessingManager m = SAXProcessingManagerBuilder.buildFromStream(new FileInputStream(
                            file));
                container.setProcessingManager(m);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
