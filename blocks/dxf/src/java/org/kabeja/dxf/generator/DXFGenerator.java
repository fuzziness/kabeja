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
package org.kabeja.dxf.generator;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.kabeja.DraftDocument;
import org.kabeja.dxf.generator.conf.DXFProfile;
import org.kabeja.io.GenerationException;
import org.kabeja.processing.Generator;
import org.kabeja.util.Constants;

public class DXFGenerator implements Generator {

	public static final String MIME_TYPE = "application/cad";

	public static final String DXF_SUFFIX = "dxf";

	public static final String DEFAUL_CONTEXT = "/profiles.xml";

	protected DXFGeneratorManager manager;

	protected DXFGenerationContext generationContext;

	protected Map properties = new HashMap();

	public DXFGenerator(DXFGenerationContext context) {
		this.generationContext = context;
	}

	public DXFGenerator() {
		this(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kabeja.io.Generator#generate(org.kabeja.dxf.DXFDocument,
	 * java.io.OutputStream)
	 */
	public void generate(DraftDocument doc, Map<String, Object> context,
			OutputStream out) throws GenerationException {

		String profileName = null;
		if (context.containsKey(DXFGenerationContext.ATTRIBUTE_PROFILE)) {
			profileName = context.get(DXFGenerationContext.ATTRIBUTE_PROFILE)
					.toString();

		} else if (this.generationContext
				.hasAttribute(DXFGenerationContext.ATTRIBUTE_DEFAULT_PROFILE)) {
			profileName = this.generationContext.getAttribute(
					DXFGenerationContext.ATTRIBUTE_DEFAULT_PROFILE).toString();
		}

		if (this.generationContext.getDXFGeneratorManager().hasDXFProfile(
				profileName)) {
			DXFProfile profile = this.generationContext
					.getDXFGeneratorManager().getDXFProfile(profileName);
			BufferedOutputStream buffered = new BufferedOutputStream(out);

			if (!this.generationContext
					.hasAttribute(DXFGenerationConstants.DEFAULT_TEXT_HEIGHT)) {
				this.generationContext.addAttribute(
						DXFGenerationConstants.DEFAULT_TEXT_HEIGHT, "1.0");

			}

			DXFOutput output = null;
			if (this.generationContext
					.hasAttribute(DXFGenerationConstants.DXF_ENCODING)) {
				output = new DXFOutput(buffered, this.generationContext
						.getAttribute(DXFGenerationConstants.DXF_ENCODING)
						.toString());
			} else {
				output = new DXFOutput(buffered,
						DXFGenerationConstants.DEFAULT_ENCODING);
			}

			this.outputSection(Constants.SECTION_HEADER, profile, output, doc);
			this.outputSection(Constants.SECTION_CLASSES, profile, output, doc);
			this.outputSection(Constants.SECTION_TABLES, profile, output, doc);
			this.outputSection(Constants.SECTION_BLOCKS, profile, output, doc);
			this.outputSection(Constants.SECTION_ENTITIES, profile, output, doc);
			this.outputSection(Constants.SECTION_OBJECTS, profile, output, doc);
			this.outputSection(Constants.SECTION_THUMBNAILIMAGE, profile,
					output, doc);

			output.output(0, "EOF");
			try {
				buffered.flush();
				buffered.close();

			} catch (IOException e) {
				throw new GenerationException("Could not close stream.", e);
			}
		} else {
			throw new GenerationException(
					"No DXF-Profile defined or not found, you have to configure a DXF-Profile.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kabeja.io.Generator#getMimeType()
	 */
	public String getMimeType() {
		return MIME_TYPE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kabeja.io.Generator#getSuffix()
	 */
	public String getSuffix() {

		return DXF_SUFFIX;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kabeja.io.Generator#setProperties(java.util.Map)
	 */
	public void setProperties(Map<String, String> properties) {
		for (Map.Entry<String, String> entry : properties.entrySet()) {
			this.generationContext.addAttribute(entry.getKey(), entry
					.getValue());
		}
	}

	protected void outputSection(String section, DXFProfile profile,
			DXFOutput output, DraftDocument doc) throws GenerationException {

		if (this.generationContext.getDXFGeneratorManager()
				.hasDXFSectionGenerator(section)
				&& profile.hasDXFType(section)) {
			DXFSectionGenerator generator = this.generationContext
					.getDXFGeneratorManager().getDXFSectionGenerator(section);
			output.output(0, Constants.SECTION_START);
			output.output(2, section);
			generator.generate(output, doc, generationContext, profile);
			output.output(0, Constants.SECTION_END);
		}
	}

}
