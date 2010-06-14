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

import org.kabeja.common.Type;
import org.kabeja.math.Bounds;
import org.kabeja.math.Point3D;
import org.kabeja.math.TransformContext;

/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 * 
 */
public class Vertex extends Entity{

	protected static final int LAZY_INDEX_STARTWIDTH=15;
	protected static final int LAZY_INDEX_ENDWIDTH=16;
	protected static final int LAZY_INDEX_BULGE=17;
	protected static final int LAZY_INDEX_POLYFACEMESHVERTEX_0=18;
	protected static final int LAZY_INDEX_POLYFACEMESHVERTEX_1=19;
	protected static final int LAZY_INDEX_POLYFACEMESHVERTEX_2=20;
	protected static final int LAZY_INDEX_POLYFACEMESHVERTEX_3=21;
	
    private Point3D p = null;
	
	public Vertex(Point3D p) {
		super();
        this.p = p;
		
	}

	public Vertex() {
        this(new Point3D());
    }

    /**
	 * @return Returns the endWidth.
	 */
	public double getEndWidth() {
		if (this.lazyContainer.contains(LAZY_INDEX_ENDWIDTH)) {
			return ((Double)this.lazyContainer.get(LAZY_INDEX_ENDWIDTH)).doubleValue();
		}
		return 0.0;
	}

	/**
	 * @param endWidth
	 *            The endWidth to set.
	 */
	public void setEndWidth(double endWidth) {
		    if(this.lazyContainer.contains(LAZY_INDEX_ENDWIDTH) || endWidth != 0.0){
		    	this.lazyContainer.set(new Double(endWidth),LAZY_INDEX_ENDWIDTH);
		    }
	}

	/**
	 * @return Returns the startWidth.
	 */
	public double getStartWidth() {
		if (this.lazyContainer.contains(LAZY_INDEX_STARTWIDTH)) {
			return ((Double)this.lazyContainer.get(LAZY_INDEX_STARTWIDTH)).doubleValue();
		}
		return 0.0;
	}

	/**
	 * @param startWidth
	 *            The startWidth to set.
	 */
	public void setStartWidth(double startWidth) {
	    if(this.lazyContainer.contains(LAZY_INDEX_STARTWIDTH) || startWidth != 0.0){
	    	this.lazyContainer.set(new Double(startWidth),LAZY_INDEX_STARTWIDTH);
	    }
	}



	public Bounds getBounds() {
		Bounds bounds = new Bounds();
        bounds.addToBounds(this.p);
        
        
        return bounds;
	}

	/**
	 * @return Returns the bulge.
	 */
	public double getBulge() {
		if (this.lazyContainer.contains(LAZY_INDEX_BULGE)) {
			return ((Double)this.lazyContainer.get(LAZY_INDEX_BULGE)).doubleValue();
		}
		return 0.0;

	}

	/**
	 * @param bulge
	 *            The bulge to set.
	 */
	public void setBulge(double bulge) {
	    if(this.lazyContainer.contains(LAZY_INDEX_BULGE) || bulge != 0.0){
	    	this.lazyContainer.set(new Double(bulge),LAZY_INDEX_BULGE);
	    }

	}


	public Type<Vertex> getType() {
		return Type.TYPE_VERTEX;
	}

	public boolean isConstantWidth() {
		if (this.lazyContainer.contains(LAZY_INDEX_STARTWIDTH) && this.lazyContainer.contains(LAZY_INDEX_ENDWIDTH)) {
			return ((Double)this.lazyContainer.get(LAZY_INDEX_STARTWIDTH)).doubleValue() ==  ((Double)this.lazyContainer.get(LAZY_INDEX_ENDWIDTH)).doubleValue();
		} else {
			return true;
		}
	}

	public boolean isCurveFitVertex() {
		return (this.flags & 1) == 1;
	}

	public boolean isTagentUsed() {
		return (this.flags & 2) == 2;
	}

	public boolean is2DSplineControlVertex() {
		return (this.flags & 16) == 16;
	}

	public boolean is2DSplineApproximationVertex() {
		return (this.flags & 8) == 8;
	}

	public boolean isPolyFaceMeshVertex() {
		// bit 7 and 8 are set
		return (((this.flags & 64) == 64) && ((this.flags & 128) == 128));
	}

	public boolean isFaceRecord() {
		return this.flags == 128;
	}

	public boolean isMeshApproximationVertex() {
		return ((this.flags & 64) == 64) && ((this.flags & 8) == 8);
	}

