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

import java.util.Iterator;

import javax.swing.tree.TreeNode;


public class PostProcessorsTreeNode extends AbstractProcessingTreeNode {
    public final static String LABEL = "PostProcessors";

    public PostProcessorsTreeNode(TreeNode parent) {
        super(parent, LABEL);
    }

    protected void initializeChildren() {
        Iterator i = this.manager.getPostProcessors().keySet().iterator();

        while (i.hasNext()) {
            String key = (String) i.next();
            PostProcessorTreeNode node = new PostProcessorTreeNode(this,
                    this.manager.getPostProcessor(key), key);
            this.addChild(node);
        }
    }

    public boolean getAllowsChildren() {
        return true;
    }

    public boolean isLeaf() {
        return false;
    }
}
