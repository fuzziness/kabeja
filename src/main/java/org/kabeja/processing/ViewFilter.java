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
import org.kabeja.common.View;
import org.kabeja.math.Bounds;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 *
 */
public class ViewFilter extends AbstractPostProcessor {
    public static final String CONTEXT_OPTION_VIEW_NAME = "view.name";


    public void process(DraftDocument doc, Map context) throws ProcessorException {
        // get the active viewport
        View view = null;
        Iterator<View> i = doc.getViews().iterator();

        if (context.containsKey(CONTEXT_OPTION_VIEW_NAME)) {
            String name = (String) context.get(CONTEXT_OPTION_VIEW_NAME);

            boolean found = false;
          
            while (i.hasNext() && !found) {
                View v = i.next();

                if (v.getName().trim().equals(name.trim())) {
                    view = v;
                    found = true;
                }
            }
        } else if (i.hasNext()) {
            // get the first view
            view = (View) i.next();
        }

        if (view != null) {
            double w = view.getWidth() / 2;
            double h = view.getHeight() / 2;
            Bounds b = new Bounds();

            // the upper right corner
            b.addToBounds(view.getCenterPoint().getX() + w,
                view.getCenterPoint().getY() + h, view.getCenterPoint().getZ());

            // the lower left corner
            b.addToBounds(view.getCenterPoint().getX() - w,
                view.getCenterPoint().getY() - h, view.getCenterPoint().getZ());
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

    /* (non-Javadoc)
         * @see org.kabeja.tools.PostProcessor#setProperties(java.util.Map)
         */
    public void setProperties(Map properties) {
        // TODO Auto-generated method stub
    }
}
