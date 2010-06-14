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

package org.kabeja.processing;

import java.util.Iterator;
import java.util.Map;

import org.kabeja.DraftDocument;
import org.kabeja.common.DraftEntity;
import org.kabeja.common.Layer;
import org.kabeja.common.Type;
import org.kabeja.entities.Viewport;
import org.kabeja.math.Bounds;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 *
 */
public class ViewportFilter extends AbstractPostProcessor {

    public void process(DraftDocument doc, Map<String,Object> context) throws ProcessorException {
        Viewport viewport = null;
        Iterator<Viewport> i = doc.getViewports().iterator();

        boolean found = false;

        while (i.hasNext() && !found) {
            Viewport v =i.next();

            if (v.isActive()) {
                viewport = v;
                found = true;
            }
        }

        if (viewport != null) {
            double h = viewport.getHeight() / 2;
            double w = (viewport.getHeight() * viewport.getAspectRatio()) / 2;
            Bounds b = new Bounds();

            // the upper right corner
            b.addToBounds(viewport.getCenterPoint().getX() + w,
                viewport.getCenterPoint().getY() + h,
                viewport.getCenterPoint().getZ());

            // the lower left corner
            b.addToBounds(viewport.getCenterPoint().getX() - w,
                viewport.getCenterPoint().getY() - h,
                viewport.getCenterPoint().getZ());
            filterEntities(b, doc);
        }
    }

    protected void filterEntities(Bounds b, DraftDocument doc) {
      for( Layer l:doc.getLayers()){
         for(Type<?> type:l.getEntityTypes()){
                Iterator<? extends DraftEntity> ei = l.getEntitiesByType(type).iterator();
                while (ei.hasNext()) {
                    DraftEntity entity = (DraftEntity) ei.next();
                    Bounds currentBounds = entity.getBounds();

                    if (!b.contains(currentBounds)) {
                        ei.remove();
                    }
                }
            }
        }
    }


}
