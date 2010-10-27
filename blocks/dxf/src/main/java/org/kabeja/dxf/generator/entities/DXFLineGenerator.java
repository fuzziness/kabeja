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
 * Created on 16.11.2008
 *
 */
package org.kabeja.dxf.generator.entities;

import org.kabeja.common.DraftEntity;
import org.kabeja.dxf.generator.DXFGenerationConstants;
import org.kabeja.dxf.generator.DXFGenerationContext;
import org.kabeja.dxf.generator.DXFOutput;
import org.kabeja.dxf.generator.conf.DXFSubType;
import org.kabeja.entities.Line;
import org.kabeja.io.GenerationException;
import org.kabeja.util.Constants;

public class DXFLineGenerator extends AbstractDXFEntityGenerator {

	protected void generateSubType(DXFSubType subtype, DraftEntity entity, DXFOutput out, DXFGenerationContext context) throws GenerationException {
		Line line = (Line) entity;
		for (int groupCode : subtype.getGroupCodes()) {
			switch (groupCode) {
			case 10:
				out.output(10, line.getStartPoint().getX());
				break;
			case 20:
				out.output(20, line.getStartPoint().getY());
				break;
			case 30:
				out.output(30, line.getStartPoint().getZ());
				break;
			case 11:
				out.output(11, line.getEndPoint().getX());
				break;
			case 21:
				out.output(21, line.getEndPoint().getY());
				break;
			case 31:
				out.output(31, line.getEndPoint().getZ());
				break;
			case 39:
				out.output(39, line.getThickness());
				break;
			case DXFGenerationConstants.DXF_ENITY_TYPE_SUBCLASS_MARKER:
				out.output(100, Constants.SUBCLASS_MARKER_ENTITY_LINE);
				break;

			}
		}
	}

	public String getDXFEntityType() {

		return Constants.ENTITY_TYPE_LINE;
	}

}
