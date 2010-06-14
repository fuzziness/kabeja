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

import java.util.Map;

import org.kabeja.DraftDocument;
import org.kabeja.common.Block;
import org.kabeja.common.DraftEntity;
import org.kabeja.common.Layer;
import org.kabeja.common.Type;
import org.kabeja.entities.Face3D;
import org.kabeja.entities.Text;
import org.kabeja.math.Bounds;

/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 * 
 */
public class BoundsDebugger extends AbstractPostProcessor {
	public static final String LAYER_NAME = "kabeja_bounds_debug";

	public void process(DraftDocument doc, Map context)
			throws ProcessorException {

		// get or create the debug layer
		Layer layer = null;
		if (doc.containsLayer(LAYER_NAME)) {
			layer = doc.getLayer(LAYER_NAME);
		} else {
			layer = new Layer();
			layer.setName(LAYER_NAME);
			doc.addLayer(layer);
		}

		// set all blocks to color gray
		for (Block b : doc.getBlocks()) {
			for (DraftEntity entity : b.getEntities()) {
				// set to gray
				entity.setColor(9);
			}
		}

		DraftEntity left = null;
		DraftEntity top = null;
		DraftEntity right = null;
		DraftEntity bottom = null;

		Bounds b = doc.getBounds();
		double x = b.getMinimumX() + (b.getWidth() / 2);
		double y = b.getMinimumY() + (b.getHeight() / 2);

		// starting at the center point of the draft
		Bounds lBounds = new Bounds(x, x, y, y);
		Bounds rBounds = new Bounds(x, x, y, y);
		Bounds tBounds = new Bounds(x, x, y, y);
		Bounds bBounds = new Bounds(x, x, y, y);

		for (Layer l : doc.getLayers()) {

			// set color to gray
			l.setColor(8);

			for (Type<? extends DraftEntity> type : l.getEntityTypes()) {
				for (DraftEntity entity : l.getEntitiesByType(type)) {
					// set to gray
					entity.setColor(8);
					Bounds currentBounds = entity.getBounds();

					if (currentBounds.isValid()) {
						if (currentBounds.getMinimumX() <= lBounds
								.getMinimumX()) {
							lBounds = currentBounds;
							left = entity;
						}

						if (currentBounds.getMinimumY() <= bBounds
								.getMinimumY()) {
							bBounds = currentBounds;
							bottom = entity;
						}

						if (currentBounds.getMaximumX() >= rBounds
								.getMaximumX()) {
							rBounds = currentBounds;
							right = entity;
						}

						if (currentBounds.getMaximumY() >= tBounds
								.getMaximumY()) {
							tBounds = currentBounds;
							top = entity;
						}
					}
				}
			}
		}

		// left -> red
		left.setColor(0);
		addBounds(lBounds, doc, 0, left.getType() + "=" + left.getID(), layer);

		// right -> green
		right.setColor(2);
		addBounds(rBounds, doc, 2, right.getType() + "=" + right.getID(), layer);

		// bottom blue
		bottom.setColor(4);
		addBounds(bBounds, doc, 4, bottom.getType() + "=" + bottom.getID(),
				layer);

		// top color -> magenta
		top.setColor(5);
		addBounds(tBounds, doc, 5, top.getType() + "=" + top.getID(), layer);

		// the color -> magenta
		top.setColor(5);
		addBounds(b, doc, 6, "ALL", layer);
	}

	protected void addBounds(Bounds bounds, DraftDocument doc, int color,
			String type, Layer layer) {
		Face3D face = new Face3D();
		face.getPoint1().setX(bounds.getMinimumX());
		face.getPoint1().setY(bounds.getMinimumY());

		face.getPoint2().setX(bounds.getMinimumX());
		face.getPoint2().setY(bounds.getMaximumY());

		face.getPoint3().setX(bounds.getMaximumX());
		face.getPoint3().setY(bounds.getMaximumY());

		face.getPoint4().setX(bounds.getMaximumX());
		face.getPoint4().setY(bounds.getMinimumY());

		face.setColor(color);
		face.setLayer(layer);

		doc.addEntity(face);

		Text t = new Text();
		t.setDocument(doc);
		t.setText("DEBUG-" + type);
		t.getInsertPoint().setX(bounds.getMinimumX());
		t.getInsertPoint().setY(bounds.getMaximumY());
		t.setColor(color);
		t.setLayer(layer);
		doc.addEntity(t);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kabeja.tools.PostProcessor#setProperties(java.util.Map)
	 */
	public void setProperties(Map properties) {
	}
}
