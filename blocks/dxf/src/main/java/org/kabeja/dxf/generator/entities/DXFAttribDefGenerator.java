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
 * Created on 02.11.2008
 *
 */
package org.kabeja.dxf.generator.entities;

import org.kabeja.dxf.generator.DXFGenerationConstants;
import org.kabeja.dxf.generator.DXFGenerationContext;
import org.kabeja.dxf.generator.DXFOutput;
import org.kabeja.entities.AttribDefinition;
import org.kabeja.io.GenerationException;
import org.kabeja.util.Constants;

public class DXFAttribDefGenerator extends DXFTextGenerator {

	protected AttribDefinition attribDef;

	public String getDXFEntityType() {

		return Constants.ENTITY_TYPE_ATTDEF;
	}

	protected void outputGroupCode(int groupCode, DXFOutput out, DXFGenerationContext context) throws GenerationException {
		switch (groupCode) {
		case DXFGenerationConstants.DXF_ENITY_TYPE_SUBCLASS_MARKER_1:
			out.output(100, Constants.SUBCLASS_MARKER_ENTITY_ATTRIB_DEF);
			break;
		case 3:
			out.output(3, attribDef.getPrompt());
			break;
		case 2:
			out.output(2, attribDef.getTag());
			break;
		case 70:
			out.output(70, attribDef.getFlags());
			break;
		case 73:
			out.output(73, attribDef.getTextFieldLength());
			break;
		case 74:
			out.output(74, attribDef.getValign());
			break;
		default:
			super.outputGroupCode(groupCode, out, context);

		}

	}

}
