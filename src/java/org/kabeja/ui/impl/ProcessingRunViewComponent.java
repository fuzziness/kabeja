package org.kabeja.ui.impl;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;

import org.kabeja.processing.ProcessingManager;
import org.kabeja.ui.DXFDocumentViewComponent;
import org.kabeja.ui.ServiceManager;
import org.kabeja.ui.Serviceable;
import org.kabeja.ui.ViewComponent;

import de.miethxml.toolkit.ui.PanelFactory;

public class ProcessingRunViewComponent implements ViewComponent,Serviceable{

	protected JTabbedPane tabbedPane;
	protected JComponent view;
	protected JPanel pipelinePanel;

	protected boolean initialized = false;

	public String getTitle() {

		return "Run Processing";
	}

	public JComponent getView() {
        this.initialize();
		return this.view;

	}

	protected void initialize() {
		if (!this.initialized) {
			JPanel panel = PanelFactory.createTitledPanel(new JPanel(),
					"ProcessingPipelines", "icons/project.gif");
			pipelinePanel = new JPanel(new GridLayout(0,1,0,3));
		
			//pipelinePanel.setLayout(new BoxLayout(pipelinePanel, BoxLayout.Y_AXIS));
			pipelinePanel.setBorder(BorderFactory.createEmptyBorder(4, 5, 4, 5));
			JScrollPane scroll = new JScrollPane(pipelinePanel);
			scroll.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
			panel.add(scroll);
			
			JSplitPane sp = PanelFactory.createOneTouchSplitPane();
			sp.setLeftComponent(panel);
			tabbedPane = new JTabbedPane();
			tabbedPane.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

			sp.setRightComponent(tabbedPane);
            sp.setDividerLocation(150);
			JSplitPane sp2 = PanelFactory
					.createOneTouchSplitPane(JSplitPane.VERTICAL_SPLIT);
			sp2.setTopComponent(sp);

			JPanel p = PanelFactory.createTitledPanel(new JPanel(),
					"ProcessingOutput", null);
			sp2.setBottomComponent(p);
			sp2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			
			sp2.setDividerLocation(350);
			this.view = sp2;
			this.initialized = true;
		}
	}

	public void setProcessingManager(ProcessingManager manager) {
		  this.initialize();
		//setup the pipelines
		pipelinePanel.removeAll();
		Iterator i = manager.getProcessPipelines().keySet().iterator();
		while(i.hasNext()){
			String pipelineName = (String)i.next();
			JButton button = new JButton(pipelineName);
			pipelinePanel.add(button);
			
		}
		pipelinePanel.revalidate();
		
		

	}

	protected void addDXFDocumentViewComponent(
			DXFDocumentViewComponent component) {
		this.tabbedPane.add(component.getTitle(), component.getView());
         
	}

	public void setServiceManager(ServiceManager manager) {
		Object[] objects = manager.getServiceComponents(DXFDocumentViewComponent.SERVICE);
		for(int i=0;i<objects.length;i++){
			this.addDXFDocumentViewComponent((DXFDocumentViewComponent)objects[i]);
		}
	}
	

}
