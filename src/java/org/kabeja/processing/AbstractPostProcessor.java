package org.kabeja.processing;

import java.util.Map;

public abstract class AbstractPostProcessor implements PostProcessor{

	protected Map properties;

	public void setProperties(Map properties) {
		this.properties=properties;

	}


}
