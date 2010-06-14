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

package org.kabeja.parser.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kabeja.math.Point3D;

public class PointParser {
	
	
	public static final int COORDINATE_FORMAT_1D=1;
	public static final int COORDINATE_FORMAT_2D=2;
	public static final int COORDINATE_FORMAT_3D=3;

	
	
	private static final int STATE_SEPARATOR=0;
	private static final int STATE_NUMBER=1;
	
	
	
	
	
	private StringBuffer pointString = new StringBuffer();
	private int coordinateType=3;
	private int currentCoordinate=COORDINATE_FORMAT_1D;
	private int state=STATE_NUMBER;
	private char numberSeparator='.';
	private  char separator=' ';
	private char numberGrouping=',';
    private List points = new ArrayList();
	private Point3D currentPoint=new Point3D();
	 
	

	
	
	
	/**
     * @return the coordinateType
     */
    public int getCoordinateType() {
        return coordinateType;
    }

    /**
     * @param coordinateType the coordinateType to set
     */
    public void setCoordinateType(int coordinateType) {
        this.coordinateType = coordinateType;
    }

    /**
     * @return the numberSeparator
     */
    public char getNumberSeparator() {
        return numberSeparator;
    }

    /**
     * @param numberSeparator the numberSeparator to set
     */
    public void setNumberSeparator(char numberSeparator) {
        this.numberSeparator = numberSeparator;
    }

    /**
     * @return the separator
     */
    public char getSeparator() {
        return separator;
    }

    /**
     * @param separator the separator to set
     */
    public void setSeparator(char separator) {
        this.separator = separator;
    }

    /**
     * @return the numberGrouping
     */
    public char getNumberGrouping() {
        return numberGrouping;
    }

    /**
     * @param numberGrouping the numberGrouping to set
     */
    public void setNumberGrouping(char numberGrouping) {
        this.numberGrouping = numberGrouping;
    }

    public void appendPointString(String pointString){	
		parse(pointString.toCharArray(),0,pointString.length());
	}
	
	public void appendPointString(char[] c){
		parse(c,0,c.length);
		
	}
	

	public void appendPointString(char[] c,int start,int length){
		parse(c,start,length);
		
	}
	
	

	
	
	public Iterator getPointIterator(){
	    //flush buffer
	    parse(new char[]{separator},0,1);
	    state=STATE_NUMBER;
		return this.points.iterator();
	}
	
	
	
	
	
	private void parse(char[] c,int start,int length){
	    int end = start+length;
	    for(int i=start;i<end;i++){
	        char dt = c[i];
	       
	        if(Character.isDigit(c[i])){
	         
	            this.pointString.append(c[i]);
	            this.state=STATE_NUMBER;
	        }else if(c[i]==numberGrouping ){
	            //we ignore grouping
	            this.state=STATE_NUMBER;
	           
	        }else if(c[i] == numberSeparator){
	        
	            this.pointString.append('.');
	            this.state=STATE_NUMBER;
	        }else if(state==STATE_SEPARATOR){
	            //do nothing the state was changed 
	            //before
	           
	        }else{
	        
	            state = STATE_SEPARATOR;
	            if(this.pointString.length()>0){
	                //System.out.println("eval buffer:"+this.pointString.toString());
                   
	                try {
                        double d = Double.parseDouble(this.pointString.toString());
                        this.pointString.delete(0,this.pointString.length());
         
                        switch(currentCoordinate){
                        case COORDINATE_FORMAT_1D:
                            this.currentPoint = new Point3D();
                            this.points.add(this.currentPoint);
                            this.currentPoint.setX(d);
                            break;
                        case COORDINATE_FORMAT_2D:
                            this.currentPoint.setY(d);
                            break;
                        case COORDINATE_FORMAT_3D:
                            this.currentPoint.setZ(d);
                            break;
                        }
                        if(currentCoordinate == coordinateType){
                            //reset 
                            currentCoordinate =COORDINATE_FORMAT_1D;
                        }else{
                        
                        currentCoordinate++;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Unsupported Format:"+this.pointString.toString());
                        this.pointString.delete(0,this.pointString.length());
                        state=STATE_NUMBER;
                       
                    }
	            }
	        }
	    }
	
	    
	}
	
	
	public void clear(){
	    this.pointString.delete(0,this.pointString.length());
	    this.points.clear();
	}
	
	
	
	
	public static void main(String[] args){
	    
	    String pointString="1512551.10509471";
		PointParser parser = new PointParser();
		parser.setCoordinateType(PointParser.COORDINATE_FORMAT_2D);
		parser.appendPointString(pointString);
		parser.appendPointString(" 5041237.15842429");
		parser.appendPointString("01 11.");
		Iterator i = parser.getPointIterator();
		while(i.hasNext()){
		Point3D p = (Point3D)i.next();
		System.out.println(p);
		}
		
		
	}


}
