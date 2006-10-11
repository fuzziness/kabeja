/*
   Copyright 2005 Simon Mieth

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package org.kabeja.dxf;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kabeja.svg.SVGConstants;
import org.kabeja.svg.SVGSAXGenerator;
import org.kabeja.svg.SVGUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 *
 */
public class DXFLayer implements SVGSAXGenerator {
    private Hashtable entities = new Hashtable();
    private String name = "";
    private int color = 6;
    private DXFDocument doc;
    private Bounds bounds = new Bounds();
    private String ltype = "";
    private int flags = 0;

    public DXFLayer() {
    }

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

    public void addDXFEntity(DXFEntity entity) {
    	entity.setDXFDocument(this.doc);
        if (entities.containsKey(entity.getType())) {
            ((ArrayList) entities.get(entity.getType())).add(entity);
        } else {
            ArrayList list = new ArrayList();

            list.add(entity);
            entities.put(entity.getType(), list);
        }
    }

    public void removeDXFEntity(DXFEntity entity){
    	  if (entities.containsKey(entity.getType())) {
             ArrayList list = (ArrayList) entities.get(entity.getType());
             list.remove(entity);
             if(list.isEmpty()){
            	 entities.remove(entity.getType());
             }
          } 
    }
    
    
    public void setDXFDocument(DXFDocument doc) {
        this.doc = doc;
    }

    public void toSAX(ContentHandler handler, Map context)
        throws SAXException {
        AttributesImpl attr = new AttributesImpl();

        // TODO change the color and other attributes here
        SVGUtils.addAttribute(attr, "id", SVGUtils.validateID(getName()));

        //SVGUtils.addAttribute(attr, "id", "ID" + getName());
        SVGUtils.addAttribute(attr, "color",
            "rgb(" + DXFColor.getRGBString(Math.abs(getColor())) + ")");
        SVGUtils.addAttribute(attr, "stroke", "currentColor");
        SVGUtils.addAttribute(attr, "fill", "none");

        if (!isVisible()) {
            SVGUtils.addAttribute(attr, "visibility", "hidden");
        }

        if (ltype.length() > 0) {
            DXFLineType ltype = doc.getDXFLineType(this.ltype);
            SVGUtils.addStrokeDashArrayAttribute(attr, ltype);
        }

        SVGUtils.startElement(handler, SVGConstants.SVG_GROUP, attr);

        Enumeration e = entities.keys();

        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            ArrayList list = (ArrayList) entities.get(key);

            Iterator i = list.iterator();

            while (i.hasNext()) {
                DXFEntity entity = (DXFEntity) i.next();
                entity.toSAX(handler, context);
            }
        }

        SVGUtils.endElement(handler, SVGConstants.SVG_GROUP);
    }

    public Bounds getBounds() {
        Enumeration e = entities.elements();

        while (e.hasMoreElements()) {
            ArrayList list = (ArrayList) e.nextElement();

            Iterator i = list.iterator();

            while (i.hasNext()) {
                DXFEntity entity = (DXFEntity) i.next();
                Bounds b = entity.getBounds();

                if (b.isValid()) {
                    bounds.addToBounds(b);
                }
            }
        }

        return bounds;
    }

    /**
     * Returns the list of the DXFenetities of the Type or null.
     *
     * @param type
     * @return List or null
     */
    public List getDXFEntities(String type) {
        if (entities.containsKey(type)) {
            return (ArrayList) entities.get(type);
        }

        return null;
    }

    public boolean hasDXFEntities(String type) {
        return entities.containsKey(type);
    }

    /**
     *
     * @return a iterator over all entity types of this layer
     */
    public Iterator getDXFEntityTypeIterator() {
        return entities.keySet().iterator();
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setLineType(String ltype) {
        this.ltype = ltype;
    }

    public String getLineType() {
        return ltype;
    }

    /**
     * @return Returns the flags.
     */
    public int getFlags() {
        return flags;
    }

    /**
     * @param flags The flags to set.
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
}
