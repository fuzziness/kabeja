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
 * Created on 04.11.2008
 *
 */
package org.kabeja.dxf.generator.entities;

import org.kabeja.common.DraftEntity;
import org.kabeja.dxf.generator.DXFEntityGenerator;
import org.kabeja.dxf.generator.DXFGenerationConstants;
import org.kabeja.dxf.generator.DXFGenerationContext;
import org.kabeja.dxf.generator.DXFOutput;
import org.kabeja.dxf.generator.conf.DXFSubType;
import org.kabeja.entities.Attrib;
import org.kabeja.entities.Insert;
import org.kabeja.io.GenerationException;
import org.kabeja.util.Constants;

public class DXFInsertGenerator extends AbstractDXFEntityGenerator {

	public String getDXFEntityType() {
		return Constants.ENTITY_TYPE_INSERT;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.kabeja.dxf.generator.entities.AbstractDXFEntityGenerator#output(int,
	 * org.kabeja.dxf.generator.DXFOutput)
	 */
	protected void generateSubType(DXFSubType subtype, DraftEntity entity, DXFOutput out, DXFGenerationContext context) throws GenerationException {
		Insert insert = (Insert) entity;
		for (int groupCode : subtype.getGroupCodes()) {
			switch (groupCode) {
			case DXFGenerationConstants.DXF_CHILDREN_INSERT_MARK:
				if (context.getDXFGeneratorManager().hasDXFEntityGenerator(Constants.ENTITY_TYPE_ATTRIB)) {

					DXFEntityGenerator attribGenerator = context.getDXFGeneratorManager().getDXFEntityGenerator(Constants.ENTITY_TYPE_ATTRIB);

			    	for(Attrib a:insert.getAttributes()){
						attribGenerator.generate(out, a, context, null);
					}
				}

				break;

			case 2:
				out.output(2, insert.getBlockName());
				break;
			case 10:
				out.output(10, insert.getInsertPoint().getX());
				break;
			case 20:
				out.output(20, insert.getInsertPoint().getY());
				break;
			case 30:
				out.output(30, insert.getInsertPoint().getZ());
				break;
			case 41:
				out.output(41, insert.getScaleX());
				break;
			case 42:
				out.output(42, insert.getScaleY());
				break;
			case 43:
				out.output(43, insert.getScaleZ());
				break;
			case 44:
				out.output(44, insert.getColumnSpacing());
				break;
			case 45:
				out.output(45, insert.getRowSpacing());
				break;
			case 50:
				out.output(50, insert.getRotate());
				break;
			case 66:
				out.output(66, insert.hasAttributes() ? 1 : 0);
				break;
			case 70:
				out.output(70, insert.getColumns());
				break;
			case 71:
				out.output(71, insert.getRows());
				break;

			}

		}
	}

}
