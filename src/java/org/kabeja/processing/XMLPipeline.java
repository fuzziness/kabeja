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
import org.kabeja.io.GenerationException;
import org.kabeja.processing.helper.MergeMap;
import org.kabeja.processing.xml.SAXFilterConfig;
import org.kabeja.xml.SAXFilter;
import org.kabeja.xml.SAXGenerator;
import org.kabeja.xml.SAXSerializer;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class XMLPipeline implements Generator {

	private Map serializerProperties = new HashMap();

	private SAXSerializer serializer;

	private SAXGenerator generator;
	private List saxFilterConfigs = new ArrayList();
	private ProcessingManager manager;
	private Map properties = new HashMap();
	

	public void generate(DraftDocument doc, Map context, OutputStream out)
			throws GenerationException {
		
		
		ContentHandler handler = null;
		List saxFilterProperties = new ArrayList();

		// setup saxfilters
		if (this.saxFilterConfigs.size() > 0) {
			Iterator i = saxFilterConfigs.iterator();
			SAXFilterConfig sc = (SAXFilterConfig) i.next();
			SAXFilter first = this.manager.getSAXFilter(sc.getFilterName());
			saxFilterProperties
					.add(new MergeMap(first.getProperties(), context));

			handler = first;
			first.setProperties(sc.getProperties());

			while (i.hasNext()) {
				sc = (SAXFilterConfig) i.next();
				SAXFilter f = this.manager.getSAXFilter(sc.getFilterName());
				first.setContentHandler(f);
				saxFilterProperties.add(f.getProperties());
				f.setProperties(sc.getProperties());
				first = f;
			}
			first.setContentHandler(this.serializer);
		} else {
			// no filter
			handler = this.serializer;
		}

		Map oldProbs = this.serializer.getProperties();

		this.serializer.setProperties(new MergeMap(oldProbs, new MergeMap(
				this.serializerProperties, context)));

		// invoke the filter and serializer
		this.serializer.setOutput(out);

		try {
			Map oldGenProps = this.generator.getProperties();
			this.generator.setProperties(this.properties);
			this.generator.generate(doc, handler, context);
			// restore the old props
			this.generator.setProperties(oldGenProps);
		} catch (SAXException e) {
			throw new GenerationException(e);
		}

		// restore the serializer properties
		this.serializer.setProperties(oldProbs);

		// restore the filter properties
		for (int x = 0; x < saxFilterProperties.size(); x++) {
			SAXFilterConfig sc = (SAXFilterConfig) saxFilterConfigs.get(x);
			this.manager.getSAXFilter(sc.getFilterName()).setProperties(
					(Map) saxFilterProperties.get(x));
		}

	}

	public String getMimeType() {
		return this.serializer.getMimeType();
	}

	public String getSuffix() {	
		return this.serializer.getSuffix();
	}

	public void setProperties(Map properties) {

	}

	/**
	 * @return Returns the serializer.
	 */
	public SAXSerializer getSAXSerializer() {
		return serializer;
	}

	/**
	 * @param serializer
	 *            The serializer to set.
	 */
	public void setSAXSerializer(SAXSerializer serializer) {
		this.serializer = serializer;
	}

	public void setSAXGenerator(SAXGenerator generator){
		this.generator=generator;
	}
	
	
	
	/**
	 * @return Returns the serializerProperties.
	 */
	public Map getSerializerProperties() {
		return serializerProperties;
	}

	/**
	 * @param serializerProperties
	 *            The serializerProperties to set.
	 */
	public void setSAXSerializerProperties(Map serializerProperties) {
		this.serializerProperties = serializerProperties;
	}

}
