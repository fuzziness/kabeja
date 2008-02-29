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
package org.kabeja.ui;

import javax.swing.JComponent;

import org.kabeja.dxf.DXFDocument;

/**
 * A DXFDocumentViewComponent provides a view of the current DXFDocument.
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 *
 */
public interface DXFDocumentViewComponent extends Component {
    public static final String SERVICE = DXFDocumentViewComponent.class.getName();

    /**
     *
     * @return the title of the component
     */
    abstract String getTitle();

    /**
     *
     * @return the view of this component
     */
    abstract JComponent getView();

    /**
     * Show the DXFDocument in the view of this component
     * @param doc
     * @throws UIException
     */
    abstract void showDXFDocument(DXFDocument doc) throws UIException;
}
