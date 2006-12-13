package org.kabeja.processing;

import java.util.Iterator;
import java.util.Map;

import org.kabeja.dxf.Bounds;
import org.kabeja.dxf.DXFDocument;
import org.kabeja.dxf.DXFEntity;
import org.kabeja.dxf.DXFLayer;

public class BoundsFilter extends AbstractPostProcessor {

	public final static String PROPERTY_X = "boundsfilter.x";

	public final static String PROPERTY_Y = "boundsfilter.y";

	public final static String PROPERTY_WIDTH = "boundsfilter.width";

	public final static String PROPERTY_HEIGHT = "boundsfilter.height";

	public final static String PROPERTY_PROCESS = "boundsfilter.process";

	public void process(DXFDocument doc, Map context) {
		if (this.properties.containsKey(PROPERTY_PROCESS)
				&& Boolean.valueOf((String) this.properties
						.get(PROPERTY_PROCESS)).booleanValue()) {

			Bounds bounds = new Bounds();
			if (this.properties.containsKey(PROPERTY_X)) {
				bounds.setMinimumX(Double.parseDouble((String) this.properties
						.get(PROPERTY_X)));

			}
			if (this.properties.containsKey(PROPERTY_Y)) {
				bounds.setMinimumY(Double.parseDouble((String) this.properties
						.get(PROPERTY_Y)));

			}
			if (this.properties.containsKey(PROPERTY_WIDTH)) {
				bounds.setMaximumX(bounds.getMinimumX()
						+ Double.parseDouble((String) this.properties
								.get(PROPERTY_WIDTH)));

			}
			if (this.properties.containsKey(PROPERTY_WIDTH)) {
				bounds.setMaximumY(bounds.getMinimumY()
						+ Double.parseDouble((String) this.properties
								.get(PROPERTY_HEIGHT)));

			}

			// the bounds should be setup now
			// we remove all entities which are
			// not inside our bounds
			Iterator i = doc.getDXFLayerIterator();
			while (i.hasNext()) {
				DXFLayer layer = (DXFLayer) i.next();
				filterLayer(layer, bounds);
			}

		}
	}

	protected void filterLayer(DXFLayer layer, Bounds bounds) {
		Iterator i = layer.getDXFEntityTypeIterator();
		while (i.hasNext()) {
			String type = (String) i.next();
			Iterator entities = layer.getDXFEntities(type).iterator();
			while (entities.hasNext()) {
				DXFEntity entity = (DXFEntity) entities.next();
				if (!bounds.enclose(entity.getBounds())) {
					// the bounds not contains this entity
					// we remove it
					entities.remove();
				}
			}
		}

	}

}
