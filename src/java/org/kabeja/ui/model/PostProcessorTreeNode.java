package org.kabeja.ui.model;

import javax.swing.tree.TreeNode;

import org.kabeja.processing.PostProcessor;

public class PostProcessorTreeNode extends AbstractProcessingTreeNode {

	protected PostProcessor pp;

	public PostProcessorTreeNode(TreeNode parent, PostProcessor pp, String label) {
		super(parent, label);
		this.pp = pp;
	}

	protected void initializeChildren() {
		        this.propertiesToChildren(this.pp.getProperties());
	}

	public boolean getAllowsChildren() {
		return true;
	}

	public boolean isLeaf() {
		return this.pp.getProperties().size()>0;
	}

}
