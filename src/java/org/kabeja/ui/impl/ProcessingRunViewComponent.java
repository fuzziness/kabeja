package org.kabeja.ui.impl;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import org.kabeja.processing.ProcessingManager;
import org.kabeja.ui.DXFDocumentViewComponent;
import org.kabeja.ui.ServiceManager;
import org.kabeja.ui.Serviceable;
import org.kabeja.ui.ViewComponent;

import de.miethxml.toolkit.ui.PanelFactory;

public class ProcessingRunViewComponent implements ViewComponent,Serviceable{

	protected JTabbedPane tabbedPane;
	protected JComponent view;

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
