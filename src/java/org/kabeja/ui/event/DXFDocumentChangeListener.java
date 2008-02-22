package org.kabeja.ui.event;

import org.kabeja.dxf.DXFDocument;
/**
 * Interface for Listeners which want to receive changes on
 * the DXFDocument.
 * 
 *
 */
public interface DXFDocumentChangeListener {

	/**
	 * Called if the DXFDocument is changed or parts are
	 * changed
	 * @param doc the changed or exchanged DXFDocument
	 */
	public void changed(DXFDocument doc);
}
