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
import org.kabeja.entities.Solid;
import org.kabeja.io.GenerationException;
import org.kabeja.util.Constants;

public class DXF3DFaceGenerator  extends AbstractDXFEntityGenerator {


	protected void generateSubType(DXFSubType subtype, DraftEntity entity, DXFOutput out, DXFGenerationContext context) throws GenerationException {
		Solid solid = (Solid) entity;
		
		if(subtype.getName().equals(Constants.SUBCLASS_MARKER_ENTITY_3DFACE)){	
		  for (int groupCode : subtype.getGroupCodes()) {
			switch (groupCode) {

			case DXFGenerationConstants.DXF_ENITY_TYPE_SUBCLASS_MARKER:
				out.output(100, Constants.SUBCLASS_MARKER_ENTITY_3DFACE);
				break;

			case 10:
				out.output(10, solid.getPoint1().getX());
				break;
			case 20:
				out.output(20, solid.getPoint1().getY());
				break;
			case 30:
				out.output(30, solid.getPoint1().getZ());
				break;
			case 11:
				out.output(11, solid.getPoint2().getX());
				break;
			case 21:
				out.output(21, solid.getPoint2().getY());
				break;
			case 31:
				out.output(31, solid.getPoint2().getZ());
				break;
			case 12:
				out.output(12, solid.getPoint3().getX());
				break;
			case 22:
				out.output(22, solid.getPoint3().getY());
				break;
			case 32:
				out.output(32, solid.getPoint3().getZ());
				break;
			case 13:
				out.output(13, solid.getPoint4().getX());
				break;
			case 23:
				out.output(23, solid.getPoint4().getY());
				break;
			case 33:
				out.output(33, solid.getPoint4().getZ());
				break;
			case 39:
				out.output(39, solid.getThickness());
				break;
			}
		  }
		}
	}
	

	public String getDXFEntityType() {
		return Constants.ENTITY_TYPE_3DFACE;
	}

}
