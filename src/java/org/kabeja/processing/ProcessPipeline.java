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

package org.kabeja.processing;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kabeja.DraftDocument;
import org.kabeja.processing.helper.MergeMap;
import org.kabeja.processing.xml.SAXFilterConfig;

/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 * 
 */
public class ProcessPipeline {
	
	private ProcessingManager manager;
	private List postProcessorConfigs = new ArrayList();
	private List saxFilterConfigs = new ArrayList();
	private Generator generator;
	private Map generatorProperties = new HashMap();
	private String name;
	private String description = "";

	public void process(DraftDocument doc, Map context, OutputStream out)
			throws ProcessorException {
		

		// postprocess
		Iterator i = this.postProcessorConfigs.iterator();

		while (i.hasNext()) {
			PostProcessorConfig ppc = (PostProcessorConfig) i.next();
			PostProcessor pp = this.manager.getPostProcessor(ppc
					.getPostProcessorName());

			// backup the default props
			Map oldProps = pp.getProperties();
			// setup the pipepine props
			pp.setProperties(new MergeMap(ppc.getProperties(), context));
			pp.process(doc, context);
			// restore the default props
			pp.setProperties(oldProps);
		}

	}

	/**
	 * @return Returns the manager.
	 */
	public ProcessingManager getProcessorManager() {
		return manager;
	}

	/**
	 * @param manager
	 *            The manager to set.
	 */
	public void setProcessorManager(ProcessingManager manager) {
		this.manager = manager;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	public void prepare() {
	}

	public List getPostProcessorConfigs() {
		return this.postProcessorConfigs;
	}

	public void addSAXFilterConfig(SAXFilterConfig config) {
		this.saxFilterConfigs.add(config);
	}

	public void addPostProcessorConfig(PostProcessorConfig config) {
		this.postProcessorConfigs.add(config);
	}


	public void setGeneratorProperties(Map generatorProperties) {
		this.generatorProperties = generatorProperties;
	}

	public Map getSAXGeneratorProperties(Map generatorProperties) {
		return this.generatorProperties;
	}

	public void setGenerator(Generator generator) {
		this.generator = generator;
	}

	public Generator getGenerator() {
		return this.generator;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
