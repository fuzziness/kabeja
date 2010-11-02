/*******************************************************************************
 * Copyright 2010 Simon Mieth
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package org.kabeja.objects;

import org.kabeja.DraftDocument;


/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 */
public abstract class DraftObject {
    protected DraftDocument doc;
    protected long softID;
    protected long hardID;
    protected long handleID;

    public long getSoftPointerID() {
        return softID;
    }

    public void setSoftPointerID(long id) {
        this.softID = id;
    }

    public long getHardOwnerID() {
        return hardID;
    }

    public void setHardOwnerID(long id) {
        this.hardID = id;
    }

    public void setDocument(DraftDocument doc) {
        this.doc = doc;
    }

    public abstract String getObjectType();

    /**
     * @return Returns the handleID.
     */
    public long getID() {
        return handleID;
    }

    /**
     * @param handleID The handleID to set.
     */
    public void setID(long handleID) {
        this.handleID = handleID;
    }
}
