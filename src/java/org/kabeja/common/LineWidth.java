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
package org.kabeja.common;

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
