package org.kabeja.ui.model;

import java.util.Iterator;

import javax.swing.tree.TreeNode;

import org.kabeja.xml.SAXGenerator;

public class SAXGeneratorsTreeNode extends AbstractProcessingTreeNode{

	protected final static String LABEL="SAXSerializers";
	
	public SAXGeneratorsTreeNode(TreeNode parent){
		super(parent,LABEL);
	}
	


	protected void initializeChildren() {
	   Iterator i = this.manager.getSAXGenerators().keySet().iterator();
	   while(i.hasNext()){
		   String key = (String)i.next();
		   SAXGeneratorTreeNode node = new SAXGeneratorTreeNode(this,this.manager.getSAXGenerator(key),key);
	       this.addChild(node);
	   }
		
	}

	public boolean getAllowsChildren() {
		return true;
	}

	public boolean isLeaf() {
		return false;
	}

}
