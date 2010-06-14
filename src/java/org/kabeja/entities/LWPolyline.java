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
/*
 * Created on 13.04.2005
 *
 */
package org.kabeja.entities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kabeja.DraftDocument;
import org.kabeja.common.Type;
import org.kabeja.math.Bounds;
import org.kabeja.math.MathUtils;
import org.kabeja.math.Point3D;
import org.kabeja.math.TransformContext;
import org.kabeja.math.Vector;


/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 */
public class LWPolyline extends Entity{
   
    
	private final static int LAZY_INDEX_CONSTANTWIDTH=10;
	private final static int LAZY_INDEX_STARTWIDTH=11;
	private final static int LAZY_INDEX_ENDWIDTH=12;
	private final static int LAZY_INDEX_ELEVATION=13;
	private final static int BOOLEAN_BIT_CONSTANTWIDTH=10;
	
    protected static final double QUARTER_CIRCLE_ANGLE = Math.tan(0.39269908169872414D);
    protected List<LW2DVertex> vertices = new ArrayList<LW2DVertex>();
   
    
    
   
 
   
  

    public LWPolyline() {
    }

    public void setConstantWidth(double width) {
    	if(width != 0.0){
          this.lazyContainer.set(new Double(width), LAZY_INDEX_CONSTANTWIDTH);
    	}
    }

    public double getContstantWidth() {
    	if(this.lazyContainer.contains(LAZY_INDEX_CONSTANTWIDTH)){
    		return ((Double)this.lazyContainer.get(LAZY_INDEX_CONSTANTWIDTH)).doubleValue();
    	}
        return 0.0;
    }


    public Type<LWPolyline>getType() {
        return Type.TYPE_LWPOLYLINE;
    }
    


  
    public Bounds getBounds() {
        Bounds bounds = new Bounds();

        Iterator<LW2DVertex> i = vertices.iterator();

        if (i.hasNext()) {
        	LW2DVertex last;
        	LW2DVertex first;
        	LW2DVertex v = null;

            last = first = i.next();
            bounds.addToBounds(last.getX(),last.getY(),0.0);

            while (i.hasNext()) {
            	v=i.next();
                addToBounds(last, v, bounds);
                last = v;
            }

            if ((v != null) && (v.getBulge() != 0.0)) {
                addToBounds(v, first, bounds);
            }
        } else {
            bounds.setValid(false);
        }

        return bounds;
    }

    public void addVertex(LW2DVertex vertex) {
        vertices.add(vertex);
    }

    public int getVertexCount() {
        return this.vertices.size();
    }

    public List<LW2DVertex> getVertices() {
        return this.vertices;
    }

    public void removeVertex(LW2DVertex vertex) {
        // remove and check the constantwidth
     this.setBit(BOOLEAN_BIT_CONSTANTWIDTH, true);
          for(Iterator<LW2DVertex> i=this.vertices.iterator();i.hasNext();){
        	  LW2DVertex v =i.next();
            if (v == vertex) {
                i.remove();
            } else if (!v.isConstantWidth()) {
            	  this.setBit(BOOLEAN_BIT_CONSTANTWIDTH, false);
            }
        }
    }

    public void removeVertex(int index) {
    	  this.setBit(BOOLEAN_BIT_CONSTANTWIDTH, true);
        int count=0;
          for(Iterator<LW2DVertex> i=this.vertices.iterator();i.hasNext();){
        	  LW2DVertex v =i.next();
            if (count == index) {
                i.remove();
            } else if (!v.isConstantWidth()) {
            	  this.setBit(BOOLEAN_BIT_CONSTANTWIDTH, false);
            }
            count++;
        }
    }

    public LW2DVertex getVertex(int i) {
        return (LW2DVertex) vertices.get(i);
    }



    /**
     * Caculate the radius of a cut circle segment between 2 Vertex
     *
     * @param bulge
     *            the vertex bulge
     * @param length
     *            the length of the circle cut
     */
    public double getRadius(double bulge, double length) {
        double h = (bulge * length) / 2;
        double value = (h / 2) + (Math.pow(length, 2) / (8 * h));

        return Math.abs(value);
    }



