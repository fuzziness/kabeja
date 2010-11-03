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

package org.kabeja.entities;

import org.kabeja.DraftDocument;
import org.kabeja.common.DraftEntity;
import org.kabeja.common.Layer;
import org.kabeja.common.LineType;
import org.kabeja.common.Type;
import org.kabeja.entities.util.Utils;
import org.kabeja.math.Bounds;
import org.kabeja.math.Extrusion;
import org.kabeja.math.TransformContext;
import org.kabeja.tools.LazyContainer;
import org.kabeja.util.Constants;

/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 * 
 * 
 */
public abstract class Entity implements DraftEntity {

	protected static final int LAZY_INDEX_LAYER = 0;
	protected static final int LAZY_INDEX_LINETYPE = 1;
	protected static final int LAZY_INDEX_EXTRUSION = 2;
	protected static final int LAZY_INDEX_LINETYPE_SCALE = 3;
	protected static final int LAZY_INDEX_LINEWEIGHT = 4;
	protected static final int LAZY_INDEX_TRANSPARENCY = 5;
	protected static final int LAZY_INDEX_THICKNESS = 6;
	protected static final int LAZY_INDEX_COLOR = 7;

	protected LazyContainer lazyContainer = new LazyContainer();

	protected DraftDocument doc;

	protected long id;
	protected long ownerID;

	protected int flags = 0;

	protected final static int BOOLEAN_BIT_VISIBILITY = 0;
	protected final static int BOOLEAN_BIT_BLOCKENTITY = 1;
	protected final static int BOOLEAN_BIT_MODELSPACE = 2;
	protected final static int BOOLEAN_BIT_OWN_LINETYPE = 3;
	protected int entityFlags;

	public Entity() {
		init();
	}

	/**
	 * Set the owner document of the entity
	 * 
	 * @param doc
	 */

	public void setDocument(DraftDocument doc) {
		this.doc = doc;
	}

	/**
	 * Get the owner document of the entity
	 * 
	 * @return
	 */

	public DraftDocument getDocument() {
		return this.doc;
	}

	/**
	 * Returns the parent layer of the entity
	 * 
	 * @return
	 */
	public Layer getLayer() {
		if (this.lazyContainer.contains(LAZY_INDEX_LAYER)) {
			return ((Layer) this.lazyContainer.get(LAZY_INDEX_LAYER));
		}
		return this.doc.getRootLayer();
	}

	/**
	 * Set the parent layer of the entity
	 * 
	 * @param layer
	 */
	public void setLayer(Layer layer) {
		this.lazyContainer.set(layer, LAZY_INDEX_LAYER);
	}

	public abstract Bounds getBounds();

	public LineType getLineType() {
		if (this.lazyContainer.contains(LAZY_INDEX_LINETYPE)) {
			return (LineType) this.lazyContainer.get(LAZY_INDEX_LINETYPE);
		}
		return this.getLayer().getLineType();
	}

	public void setLineType(LineType ltype) {
		this.lazyContainer.set(ltype, LAZY_INDEX_LINETYPE);
	}

	public boolean hasLineType() {
		return this.lazyContainer.contains(LAZY_INDEX_LINETYPE);
	}

	/**
	 * @return Returns the visibile.
	 */
	public boolean isVisibile() {
		return Utils.isBitEnabled(this.entityFlags, BOOLEAN_BIT_VISIBILITY);

	}

	/**
	 * @param visibile
	 *            The visibile to set.
	 */
	public void setVisibile(boolean visible) {
		this.entityFlags = Utils.setBit(this.entityFlags,
				BOOLEAN_BIT_VISIBILITY, visible);
	}

	/**
	 * @return Returns the flags.
	 */
	public int getFlags() {
		return flags;
	}

	/**
	 * @param flags
	 *            The flags to set.
	 */
	public void setFlags(int flags) {
		this.flags = flags;
	}

	public void setBlockEntity(boolean b) {
		this.entityFlags = Utils.setBit(this.entityFlags,
				BOOLEAN_BIT_BLOCKENTITY, b);
	}

	public boolean isBlockEntity() {
		return Utils.isBitEnabled(this.entityFlags, BOOLEAN_BIT_BLOCKENTITY);
	}

	public void setExtrusion(Extrusion extrusion) {
		this.lazyContainer.set(extrusion, LAZY_INDEX_LAYER);
	}

	public Extrusion getExtrusion() {

		if (this.lazyContainer.contains(LAZY_INDEX_EXTRUSION)) {
			return (Extrusion) this.lazyContainer.get(LAZY_INDEX_EXTRUSION);
		}

		return new Extrusion() {
			public void setX(double x) {
				if (x != 0.0) {
					this.addToContainer();
					super.setX(x);
				}
			}

			public void setY(double y) {
				if (y != 0.0) {
					this.addToContainer();
					super.setY(y);
				}
			}

			public void setZ(double z) {
				if (z != 1.0) {
					this.addToContainer();
					super.setZ(z);
				}
			}

			private void addToContainer() {
				if (!lazyContainer.contains(LAZY_INDEX_EXTRUSION)) {
					lazyContainer.set(this, LAZY_INDEX_EXTRUSION);
				}
			}

		};

	}

