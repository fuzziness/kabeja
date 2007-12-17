package org.kabeja.ui.impl;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.kabeja.processing.ProcessingManager;
import org.kabeja.ui.ViewComponent;

import de.miethxml.toolkit.ui.PanelFactory;

public class ProcessingEditorViewComponent implements ViewComponent{

	protected boolean initialized=false;
	protected JComponent view;
	protected ProcessingManager manager;
	
	public String getTitle() {
		
		return "ProcessingEditor";
	}

	public JComponent getView() {
		if(!this.initialized){
			this.initialize();
		}
		return view;
	}

	
	protected void initialize(){
			
		JSplitPane sp = PanelFactory.createOneTouchSplitPane(JSplitPane.VERTICAL_SPLIT);
		sp.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		//sp.setDividerLocation(150);

		ProcessingTreeViewBuilder treeBuilder = new ProcessingTreeViewBuilder(
				this.manager);
		sp.setTopComponent(treeBuilder.getView());
		sp.setBottomComponent(PanelFactory.createTitledPanel(new JPanel(), "PipelineView"));	
		this.view = sp;
	}

	public void setProcessingManager(ProcessingManager manager) {
		 this.manager = manager;	
	}
	
	
}
