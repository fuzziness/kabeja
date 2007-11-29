package org.kabeja.ui.model;

import javax.swing.tree.TreeNode;

import org.kabeja.xml.SAXSerializer;

public class SAXSerializerTreeNode extends AbstractProcessingTreeNode{

	protected SAXSerializer serializer;
	
	public SAXSerializerTreeNode(TreeNode parent,SAXSerializer serializer,String label){
		super(parent,label);	
		this.serializer=serializer;
		
	}
	


	protected void initializeChildren() {	
		
	}

	public boolean getAllowsChildren() {
		
		return true;
	}

	public boolean isLeaf() {
		
		return false;
	}

}
