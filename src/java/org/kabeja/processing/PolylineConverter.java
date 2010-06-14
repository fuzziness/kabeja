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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kabeja.DraftDocument;
import org.kabeja.common.DraftEntity;
import org.kabeja.common.Layer;
import org.kabeja.common.Type;
import org.kabeja.entities.Arc;
import org.kabeja.entities.LW2DVertex;
import org.kabeja.entities.LWPolyline;
import org.kabeja.entities.Line;
import org.kabeja.entities.Polyline;
import org.kabeja.math.Point3D;
import org.kabeja.processing.helper.PolylineQueue;
import org.kabeja.util.Constants;


public class PolylineConverter extends AbstractPostProcessor {
    public final static String PROPERTY_POINT_DISTANCE = "point.distance";
    private List queues;
    private double radius = Constants.POINT_CONNECTION_RADIUS;

    public void process(DraftDocument doc, Map context) throws ProcessorException {
     for(Layer layer:doc.getLayers()){
            processLayer(layer);
        }

        // TODO process the blocks too
    }

    public void setProperties(Map properties) {
        if (properties.containsKey(PROPERTY_POINT_DISTANCE)) {
            this.radius = Double.parseDouble((String) properties.get(
                        PROPERTY_POINT_DISTANCE));
        }
    }

    protected void processLayer(Layer layer) {
        this.queues = new ArrayList();

        // check the lines
        if (layer.hasEntities(Type.TYPE_LINE)) {
            List<Line> l = layer.getEntitiesByType(Type.TYPE_LINE);
            for(Line line:l){
                Point3D start = line.getStartPoint();
                Point3D end = line.getEndPoint();
                checkEntity(line, start, end);
            }
        }

        // check the polylines
        if (layer.hasEntities(Type.TYPE_POLYLINE)) {
            List<Polyline> l = layer.getEntitiesByType(Type.TYPE_POLYLINE);
             for(Polyline pl:l) {
                if (!pl.isClosed() && !pl.is3DPolygonMesh() &&
                        !pl.isClosedMeshMDirection() &&
                        !pl.isClosedMeshNDirection() &&
                        !pl.isCubicSurefaceMesh()) {
                    Point3D start = pl.getVertex(0).getPoint();
                    Point3D end = pl.getVertex(pl.getVertexCount() - 1).getPoint();
                    checkEntity(pl, start, end);
                }
            }
        }

        // check the lwpolylines
        if (layer.hasEntities(Type.TYPE_LWPOLYLINE)) {
            List<LWPolyline> l = layer.getEntitiesByType(Type.TYPE_LWPOLYLINE);
            for(LWPolyline pl:l) {
                if (!pl.isClosed()){
                    LW2DVertex startVertex = pl.getVertex(0);
                    LW2DVertex endVertex = pl.getVertex(pl.getVertexCount() - 1);
                    Point3D start = new Point3D(startVertex.getX(),startVertex.getY(),0.0);
                    Point3D end = new Point3D(endVertex.getX(),endVertex.getY(),0.0);
                    checkEntity(pl, start, end);
                }
            }
        }

        // check the arcs
        if (layer.hasEntities(Type.TYPE_ARC)) {
            List<Arc> l = layer.getEntitiesByType(Type.TYPE_ARC);
            for(Arc arc:l) {
              
                // note that this points are calculated
                // and could be not connected to the rest
                // even though they should in you CAD
                Point3D start = arc.getStartPoint();
                Point3D end = arc.getEndPoint();
                checkEntity(arc, start, end);
            }
        }

        // finish up the connection search
        // and connect parts, if it is possible
        connectPolylineQueues();

        cleanUp(layer);
    }

    protected void checkEntity(DraftEntity e, Point3D start, Point3D end) {
        Iterator i = this.queues.iterator();

        while (i.hasNext()) {
            PolylineQueue queue = (PolylineQueue) i.next();

            if (queue.connectEntity(e, start, end)) {
                return;
            }
        }

        // nothing found create a new queue
        PolylineQueue queue = new PolylineQueue(e, start, end, this.radius);

        this.queues.add(queue);
    }

    protected void cleanUp(Layer layer) {
        Iterator i = this.queues.iterator();

        while (i.hasNext()) {
            PolylineQueue queue = (PolylineQueue) i.next();

            if (queue.size() > 1) {
                queue.createPolyline(layer);
            } else {
                // ignore
                i.remove();
            }
        }
    }

    /**
     * Goes through all polylinequeues and connect them, if they have the same
     * start and end points.
     *
     */
    protected void connectPolylineQueues() {
        for (int i = 0; i < this.queues.size(); i++) {
            PolylineQueue queue = (PolylineQueue) this.queues.get(i);

            boolean connected = false;

            //inner loop -> test all following polylines if
            //we can connect
            for (int x = i + 1; (x < this.queues.size()) && !connected; x++) {
                if (((PolylineQueue) this.queues.get(x)).connect(queue)) {
                    this.queues.remove(i);
                    i--;
                    connected = true;
                }
            }
        }
    }
}
