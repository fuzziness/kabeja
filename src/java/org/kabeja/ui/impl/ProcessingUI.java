package org.kabeja.ui.impl;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JTree;

import org.kabeja.processing.ProcessingManager;
import org.kabeja.ui.ProcessingUIComponent;
import org.kabeja.ui.ServiceManager;
import org.kabeja.ui.Serviceable;
import org.kabeja.ui.Startable;
import org.kabeja.ui.ApplicationToolBar;
import org.kabeja.ui.ViewComponent;
import org.kabeja.ui.model.ProcessingTreeModelPresenter;

import de.miethxml.toolkit.ui.PanelFactory;
import de.miethxml.toolkit.ui.SelectorComponent;

public class ProcessingUI implements Serviceable,Startable,ProcessingUIComponent,ApplicationToolBar{

	protected ServiceManager serviceManager;
	protected ProcessingManager manager;
	protected JFrame frame;
	protected List components = new ArrayList();
	private JPanel mainPanel;
	private CardLayout mainContainer;
	private SelectorComponent selector;
	private JPanel panel;

	private JToolBar toolbar;
   
	

	public ProcessingUI() {
		
	}

	protected void initialize() {
		this.frame = new JFrame("Kabeja");
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder());

		mainContainer = new CardLayout();
		mainPanel = new JPanel(mainContainer);

		toolbar = new JToolBar();
		toolbar.add(Box.createHorizontalGlue());

		selector = new SelectorComponent();
		toolbar.add(selector.getView());

		
		frame.getContentPane().add(toolbar,BorderLayout.NORTH);
		
		this.frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
		this.frame.setSize(700, 580);
		this.frame.validate();

	}

	public void setVisible(boolean b) {
		if (b) {
			
			this.frame.setVisible(true);
		} else {
			this.frame.setVisible(false);
			this.frame.dispose();
		}
	}

	public void addViewComponent(ViewComponent component) {
    
		int index = components.size();
		components.add(component);
      
		JComponent view = component.getView();
		 
		if (mainPanel.getMinimumSize().getWidth() < view.getPreferredSize()
				.getWidth()) {

			mainPanel.setPreferredSize(view.getPreferredSize());

			// view.setPreferredSize(view.getPreferredSize());
		}

		mainPanel.add(view, "" + index);
       
		AbstractAction action = new SwitchAction(component.getTitle(),index);
		
		//action.setKeyStroke("control " + components.size());

//		// add new action to the menu
//		menubar.addMenuLabel("menu.view.showview." + index, component
//				.getLabel());
//		menubar.addMenuItem("menu.view.showview",
//				"menu.view.showview." + index, new JMenuItem(action));

		// add to selector component
		selector.addAction(action);
	}

	public class SwitchAction extends AbstractAction {
		int index;

		public SwitchAction(String label, int index) {
            super(label);
            this.index=index;
		}

		public void actionPerformed(ActionEvent e) {
		
			mainContainer.show(mainPanel, "" + this.index);
			
			
		}

	}

	public void setServiceManager(ServiceManager manager) {
		this.serviceManager=manager;
		Object[] objects = this.serviceManager.getServiceComponents(ViewComponent.SERVICE);
		this.initialize();
		
		for(int i=0;i<objects.length;i++){
			this.addViewComponent((ViewComponent)objects[i]);
		}
		
		
		
	}

	public void setProcessingManager(ProcessingManager manager) {
		this.manager = manager;
		
	}

	public void start() {
		
		this.frame.setVisible(true);
		
	}

	public void stop() {
		this.frame.setVisible(false);
		
	}

	public void addAction(Action action) {
		JButton button = new JButton(action);
		button.setToolTipText( button.getText());
		button.setText("");
		this.toolbar.add(button,0);
		
	}
	
	
	
}
