package org.kabeja.ui.event;

import org.kabeja.ui.ViewComponent;

/**
 * This component will fire a change or exchange of the DXFDocument
 * to all listeners.
 * @author simon
 *
 */
public interface DXFDocumentChangeEventProvider {

	public static final String SERVICE=DXFDocumentChangeEventProvider.class.getName();
	
	public void addDXFDocumentChangeListener(DXFDocumentChangeListener listener);
	public void removeDXFDocumentChangeListener(DXFDocumentChangeListener listener);
	
}
