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

package org.kabeja.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kabeja.DraftDocument;
import org.kabeja.entities.Entity;
import org.kabeja.entities.util.Utils;
import org.kabeja.math.Bounds;
import org.kabeja.util.Constants;


/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 *
 */
public class Layer {
	
	
  
	private Map<Type<?>,List<DraftEntity>> entities = new HashMap<Type<?>,List<DraftEntity>>();
    private String name = "";
    private String id="";
    
    private int color = 0;
    private DraftDocument doc;
    private LineType ltype;
    private int flags = 0;
    private int lineWeight = 0;
    private String plotStyle = "";
    private int zIndex=0;
    
    protected final static int BIT_PLOTTABLE=0;
    protected final static int BIT_OWN_LINETYPE=1;
    
    private int bitField;
  
    
    
 

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    public void addEntity(DraftEntity entity) {

        entity.setDocument(this.doc);
        entity.setLayer(this);
        if (entities.containsKey(entity.getType())) {
           entities.get(entity.getType()).add(entity);
        } else {
            List<DraftEntity> list = new ArrayList<DraftEntity>();
            list.add(entity);
            entities.put(entity.getType(), list);
        }
    }

    public void removeEntity(DraftEntity entity) {
        if (entities.containsKey(entity.getType())) {
             List<DraftEntity> list = entities.get(entity.getType());
             list.remove(entity);

            if (list.isEmpty()) {
                entities.remove(entity.getType());
            }
        }
    }

   /**
    * Set the Owner Document of the layer and
    * all including entities
    * @param doc
    */
    
    public void setDocument(DraftDocument doc) {
        this.doc = doc;
        //add to all entities
        for(List<DraftEntity> list:entities.values()){
        	for(DraftEntity entity:list){
        		entity.setDocument(doc);
        	}
        }
    }

    public DraftDocument getDocument() {
        return this.doc;
    }

    public Bounds getBounds() {
        Bounds bounds = new Bounds();
        
        for(List<DraftEntity> list:entities.values()){
        	for(DraftEntity entity:list){
        		 Bounds b = entity.getBounds();
                 if (b.isValid()) {
                     bounds.addToBounds(b);
                 }
        	}
        }

        return bounds;
    }

    /**
     * Get the bounds for the given filter flag. If true the bounds contains only
     * entity bounds which are on model space. Else returns the bounds which contains the entity bounds which are on
     * paperspace.
     * @param onModelspace
     * @return
     */
    public Bounds getBounds(boolean onModelspace) {
     
        Bounds bounds = new Bounds();

        Iterator<List<DraftEntity>> typeIterator = this.entities.values().iterator();

        while (typeIterator.hasNext()) {
            List<DraftEntity> list = typeIterator.next();


            Iterator<DraftEntity> i = list.iterator();

            while (i.hasNext()) {
                DraftEntity entity = i.next();

                if ((onModelspace && entity.isModelSpace()) ||
                        (!onModelspace && !entity.isModelSpace())) {
                    Bounds b = entity.getBounds();

                    if (b.isValid()) {
                        bounds.addToBounds(b);
                    }
                }
            }
        }

        return bounds;
    }

    /**
     * Returns a List of the Entities of the given Type.
     *
     * @param type
     * @return List 
     */
    public <T extends DraftEntity> List<T> getEntitiesByType(Type<T> type) {
        if (entities.containsKey(type)) {
            return (List<T>)entities.get(type);
        }

        return new ArrayList<T>(0);
    }

    public <T extends DraftEntity> boolean hasEntities(Type<T> type) {
        return entities.containsKey(type);
    }

  
    
    public Collection<Type<? extends DraftEntity>> getEntityTypes(){
    	return this.entities.keySet();
    }
    
    /**
     * Gets the @see DraftEntity with the specified ID.
     * @param id  of the @see DraftEntity
     * @return the @see DraftEntity with the specified ID or null if there is no
     */
    public DraftEntity getEntityByID(long id) {
        Entity entity = null;
        Iterator<List<DraftEntity>> i = this.entities.values().iterator();

        while (i.hasNext()) {
            Iterator<DraftEntity> entityIterator = (i.next()).iterator();

            while (entityIterator.hasNext()) {
                DraftEntity e = entityIterator.next();

                if (e.getID() == id) {
                    return e;
                }
            }
        }

        return entity;
    }

    public int getColor() {
        return this.color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setLineType(LineType ltype) {
         Utils.enableBit(this.bitField, BIT_PLOTTABLE);
        this.ltype = ltype;
    }

    public LineType getLineType() {
        if(Utils.isBitEnabled(this.bitField, BIT_PLOTTABLE)){
               return ltype;
        }else{
               return Constants.DEFAULT_LINETYPE;   
        }
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

    public boolean isVisible() {
        return color >= 0;
    }

    public boolean isFrozen() {
        return ((this.flags & 1) == 1);
    }

    public int getLineWeight() {
        return lineWeight;
    }

    public void setLineWeight(int lineWeight) {
        this.lineWeight = lineWeight;
    }

    public String getPlotStyle() {
        return plotStyle;
    }

    public void setPlotStyle(String plotStyle) {
        this.plotStyle = plotStyle;
    }
    
    
    public boolean isEmpty(){
    	return this.entities.size()==0;
    }

	/**
	 * @return the zIndex
	 */
	public int getZIndex() {
		return zIndex;
	}

	/**
	 * @param index the zIndex to set
	 */
	public void setZIndex(int index) {
		zIndex = index;
	}
	
	
	public String getID(){
		return this.id;
	}
	
	
	public void setID(String id){
		this.id =id;
	}

	/**
	 * @return the plottable
	 */
	public boolean isPlottable() {
		return Utils.isBitEnabled(this.bitField, BIT_PLOTTABLE);
	}

	/**
	 * @param plottable the plottable to set
	 */
	public void setPlottable(boolean plottable) {
	    Utils.enableBit(this.bitField,BIT_PLOTTABLE);
	}
	
	
	public boolean hasLineType(){
	    return Utils.isBitEnabled(this.bitField, BIT_OWN_LINETYPE);
	}
}
