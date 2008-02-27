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

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.kabeja.processing.ProcessingManager;
import org.kabeja.ui.ViewComponent;

import de.miethxml.toolkit.ui.PanelFactory;


public class ProcessingEditorViewComponent implements ViewComponent {
    protected boolean initialized = false;
    protected JComponent view;
    protected ProcessingManager manager;

    public String getTitle() {
        return "ProcessingEditor";
    }

    public JComponent getView() {
        if (!this.initialized) {
            this.initialize();
        }

        return view;
    }

    protected void initialize() {
        JSplitPane sp = PanelFactory.createOneTouchSplitPane(JSplitPane.VERTICAL_SPLIT);
        sp.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //sp.setDividerLocation(150);
        ProcessingTreeViewBuilder treeBuilder = new ProcessingTreeViewBuilder(this.manager);
        sp.setTopComponent(treeBuilder.getView());
        sp.setBottomComponent(PanelFactory.createTitledPanel(new JPanel(),
                "PipelineView"));
        this.view = sp;
    }

    public void setProcessingManager(ProcessingManager manager) {
        this.manager = manager;
    }
}
