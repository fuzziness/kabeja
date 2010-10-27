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
/*
 * Created on 04.10.2009
 *
 */
package org.kabeja.dxf.generator.conf;

public class DXFSubType {
    
      private String name="";
      private int[] groupCodes;
      
      
      public DXFSubType(String name,int[] groupCodes){
          this.name=name;
          this.groupCodes = groupCodes;
      }
      
      
      
      
      
      public String getName(){
          return this.name;
      }
    
      
      public int[] getGroupCodes(){
          return this.groupCodes;
      }
      
}
