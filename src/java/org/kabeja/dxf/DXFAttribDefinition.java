/*
   Copyright 2008 Simon Mieth

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
package org.kabeja.dxf;

public class DXFAttribDefinition extends DXFAttrib implements Cloneable{

	
	protected int textLength=200;
	
	
	public String getType() {
		return DXFConstants.ENTITY_TYPE_ATTDEF;
	}



	
	public DXFAttrib generateDXFAttrib(){

		DXFAttrib attrib=null;
		try {
			attrib = (DXFAttrib)this.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//we have to set the right Text 
		//content
		if(this.isBlockEntity() && this.isConstant() && !this.isInvisible()){
			attrib.setText(this.getText());
		}else if(!this.isBlockEntity()){
			attrib.setText(this.getTag());
		}
		return attrib;
	}




    /**
     * @return the textLength
     */
    public int getTextFieldLength() {
        return textLength;
    }




    /**
     * @param textLength the textLength to set
     */
    public void setTextFieldLength(int textLength) {
        this.textLength = textLength;
    }


	
}
