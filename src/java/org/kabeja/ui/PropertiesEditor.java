package org.kabeja.ui;

import java.util.Map;

public interface PropertiesEditor {

	public static final String SERVICE=PropertiesEditor.class.getName();
	public void setProperties(Map properties);
	
	public Map getProperties();
	
	public void addPropertiesListener(PropertiesListener listener);
	
	public void removePropertiesListener(PropertiesListener listener);
	
	
}