	public double getLinetypeScaleFactor() {
		if (this.lazyContainer.contains(LAZY_INDEX_LINETYPE_SCALE)) {
			return ((Double) this.lazyContainer.get(LAZY_INDEX_LINETYPE_SCALE))
					.doubleValue();
		} else {
			return 1.0;
		}

	}

	public void setLinetypeScaleFactor(double linetypeScaleFactor) {
		this.lazyContainer.set(new Double(linetypeScaleFactor),
				LAZY_INDEX_LINETYPE_SCALE);

	}

	public int getColor() {
		if (this.lazyContainer.contains(LAZY_INDEX_COLOR)) {
			return (int) ((byte[]) this.lazyContainer.get(LAZY_INDEX_COLOR))[0];
		} else {
			return Constants.COLOR_VALUE_BY_LAYER;
		}
	}

	public void setColor(int color) {
		if (this.lazyContainer.contains(LAZY_INDEX_COLOR)
				|| color != Constants.COLOR_VALUE_BY_LAYER) {
			this.lazyContainer.set(new byte[] { (byte) color },
					LAZY_INDEX_COLOR);
		}
	}

	public byte[] getColorRGB() {
		if (this.lazyContainer.contains(LAZY_INDEX_COLOR)) {
			return ((byte[]) this.lazyContainer.get(LAZY_INDEX_COLOR));
		}
		return new byte[0];
	}

	public void setColorRGB(byte[] colorRGB) {
		if (this.lazyContainer.contains(LAZY_INDEX_COLOR)
				|| colorRGB.length > 0) {
			this.lazyContainer.set(colorRGB, LAZY_INDEX_COLOR);
		}

	}

	public int getLineWeight() {
		if (this.lazyContainer.contains(LAZY_INDEX_LINEWEIGHT)) {
			return ((Integer) this.lazyContainer.get(LAZY_INDEX_LINEWEIGHT))
					.intValue();
		}
		return 0;
	}

	public void setLineWeight(int lineWeight) {
		if (this.lazyContainer.contains(LAZY_INDEX_LINETYPE) || lineWeight != 0) {
			this.lazyContainer.set(new Integer(lineWeight),
					LAZY_INDEX_LINEWEIGHT);
		}
	}

	public double getTransparency() {
		if (this.lazyContainer.contains(LAZY_INDEX_TRANSPARENCY)) {
			return ((Double) this.lazyContainer.get(LAZY_INDEX_TRANSPARENCY))
					.doubleValue();
		} else {
			return 0.0;
		}
	}

	public void setTransparency(double transparency) {
		if (this.lazyContainer.contains(LAZY_INDEX_TRANSPARENCY)
				|| transparency != 0.0) {
			this.lazyContainer.set(new Double(transparency),
					LAZY_INDEX_TRANSPARENCY);
		}

	}

	public void setID(long id) {

		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kabeja.entities.EntityType#getID()
	 */

	public long getID() {
		return this.id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kabeja.entities.EntityType#getType()
	 */

	public abstract Type<?> getType();

	/**
	 * The thickness reflects the height of the entity.
	 * 
	 * 
	 * @return Returns the thickness.
	 */
	public double getThickness() {
		if (this.lazyContainer.contains(LAZY_INDEX_THICKNESS)) {
			return ((Double) this.lazyContainer.get(LAZY_INDEX_THICKNESS))
					.doubleValue();
		} else {
			return 0.0;
		}
	}

	/**
	 * @param thickness
	 *            The thickness /height of the entity to set.
	 */
	public void setThickness(double thickness) {
		if (this.lazyContainer.contains(LAZY_INDEX_THICKNESS)
				|| thickness != 0.0) {
			this.lazyContainer.set(new Double(thickness), LAZY_INDEX_THICKNESS);
		}

	}

	public boolean isOmitLineType() {
		return false;
	}

	/**
	 * @return Returns the modelSpace.
	 */
	public boolean isModelSpace() {
		return Utils.isBitEnabled(this.entityFlags, BOOLEAN_BIT_MODELSPACE);
	}

	/**
	 * @param modelSpace
	 *            The modelSpace to set.
	 */
	public void setModelSpace(boolean modelSpace) {
		this.entityFlags = Utils.setBit(this.entityFlags,
				BOOLEAN_BIT_MODELSPACE, modelSpace);
	}

	/**
	 * Returns the length of the entity or 0 if the entity has no length
	 * 
	 * @return
	 */
	public abstract double getLength();

	public long getOwnerID() {
		return ownerID;
	}

	public void setOwnerID(long ownerID) {
		this.ownerID = ownerID;
	}

	protected void init() {

		// setup the default bis
		this.entityFlags = 0;
		// visibility to true
		this.entityFlags = Utils.enableBit(this.entityFlags, 0);
		// modelspace to true
		this.entityFlags = Utils.enableBit(this.entityFlags, 6);

	}

	protected void setBit(int bit, boolean b) {
		this.entityFlags = Utils.setBit(this.entityFlags, bit, b);
	}

	protected boolean isBitEnabled(int bit) {
		return Utils.isBitEnabled(this.entityFlags, bit);
	}

	public abstract void transform(TransformContext context);

}
