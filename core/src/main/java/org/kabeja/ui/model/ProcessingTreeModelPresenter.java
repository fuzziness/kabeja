/*
   Copyright 2008 Simon Mieth

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package org.kabeja.ui.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.kabeja.processing.ProcessingManager;


public class ProcessingTreeModelPresenter extends AbstractProcessingTreeNode
    implements TreeModel {
    protected List listeners = new ArrayList();

    public ProcessingTreeModelPresenter(ProcessingManager manager) {
        super(null, "ProcessManager");
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
        AbstractProcessingTreeNode node = (AbstractProcessingTreeNode) parent;

        return node.getChildAt(index);
    }

    public int getChildCount(Object parent) {
        AbstractProcessingTreeNode node = (AbstractProcessingTreeNode) parent;

        return node.getChildCount();
    }

    public int getIndexOfChild(Object parent, Object child) {
        AbstractProcessingTreeNode node = (AbstractProcessingTreeNode) parent;

        return node.getIndex((AbstractProcessingTreeNode) child);
    }

    public Object getRoot() {
        return this;
    }

    public boolean isLeaf(Object obj) {
        AbstractProcessingTreeNode node = (AbstractProcessingTreeNode) obj;

        return node.isLeaf();
    }

    public void removeTreeModelListener(TreeModelListener l) {
        this.listeners.remove(l);
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
        System.out.println("Changed path=" + path);
    }
}
