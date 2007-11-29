package org.kabeja.ui.model;

import java.util.Iterator;
import java.util.Map;

import javax.swing.tree.TreeNode;

public class PropertiesTreeNode extends AbstractProcessingTreeNode {

	protected Map properties;
	protected final static String LABEL = "Properties";

	public PropertiesTreeNode(TreeNode parent, Map properties) {
		super(parent,LABEL);
		this.properties = properties;
	}


	protected void initializeChildren() {
		Iterator i = this.properties.keySet().iterator();
		while (i.hasNext()) {
			this.addChild(new PropertyTreeNode(this, properties, (String) i
					.next()));
		}

	}

	public boolean getAllowsChildren() {
		return false;
	}

	public boolean isLeaf() {
		return false;
	}

}
