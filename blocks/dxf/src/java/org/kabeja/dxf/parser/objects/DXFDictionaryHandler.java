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
package org.kabeja.dxf.parser.objects;

import org.kabeja.dxf.parser.DXFValue;
import org.kabeja.entities.util.Utils;
import org.kabeja.objects.Dictionary;
import org.kabeja.objects.DraftObject;
import org.kabeja.util.Constants;


public class DXFDictionaryHandler extends AbstractDXFObjectHandler {
    public final int GROUPCODE_RECORD_NAME = 3;
    public final int GROUPCODE_RECORD_ID = 350;
    protected Dictionary dictionary;
    protected String objectName;
    protected boolean rootDictionaryParsed = false;

    public void endObject() {
    }

    public DraftObject getDXFObject() {
        return dictionary;
    }

    public String getObjectType() {
        return Constants.OBJECT_TYPE_DICTIONARY;
    }

    public void parseGroup(int groupCode, DXFValue value) {
        switch (groupCode) {
        case GROUPCODE_RECORD_NAME:
            this.objectName = value.getValue();

            break;

        case GROUPCODE_RECORD_ID:
            this.dictionary.putObjectRelation(this.objectName,Utils.parseIDString(value.getValue()));
            break;

        default:
            super.parseCommonGroupCode(groupCode, value, this.dictionary);
        }
    }

    public void startObject() {
        if (this.rootDictionaryParsed) {
            this.dictionary = new Dictionary();
        } else {
            this.dictionary = this.doc.getRootDictionary();
            this.rootDictionaryParsed = true;
        }
    }
}
