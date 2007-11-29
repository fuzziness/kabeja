package org.kabeja.ui.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.tree.TreeNode;

import org.kabeja.processing.ProcessingManager;

public abstract class AbstractProcessingTreeNode implements TreeNode {
	protected ProcessingManager manager;
	protected TreeNode parent;
	protected List children = new ArrayList();
	protected String label;

	
	public AbstractProcessingTreeNode(TreeNode parent,String label){
		this.parent = parent;
		this.label=label;
	}
	
	public void setProcessorManager(ProcessingManager manager) {
		this.manager = manager;
		this.initializeChildren();
	}

	public TreeNode getParent() {

		return parent;
	}

	protected void addChild(AbstractProcessingTreeNode child) {
		child.setProcessorManager(this.manager);
		this.children.add(child);
	}

	public int getChildCount() {

		return this.children.size();
	}

	public int getIndex(TreeNode node) {
		return this.children.indexOf(node);

	}

	public TreeNode getChildAt(int childIndex) {
		return (TreeNode) this.children.get(childIndex);
	}

	public Enumeration children() {
		return Collections.enumeration(this.children);
	}

	protected abstract void initializeChildren();

	public String toString() {
		return this.getLabel();
	}

	protected void propertiesToChildren(Map properties) {
		if(properties == null){
			System.out.println("huuuuuuuuu="+getClass());
		}
		Iterator i = properties.keySet().iterator();
		while (i.hasNext()) {
			this.addChild(new PropertyTreeNode(this, properties, (String) i
					.next()));
		}
	}
	
	protected String getLabel(){
		return this.label;
	}

}
