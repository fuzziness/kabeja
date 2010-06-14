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
package org.kabeja.svg;

public class Paper {
  
	
    public static final float INCH_TO_MM = 25.4f;
    public static final float PT_TO_MM = 0.3527777777777f;
    public int dpi = 96;
    public float pixelUnitToMM = INCH_TO_MM / dpi;
    
    
    
    protected float width;
    protected float height;
    
    
    
    public void setDPI(int dpi){
    	this.dpi = dpi;
    	this.pixelUnitToMM=INCH_TO_MM/dpi;
    }
    
    
    public void setPaperFormat(String paper){
    	this.parsePaper(paper);
    }
    
    public float getPixelWidth(){
    	return this.width;
    }
    
    
    public float getPixelHeight(){
    	return this.height;
    }
    
    protected void parsePaper(String paper) {
        if (paper.equals("a0")) {
            this.width = 841 / pixelUnitToMM;
            this.height = 1189 / pixelUnitToMM;
        } else if (paper.equals("a1")) {
            this.width = 594 / pixelUnitToMM;
            this.height = 841 / pixelUnitToMM;
        } else if (paper.equals("a2")) {
            this.width = 420 / pixelUnitToMM;
            this.height = 594 / pixelUnitToMM;
        } else if (paper.equals("a3")) {
            this.width = 297 / pixelUnitToMM;
            this.height = 420 / pixelUnitToMM;
        } else if (paper.equals("a4")) {
            this.width = 210 / pixelUnitToMM;
            this.height = 297 / pixelUnitToMM;
        } else if (paper.equals("a5")) {
            this.width = 148 / pixelUnitToMM;
            this.height = 210 / pixelUnitToMM;
        } else if (paper.equals("a6")) {
            this.width = 105 / pixelUnitToMM;
            this.height = 148 / pixelUnitToMM;
        } else if (paper.equals("letter")) {
            this.width = 216 / pixelUnitToMM;
            this.height = 279 / pixelUnitToMM;
        }

        //add more papers here
    }
    

    protected float unitsToPixel(String size) {
        if (size.endsWith("px")) {
            return Float.parseFloat(size.substring(0, size.length() - 2));
        } else if (size.endsWith("in")) {
            return (Float.parseFloat(size.substring(0, size.length() - 2)) * INCH_TO_MM) / pixelUnitToMM;
        } else if (size.endsWith("pt")) {
            return (Float.parseFloat(size.substring(0, size.length() - 2)) * PT_TO_MM) / pixelUnitToMM;
        } else if (size.endsWith("cm")) {
            float units = Float.parseFloat(size.substring(0, size.length() - 2));
            float pixel = (float) ((units * 100) / pixelUnitToMM);

            return pixel;
        } else if (size.endsWith("mm")) {
            float units = Float.parseFloat(size.substring(0, size.length() - 2));
            float pixel = (float) (units / pixelUnitToMM);

            return pixel;
        } else if (size.endsWith("m")) {
            float units = Float.parseFloat(size.substring(0, size.length() - 1));
            float pixel = (float) ((units * 1000) / pixelUnitToMM);

            return pixel;
        } else {
            return Float.parseFloat(size);
        }
    }
    
    
}
