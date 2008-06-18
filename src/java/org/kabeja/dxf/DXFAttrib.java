/*
   Copyright 2005 Simon Mieth

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

/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 * 
 */
public class DXFAttrib extends DXFText {

	protected boolean blockAttribute = false;
	protected String prompt = "";
	protected String tag = "";

	public String getType() {
		return DXFConstants.ENTITY_TYPE_ATTRIB;
	}

	public boolean isInvisible() {
		return ((this.flags & 1) == 1);
	}

	public boolean isConstant() {
		return ((this.flags & 2) == 2);
	}

	public boolean isVerifiable() {
		return ((this.flags & 4) == 4);
	}

	public boolean isPresent() {
		return ((this.flags & 8) == 8);
	}

	public boolean isBlockAttribute() {
		return blockAttribute;
	}

	public void setBlockAttribute(boolean blockAttribute) {
		this.blockAttribute = blockAttribute;
	}

	public String getPrompt() {
		return prompt;
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public boolean isVisibile() {

		if (this.isBlockEntity()) {
			if (!this.isConstant()) {
				return false;
			} else if (this.isInvisible()) {
				return false;
			} else if (this.doc.getDXFHeader().hasVariable(
					DXFConstants.HEADER_VARIABLE_ATTMODE)
					&& this.doc.getDXFHeader().getVariable(
							DXFConstants.HEADER_VARIABLE_ATTMODE)
							.getIntegerValue("70") == 0) {
				return false;
			}
		}

		return true;
	}

}
