/*
   Copyright 2009 Simon Mieth

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package org.kabeja.parser.util;

import java.util.Iterator;

import org.kabeja.math.Point;

public class PointParser {
	
	
	public static final int COORDINATE_FORMAT_1D=1;
	public static final int COORDINATE_FORMAT_2D=2;
	public static final int COORDINATE_FORMAT_3D=3;
	
	
	private StringBuffer pointString = new StringBuffer();
	private int pos=0;
	private String separator=" ";

	 
	
	public void setPointString(String pointString){
		this.pointString.delete(0,this.pointString.length());
		this.pointString.append(pointString);
		this.pos =0;
	}
	
	public void appendPointString(String pointString){	
		this.pointString.append(pointString);
	}
	
	public void appendPointString(char[] c){
		this.pointString.append(c);
	}
	
	public void appendPointString(char[] c,int start,int length){
		this.pointString.append(c,start,length);
	}
	
	
	private boolean hasNextCoordinate(){
		return this.pos < this.pointString.length();
	}
	
	private double parseNextCoordinate(){
	   
		if(this.pos<this.pointString.length()){
			int nextPos = this.pointString.indexOf(separator,pos);
			if(nextPos>pos){
				//inside string
				String v = this.pointString.substring(this.pos,nextPos);
				double result = Double.parseDouble(v);
				this.pos = nextPos+1;
				return result;
			}else{
				//last double in string
				double result  = Double.parseDouble(this.pointString.substring(this.pos));
				pos = this.pointString.length();
				return result;
			}
			
		}
		
		return Double.NaN;
	}
	
	public Iterator getPointIterator(int coordinateCount){
		return this.getPointIterator(coordinateCount,this.separator);
	}
	
	
	public Iterator getPointIterator(int coordinateCount,String separator){
		
		final int count = coordinateCount;
		this.separator = separator;
		
		
		Iterator i  = new Iterator(){

			
			
			/* (non-Javadoc)
			 * @see java.util.Iterator#hasNext()
			 */
			public boolean hasNext() {
				
				return hasNextCoordinate();
			}

			/* (non-Javadoc)
			 * @see java.util.Iterator#next()
			 */
			public Object next() {
				
				Point p = new Point();
				//first coordinate is available by default
				p.setX(parseNextCoordinate());
				
				if(count>=2 && hasNextCoordinate()){
					p.setY(parseNextCoordinate());
				}
				
				if(count>=3 && hasNextCoordinate()){
					p.setZ(parseNextCoordinate());
				}
							
				return p;
			}

			/* (non-Javadoc)
			 * @see java.util.Iterator#remove()
			 */
			public void remove() {
				//we do not support remove
				
			}
			
		};
		
		return i;
	}
	
	public static void main(String[] args){
	String pointString="10.00 20.00 30.00";
		
		PointParser parser = new PointParser();
		parser.appendPointString(pointString);
		Iterator i = parser.getPointIterator(3);
		Point p = (Point)i.next();	
	}

}
