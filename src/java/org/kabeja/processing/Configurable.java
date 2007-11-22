package org.kabeja.processing;

import java.util.Map;

public interface Configurable {

	/**
	 * Set configuration properties of the component
	 * 
	 * @param properties
	 */
	public void setProperties(Map properties);

	/**
	 * Get the configuration Properties of the component.
	 * 
	 * @return
	 */

	public Map getProperties();

}
