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
import org.kabeja.dxf.generator.DXFGenerationContext;
import org.kabeja.dxf.generator.DXFOutput;
import org.kabeja.dxf.generator.conf.DXFSubType;
import org.kabeja.entities.Point;
import org.kabeja.io.GenerationException;
import org.kabeja.util.Constants;

public class DXFPointGenerator extends AbstractDXFEntityGenerator {

	protected Point point;

	public String getDXFEntityType() {

		return Constants.ENTITY_TYPE_POINT;
	}

	public void endGeneration() {
		this.point = null;

	}

	protected void prepareGeneration(DraftEntity entity) {
		this.point = (Point) entity;

	}

	protected String getEntityTypeSubclassMarker() {

		return Constants.SUBCLASS_MARKER_ENTITY_POINT;
	}

	protected void generateSubType(DXFSubType subtype, DraftEntity entity, DXFOutput out, DXFGenerationContext context) throws GenerationException {
		Point point = (Point) entity;
		for (int groupCode : subtype.getGroupCodes()) {
			switch (groupCode) {
			case 10:
				out.output(10, point.getX());
				break;
			case 20:
				out.output(20, point.getY());
				break;
			case 30:
				out.output(30, point.getZ());
				break;
			case 39:
				out.output(39, point.getThickness());
				break;
			case 50:
				out.output(50, point.getAngle());
				break;

			}
		}
	}

}
