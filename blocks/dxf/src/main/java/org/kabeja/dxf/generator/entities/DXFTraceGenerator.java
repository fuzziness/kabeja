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
 * Created on 23.11.2008
 *
 */
package org.kabeja.dxf.generator.entities;

import org.kabeja.common.DraftEntity;
import org.kabeja.dxf.generator.DXFGenerationConstants;
import org.kabeja.dxf.generator.DXFGenerationContext;
import org.kabeja.dxf.generator.DXFOutput;
import org.kabeja.dxf.generator.conf.DXFSubType;
import org.kabeja.entities.Trace;
import org.kabeja.io.GenerationException;
import org.kabeja.util.Constants;

public class DXFTraceGenerator extends DXFSolidGenerator {

	protected void generateSubType(DXFSubType subtype, DraftEntity entity, DXFOutput out, DXFGenerationContext context) throws GenerationException {
		Trace trace = (Trace) entity;
		for (int groupCode : subtype.getGroupCodes()) {
			switch (groupCode) {
			case DXFGenerationConstants.DXF_ENITY_TYPE_SUBCLASS_MARKER_1:
				out.output(100, Constants.SUBCLASS_MARKER_ENTITY_TRACE);
				break;
			default:
				super.generateSubType(subtype, entity, out, context);
			}
		}
	}

	public String getDXFEntityType() {
		return Constants.ENTITY_TYPE_TRACE;
	}

}
