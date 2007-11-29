package org.kabeja.processing;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractConfigurable implements Configurable{

	protected Map properties =new HashMap();

	public void setProperties(Map properties) {
		this.properties=properties;

	}

	public Map getProperties() {	
		return this.properties;
	}

}
