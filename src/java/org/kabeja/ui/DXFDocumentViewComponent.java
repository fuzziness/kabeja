package org.kabeja.ui;

import javax.swing.JComponent;

import org.kabeja.dxf.DXFDocument;

public interface DXFDocumentViewComponent extends Component{

	public static final String SERVICE=DXFDocumentViewComponent.class.getName();
	
	
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
	
	
	/**
	 * Show the DXFDocument in the view of this component
	 * @param doc
	 * @throws UIException
	 */
	
	
	abstract void showDXFDocument(DXFDocument doc) throws UIException;
	
}
