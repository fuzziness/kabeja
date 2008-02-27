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

import org.kabeja.xml.SAXGenerator;


public class SAXGeneratorTreeNode extends AbstractProcessingTreeNode {
    protected SAXGenerator generator;
    protected String label;

    public SAXGeneratorTreeNode(TreeNode parent, SAXGenerator generator,
        String label) {
        super(parent, label);
        this.generator = generator;
    }

    protected void initializeChildren() {
        this.propertiesToChildren(this.generator.getProperties());
    }

    protected String findLabel() {
        Iterator i = manager.getSAXGenerators().keySet().iterator();

        while (i.hasNext()) {
            String key = (String) i.next();

            if (this.manager.getSAXGenerator(key) == this.generator) {
                return key;
            }
        }

        return null;
    }

    public boolean getAllowsChildren() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isLeaf() {
        // TODO Auto-generated method stub
        return false;
    }
}
