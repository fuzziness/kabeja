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
package org.kabeja.dxf.generator.table;

import org.kabeja.DraftDocument;
import org.kabeja.common.LineType;
import org.kabeja.dxf.generator.DXFGenerationConstants;
import org.kabeja.dxf.generator.DXFGenerationContext;
import org.kabeja.dxf.generator.DXFOutput;
import org.kabeja.dxf.generator.DXFTableGenerator;
import org.kabeja.dxf.generator.conf.DXFProfile;
import org.kabeja.dxf.generator.conf.DXFSubType;
import org.kabeja.dxf.generator.conf.DXFType;
import org.kabeja.entities.util.Utils;
import org.kabeja.io.GenerationException;
import org.kabeja.util.Constants;

public class DXFLineTypeTableGenerator implements DXFTableGenerator {

	protected String tableID;

	public String getTableType() {
		return Constants.TABLE_KEY_LTYPE;
	}

	public void output(DraftDocument doc, DXFOutput output, DXFGenerationContext context, DXFProfile type) throws GenerationException {

		if (type.hasDXFType(Constants.TABLE_KEY_LAYER)) {
			DXFType tableType = type.getDXFType(Constants.TABLE_KEY_LAYER);
			this.tableID = Utils.generateNewID(doc);
			for (DXFSubType subType : tableType.getDXFSubTypes()) {
				if (subType.getName().equals("AcDbLTypeEntry")) {
					int[] groupCodes = subType.getGroupCodes();
					for (LineType ltype : doc.getLineTypes()) {
						for (int i = 0; i < groupCodes.length; i++) {
							outputLinetypeEntry(ltype, groupCodes[i], output);
						}
					}
				}

			}
		}

	}

	protected void outputLinetypeEntry(LineType ltype, DXFProfile profile, DXFOutput output) throws GenerationException {
		// int[] groupCodes = profile.getGroupCodeProfile();
		// for (int i = 0; i < groupCodes.length; i++) {
		// outputLinetypeEntry(ltype, groupCodes[i], output);
		// }
	}

	protected void outputLinetypeEntry(LineType ltype, int groupCode, DXFOutput out) throws GenerationException {
		switch (groupCode) {
		case 0:
			out.output(0, Constants.TABLE_KEY_LTYPE);
			break;
		case 2:
			out.output(2, ltype.getName());
			break;
		case 3:
			out.output(3, ltype.getDescritpion());
			break;
		case 40:
			out.output(40, ltype.getPatternLength());
			break;
		case 70:
			out.output(70, ltype.getFlags());
			break;
		case 72:
			out.output(72, ltype.getAlignment());
			break;
		case 73:
			out.output(73, ltype.getSegmentCount());
			break;
		case 100:
			out.output(100, Constants.SUBCLASS_MARKER_TABLE_RECORD);
			break;
		case DXFGenerationConstants.DXF_ENITY_TYPE_SUBCLASS_MARKER_1:
			out.output(100, Constants.SUBCLASS_MARKER_TABLE_RECORD_LINETYPE);
			break;

		case DXFGenerationConstants.DXF_CHILDREN_INSERT_MARK:

			double[] pattern = ltype.getPattern();
			for (int p = 0; p < pattern.length; p++) {
				out.output(49, pattern[p]);
			}
			break;
		}

	}
}
