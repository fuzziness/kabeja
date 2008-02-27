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
package org.kabeja.ui.impl;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.TransferHandler;


public class DXFFileTransferHandler extends TransferHandler {
    private ProcessingRunViewComponent c;

    public DXFFileTransferHandler(ProcessingRunViewComponent c) {
        this.c = c;
    }

    public boolean canImport(JComponent c, DataFlavor[] flavors) {
        for (int i = 0; i < flavors.length; i++) {
            if (flavors[i].isFlavorJavaFileListType()) {
                return true;
            }
        }

        // for linux, there is the FileListFlavor not supported
        for (int i = 0; i < flavors.length; i++) {
            if (flavors[i].isFlavorTextType()) {
                return true;
            }
        }

        return false;
    }

    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY;
    }

    private List getFileList(Transferable t) {
        ArrayList files = new ArrayList();

        if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            try {
                List list = (List) t.getTransferData(DataFlavor.javaFileListFlavor);

                if (list.size() > 0) {
                    Iterator i = list.iterator();

                    while (i.hasNext()) {
                        File file = (File) i.next();
                        files.add(file);
                    }
                }
            } catch (UnsupportedFlavorException ufe) {
                ufe.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        } else if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            // this is for linux and will only works with Nautilus/Gnome
            // filemanager
            // KDE/Konqueror will not work
            try {
                String text = (String) t.getTransferData(DataFlavor.stringFlavor);
                String[] list = text.split("\n");

                if (list.length > 0) {
                    for (int i = 0; i < list.length; i++) {
                        File f = new File(list[i]);

                        if (f.exists()) {
                            files.add(f);
                        } else if (list[i].startsWith("file:")) {
                            try {
                                f = new File(new URI(list[i].trim()));
                                files.add(f);
                            } catch (URISyntaxException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                }
            } catch (UnsupportedFlavorException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return files;
    }

    public boolean importData(JComponent c, Transferable t) {
        List files = getFileList(t);

        if (files.size() > 0) {
            //we take the first one
            File f = (File) files.get(0);

            if (f.getAbsolutePath().toLowerCase().endsWith(".dxf")) {
                this.c.processInput(f);
            }

            return true;
        }

        return false;
    }
}
