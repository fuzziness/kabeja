package org.kabeja.parser.dxf.filter;

import java.util.Map;

import org.kabeja.parser.dxf.DXFHandler;

public abstract class AbstractDXFStreamFilter implements DXFStreamFilter {

	protected Map properties;
	protected DXFHandler handler;

	public void setProperties(Map properties) {
		this.properties = properties;

	}

	public void setDXFHandler(DXFHandler handler) {
		this.handler = handler;
		
	}

}
