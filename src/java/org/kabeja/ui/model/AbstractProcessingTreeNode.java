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

    public AbstractProcessingTreeNode(TreeNode parent, String label) {
        this.parent = parent;
        this.label = label;
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
        if (properties == null) {
            System.out.println("huuuuuuuuu=" + getClass());
        }

        Iterator i = properties.keySet().iterator();

        while (i.hasNext()) {
            this.addChild(new PropertyTreeNode(this, properties,
                    (String) i.next()));
        }
    }

    protected String getLabel() {
        return this.label;
    }
}
