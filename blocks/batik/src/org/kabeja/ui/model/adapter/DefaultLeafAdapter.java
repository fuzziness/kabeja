package org.kabeja.ui.model.adapter;

import java.util.Enumeration;

import javax.swing.tree.TreeNode;

import org.kabeja.processing.ProcessPipeline;

public class DefaultLeafAdapter implements TreeNode{

	
	protected String name;
	protected TreeNode parent;
	
	
	
	public DefaultLeafAdapter(String name,TreeNode parent){
		this.name = name;
		this.parent=parent;
	}
	
	
	
	
	public Enumeration children() {
		
		return null;
	}

	public boolean getAllowsChildren() {
		
		return false;
	}

	public TreeNode getChildAt(int childIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getChildCount() {
		
		return 0;
	}

	public int getIndex(TreeNode node) {
	
		return 0;
	}

	public TreeNode getParent() {
		
		return this.parent;
	}

	public boolean isLeaf() {
		
		return true;
	}

	public String toString(){
		return this.name;
	}
	
}
