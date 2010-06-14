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
/*
   Copyright 2007 Simon Mieth

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
package org.kabeja.objects;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.kabeja.util.Constants;


public class Dictionary extends DraftObject {
    protected ArrayList<DictionaryRecord> records = new ArrayList<DictionaryRecord>();

    public String getObjectType() {
        return Constants.OBJECT_TYPE_DICTIONARY;
    }

    public boolean hasObjectByID(long id) {
        return findByID(id) != null;
    }

    public String getNameForObjectID(long id) {
        return findByID(id).getName();
    }

    /**
     * Gets the
     *
     * @see DraftObject with the specified ID.
     * @param id
     * @return the AbstractObject or null if there is no such AbstractObject
     */
    public DraftObject getObjectByID(long id) {
        //search for child dictionaries
        Dictionary dic = this.getDictionaryForID(id);

        if (dic != null) {
            DictionaryRecord dicRecord = dic.findByID(id);

            if (dicRecord != null) {
                return dicRecord.getObject();
            }
        }

        return null;
    }

    public DraftObject getObjectByName(String name) {
        DictionaryRecord record = findByName(name);

        if (record != null) {
            return record.getObject();
        }

        return null;
    }

    public void putObject(DraftObject obj) {
        findByID(obj.getID()).setObject(obj);
    }

    public void putObjectRelation(String name, long id) {
        DictionaryRecord record = null;

        if ((record = findByName(name)) != null) {
            record.setID(id);
        } else {
            record = new DictionaryRecord(name, id);
            this.records.add(record);
        }
    }

    protected DictionaryRecord findByName(String name) {
        for (int i = 0; i < this.records.size(); i++) {
            DictionaryRecord record =  records.get(i);

            if (record.getName().equals(name)) {
                return record;
            }
        }

        return null;
    }

    protected DictionaryRecord findByID(long id) {
        for (int i = 0; i < this.records.size(); i++) {
            DictionaryRecord record = records.get(i);

            if (record.getID() == id) {
                return record;
            }
        }

        return null;
    }

    /**
     * Searches recursive for the dictionary which holds the ID
     *
     * @param id
     * @return the dictionary or null
     */
    public Dictionary getDictionaryForID(long id) {
        Set<Dictionary> dictionaries = new HashSet<Dictionary>();
        DraftObject obj = null;

        for (int i = 0; i < this.records.size(); i++) {
            DictionaryRecord record = (DictionaryRecord) records.get(i);

            if (record.getID() == id) {
                return this;
            } else if (((obj = record.getObject()) != null) &&
                    obj.getObjectType()
                           .equals(Constants.OBJECT_TYPE_DICTIONARY)) {
                dictionaries.add((Dictionary)obj);
            }
        }

        Iterator<Dictionary> ie = dictionaries.iterator();

        while (ie.hasNext()) {
            Dictionary dic =ie.next();
            Dictionary d = dic.getDictionaryForID(id);

            if (d != null) {
                return d;
            }
        }

        return null;
    }

    /**
     *
     * @return iterator over all AbstractObjects in this dictionary
     */
    public Iterator<DraftObject> getObjectIterator() {
        Iterator<DraftObject> i = new Iterator<DraftObject>() {
                int count = 0;

                public boolean hasNext() {
                    return count < records.size();
                }

                public DraftObject next() {
                    return ((DictionaryRecord) records.get(count++)).getObject();
                }

                public void remove() {
                    records.remove(count - 1);
                }
            };

        return i;
    }

    private class DictionaryRecord {
        private long id;
        private String name;
        private DraftObject obj;

        public DictionaryRecord(String name, long id) {
            this.id = id;
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public long getID() {
            return this.id;
        }

        public void setID(long id) {
            this.id = id;
        }

        public void setObject(DraftObject obj) {
            this.obj = obj;
        }

        public DraftObject getObject() {
            return this.obj;
        }
    }
}