    /**
     * @return Returns the endWidth.
     */
    public double getEndWidth() {
    	if(this.lazyContainer.contains(LAZY_INDEX_ENDWIDTH)){
    		return ((Double)this.lazyContainer.get(LAZY_INDEX_ENDWIDTH)).doubleValue();
    	}
        return 0.0;
    }

    /**
     * @param endWidth
     *            The endWidth to set.
     */
    public void setEndWidth(double endWidth) {
    	if(endWidth!=0.0){
         this.lazyContainer.set(new Double(endWidth),LAZY_INDEX_ENDWIDTH);
    	}
    }

    /**
     * @return Returns the startWidth.
     */
    public double getStartWidth() {
          if(this.lazyContainer.contains(LAZY_INDEX_STARTWIDTH)){
        	  return ((Double)this.lazyContainer.get(LAZY_INDEX_STARTWIDTH)).doubleValue();
          }
          return 0.0;
    }

    /**
     * @param startWidth
     *            The startWidth to set.
     */
    public void setStartWidth(double startWidth) {
    	if(startWidth!=0.0){
            this.lazyContainer.set(new Double(startWidth),LAZY_INDEX_STARTWIDTH);
       	}
    }

    public boolean isClosed() {
        // the closed Flag
        return (this.flags & 1) == 1;
    }


  
    public boolean isConstantWidth() {
        //TODO review to see if the 
        //property is always set correct
        if (!isBitEnabled(BOOLEAN_BIT_CONSTANTWIDTH)) {
            return false;
        } else {
           setBit(BOOLEAN_BIT_CONSTANTWIDTH, true);

            Iterator<LW2DVertex> i = vertices.iterator();

            while (i.hasNext()) {
                LW2DVertex vertex = i.next();

                if (!vertex.isConstantWidth()) {
                	 setBit(BOOLEAN_BIT_CONSTANTWIDTH, false);

                    return  isBitEnabled(BOOLEAN_BIT_CONSTANTWIDTH);
                }
            }
        }

         return  isBitEnabled(BOOLEAN_BIT_CONSTANTWIDTH);
    }




    protected void addToBounds(LW2DVertex start, LW2DVertex end, Bounds bounds) {
        if (start.getBulge() != 0) {
            // calculte the height
        	
            double l = MathUtils.distance(start.getPoint(), end.getPoint());

            // double h = Math.abs(last.getBulge()) * l / 2;
            double r = this.getRadius(start.getBulge(), l);

            double s = l / 2;
            Vector edgeDirection = MathUtils.getVector(start.getPoint(),
                    end.getPoint());
            edgeDirection = MathUtils.normalize(edgeDirection);

            Point3D centerPoint = MathUtils.getPointOfStraightLine(start.getPoint(),
                    edgeDirection, s);

            Vector centerPointDirection = MathUtils.crossProduct(edgeDirection,
                    this.getExtrusion().getNormal());
            centerPointDirection = MathUtils.normalize(centerPointDirection);

            // double t = Math.sqrt(Math.pow(r, 2) - Math.pow(s, 2));
            // double t = 0;
            double h = Math.abs(start.getBulge() * l) / 2;

            // if(Math.abs(start.getBulge())>=1.0){
            // t = h-r;
            // }else{
            // //t = Math.sqrt(Math.pow(r, 2) - Math.pow(s, 2));
            // t=r-h;
            // }
            // the center point of the arc
            int startQ = 0;
            int endQ = 0;

            double bulge = start.getBulge();

            if (bulge > 0) {
                // the arc goes over the right side, but where is the center
                // point?
                if (bulge > 1.0) {
                    double t = h - r;
                    centerPoint = MathUtils.getPointOfStraightLine(centerPoint,
                            centerPointDirection, t);
                } else {
                    double t = r - h;
                    centerPoint = MathUtils.getPointOfStraightLine(centerPoint,
                            centerPointDirection, (-1 * t));
                }

                endQ = MathUtils.getQuadrant(end.getPoint(), centerPoint);
                startQ = MathUtils.getQuadrant(start.getPoint(), centerPoint);
            } else {
                // the arc goes over the left side, but where is the center
                // point?
                if (bulge < -1.0) {
                    double t = h - r;
                    centerPoint = MathUtils.getPointOfStraightLine(centerPoint,
                            centerPointDirection, (-1 * t));
                } else {
                    double t = r - h;
                    centerPoint = MathUtils.getPointOfStraightLine(centerPoint,
                            centerPointDirection, t);
                }

                startQ = MathUtils.getQuadrant(end.getPoint(), centerPoint);
                endQ = MathUtils.getQuadrant(start.getPoint(), centerPoint);
            }

            if (endQ < startQ) {
                endQ += 4;
            } else if ((endQ == startQ) &&
                    (Math.abs(start.getBulge()) > QUARTER_CIRCLE_ANGLE)) {
                endQ += 4;
            }

            while (endQ > startQ) {
                switch (startQ) {
                case 0:
                    bounds.addToBounds(centerPoint.getX(),
                        centerPoint.getY() + r, centerPoint.getZ());

                    break;

                case 1:
                    bounds.addToBounds(centerPoint.getX() - r,
                        centerPoint.getY(), centerPoint.getZ());

                    break;

                case 2:
                    bounds.addToBounds(centerPoint.getX(),
                        centerPoint.getY() - r, centerPoint.getZ());

                    break;

                case 3:
                    bounds.addToBounds(centerPoint.getX() + r,
                        centerPoint.getY(), centerPoint.getZ());
                    endQ -= 4;
                    startQ -= 4;

                    break;
                }

                startQ++;
            }
        }

        bounds.addToBounds(start.getPoint());
        bounds.addToBounds(end.getPoint());
    }



