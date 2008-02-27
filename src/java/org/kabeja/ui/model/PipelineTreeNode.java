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

import org.kabeja.processing.ProcessPipeline;


public class PipelineTreeNode extends AbstractProcessingTreeNode {
    protected ProcessPipeline pipeline;

    public PipelineTreeNode(TreeNode parent, ProcessPipeline pipeline,
        String label) {
        super(parent, label);
        this.pipeline = pipeline;
    }

    protected void initializeChildren() {
    }

    public boolean getAllowsChildren() {
        return false;
    }

    public boolean isLeaf() {
        return false;
    }
}
