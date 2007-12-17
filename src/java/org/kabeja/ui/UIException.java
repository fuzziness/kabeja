package org.kabeja.ui;

public class UIException extends Exception{

	public UIException(Exception e){
		super(e);
	}
	
	public UIException(String  message){
		super(message);
	}
}