    public double getLength() {
        double length = 0.0;

            // a normal polyline with or without bulges
            Iterator<LW2DVertex> i = this.vertices.iterator();
            LW2DVertex first;
            LW2DVertex last = first = i.next();

            while (i.hasNext()) {
                LW2DVertex v =  i.next();
                length += this.getSegmentLength(last, v);
                last = v;
            }

            if (this.isClosed()) {
                length += this.getSegmentLength(last, first);
            }
     

        return length;
    }

    protected double getSegmentLength(LW2DVertex start, LW2DVertex end) {
        double l = MathUtils.distance(start.getPoint(), end.getPoint());

        if (start.getBulge() == 0.0) {
            return l;
        } else {
            double alpha = 4 * Math.atan(Math.abs(start.getBulge()));

            double r = l / (2 * Math.sin(alpha / 2));
            double d = (Math.PI * Math.toDegrees(alpha) * r) / 180;

            return d;
        }
    }

   
  
 
    public void setClosed(boolean b){
        if(b){
            this.flags = this.flags | 1;
        }else if(this.isClosed()){
            this.flags = this.flags ^ 1;
        }
    }

    /**
     * @return the elevation
     */
    public Point3D getElevation() {
    	if (this.lazyContainer.contains(LAZY_INDEX_ELEVATION)) {
			return (Point3D) this.lazyContainer.get(LAZY_INDEX_ELEVATION);
		}

		return new Point3D() {
			public void setX(double x) {
				this.addToContainer();
				super.setX(x);
			}

			public void setY(double y) {
				this.addToContainer();
				super.setY(y);
			}

			public void setZ(double z) {
				this.addToContainer();
				super.setZ(z);
			}

			private void addToContainer() {
				if (!lazyContainer.contains(LAZY_INDEX_ELEVATION)) {
					lazyContainer.set(this, LAZY_INDEX_ELEVATION);
				}
			}
		};
    }

    /**
     * @param elevation the elevation to set
     */
    public void setElevation(Point3D elevation) {
        if(elevation.getX()!= 0.0 && elevation.getY()!=0.0 && elevation.getZ()!=0.0){
        	this.lazyContainer.set(elevation, LAZY_INDEX_ELEVATION);
        }
    }


    public void setDocument(DraftDocument doc) {
        super.setDocument(doc);
    }
    
    /**
     * Not implemented yet
     */
    
    public void transform(TransformContext context) {
      
    }
}
