package org.kabeja.ui;

import javax.swing.Action;

public interface ApplicationToolBar {
	public static final String SERVICE=ApplicationToolBar.class.getName();
	
	public void addAction(Action action);
}
