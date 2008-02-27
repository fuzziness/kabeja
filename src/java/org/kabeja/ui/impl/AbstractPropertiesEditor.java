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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.kabeja.ui.PropertiesEditor;
import org.kabeja.ui.PropertiesListener;


public abstract class AbstractPropertiesEditor implements PropertiesEditor {
    protected ArrayList listeners = new ArrayList();
    protected Map properties = new HashMap();

    public void addPropertiesListener(PropertiesListener listener) {
        this.listeners.add(listener);
    }

    public Map getProperties() {
        return this.properties;
    }

    public void removePropertiesListener(PropertiesListener listener) {
        this.listeners.remove(listeners);
    }

    public void setProperties(Map properties) {
        this.properties = properties;
    }

    protected void firePropertiesChangedEvent() {
        Iterator i = ((ArrayList) this.listeners.clone()).iterator();

        while (i.hasNext()) {
            PropertiesListener l = (PropertiesListener) i.next();
            l.propertiesChanged(this.properties);
        }
    }
}