	/**
	 * @return Returns the polyFaceMeshVertex0.
	 */
	public int getPolyFaceMeshVertex0() {
		if (this.lazyContainer.contains(LAZY_INDEX_POLYFACEMESHVERTEX_0)) {
			return Math.abs(((Integer)this.lazyContainer.get(LAZY_INDEX_POLYFACEMESHVERTEX_0)).intValue());
		} else {
			return 0;
		}
	}

	/**
	 * @param polyFaceMeshVertex0
	 *            The polyFaceMeshVertex0 to set.
	 */
	public void setPolyFaceMeshVertex0(int polyFaceMeshVertex0) {
	    	this.lazyContainer.set(new Integer(polyFaceMeshVertex0),LAZY_INDEX_POLYFACEMESHVERTEX_0);    
	}

	/**
	 * @return Returns the polyFaceMeshVertex1.
	 */
	public int getPolyFaceMeshVertex1() {
		if (this.lazyContainer.contains(LAZY_INDEX_POLYFACEMESHVERTEX_1)) {
			return Math.abs(((Integer)this.lazyContainer.get(LAZY_INDEX_POLYFACEMESHVERTEX_1)).intValue());
		} else {
			return 0;
		}

	}

	/**
	 * @param polyFaceMeshVertex1
	 *            The polyFaceMeshVertex1 to set.
	 */
	public void setPolyFaceMeshVertex1(int polyFaceMeshVertex1) {
		this.lazyContainer.set(new Integer(polyFaceMeshVertex1),LAZY_INDEX_POLYFACEMESHVERTEX_1);   

	}

	/**
	 * @return Returns the polyFaceMeshVertex2.
	 */
	public int getPolyFaceMeshVertex2() {
		if (this.lazyContainer.contains(LAZY_INDEX_POLYFACEMESHVERTEX_2)) {
			return Math.abs(((Integer)this.lazyContainer.get(LAZY_INDEX_POLYFACEMESHVERTEX_2)).intValue());
		} else {
			return 0;
		}

	}

	/**
	 * @param polyFaceMeshVertex2
	 *            The polyFaceMeshVertex2 to set.
	 */
	public void setPolyFaceMeshVertex2(int polyFaceMeshVertex2) {
		this.lazyContainer.set(new Integer(polyFaceMeshVertex2),LAZY_INDEX_POLYFACEMESHVERTEX_2);   
	}

	/**
	 * @return Returns the polyFaceMeshVertex3.
	 */
	public int getPolyFaceMeshVertex3() {
		if (this.lazyContainer.contains(LAZY_INDEX_POLYFACEMESHVERTEX_3)) {
			return Math.abs(((Integer)this.lazyContainer.get(LAZY_INDEX_POLYFACEMESHVERTEX_3)).intValue());
		} else {
			return 0;
		}

	}

	/**
	 * @param polyFaceMeshVertex3
	 *            The polyFaceMeshVertex3 to set.
	 */
	public void setPolyFaceMeshVertex3(int polyFaceMeshVertex3) {
		this.lazyContainer.set(new Integer(polyFaceMeshVertex3),LAZY_INDEX_POLYFACEMESHVERTEX_3);   
	}

	public boolean isPolyFaceEdge0Visible() {
		if (this.lazyContainer.contains(LAZY_INDEX_POLYFACEMESHVERTEX_0)) {
			return ((Integer)this.lazyContainer.get(LAZY_INDEX_POLYFACEMESHVERTEX_0)).intValue()>0;
		} else {
			return false;
		}
	}

	public boolean isPolyFaceEdge1Visible() {
		if (this.lazyContainer.contains(LAZY_INDEX_POLYFACEMESHVERTEX_1)) {
			return ((Integer)this.lazyContainer.get(LAZY_INDEX_POLYFACEMESHVERTEX_1)).intValue()>0;
		} else {
			return false;
		}
		
	}

	public boolean isPolyFaceEdge2Visible() {
		if (this.lazyContainer.contains(LAZY_INDEX_POLYFACEMESHVERTEX_2)) {
			return ((Integer)this.lazyContainer.get(LAZY_INDEX_POLYFACEMESHVERTEX_2)).intValue()>0;
		} else {
			return false;
		}
		
	}

	public boolean isPolyFaceEdge3Visible() {
		if (this.lazyContainer.contains(LAZY_INDEX_POLYFACEMESHVERTEX_3)) {
			return ((Integer)this.lazyContainer.get(LAZY_INDEX_POLYFACEMESHVERTEX_3)).intValue()>0;
		} else {
			return false;
		}
	}

    public double getLength(){
        return 0.0;
    }

    public Point3D getPoint(){
        return this.p;
    }

    
    /**
     * Not implemented yet
     */
    
    public void transform(TransformContext context) {
      this.p = context.transform(this.p);
       
    }


	
}
