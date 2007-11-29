package org.kabeja.ui.model;

import java.util.Enumeration;

import javax.swing.tree.TreeNode;

import org.kabeja.parser.Parser;

public class ParserTreeNode extends AbstractProcessingTreeNode{

	
	protected Parser parser;
	
	public ParserTreeNode(TreeNode parent,Parser parser){
		super(parent,parser.getName());
	
		this.parser=parser;
	}

	protected String getLabel() {
		// TODO Auto-generated method stub
		return parser.getName();
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
