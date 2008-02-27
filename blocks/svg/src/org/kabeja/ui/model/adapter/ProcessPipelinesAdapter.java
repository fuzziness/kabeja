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
package org.kabeja.ui.model.adapter;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.tree.TreeNode;


public class ProcessPipelinesAdapter implements TreeNode {
    private Map pipelines;
    private TreeNode[] nodes;

    public ProcessPipelinesAdapter(Map pipelines) {
        this.pipelines = pipelines;
    }

    protected void buildNodeList() {
        nodes = new TreeNode[this.pipelines.size()];

        List list = new ArrayList(this.pipelines.keySet());
        Iterator i = list.iterator();
        int count = 0;

        while (i.hasNext()) {
            String name = (String) i.next();
            nodes[count] = new DefaultLeafAdapter(name, this);
        }
    }

    public int getChildCount() {
        return this.pipelines.size();
    }

    public Enumeration children() {
        return null;
    }

    public boolean getAllowsChildren() {
        return true;
    }

    public TreeNode getChildAt(int childIndex) {
        // TODO Auto-generated method stub
        return nodes[childIndex];
    }

    public int getIndex(TreeNode node) {
        return 0;
    }

    public TreeNode getParent() {
        return null;
    }

    public boolean isLeaf() {
        // TODO Auto-generated method stub
        return false;
    }
}
