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

package org.kabeja.entities;

import org.kabeja.common.Type;
import org.kabeja.util.Constants;

/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 * 
 */
public class Attrib extends Text {

	protected boolean blockAttribute = false;
	protected String tag = "";

	
	private static final int LAZY_INDEX_PROMPT=10;
	
	public Type<?> getType() {
		return Type.TYPE_ATTRIB;
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
		if(this.lazyContainer.contains(LAZY_INDEX_PROMPT)){
			return (String)this.lazyContainer.get(LAZY_INDEX_PROMPT);
		}
		return "";
	}

	public void setPrompt(String prompt) {		
	    this.lazyContainer.set(prompt, LAZY_INDEX_PROMPT);
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
			} else if (this.doc.getHeader().hasVariable(
					Constants.HEADER_VARIABLE_ATTMODE)
					&& this.doc.getHeader().getVariable(
							Constants.HEADER_VARIABLE_ATTMODE)
							.getIntegerValue("70") == 0) {
				return false;
			}
		}

		return true;
	}

}
