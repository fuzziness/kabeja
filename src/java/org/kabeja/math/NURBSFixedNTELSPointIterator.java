package org.kabeja.math;

import java.util.Iterator;

import org.kabeja.dxf.helpers.Point;

public class NURBSFixedNTELSPointIterator implements Iterator {

	private NURBS nurbs;
	private int ntels;

	private double dt = 0;
	private double t = 0;

	private int interval;
	private int lastInterval;

	/**
	 * 
	 * @param nurbs
	 *            The NURBS curve to draw
	 * @param ntels
	 *            the ntels per interval to use
	 */

	public NURBSFixedNTELSPointIterator(NURBS nurbs, int ntels) {

		this.nurbs = nurbs;
		this.ntels = ntels;
		if (this.nurbs.getKnots().length == (this.nurbs.getDegree()
				+ this.nurbs.controlPoints.length + 1)) {
			this.lastInterval = this.nurbs.getKnots().length
					- this.nurbs.getDegree() - 1;
			this.interval = this.nurbs.getDegree();
		} else if(this.nurbs.getKnots().length>0){
			// find self the start and end interval
			this.interval = 0;
			double start = this.nurbs.getKnots()[0];
			while (start == this.nurbs.getKnots()[this.interval + 1]) {
				this.interval++;
			}
			this.lastInterval = this.nurbs.getKnots().length - 1;
			double end = this.nurbs.getKnots()[this.lastInterval];
			while (end == this.nurbs.getKnots()[this.lastInterval]) {
				this.lastInterval--;
			}
		}
	    this.t=0;
		this.nextInterval();
		//fix for some problem nurbs
//		if(this.interval-1<this.nurbs.getDegree()){
//			this.interval=this.nurbs.getDegree()+1;
//		}
		
	}

	public boolean hasNext() {
		if (this.t < this.nurbs.getKnots()[this.interval] && this.interval<this.lastInterval) {
			return true;
		} else if (this.interval < this.lastInterval) {
			this.nextInterval();
			return hasNext();
		}
		return false;

	}

	public Object next() {

	
		Point p = this.nurbs.getPointAt(this.interval-1,t);
		this.t += this.dt;
		
		return p;
	}

	public void remove() {
       //nothing todo here
	}

	protected void nextInterval() {

		this.interval++;
		while (this.t>this.nurbs.getKnots()[this.interval] && this.interval<this.lastInterval) {
			this.interval++;
          
		}
		double length =this.nurbs.getKnots()[this.interval]-this.t;
		this.dt = length / this.ntels;
	}
}
