package org.kabeja.processing.event;

import org.kabeja.processing.ProcessingManager;

public interface ProcessingListener {

	public void startProcessig(ProcessingEvent e);
	
	public void endProcessing(ProcessingEvent e);
	
	
	public void configurationChanged(ProcessingManager mangager);
}
