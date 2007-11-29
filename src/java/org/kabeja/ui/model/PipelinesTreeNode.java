package org.kabeja.ui.model;

import java.util.Iterator;

import javax.swing.tree.TreeNode;

public class PipelinesTreeNode extends AbstractProcessingTreeNode{

	public final static String LABEL="Pipelines";
	public PipelinesTreeNode(TreeNode parent){
		super(parent,LABEL);
	}

	protected void initializeChildren() {
		Iterator i = this.manager.getProcessPipelines().keySet().iterator();
		while(i.hasNext()){
			String key = (String)i.next();
			PipelineTreeNode node = new PipelineTreeNode(this,this.manager.getProcessPipeline(key),key);
			this.addChild(node);
		}
		
	}

	public boolean getAllowsChildren() {	
		return false;
	}

	public boolean isLeaf() {	
		return false;
	}

}
