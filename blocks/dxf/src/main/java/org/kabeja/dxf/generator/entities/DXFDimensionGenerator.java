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
import org.kabeja.dxf.generator.DXFGenerationContext;
import org.kabeja.dxf.generator.DXFOutput;
import org.kabeja.dxf.generator.conf.DXFSubType;
import org.kabeja.entities.Dimension;
import org.kabeja.io.GenerationException;
import org.kabeja.util.Constants;

public class DXFDimensionGenerator extends AbstractDXFEntityGenerator {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.kabeja.dxf.generator.entities.AbstractDXFEntityGenerator#outputGroupCode
	 * (int, org.kabeja.dxf.generator.DXFOutput,
	 * org.kabeja.dxf.generator.DXFGenerationContext)
	 */
	protected void generateSubType(DXFSubType subtype, DraftEntity entity, DXFOutput out, DXFGenerationContext context) throws GenerationException {
		Dimension dimension = (Dimension) entity;
		for (int groupCode : subtype.getGroupCodes()) {
			switch (groupCode) {
			case 2:
				out.output(2, dimension.getDimensionBlock());
				break;
			case 3:
				out.output(3, dimension.getDimensionStyleID());
				break;
			case 10:
				out.output(10, dimension.getReferencePoint().getX());
				break;
			case 20:
				out.output(20, dimension.getReferencePoint().getY());
				break;
			case 30:
				out.output(30, dimension.getReferencePoint().getZ());
				break;
			case 11:
				out.output(11, dimension.getTextPoint().getX());
				break;
			case 21:
				out.output(21, dimension.getTextPoint().getY());
				break;
			case 31:
				out.output(31, dimension.getTextPoint().getZ());
				break;
			case 12:
				out.output(12, dimension.getInsertPoint().getX());
				break;
			case 22:
				out.output(22, dimension.getInsertPoint().getY());
				break;
			case 32:
				out.output(32, dimension.getInsertPoint().getZ());
				break;

			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kabeja.dxf.generator.DXFEntityGenerator#getDXFEntityType()
	 */
	public String getDXFEntityType() {
		return Constants.ENTITY_TYPE_DIMENSION;
	}

}
