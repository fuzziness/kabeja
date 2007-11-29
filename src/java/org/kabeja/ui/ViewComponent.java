package org.kabeja.ui;

import javax.swing.JComponent;

import org.kabeja.processing.ProcessingManager;

public interface ViewComponent extends ProcessingUIComponent{
  
	public static final String SERVICE=ViewComponent.class.getName();
	/**
	 * 
	 * @return the title of the component
	 */
	
	abstract String getTitle();
	/**
	 * 
	 * @return the view of this component
	 */
	abstract JComponent getView();
	
	
}
