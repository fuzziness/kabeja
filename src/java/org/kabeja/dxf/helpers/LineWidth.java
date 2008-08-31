package org.kabeja.dxf.helpers;

public class LineWidth {

	public static final int TYPE_LINE_WEIGHT=0;
    public static final int TYPE_PERCENT=1;
    public static final int TYPE_LINE_WIDTH=2;
	
	private double value=18;
    private int type=TYPE_LINE_WEIGHT;

    public LineWidth(int type, double value){
    	this.type=type;
    	this.value=value;
    }
    
    
    public LineWidth(){
    	this(TYPE_PERCENT,0.02);
    }
    

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public double getValue(){
		return this.value;
	}
	
	public void setValue(double value){
		this.value=value;
	}
	
}
