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
import org.kabeja.dxf.generator.conf.DXFProfile;
import org.kabeja.dxf.generator.conf.DXFSubType;
import org.kabeja.dxf.generator.conf.DXFType;
import org.kabeja.entities.Polyline;
import org.kabeja.entities.Vertex;
import org.kabeja.io.GenerationException;
import org.kabeja.util.Constants;

public class DXFPolylineGenerator extends AbstractDXFEntityGenerator {

	public String getDXFEntityType() {
		return Constants.ENTITY_TYPE_POLYLINE;
	}

	@Override
	protected void generateSubType(DXFSubType subtype, DraftEntity entity, DXFOutput output, DXFGenerationContext context) throws GenerationException {
		Polyline pline = (Polyline) entity;
		if (subtype.getName().equals(Constants.SUBCLASS_MARKER_ENTITY_POLYLINE)) {
			for (int groupCode : subtype.getGroupCodes()) {
				switch (groupCode) {
				case 10:
					output.output(10, 0);
					break;
				case 20:
					output.output(20, 0);
					break;
				case 30:
					output.output(30, pline.getElevation().getZ());
					break;
				case 39:
					output.output(39, pline.getThickness());
					break;
				case 40:
					output.output(40, pline.getStartWidth());
					break;
				case 41:
					output.output(41, pline.getEndWidth());
					break;
				case 66:
					output.output(66, 1);
					break;
				case 75:
					if (pline.isCubicSpline()) {
						output.output(75, 6);
					} else if (pline.isQuadSpline()) {
						output.output(75, 5);

					} else {
						output.output(75, 0);
					}
					break;
				default:
					super.outputCommonGroupCode(groupCode, pline, output);
				}

			}

		} else if (subtype.getName().equals(Constants.SUBCLASS_MARKER_ENTITY_VERTEX)) {
         
			for (Vertex vertex : pline.getVertices()) {
				for (int groupCode : subtype.getGroupCodes()) {
					switch (groupCode) {			
					case 10:
						output.output(10, vertex.getPoint().getX());
						break;
					case 20:
						output.output(20, vertex.getPoint().getY());
						break;
					case 30:
						output.output(30, vertex.getPoint().getZ());
						break;
					case 40:
						output.output(40, vertex.getStartWidth());
						break;
					case 41:
						output.output(41, vertex.getEndWidth());
						break;
					case 42:
						output.output(42, vertex.getBulge());
						break;
					case  100:
						output.output(100, Constants.SUBCLASS_MARKER_ENTITY_VERTEX);
					default:
						super.outputCommonGroupCode(groupCode, vertex, output);
					}
				}
			}
			output.output(0, Constants.END_SEQUENCE);
		}

	}

}
