package org.kabeja.ui.model;

import java.util.Map;

import javax.swing.tree.TreeNode;

public class PropertyTreeNode extends AbstractProcessingTreeNode {

	protected Map properties;
	protected String propertyKey;

	public PropertyTreeNode(TreeNode parent, Map properties, String propertyKey) {
	    super(parent,propertyKey+"="+properties.get(propertyKey));
		
		this.properties = properties;
		this.propertyKey = propertyKey;
	}

	protected String getLabel() {

		return this.propertyKey+"="+this.properties.get(this.propertyKey);
	}

	protected void initializeChildren() {

	}

	public boolean getAllowsChildren() {
		return false;
	}

	public boolean isLeaf() {
		return true;
	}

}
