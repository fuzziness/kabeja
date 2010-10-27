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
package org.kabeja.dxf.generator.entities;

import org.kabeja.common.DraftEntity;
import org.kabeja.dxf.generator.DXFEntityGenerator;
import org.kabeja.dxf.generator.DXFGenerationConstants;
import org.kabeja.dxf.generator.DXFGenerationContext;
import org.kabeja.dxf.generator.DXFOutput;
import org.kabeja.dxf.generator.conf.DXFSubType;
import org.kabeja.dxf.generator.conf.DXFType;
import org.kabeja.entities.util.Utils;
import org.kabeja.io.GenerationException;
import org.kabeja.util.Constants;

public abstract class AbstractDXFEntityGenerator implements DXFEntityGenerator {
	
	

	protected void outputCommonGroupCode(int groupCode, DraftEntity entity, DXFOutput out) throws GenerationException {
		switch (groupCode) {
		case 0:
			out.output(0, entity.getType().getHandle());
			break;
		case 5:
			out.output(5,Long.toHexString(entity.getID()).toUpperCase());
			break;
		case 6:
			if (entity.hasLineType()) {
				out.output(6, entity.getLineType().getName());
			}
			break;
		case 8:
			out.output(8, entity.getLayer().getName());
			break;
		case 48:
			out.output(48, entity.getLinetypeScaleFactor());
			break;
		case 60:
			out.output(60, entity.isVisibile() ? 0 : 1);
			break;
		case 62:
			out.output(62, entity.getColor());
			break;
		case 67:
			out.output(67, entity.isModelSpace() ? 0 : 1);
			break;
		case 70:
			out.output(70, entity.getFlags());
			break;
		case 100:
			out.output(100, Constants.SUBCLASS_MARKER_ENTITY);
			break;
		case 210:
			out.output(210, entity.getExtrusion().getX());
			break;
		case 220:
			out.output(220, entity.getExtrusion().getY());
			break;
		case 230:
			out.output(230, entity.getExtrusion().getZ());
			break;
		case 330:
			out.output(330, entity.getOwnerID());
			break;
		case 370:
			out.output(370, entity.getLineWeight());
			break;
		}
	}

	protected abstract void generateSubType(DXFSubType subtype, DraftEntity entity, DXFOutput output, DXFGenerationContext context) throws GenerationException;

	public void generate(DXFOutput output, DraftEntity entity, DXFGenerationContext context, DXFType type) throws GenerationException {
   
		for(DXFSubType subtype:type.getDXFSubTypes()){
			if (subtype.getName().equals(DXFGenerationConstants.SUBTYPE_ACDB_ENTITY)) {
				int[] groupCodes = subtype.getGroupCodes();
				for (int groupCode : groupCodes) {
					outputCommonGroupCode(groupCode, entity, output);
				}
			}else{
				generateSubType(subtype, entity, output, context);
			}
		}
	}

}
