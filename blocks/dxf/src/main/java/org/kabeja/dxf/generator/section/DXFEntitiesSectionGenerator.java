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

import org.kabeja.DraftDocument;
import org.kabeja.common.DraftEntity;
import org.kabeja.common.Layer;
import org.kabeja.common.Type;
import org.kabeja.dxf.generator.DXFEntityGenerator;
import org.kabeja.dxf.generator.DXFGenerationContext;
import org.kabeja.dxf.generator.DXFOutput;
import org.kabeja.dxf.generator.DXFSectionGenerator;
import org.kabeja.dxf.generator.conf.DXFProfile;
import org.kabeja.io.GenerationException;
import org.kabeja.util.Constants;

public class DXFEntitiesSectionGenerator implements DXFSectionGenerator {

	public String getSectionName() {
		return Constants.SECTION_ENTITIES;
	}

	public void generate(DXFOutput output, DraftDocument doc,
			DXFGenerationContext dxfContext, DXFProfile profile)
			throws GenerationException {
		// iterate over all layers

		for (Layer layer : doc.getLayers()) {
			// get all entity types of the layer
			for (Type<?> type : layer.getEntityTypes()) {
				// check if have a generator for the type
				if (dxfContext.getDXFGeneratorManager().hasDXFEntityGenerator(
						type.getHandle())
						&& profile.hasDXFType(type.getHandle())) {
					DXFEntityGenerator generator = dxfContext
							.getDXFGeneratorManager().getDXFEntityGenerator(
									type.getHandle());

					for (DraftEntity entity : layer.getEntitiesByType(type)) {
						// output the entity
						//generator.generate(output, entity, dxfContext, profile.getDXFType(type));
						generator.generate(output, entity, dxfContext,profile.getDXFType(type.getHandle()));
					}

				}
			}

		}
	}

}
