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
package org.kabeja.dxf.generator.section;

import java.util.Iterator;

import org.kabeja.DraftDocument;
import org.kabeja.dxf.generator.DXFGenerationContext;
import org.kabeja.dxf.generator.DXFOutput;
import org.kabeja.dxf.generator.DXFSectionGenerator;
import org.kabeja.dxf.generator.DXFTableGenerator;
import org.kabeja.dxf.generator.conf.DXFProfile;
import org.kabeja.dxf.generator.conf.DXFSubType;
import org.kabeja.io.GenerationException;
import org.kabeja.util.Constants;

public class DXFTablesSectionGenerator implements DXFSectionGenerator {

	protected final static String[] tables = new String[] { Constants.TABLE_KEY_VPORT, Constants.TABLE_KEY_LTYPE, Constants.TABLE_KEY_LAYER, Constants.TABLE_KEY_STYLE, Constants.TABLE_KEY_VIEW,
			Constants.TABLE_KEY_DIMSTYLE, Constants.TABLE_KEY_UCS, Constants.TABLE_KEY_APPID, Constants.TABLE_KEY_BLOCK_RECORD };

	public String getSectionName() {
		return Constants.SECTION_TABLES;
	}

	public void generate(DXFOutput output, DraftDocument doc, DXFGenerationContext context, DXFProfile profile) throws GenerationException {

		for (DXFSubType subtype : profile.getDXFType(Constants.SECTION_TABLES).getDXFSubTypes()) {
			//we only deal with table entries here
			if (subtype.getName().equals("AcDbTableEntry")) {
				int[] groupCodes = subtype.getGroupCodes();
				for (int i = 0; i < tables.length; i++) {
					if (context.getDXFGeneratorManager().hasDXFTableGenerator(tables[i]) && profile.hasDXFType(tables[i])) {
						boolean first = true;
						for (int index = 0; index < groupCodes.length; index++) {
							switch (groupCodes[index]) {
							case 0:
								if (first) {
									output.output(0, "TABLE");
									first = false;
								} else {
									output.output(0, Constants.TABLES_END);
								}
								break;
							case 2:
								if (profile.hasDXFType(tables[i])) {
									output.output(2, tables[i]);
									DXFTableGenerator tableGenerator = context.getDXFGeneratorManager().getDXFTableGenerator(tables[i]);
									tableGenerator.output(doc, output, context, profile);
								}
								break;

							case 5:
								// handle
								break;

							case 70:
								// max entry count of table
								break;

							}
						}
					}
				}
			}
		}

	}

}
