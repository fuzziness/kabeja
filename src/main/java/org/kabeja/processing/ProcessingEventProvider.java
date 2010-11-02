package org.kabeja.processing;

public interface ProcessingEventProvider {

	public void add(ProcessingEventListener listener);
	public void remove(ProcessingEventListener listener);
}
