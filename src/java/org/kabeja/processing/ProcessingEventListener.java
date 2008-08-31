package org.kabeja.processing;

public interface ProcessingEventListener {

	public void startParsing(String uri);
	public void finishedParsing(String uri);
	public void postprocess(String postprocessor);
	public void generate(String generator);
	public void filter(String filter);
	public void serialize(String serializer);
	
}
