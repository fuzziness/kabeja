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
package org.kabeja.ui.event;

import org.kabeja.dxf.DXFDocument;


/**
 * Interface for Listeners which want to receive changes on
 * the DXFDocument.
 *
 *
 */
public interface DXFDocumentChangeListener {
    /**
     * Called if the DXFDocument is changed or parts are
     * changed
     * @param doc the changed or exchanged DXFDocument
     */
    public void changed(DXFDocument doc);
}
