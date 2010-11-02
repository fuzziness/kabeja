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

import javax.swing.tree.TreeNode;

import org.kabeja.xml.SAXFilter;


public class SAXFilterTreeNode extends AbstractProcessingTreeNode {
    protected SAXFilter filter;
    protected String label;

    public SAXFilterTreeNode(TreeNode parent, SAXFilter filter, String label) {
        super(parent, label);

        this.filter = filter;
    }

    protected void initializeChildren() {
        this.propertiesToChildren(filter.getProperties());
    }

    public boolean getAllowsChildren() {
        return false;
    }

    public boolean isLeaf() {
        return false;
    }
}
