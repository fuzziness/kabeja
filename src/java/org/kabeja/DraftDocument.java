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

package org.kabeja;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.kabeja.common.Block;
import org.kabeja.common.DraftEntity;
import org.kabeja.common.Header;
import org.kabeja.common.Layer;
import org.kabeja.common.LineType;
import org.kabeja.common.Style;
import org.kabeja.common.View;
import org.kabeja.entities.Entity;
import org.kabeja.entities.Viewport;
import org.kabeja.entities.util.DimensionStyle;
import org.kabeja.entities.util.HatchPattern;
import org.kabeja.entities.util.Utils;
import org.kabeja.math.Bounds;
import org.kabeja.objects.Dictionary;
import org.kabeja.objects.DraftObject;
import org.kabeja.util.Constants;

/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 * 
 * 
 */
public class DraftDocument {

	public static final String PROPERTY_ENCODING = "encoding";

	public static final double DEFAULT_MARGIN = 5;

	private Map<String, Layer> layers = new TreeMap<String, Layer>();

	private Map<String, Block> blocks = new TreeMap<String, Block>();

	private Map<String, LineType> lineTypes = new TreeMap<String, LineType>();

	private Map<String, DimensionStyle> dimensionStyles = new HashMap<String, DimensionStyle>();

	private Map<String, Style> textStyles = new HashMap<String, Style>();

	// the user coordinate systems
	private Hashtable ucs = new Hashtable();

	private Map<String,String> properties = new HashMap<String,String>();

	private List<Viewport> viewports = new ArrayList<Viewport>();

	private Bounds bounds = new Bounds();

	private Header header = new Header();

	private HashMap<String, Map<Long, DraftObject>> objects = new HashMap<String, Map<Long, DraftObject>>();

	private HashMap<String, HatchPattern> patterns = new HashMap<String, HatchPattern>();

	private List<View> views = new ArrayList<View>();

	private Dictionary rootDictionary = new Dictionary();

	private Layer defaultLayer = new Layer();

	public DraftDocument() {
		// the defalut layer

		defaultLayer.setDocument(this);
		defaultLayer.setName(Constants.DEFAULT_LAYER);
		defaultLayer.setID(Utils.generateNewID(this));
		this.layers.put(Constants.DEFAULT_LAYER, defaultLayer);

		// setup the root Dictionary
		this.rootDictionary = new Dictionary();
		this.rootDictionary.setDocument(this);
	}

	public void addLayer(Layer layer) {
		layer.setDocument(this);
		this.layers.put(layer.getName(), layer);
		layer.setZIndex(this.layers.size());

	}

	/**
	 * 
	 * Returns the specified layer.
	 * 
	 * @param key
	 *            The layer id
	 * @return the layer or if not found the default layer (layer "0")
	 */
	public Layer getLayer(String key) {
		if (this.layers.containsKey(key)) {
			return (Layer) layers.get(key);
		}

		// retrun the default layer
		if (this.layers.containsKey(Constants.DEFAULT_LAYER)) {
			return (Layer) layers.get(Constants.DEFAULT_LAYER);
		} else {
			Layer layer = new Layer();
			layer.setName(Constants.DEFAULT_LAYER);
			this.addLayer(layer);
			return layer;
		}
	}

	/**
	 * Returns true if the document contains the specified layer.
	 * 
	 * @param layerName
	 *            the layer name
	 * @return true - if the document contains the layer, otherwise false
	 */
	public boolean containsLayer(String layerName) {
		return this.layers.containsKey(layerName);
	}

	public Collection<Layer> getLayers() {
		return layers.values();
	}

	/**
	 * Adds a line type to this document
	 * 
	 * @param ltype
	 *            - the line type to add
	 */

	public void addLineType(LineType ltype) {
		this.lineTypes.put(ltype.getName(), ltype);
	}

	/**
	 * Gets the line type by name
	 * 
	 * @param name of the {@see org.kabeja.common.LineType}
	 * @return
	 */
	public LineType getLineType(String name) {
		return this.lineTypes.get(name);
	}

	/**
	 * Get the {@see java.util.Collection} of all containing {@see org.kabeja.common.LineType}
	 * @return the {@see java.util.Collection} of {@see org.kabeja.common.LineType}
	 */
	
	public Collection<LineType> getLineTypes() {
		return this.lineTypes.values();
	}

	
	public boolean containsLineType(String name){
		return this.lineTypes.containsKey(name);
	}
	
	/**
	 * Adds the given to the DraftDocument. It will placed on the layer of the
	 * entity if this one exists otherwise the entity will be added to the
	 * RootLayer "0".
	 * 
	 * @param entity {@see org.kabeja.common.DraftEntity}
	 */

	public void addEntity(DraftEntity entity) {
		Layer layer = entity.getLayer();
		if (layer == null) {
			layer = this.getRootLayer();
		}
		layer.addEntity(entity);
		layer.setDocument(this);
	}

	/**
	 * Add a block to the DraftDocument, all entities of the block get this
	 * DraftDocument as OwnerDocument
	 * 
	 * @param block
	 */

	public void addBlock(Block block) {
		block.setDocument(this);
		this.blocks.put(block.getName(), block);
	}

	/**
	 * Get a block by the given name.
	 * @param name
	 * @return The Block with the given by {@link org.kabeja.common.Block#getName() or null if there is no such block
	 */
	
	public Block getBlock(String name) {
		return (Block) blocks.get(name);
	}

