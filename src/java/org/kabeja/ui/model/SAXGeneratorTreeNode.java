package org.kabeja.ui.model;

import java.util.Iterator;

import javax.swing.tree.TreeNode;

import org.kabeja.xml.SAXGenerator;

public class SAXGeneratorTreeNode extends AbstractProcessingTreeNode{

	protected SAXGenerator generator;
	protected String label;
	public  SAXGeneratorTreeNode(TreeNode parent,SAXGenerator generator,String label){
		super(parent,label);
		this.generator=generator;
		
	}

	
	protected void initializeChildren() {
		this.propertiesToChildren(this.generator.getProperties());		
	}
	
	protected String findLabel(){
		Iterator i =manager.getSAXGenerators().keySet().iterator();
		while(i.hasNext()){
			String key = (String)i.next();
			if(this.manager.getSAXGenerator(key)==this.generator){
				return key;
			}
		}
		return null;
	}

	public boolean getAllowsChildren() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isLeaf() {
		// TODO Auto-generated method stub
		return false;
	}
	

}
