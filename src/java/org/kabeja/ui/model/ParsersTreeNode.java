package org.kabeja.ui.model;

import java.util.Enumeration;
import java.util.Iterator;

import javax.swing.tree.TreeNode;

import org.kabeja.parser.Parser;

public class ParsersTreeNode extends AbstractProcessingTreeNode{

	public static String LABEL="Parsers";
	
	public ParsersTreeNode(TreeNode parent){
		super(parent,LABEL);
	}


	public boolean getAllowsChildren() {	
		return true;
	}



	public boolean isLeaf() {	
		return false;
	}


	protected void initializeChildren() {
		Iterator i = this.manager.getParsers().iterator();
		while(i.hasNext()){
			ParserTreeNode ptn = new ParserTreeNode(this,(Parser)i.next());
			this.addChild(ptn);
		}
	}

	
	
}
