package org.kabeja.dxf.objects;

public class DXFMLineStyleElement {
   protected int lineColor=0;
   protected String lineType="BYLAYER";
   protected double offset;

	public int getLineColor() {
		return lineColor;
	}

	public void setLineColor(int lineColor) {
		this.lineColor = lineColor;
	}

	public String getLineType() {
		return lineType;
	}

	public void setLineType(String lineStyle) {
		this.lineType = lineStyle;
	}

	public double getOffset() {
		return offset;
	}

	public void setOffset(double offset) {
		this.offset = offset;
	}
}
