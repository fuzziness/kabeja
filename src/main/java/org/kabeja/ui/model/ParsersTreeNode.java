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

import org.kabeja.parser.Parser;


public class ParsersTreeNode extends AbstractProcessingTreeNode {
    public static String LABEL = "Parsers";

    public ParsersTreeNode(TreeNode parent) {
        super(parent, LABEL);
    }

    public boolean getAllowsChildren() {
        return true;
    }

    public boolean isLeaf() {
        return false;
    }

    protected void initializeChildren() {
        Iterator i = this.manager.getParsers().iterator();

        while (i.hasNext()) {
            ParserTreeNode ptn = new ParserTreeNode(this, (Parser) i.next());
            this.addChild(ptn);
        }
    }
}
