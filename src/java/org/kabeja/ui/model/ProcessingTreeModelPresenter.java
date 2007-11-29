package org.kabeja.ui.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.kabeja.processing.ProcessingManager;
public class ProcessingTreeModelPresenter extends AbstractProcessingTreeNode implements TreeModel{

	
	protected List listeners = new ArrayList();
	
	
	public ProcessingTreeModelPresenter(ProcessingManager manager){
		super(null,"ProcessManager");
		this.setProcessorManager(manager);
	}


	public boolean isLeaf() {
		
		return false;
	}


	protected void initializeChildren() {
		AbstractProcessingTreeNode node = new ParsersTreeNode(this);
		addChild(node);
		
		node = new SAXGeneratorsTreeNode(this);
		addChild(node);
		
		node = new PostProcessorsTreeNode(this);
		addChild(node);
		
		node = new SAXFiltersTreeNode(this);
		addChild(node);
		
		node = new SAXSerializersTreeNode(this);
		addChild(node);
		node = new PipelinesTreeNode(this);
		addChild(node);
		
	}


	public boolean getAllowsChildren() {	
		return false;
	}


	public void addTreeModelListener(TreeModelListener l) {
		this.listeners.add(l);
		
	}


	public Object getChild(Object parent, int index) {
		//delegate to nodes
		AbstractProcessingTreeNode node= (AbstractProcessingTreeNode)parent;
		return node.getChildAt(index);
		
	}


	public int getChildCount(Object parent) {
		AbstractProcessingTreeNode node= (AbstractProcessingTreeNode)parent;
		return node.getChildCount();
	}


	public int getIndexOfChild(Object parent, Object child) {
		AbstractProcessingTreeNode node= (AbstractProcessingTreeNode)parent;
		return node.getIndex((AbstractProcessingTreeNode)child);
	}


	public Object getRoot() {
		
		return this;
	}


	public boolean isLeaf(Object obj) {
		AbstractProcessingTreeNode node= (AbstractProcessingTreeNode)obj;
		return node.isLeaf();
	}


	public void removeTreeModelListener(TreeModelListener l) {
		this.listeners.remove(l);
		
	}


	public void valueForPathChanged(TreePath path, Object newValue) {
		System.out.println("Changed path="+path);
		
	}


	
	
	
}
