package org.kabeja.processing;

import java.util.Iterator;
import java.util.Map;

import org.kabeja.dxf.DXFConstants;
import org.kabeja.dxf.DXFDocument;
import org.kabeja.dxf.DXFEntity;
import org.kabeja.dxf.DXFLayer;

public class LayerMerger implements PostProcessor {

	public void setProperties(Map properties) {
		// We don't need properties 

	}

	public void process(DXFDocument doc, Map context) {
		Iterator i = doc.getDXFLayerIterator();
		while (i.hasNext()) {
			DXFLayer layer = (DXFLayer) i.next();
			if (!DXFConstants.DEFAULT_LAYER.equals( layer.getName() )) {
				Iterator types = layer.getDXFEntityTypeIterator();
				while (types.hasNext()) {
                    String type =(String)types.next();
                    Iterator entities = layer.getDXFEntities(type).iterator();
                    while(entities.hasNext()){
                    	DXFEntity e = (DXFEntity)entities.next();
                    	//we set all entities to the default layer 
                    	//and remove them from the last layer
                    	e.setLayerName(DXFConstants.DEFAULT_LAYER);
          
                    	//set again to the doc, which will
                    	//place the entity on the right 
                    	//layer -> the DEFAULT_LAYER = "0"
                    	doc.addDXFEntity(e);
                    	entities.remove();
                    }
             
				}
			}
		}
	}

}
