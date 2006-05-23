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
import java.util.Iterator;
import java.util.Map;

import org.kabeja.dxf.helpers.Point;
import org.kabeja.svg.SVGConstants;
import org.kabeja.svg.SVGFragmentGenerator;
import org.kabeja.svg.SVGUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 */
public class DXFBlock implements SVGFragmentGenerator {
    public static String TYPE = "BLOCK";
    private Point referencePoint;
    private String layerID = DXFConstants.DEFAULT_LAYER;
    private String name = "";
    private String description = "";
    private ArrayList entities;
    private DXFDocument doc;
    private Bounds bounds;

    /**
     *
     */
    public DXFBlock() {
        super();

        entities = new ArrayList();
        this.referencePoint = new Point();
        bounds = new Bounds();
    }

    public Bounds getBounds() {
        // first set the own point

    	Bounds bounds = new Bounds();
    	Iterator i = entities.iterator();

        if (i.hasNext()) {
            while (i.hasNext()) {
                DXFEntity entity = (DXFEntity) i.next();
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
    public Point getReferencePoint() {
        return referencePoint;
    }

    /**
     * @param p
     *            The p to set.
     */
    public void setReferencePoint(Point p) {
        this.referencePoint = p;
    }

    public void addDXFEntity(DXFEntity entity) {
        entities.add(entity);
    }

    /**
     *
     * @return a iterator over all entities of this block
     */
    public Iterator getDXFEntitiesIterator() {
        return entities.iterator();
    }

    /**
     * @return Returns the layerID.
     */
    public String getLayerID() {
        return layerID;
    }

    /**
     * @param layerID
     *            The layerID to set.
     */
    public void setLayerID(String layerID) {
        this.layerID = layerID;
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

    /*
     * (non-Javadoc)
     *
     * @see org.dxf2svg.svg.SVGGenerator#toSAX(org.xml.sax.ContentHandler)
     */
    public void toSAX(ContentHandler handler, Map svgContext)
        throws SAXException {
        AttributesImpl attr = new AttributesImpl();
        SVGUtils.addAttribute(attr, "id", SVGUtils.validateID(getName()));

        // SVGUtils.addAttribute(attr,"stroke-width","0.1");
        // String b = bounds.getMinimumX()+"
        // "+doc.translateY(bounds.getMinimumY())+" "+bounds.getWidth()+"
        // "+bounds.getHeight();
        // the viewBox is needed otherwise the block is not visible
        // SVGUtils.addAttribute(attr,"viewBox",b);
        SVGUtils.startElement(handler, SVGConstants.SVG_GROUP, attr);

        Iterator i = entities.iterator();

        while (i.hasNext()) {
            DXFEntity entity = (DXFEntity) i.next();
            entity.toSAX(handler, svgContext);
        }

        SVGUtils.endElement(handler, SVGConstants.SVG_GROUP);
    }

    /**
     * @param doc
     *            The doc to set.
     */
    public void setDXFDocument(DXFDocument doc) {
        this.doc = doc;

        Iterator i = entities.iterator();

        while (i.hasNext()) {
            DXFEntity entity = (DXFEntity) i.next();
            entity.setDXFDocument(doc);
        }
    }
    
    public double getLength(){
    	 
    	 double length=0;
    	 Iterator i = entities.iterator();

          while (i.hasNext()) {
              DXFEntity entity = (DXFEntity) i.next();
              length+=entity.getLength();
              
          }
          
          return length;
    }
    
    
    
}
