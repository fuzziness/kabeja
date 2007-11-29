package org.kabeja.ui.model;

import java.util.Iterator;

import javax.swing.tree.TreeNode;

public class PostProcessorsTreeNode extends AbstractProcessingTreeNode{

     public final static String LABEL="PostProcessors";
     
     public PostProcessorsTreeNode(TreeNode parent){
    	 super(parent,LABEL);
     }

	protected void initializeChildren() {
		Iterator i = this.manager.getPostProcessors().keySet().iterator();
		while(i.hasNext()){
			String key = (String)i.next();
			PostProcessorTreeNode node = new PostProcessorTreeNode(this,this.manager.getPostProcessor(key),key);
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
