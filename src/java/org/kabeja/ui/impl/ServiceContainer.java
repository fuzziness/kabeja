package org.kabeja.ui.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kabeja.processing.ProcessingManager;
import org.kabeja.ui.Application;
import org.kabeja.ui.Component;
import org.kabeja.ui.ProcessingUIComponent;
import org.kabeja.ui.ServiceManager;
import org.kabeja.ui.Serviceable;
import org.kabeja.ui.Startable;

public class ServiceContainer implements ServiceManager,Application{

	protected List components = new ArrayList();
	protected ProcessingManager manager;
	
	public ServiceContainer(){
		this.components.add(this);
	}
	
	
	public void addComponent(Component c){

		this.components.add(c);
	}
	
	
	public Object[] getServiceComponents(String service) {
		List l = this.getServiceComponentsByServiceField(service);
		return l.toArray();
	}
	
	protected List getServiceComponentsByServiceField(String service){
		List list = new ArrayList();
		Iterator i = this.components.iterator();
		try {
			Class serviceClass = this.getClass().getClassLoader().loadClass(service);
			while(i.hasNext()){
				Object obj = i.next();
				if(serviceClass.isInstance(obj)){
					list.add(obj);
				}

			}
		} catch (ClassNotFoundException e) {
						e.printStackTrace();
		}
		return list;
	}

	
	public void setupComponents(){
		List list = new ArrayList();
		Iterator i = this.components.iterator();
		while(i.hasNext()){
			Object obj = i.next();

			if(obj instanceof Serviceable){
				((Serviceable)obj).setServiceManager(this);
			}
			
		}
		
		
	}


	public void start() {
		this.setupComponents();		
		Iterator i = this.components.iterator();
		while(i.hasNext()){
			Object obj = i.next();
			if(obj instanceof Startable){
				((Startable)obj).start();
			}
		}
		
	}


	public void stop() {
		Iterator i = this.components.iterator();
		while(i.hasNext()){
			Object obj = i.next();
			if(obj instanceof Startable){
				((Startable)obj).stop();
			}
		}
		
	}


	public void setProcessingManager(ProcessingManager manager) {
		this.manager=manager;
		Iterator i = this.components.iterator();
		while(i.hasNext()){
			Object obj = i.next();
			if(obj instanceof ProcessingUIComponent){
				((ProcessingUIComponent)obj).setProcessingManager(manager);
			}
		}
		
	}
	
	
}
