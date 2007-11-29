package org.kabeja.ui;

import javax.swing.JComponent;

import org.kabeja.dxf.DXFDocument;

public interface DXFDocumentViewComponent extends ViewComponent{

	public static final String SERVICE=DXFDocumentViewComponent.class.getName();
	
	abstract void showDXFDocument(DXFDocument doc);
	
}
