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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.kabeja.DraftDocument;
import org.kabeja.common.Block;
import org.kabeja.common.DraftEntity;
import org.kabeja.common.Layer;
import org.kabeja.common.Type;
import org.kabeja.entities.Entity;

public class LayerFilter extends AbstractPostProcessor {
	public final static String PROPERTY_REMOVE_LAYERS = "layers.remove";
	public final static String PROPERTY_MERGE_LAYERS = "layers.merge";
	public final static String PROPERTY_REMOVE_EMPTY_LAYERS = "remove.empty.layers";
	public final static String MERGED_LAYER_NAME = "ALL";
	protected boolean merge = false;
	protected boolean removeEmptyLayer = false;

	protected Set<String> removableLayers = new HashSet<String>();

	public void setProperties(Map properties) {
		super.setProperties(properties);

		if (properties.containsKey(PROPERTY_MERGE_LAYERS)) {
			this.merge = Boolean.valueOf(
					(String) properties.get(PROPERTY_MERGE_LAYERS))
					.booleanValue();
		}

		if (properties.containsKey(PROPERTY_REMOVE_LAYERS)) {
			this.removableLayers.clear();

			StringTokenizer st = new StringTokenizer((String) properties
					.get(PROPERTY_REMOVE_LAYERS), "|");

			while (st.hasMoreTokens()) {
				this.removableLayers.add(st.nextToken());
			}
		}
		if (properties.containsKey(PROPERTY_REMOVE_EMPTY_LAYERS)) {
			this.removeEmptyLayer = Boolean.valueOf(
					(String) properties.get(PROPERTY_REMOVE_EMPTY_LAYERS))
					.booleanValue();
		}
	}

	public void process(DraftDocument doc, Map context) throws ProcessorException {
		Layer mergeLayer = null;

		if (this.merge) {
			if (doc.containsLayer(MERGED_LAYER_NAME)) {
				mergeLayer = doc.getLayer(MERGED_LAYER_NAME);
			} else {
				mergeLayer = new Layer();
				mergeLayer.setName(MERGED_LAYER_NAME);
				doc.addLayer(mergeLayer);
			}
		}
		// check if the remove layer

		Set<String> blockLayer = new HashSet<String>();
	for(Block block :doc.getBlocks()){
		for(DraftEntity e:block.getEntities()){
				blockLayer.add(e.getLayer().getName());
			}
		}

		// iterate over all layers
		Iterator<Layer> i = doc.getLayers().iterator();
		int count = 0;
		while (i.hasNext()) {
			Layer layer = (Layer) i.next();
			count++;

			if (this.removableLayers.contains(layer.getName())) {
				i.remove();
			} else if (this.merge) {
				if (layer != mergeLayer) {
				for(Type<?> type :layer.getEntityTypes()){
						Iterator<?> entityIterator = layer.getEntitiesByType(type)
								.iterator();
						while (entityIterator.hasNext()) {
							Entity e = (Entity) entityIterator.next();
							// we set all entities to the merged layer
							// and remove them from the last layer
							e.setLayer(mergeLayer);

							// set again to the doc, which will
							// place the entity on the right
							// layer -> the LAYER = "ALL"
							doc.addEntity(e);
							entityIterator.remove();
						}
					}

					// remove the layer
					i.remove();
				}
			} else if (this.removeEmptyLayer && layer.isEmpty() && !blockLayer.contains(layer.getName())) {
				i.remove();

			}
		}
		


	}
}