	/**
	 * Get the collection of all Blocks of the DraftDocument 
	 * @return the collection of all Blocks
	 */
	public Collection<Block> getBlocks() {
		return blocks.values();
	}

	
	/**
	 * set a property of the DraftDocument
	 * @param property key 
	 * @param property value 
	 */
	
	
	public void setProperty(String key, String value) {
		this.properties.put(key, value);
	}

	/**
	 * Get a property of the given name
	 * @param key
	 * @return the property value or null if there is no such property
	 */
	
	public String getProperty(String key) {
		if (properties.containsKey(key)) {
			return (String) properties.get(key);
		}

		return null;
	}

	public boolean hasProperty(String key) {
		return this.properties.containsKey(key);
	}

	/**
	 * Returns the {@see org.kabeja.math.Bounds} of this document
	 * 
	 * @return
	 */
	public Bounds getBounds() {
		this.bounds = new Bounds();
		
		for (Layer layer : this.layers.values()) {
			if (!layer.isFrozen()) {
				Bounds b = layer.getBounds();

				if (b.isValid()) {
					this.bounds.addToBounds(b);
				}
			}
		}
		return bounds;
	}

	/**
	 * Returns the bounds of this document by
	 * distinguished model space or paper space
	 * 
	 * @return
	 */
	public Bounds getBounds(boolean onModelspace) {
		Bounds bounds = new Bounds();

		for (Layer layer : this.layers.values()) {
			if (!layer.isFrozen()) {
				Bounds b = layer.getBounds(onModelspace);

				if (b.isValid()) {
					bounds.addToBounds(b);
				}
			}
		}

		return bounds;
	}


	public Header getHeader() {
		return this.header;
	}

	public void setHeader(Header header) {
		this.header = header;
	}

	public void addDimensionStyle(DimensionStyle style) {
		this.dimensionStyles.put(style.getName(), style);
	}

	public DimensionStyle getDimensionStyle(String name) {
		return (DimensionStyle) this.dimensionStyles.get(name);
	}

	public Collection<DimensionStyle> getDimensionStyles() {
		return this.dimensionStyles.values();
	}

	public void addStyle(Style style) {
		this.textStyles.put(style.getName(), style);
	}

	public Style getStyle(String name) {
		return this.textStyles.get(name);
	}

	public Collection<Style> getStyles(){
		return this.textStyles.values();
	}

	public void removeLayer(String id) {
		this.layers.remove(id);
	}

	public void addViewport(Viewport viewport) {
		this.viewports.add(viewport);
	}

	public Collection<Viewport> getViewports() {
		return this.viewports;
	}

	public void removeViewport(Viewport viewport) {
		this.viewports.remove(viewport);
	}

	public void removeViewport(int index) {
		this.viewports.remove(index);
	}

	public void addView(View view) {
		this.views.add(view);
	}

	public Collection<View> getViews() {
		return this.views;
	}

	public void addObject(DraftObject obj) {
		// look if the object goes in a dictionary
		Dictionary d = this.rootDictionary.getDictionaryForID(obj.getID());

		if (d != null) {
			d.putObject(obj);
		} else {
			// is not bound to a dictionary
			Map<Long, DraftObject> type = null;

			if (this.objects.containsKey(obj.getObjectType())) {
				type = objects.get(obj.getObjectType());
			} else {
				type = new HashMap<Long, DraftObject>();
				this.objects.put(obj.getObjectType(), type);
			}

			type.put(obj.getID(), obj);
		}
	}

	/**
	 * Returns the root dictionary.
	 * 
	 * @return the root Dictionray
	 */
	public Dictionary getRootDictionary() {
		return this.rootDictionary;
	}

	public Layer getRootLayer() {
		return this.defaultLayer;
	}

	public void setRootDictionary(Dictionary root) {
		this.rootDictionary = root;
	}

	public List<Object> getObjectsByType(String type) {
		HashMap objecttypes = (HashMap) this.objects.get(type);
		List list = new ArrayList(objecttypes.values());

		return list;
	}

	/**
	 * 
	 * @param id
	 *            , the ID of the object
	 * @return the object
	 */
	public DraftObject getObjectByID(long id) {
		for(Map<Long,DraftObject> map:this.objects.values()){
			DraftObject obj;
			if ((obj = map.get(id)) != null) {
				return  obj;
			}
		}

		// Nothing found --> search in the dictionaries
		return this.rootDictionary.getObjectByID(id);
	}

	/**
	 * Gets the
	 * 
	 * @see Entity with the specified ID.
	 * @param id
	 *            of the
	 * @see Entity
	 * @return the
	 * @see Entity with the specified ID or null if there is no
	 * @see Entity with the specified ID
	 */
	public DraftEntity getEntityByID(long id) {
		DraftEntity entity = null;

		for (Layer layer : this.layers.values()) {

			if ((entity = layer.getEntityByID(id)) != null) {
				return entity;
			}
		}

		for (Block block : this.blocks.values()) {
			if ((entity = block.getEntityByID(id)) != null) {
				return entity;
			}
		}

		return entity;
	}

	/**
	 * Adds a HatchPattern to the document.
	 * 
	 * @param pattern
	 */
	public void addHatchPattern(HatchPattern pattern) {
		this.patterns.put(pattern.getID(), pattern);
	}

	/**
	 * Get all HatchPattern of the document
	 * @return  {@see java.util.Collection} of all HatchPattern of this document
	 */
	public Collection<HatchPattern> getHatchPatterns() {
		return this.patterns.values();
	}

	/**
	 * 
	 * @param ID
	 *            of the pattern (also called pattern name)
	 * @return the HatchPattern or null
	 */
	public HatchPattern getHatchPattern(String id) {
		return (HatchPattern) this.patterns.get(id);
	}
}
