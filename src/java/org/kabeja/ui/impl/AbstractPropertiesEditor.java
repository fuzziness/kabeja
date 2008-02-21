package org.kabeja.ui.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.kabeja.ui.PropertiesEditor;
import org.kabeja.ui.PropertiesListener;

public abstract class AbstractPropertiesEditor implements PropertiesEditor{

	protected ArrayList listeners = new ArrayList();

	protected Map properties = new HashMap();
	
	public void addPropertiesListener(PropertiesListener listener) {
		this.listeners.add(listener);
		
	}

	public Map getProperties() {
		
		return this.properties;
	}

	public void removePropertiesListener(PropertiesListener listener) {
		  this.listeners.remove(listeners);
		
	}

	public void setProperties(Map properties) {
		this.properties=properties;
		
	}
	
	protected void firePropertiesChangedEvent(){
		
		Iterator i = ((ArrayList)this.listeners.clone()).iterator();
		while(i.hasNext()){
			PropertiesListener l = (PropertiesListener)i.next();
			l.propertiesChanged(this.properties);
		}
	}
	
	
}
