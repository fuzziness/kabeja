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
import java.util.List;

import org.kabeja.DraftDocument;
import org.kabeja.entities.Entity;
import org.kabeja.math.Bounds;
import org.kabeja.math.Point3D;

/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 * 
 */
public class Block {
	
  

    private Point3D referencePoint = new Point3D();

    private Layer layer;

    private String name = "";

    private String description = "";

    private List<DraftEntity> entities = new ArrayList<DraftEntity>();

    private long id;

    private DraftDocument doc;

    private int flags = 0;

    /**
     *
     */
    public Block() {
        super();

    }

    public Bounds getBounds() {
    
        Bounds bounds = new Bounds();
        if (this.entities.size()>0) {
          for(DraftEntity entity:this.entities){
                Bounds b = entity.getBounds();
                if (b.isValid()) {
                    bounds.addToBounds(b);
                }
            }
        } else {
            bounds.setValid(false);
        }

        return bounds;
    }

    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     *            The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return Returns the p.
     */
    public Point3D getReferencePoint() {
        return referencePoint;
    }

    /**
     * @param p
     *            The p to set.
     */
    public void setReferencePoint(Point3D p) {
        this.referencePoint = p;
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
        entity.setBlockEntity(true);
    }

    /**
     * Get the list of the containing entities 
     * @return @see java.util.List of the containing entities
     */
    public List<DraftEntity> getEntities() {
        return entities;
    }



    /**
     * @return Returns the unique name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the unique name of the Block
     * @param name
     *            The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param doc
     *            The doc to set.
     */
    public void setDocument(DraftDocument doc) {
        this.doc = doc;
        
         for(DraftEntity entity:entities) {
            entity.setDocument(doc);
        }
    }

    /**
     * 
     * @return the parent document
     */
    public DraftDocument getDocument() {
        return this.doc;
    }

    /**
     * Returns the length of all containing @see DraftEntity
     * @return
     */
    
    
    public double getLength() {
      
       double length = 0;
       for(DraftEntity entity:entities){
            length += entity.getLength();
        }

        return length;
    }

    /**
     * Gets the
     * 
     * @see DraftEntity with the specified ID.
     * @param id
     *            of the
     * @see DraftEntity
     * @return the
     * @see DraftEntity with the specified ID or null if there is no
     * @see DraftEntity with the specified ID
     */
    public DraftEntity getEntityByID(long id) {
        DraftEntity entity = null;
       
        for(DraftEntity e:entities) {
            if (e.getID() == id) {
                return e;
            }
        }

        return entity;
    }

    
    public long getID() {
        return this.id;
    }

    public void setID(long id) {
        this.id = id;
    }

    /**
     * @return the flags
     */
    public int getFlags() {
        return flags;
    }

    /**
     * @param flags
     *            the flags to set
     */
    public void setFlags(int flags) {
        this.flags = flags;
    }

    /**
     * Get the used Layer of the Block
     * @return the @see Layer 
     */
    public Layer getLayer() {
        return layer;
    }

    /**
     * Set the used @see Layer
     * @param the @see Layer to set
     */
    public void setLayer(Layer layer) {
        this.layer = layer;
    }
    
    
    
    
}
