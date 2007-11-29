package org.kabeja.ui.model;

import java.util.Iterator;

import javax.swing.tree.TreeNode;

import org.kabeja.xml.SAXFilter;

public class SAXFilterTreeNode extends AbstractProcessingTreeNode {

	protected SAXFilter filter;
	protected String label;

	public SAXFilterTreeNode(TreeNode parent, SAXFilter filter,String label) {
		super(parent,label);
		
		this.filter = filter;
	

	}


	protected void initializeChildren() {
		this.propertiesToChildren(filter.getProperties());

	}

	public boolean getAllowsChildren() {

		return false;
	}

	public boolean isLeaf() {

		return false;
	}

}
