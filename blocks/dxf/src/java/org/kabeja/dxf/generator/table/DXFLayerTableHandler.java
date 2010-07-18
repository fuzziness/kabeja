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
import org.kabeja.dxf.generator.DXFGenerationContext;
import org.kabeja.dxf.generator.DXFOutput;
import org.kabeja.dxf.generator.DXFTableGenerator;
import org.kabeja.dxf.generator.conf.DXFProfile;
import org.kabeja.io.GenerationException;
import org.kabeja.util.Constants;

public class DXFLayerTableHandler implements DXFTableGenerator {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kabeja.dxf.generator.DXFTableGenerator#getTableType()
	 */
	public String getTableType() {

		return Constants.TABLE_KEY_LAYER;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.kabeja.dxf.generator.DXFTableGenerator#output(org.kabeja.dxf.DXFDocument
	 * , org.kabeja.dxf.generator.DXFOutput)
	 */
	public void output(DraftDocument doc, DXFOutput output, DXFGenerationContext context, DXFProfile type) throws GenerationException {
		output.output(70, doc.getLayers().size());
		for (Layer layer : doc.getLayers()) {
			output.output(2, layer.getName());
			output.output(70, layer.getFlags());
			output.output(62, layer.getColor());
			output.output(6, layer.getLineType().getName());
			if (!layer.isPlottable()) {
				output.output(290, 0);
			}
			output.output(370, layer.getLineWeight());
			output.output(390, layer.getPlotStyle());
		}
	}

}
