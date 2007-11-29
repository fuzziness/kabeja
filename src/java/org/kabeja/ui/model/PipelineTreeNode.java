package org.kabeja.ui.model;

import javax.swing.tree.TreeNode;

import org.kabeja.processing.ProcessPipeline;

public class PipelineTreeNode extends AbstractProcessingTreeNode{

	protected ProcessPipeline pipeline;
	
	public PipelineTreeNode(TreeNode parent,ProcessPipeline pipeline,String label){
		super(parent,label);
		this.pipeline = pipeline;
	}
	


	protected void initializeChildren() {
	       
	}	

	public boolean getAllowsChildren() {
		
		return false;
	}

	public boolean isLeaf() {
		
		return false;
	}

}
