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
import org.kabeja.common.Layer;
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

public class DXFLayerTableGenerator implements DXFTableGenerator {

	protected String tableID;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kabeja.dxf.generator.DXFTableGenerator#getTableType()
	 */
	public String getTableType() {

		return Constants.TABLE_KEY_LAYER;
	}

	public void output(DraftDocument doc, DXFOutput output, DXFGenerationContext dxfContext, DXFProfile type) throws GenerationException {

		if (type.hasDXFType(Constants.TABLE_KEY_LAYER)) {
			DXFType tableType = type.getDXFType(Constants.TABLE_KEY_LAYER);
			this.tableID = Utils.generateNewID(doc);
			for (DXFSubType subType : tableType.getDXFSubTypes()) {
				if (subType.getName().equals("AcDbLayerEntry")) {
					int[] groupCodes = subType.getGroupCodes();
					for (Layer layer : doc.getLayers()) {
						for (int i = 0; i < groupCodes.length; i++) {
							outputLayer(layer, groupCodes[i], output);
						}
					}
				}

			}

		}
	}

	protected void outputLayer(Layer layer, int groupCode, DXFOutput output) throws GenerationException {
		switch (groupCode) {
		case 0:
			output.output(0, Constants.TABLE_KEY_LAYER);
			break;
		case 2:
			output.output(2, layer.getName());
			break;
		case 5:

			output.output(5, layer.getID());
			break;
		case 6:
			output.output(6, layer.getLineType().getName());
			break;
		case 62:
			output.output(62, layer.getColor());
			break;

		case 70:
			output.output(70, layer.getFlags());
			break;
		case 100:
			output.output(100, Constants.SUBCLASS_MARKER_TABLE_RECORD);
			break;
		case DXFGenerationConstants.DXF_ENITY_TYPE_SUBCLASS_MARKER_1:
			output.output(100, Constants.SUBCLASS_MARKER_TABLE_RECORD_LAYER);
			break;
		case 290:
			if (!layer.isPlottable()) {
				output.output(290, 0);
			} else {
				output.output(290, 1);
			}
			break;

		case 330:
			output.output(330, tableID);
			break;

		case 370:
			output.output(370, layer.getLineWeight());
			break;
		case 390:
			output.output(390, layer.getPlotStyle());
			break;
		}

	}

}
