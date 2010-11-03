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

import org.kabeja.util.Constants;


/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 */
public class ImageDefObject extends DraftObject {
  
	protected String imageURI;

 
    public String getObjectType() {
        return Constants.OBJECT_TYPE_IMAGEDEF;
    }

    /**
     * Get the local path to the representing image.
     * @return Returns the filename.
     */
    public String getImagePath() {
        return imageURI;
    }

    /**
     * Set the local path to the representing image.
     * @param filename The filename to set.
     */
    public void setImagePath(String filename) {
        this.imageURI = filename;
    }
}
