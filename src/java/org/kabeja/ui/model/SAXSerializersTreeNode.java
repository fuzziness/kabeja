package org.kabeja.ui.model;

import java.util.Iterator;

import javax.swing.tree.TreeNode;

public class SAXSerializersTreeNode extends AbstractProcessingTreeNode{

	public final static String LABEL="SAXSerializers";
	
	public SAXSerializersTreeNode(TreeNode parent){
		super(parent,LABEL);
	}


	protected void initializeChildren() {
		   Iterator i = this.manager.getSAXSerializers().keySet().iterator();
		   while(i.hasNext()){
			   String key = (String)i.next();
			   SAXSerializerTreeNode node = new SAXSerializerTreeNode(this,this.manager.getSAXSerializer(key),key);
		       this.addChild(node);
		   }
		
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
