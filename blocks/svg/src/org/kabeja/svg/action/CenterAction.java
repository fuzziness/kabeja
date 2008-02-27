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
package org.kabeja.svg.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import org.apache.batik.swing.JSVGCanvas;

import de.miethxml.toolkit.ui.UIUtils;


public class CenterAction extends AbstractAction implements JSVGCanvasAction {
    private JSVGCanvas canvas;

    public CenterAction() {
        super("Center",
            new ImageIcon(UIUtils.resourceToBytes(CenterAction.class,
                    "/icons/zoom6.png")));
        putValue(SHORT_DESCRIPTION, Messages.getString("editor.action.zoom.all"));
    }

    public void actionPerformed(ActionEvent e) {
        this.canvas.resetRenderingTransform();
    }

    public void setJSVGCanvas(JSVGCanvas canvas) {
        this.canvas = canvas;
    }
}
