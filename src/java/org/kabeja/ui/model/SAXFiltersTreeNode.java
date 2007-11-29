package org.kabeja.ui.model;

import java.util.Iterator;

import javax.swing.tree.TreeNode;

import org.kabeja.xml.SAXFilter;

public class SAXFiltersTreeNode extends AbstractProcessingTreeNode{

	protected final static String LABEL="SAXFilters";
	
	public SAXFiltersTreeNode(TreeNode parent){
		super(parent,LABEL);
	}
	
	protected void initializeChildren() {
		Iterator i = this.manager.getSAXFilters().keySet().iterator();
		while(i.hasNext()){
			String key =(String)i.next();
			this.addChild(new SAXFilterTreeNode(this,this.manager.getSAXFilter(key),key));
		}
		
	}

	public boolean getAllowsChildren() {	
		return true;
	}

	public boolean isLeaf(){
		return false;
	}

}
