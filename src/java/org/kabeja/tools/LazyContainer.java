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
package org.kabeja.tools;

public class LazyContainer {
  private int  bitmask=0;
  private Object[] array = new Object[0];	
  
  
  public boolean contains(int index){
         return isSet(index);
  }
  
  
  public Object get(int index){
	  if(index<32 && isSet(index)){
		  return this.array[this.getIndexForBit(index)];
	  }
	  return null;
  }
  
  public void set(Object obj,int index){
	  if(index <32){
		  int pos = getIndexForBit(index);
		  if(isSet(index)){
			  this.array[pos]=obj;
		  }else{
			  //enlarge the array 
			  Object[] newArray = new Object[array.length+1];
			  //left side copy
			  System.arraycopy(array,0, newArray, 0,pos);
			  newArray[pos]=obj;
			  //right side copy
			  if(pos<this.array.length){
				  System.arraycopy(array, pos, newArray,pos+1, array.length-pos);
			  }
			  this.setBit(index,true);
			  this.array = newArray;
		  }
	  }
  }
  
  protected int  getIndexForBit(int index){
	  int pos=-1;
	  for(int i=0;i<=index;i++){
		 if(isSet(i)){
			 pos++;
		 }
	  }
	  pos = isSet(index) ? pos : pos+1;
	  return pos;
  }
  
  protected boolean isSet(int index){
	  int mask = (int) Math.pow(2.0, index);
      return (bitmask & mask) == mask; 
  }

  
  
  
   private void  setBit(int index,boolean enabled){
  	if(enabled){
  		  int v = (int) Math.pow(2, index);
  	      bitmask=bitmask| v;  
  	}else{
  		bitmask=bitmask & ~index;
  	}
  }
  
   
   public int size(){
	   return this.array.length;
   }
   

}
