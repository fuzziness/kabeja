package org.kabeja.processing;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.kabeja.dxf.DXFConstants;
import org.kabeja.dxf.DXFDocument;
import org.kabeja.dxf.DXFEntity;
import org.kabeja.dxf.DXFLayer;

public class LayerFilter extends AbstractPostProcessor {

	public final static String PROPERTY_REMOVE_LAYERS = "layers.remove";

	public final static String PROPERTY_MERGER_LAYERS = "layers.merge";

	protected boolean merge = false;

	protected Set removableLayers = new HashSet();

	public void setProperties(Map properties) {
		super.setProperties(properties);
		if (properties.containsKey(PROPERTY_MERGER_LAYERS)) {
			this.merge = Boolean.parseBoolean((String) properties
					.get(PROPERTY_MERGER_LAYERS));
		}

		if (properties.containsKey(PROPERTY_REMOVE_LAYERS)) {
			this.removableLayers.clear();
			StringTokenizer st = new StringTokenizer((String) properties
					.get(PROPERTY_REMOVE_LAYERS), "|");
			while (st.hasMoreTokens()) {
				this.removableLayers.add(st.nextToken());
			}
		}

	}

	public void process(DXFDocument doc, Map context) {
		// iterate over all layers
		Iterator i = doc.getDXFLayerIterator();
		while (i.hasNext()) {
			DXFLayer layer = (DXFLayer) i.next();
			if (this.removableLayers.contains(layer.getName())) {
				i.remove();
			} else if(this.merge){

				if (!DXFConstants.DEFAULT_LAYER.equals(layer.getName())) {
					Iterator types = layer.getDXFEntityTypeIterator();
					while (types.hasNext()) {
						String type = (String) types.next();
						Iterator entityIterator = layer.getDXFEntities(type)
								.iterator();
						while (entityIterator.hasNext()) {
							DXFEntity e = (DXFEntity) entityIterator.next();
							// we set all entities to the default layer
							// and remove them from the last layer
							e.setLayerName(DXFConstants.DEFAULT_LAYER);

							// set again to the doc, which will
							// place the entity on the right
							// layer -> the DEFAULT_LAYER = "0"
							doc.addDXFEntity(e);
							entityIterator.remove();
						}

					}
					// remove the layer
					i.remove();
				}
			}
		}
	}

}
