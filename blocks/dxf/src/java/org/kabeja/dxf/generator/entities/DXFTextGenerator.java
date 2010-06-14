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
 * Created on 28.10.2008
 *
 */
package org.kabeja.dxf.generator.entities;

import org.kabeja.common.DraftEntity;
import org.kabeja.dxf.generator.DXFGenerationConstants;
import org.kabeja.dxf.generator.DXFGenerationContext;
import org.kabeja.dxf.generator.DXFOutput;
import org.kabeja.dxf.generator.conf.DXFSubType;
import org.kabeja.entities.Text;
import org.kabeja.io.GenerationException;
import org.kabeja.util.Constants;

public class DXFTextGenerator extends AbstractDXFEntityGenerator {

	
	protected String defaultTextHeight;

	public String getDXFEntityType() {
		return Constants.ENTITY_TYPE_TEXT;
	}



	protected void outputGroupCode(int groupCode, DXFOutput out, DXFGenerationContext context) throws GenerationException {


	}

	@Override
	protected void generateSubType(DXFSubType subtype, DraftEntity entity,
			DXFOutput output, DXFGenerationContext context)
			throws GenerationException {
		if(subtype.getName().equals(Constants.SUBCLASS_MARKER_ENTITY_TEXT)){
			Text text = (Text)entity;
			 for (int groupCode : subtype.getGroupCodes()) {
					switch (groupCode) {
					case 1:
						output.output(1, text.getText());
						break;
					case 7:
						output.output(7, text.getTextStyle());
						break;
					case 10:
						output.output(10, text.getInsertPoint().getX());
						break;
					case 20:
						output.output(20, text.getInsertPoint().getY());
						break;
					case 30:
						output.output(30, text.getInsertPoint().getZ());
						break;
					case 11:
						output.output(11, text.getAlignmentPoint().getX());
						break;
					case 21:
						output.output(21, text.getAlignmentPoint().getY());
						break;
					case 31:
						output.output(31, text.getAlignmentPoint().getZ());
						break;
					case 39:
						output.output(39, text.getThickness());
						break;
					case 40:
						if (text.getHeight() > 0) {
							output.output(40, text.getHeight());
						} else if (context.hasAttribute(DXFGenerationConstants.DEFAULT_TEXT_HEIGHT)) {
							output.output(40, (String) context.getAttribute(DXFGenerationConstants.DEFAULT_TEXT_HEIGHT));
						}
						break;
					case 41:
						output.output(41, text.getScaleX());
						break;
					case 50:
						output.output(50, text.getRotation());
						break;
					case 51:
						output.output(51, text.getObliqueAngle());
						break;
					case 72:
						output.output(72, text.getAlign());
						break;
					case 73:
						output.output(73, text.getValign());
						break;

					default:
						super.outputCommonGroupCode(groupCode, text, output);

					}
			 }
		}
	}

}
