package org.kabeja.dxf.helpers;

import java.util.ArrayList;
import java.util.List;

public class DXFMLineSegment {

	protected Point startPoint = new Point();
	protected Vector direction = new Vector();
	protected Vector miterDirection = new Vector();
	protected List elements = new ArrayList();

	public Point getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(Point startPoint) {
		this.startPoint = startPoint;
	}

	public Vector getDirection() {
		return direction;
	}

	public void setDirection(Vector direction) {
		this.direction = direction;
	}

	public Vector getMiterDirection() {
		return miterDirection;
	}

	public void setMiterDirection(Vector miterDirection) {
		this.miterDirection = miterDirection;
	}
	
	public void addDXFMLineSegmentElement(DXFMLineSegmentElement el){
		this.elements.add(el);
	}
	
	public int getDXFMLineSegmentElementCount(){
		return this.elements.size();
	}

	public DXFMLineSegmentElement getDXFMLineSegmentElement(int index){
		return (DXFMLineSegmentElement)this.elements.get(index);
	}
	
}
