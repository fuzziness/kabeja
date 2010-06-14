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
Copyright 2005 Simon Mieth

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
package org.kabeja.processing;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

import org.kabeja.DraftDocument;
import org.kabeja.common.Layer;
import org.kabeja.common.Type;
import org.kabeja.entities.Image;
import org.kabeja.objects.ImageDefObject;


/**
 *
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 */
public class ImageFilter extends AbstractPostProcessor {

    public void process(DraftDocument doc, Map context) throws ProcessorException {
     for(Layer l :doc.getLayers()){

            if (l.hasEntities(Type.TYPE_IMAGE)) {
                Iterator<Image> in = l.getEntitiesByType(Type.TYPE_IMAGE).iterator();
                while (in.hasNext()) {
                    Image img = in.next();
                    long imgDef = img.getImageDefObjectID();
                    ImageDefObject def = (ImageDefObject) doc.getObjectByID(imgDef);
                    File f = new File(def.getImagePath());

                    if (!f.exists()) {
                        in.remove();
                    }
                }
            }
        }
    }
}
