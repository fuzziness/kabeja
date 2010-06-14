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
 * Created on 07.11.2008
 *
 */
package org.kabeja.dxf.generator.entities;

import org.kabeja.common.DraftEntity;
import org.kabeja.dxf.generator.DXFGenerationContext;
import org.kabeja.dxf.generator.DXFOutput;
import org.kabeja.dxf.generator.conf.DXFSubType;
import org.kabeja.entities.Vertex;
import org.kabeja.io.GenerationException;
import org.kabeja.util.Constants;

public class DXFVertexGenerator extends AbstractDXFEntityGenerator {




	@Override
	protected void generateSubType(DXFSubType subtype, DraftEntity entity,
			DXFOutput out, DXFGenerationContext context)
			throws GenerationException {
	    Vertex vertex = (Vertex)entity;
		
		for (int groupCode : subtype.getGroupCodes()) {
			switch (groupCode) {			
			case 10:
				out.output(10, vertex.getPoint().getX());
				break;
			case 20:
				out.output(20, vertex.getPoint().getY());
				break;
			case 30:
				out.output(30, vertex.getPoint().getZ());
				break;
			case 40:
				out.output(40, vertex.getStartWidth());
				break;
			case 41:
				out.output(41, vertex.getEndWidth());
				break;
			case 42:
				out.output(42, vertex.getBulge());
				break;
			case  100:
				out.output(100, Constants.SUBCLASS_MARKER_ENTITY_VERTEX);
			default:
				super.outputCommonGroupCode(groupCode, vertex, out);
			}
		}
		
	}

}
