package org.kabeja.processing;

import java.util.Map;

public abstract class AbstractConfigurable implements Configurable{

	protected Map properties;

	public void setProperties(Map properties) {
		this.properties=properties;

	}

	public Map getProperties() {	
		return this.properties;
	}

}
