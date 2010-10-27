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
package org.kabeja.dxf.generator.section;

import org.kabeja.DraftDocument;
import org.kabeja.common.Block;
import org.kabeja.common.DraftEntity;
import org.kabeja.dxf.generator.DXFEntityGenerator;
import org.kabeja.dxf.generator.DXFGenerationContext;
import org.kabeja.dxf.generator.DXFOutput;
import org.kabeja.dxf.generator.DXFSectionGenerator;
import org.kabeja.dxf.generator.conf.DXFProfile;
import org.kabeja.dxf.generator.conf.DXFSubType;
import org.kabeja.dxf.generator.conf.DXFType;
import org.kabeja.io.GenerationException;
import org.kabeja.util.Constants;

public class DXFBlocksSectionGenerator implements DXFSectionGenerator {

	public String getSectionName() {
		return Constants.SECTION_BLOCKS;
	}

	public void generate(DXFOutput output, DraftDocument doc, DXFGenerationContext dxfContext, DXFProfile profile) throws GenerationException {
		for (Block block : doc.getBlocks()) {
			DXFType type = profile.getDXFType(Constants.SECTION_BLOCKS);
			for (DXFSubType subType : type.getDXFSubTypes()) {
				// handle subType BLOCK_ENTRY self
				if (subType.getName().equals("AcDbBlockEntry")) {
					this.outputBlockEntry(subType.getGroupCodes(), block, output, dxfContext);
				} else if (subType.getName().equals("AcDbBlockEntryEnd")) {
					for (DraftEntity entity : block.getEntities()) {
						if (dxfContext.getDXFGeneratorManager().hasDXFEntityGenerator(entity.getType().getHandle())) {
							DXFEntityGenerator entityGenerator = dxfContext.getDXFGeneratorManager().getDXFEntityGenerator(entity.getType().getHandle());
						    if(profile.hasDXFType(entity.getType().getHandle())){
							entityGenerator.generate(output, entity, dxfContext, profile.getDXFType(entity.getType().getHandle()));
						    }
						}
					}
					this.outputBlocksEndEntry(subType.getGroupCodes(), block, output, dxfContext);
				}
			}
		}

	}

	protected void outputBlockEntry(int[] groupCodes, Block block, DXFOutput output, DXFGenerationContext context) throws GenerationException {
		for (int i = 0; i < groupCodes.length; i++) {
			switch (groupCodes[i]) {
			case 0:
				output.output(0, Constants.BLOCK_ENTRY);
				break;
			case 8:
				output.output(8, block.getLayer().getName());
				break;
			case 2:
				output.output(2, block.getName());
				break;
			case 70:
				output.output(70, block.getFlags());
				break;
			case 10:
				output.output(10, block.getReferencePoint().getX());
				break;
			case 20:
				output.output(20, block.getReferencePoint().getY());
				break;
			case 30:
				output.output(30, block.getReferencePoint().getZ());
				break;
			case 3:
				output.output(3, block.getName());
				break;
			case 4:
				output.output(4, block.getDescription());
				break;
			}
		}
	}

	protected void outputBlocksEndEntry(int[] groupCodes, Block block, DXFOutput output, DXFGenerationContext context) throws GenerationException {
		for (int i = 0; i < groupCodes.length; i++) {
			switch (groupCodes[i]) {
			case 0:
				output.output(0, Constants.BLOCK_ENTRY_END);
				break;
			case 8:
				output.output(8, block.getLayer().getName());
				break;
			}
		}

	}

}
