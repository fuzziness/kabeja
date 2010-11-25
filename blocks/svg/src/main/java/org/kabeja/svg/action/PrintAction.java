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
import javax.swing.SwingUtilities;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.print.PrintTranscoder;
import org.w3c.dom.svg.SVGDocument;

import de.miethxml.toolkit.ui.UIUtils;


public class PrintAction extends AbstractAction implements SVGDocumentAction {
    SVGDocument doc;

    public PrintAction() {
        super();
        super.putValue(super.SMALL_ICON,
            new ImageIcon(UIUtils.resourceToBytes(this.getClass(),
                    "/icons/print.gif")));
        super.putValue(SHORT_DESCRIPTION,
            Messages.getString("editor.action.print"));
    }

    public void setDocument(SVGDocument doc) {
        this.doc = doc;
    }

    public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    print();
                }
            });
    }

    protected void print() {
        PrintTranscoder pt = new PrintTranscoder();
        pt.addTranscodingHint(PrintTranscoder.KEY_SHOW_PAGE_DIALOG, Boolean.TRUE);
        pt.addTranscodingHint(PrintTranscoder.KEY_SHOW_PRINTER_DIALOG,
            Boolean.TRUE);

        try {
            pt.transcode(new TranscoderInput(this.doc), null);
            pt.print();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
