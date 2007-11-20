package org.kabeja.dxf.helpers;

public class DXFMLineSegmentElement {
    protected double[] lengthParameters;
    protected double[] fillParameters;
	public double[] getLengthParameters() {
		return lengthParameters;
	}
	
	public void setLengthParameters(double[] lengthParameters) {
		this.lengthParameters = lengthParameters;
	}
	
	public void setLengthParameter(int index, double v){
		this.lengthParameters[index]=v;
	}
	
	public double[] getFillParameters() {
		return fillParameters;
	}
	public void setFillParameters(double[] fillParameters) {
		this.fillParameters = fillParameters;
	}
	
	public void setFillParameter(int index,double v){
		this.fillParameters[index]=v;
	}
    
    
}
