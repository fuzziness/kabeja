package org.kabeja.ui.model;

import java.util.ArrayList;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.kabeja.processing.ProcessorManager;
public class ProcessingPresenterTreeModel implements TreeModel{

	private ProcessorManager manager;
	private ArrayList listeners = new ArrayList();
	
	
	
	ProcessingPresenterTreeModel(ProcessorManager manager){
		this.manager = manager;
	}

	public void addTreeModelListener(TreeModelListener l) {
		this.listeners.add(l);
		
	}

	public Object getChild(Object parent, int index) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getChildCount(Object parent) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getIndexOfChild(Object parent, Object child) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Object getRoot() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isLeaf(Object node) {
		
		return false;
	}

	public void removeTreeModelListener(TreeModelListener l) {
		this.listeners.remove(l);
		
	}

	public void valueForPathChanged(TreePath path, Object newValue) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
}
