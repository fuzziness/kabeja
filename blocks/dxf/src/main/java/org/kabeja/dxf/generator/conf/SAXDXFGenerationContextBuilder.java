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
 * Created on 09.11.2008
 *
 */
package org.kabeja.dxf.generator.conf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.kabeja.dxf.generator.DXFEntityGenerator;
import org.kabeja.dxf.generator.DXFGenerationContext;
import org.kabeja.dxf.generator.DXFGeneratorManagerImpl;
import org.kabeja.tools.ConfigHelper;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class SAXDXFGenerationContextBuilder extends org.xml.sax.helpers.DefaultHandler {

	private static final String CONFIG_HANDLERS="blocks/dxf/conf/handlers.properties";
	public final static String NAMESPACE = "http://kabeja.org/config/profile/1.0";

	public final static String ELEMEN_PROFILES = "profiles";

	public final static String ELEMEN_PROFILE = "profile";

	public final static String ELEMENT_TYPE = "type";

	public final static String ELEMENT_SUBTYPE = "subtype";
	
	public final static String ELEMENT_TYPEHANDLER = "typehandler";
	public final static String ELEMENT_TYPEHANDLERS = "typehandlers";

	public final static String ATTRIBUTE_NAME = "name";
	public final static String ATTRIBUTE_CLASS = "class";
	public final static String ATTRIBUTE_ID = "id";
	public final static String ATTRIBUTE_REF_ID = "ref-id";

	public final static String ATTRIBUTE_ATTRIBUTE = "type";

	public final static String ATTRIBUTE_GROUPCODES = "groupCodes";
	public final static String ATTRIBUTE_DEFAULT = "default";

	protected DefaultDXFGenerationContext context;

	protected DXFType currentType;

	protected DXFProfile currentProfile;

	protected Map<String,Object> references = new HashMap<String,Object>();

	protected DXFGeneratorManagerImpl manager;
	
	
	
	public DXFGenerationContext buildDXFGenerationContext(InputStream in) {
        this.initialize();
		context = new DefaultDXFGenerationContext(this.manager);

		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);

			XMLReader saxparser = factory.newSAXParser().getXMLReader();

			saxparser.setContentHandler(this);
			saxparser.parse(new InputSource(in));
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		return context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
	 * java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
     
		try {
			if (uri.equals(NAMESPACE)) {
				if (localName.equals(ELEMENT_SUBTYPE)) {
					if (isReferenced(attributes)) {
						DXFSubType subType = (DXFSubType) getReferencedObject(attributes);
						currentType.addDXFSubType(subType);
					} else {
						String[] codes = attributes.getValue(ATTRIBUTE_GROUPCODES).split(",");
						int[] groupCodes = new int[codes.length];
						for (int i = 0; i < codes.length; i++) {
							groupCodes[i] = Integer.parseInt(codes[i]);
						}
						DXFSubType subType = new DXFSubType(attributes.getValue(ATTRIBUTE_NAME), groupCodes);
						currentType.addDXFSubType(subType);
						this.updateReference(attributes, subType);
					}
				} else if (localName.equals(ELEMENT_TYPE)) {
					currentType = new DXFType(attributes.getValue(ATTRIBUTE_NAME));
					currentProfile.addDXFType(currentType);
				} else if (localName.equals(ELEMEN_PROFILE)) {
					currentProfile = new DXFProfile(attributes.getValue(ATTRIBUTE_NAME));
					this.manager.addDXFProfile(currentProfile);
				} else if (localName.equals(ELEMEN_PROFILES)) {
                    String defaultProfile = attributes.getValue(ATTRIBUTE_DEFAULT);
                    if(defaultProfile != null){
                    	this.context.addAttribute(DXFGenerationContext.ATTRIBUTE_DEFAULT_PROFILE, defaultProfile);
                    }
				}else if (localName.equals(ELEMENT_TYPEHANDLER)){
					String clazz = attributes.getValue(ATTRIBUTE_CLASS);
					String type = attributes.getValue(ATTRIBUTE_NAME);
					Object obj = this.getClass().getClassLoader().loadClass(clazz).newInstance();
					this.manager.addHandler(type, obj);
				}
			}
		} catch (Exception e) {
			throw new SAXException("Could not create context for dxf generation.",e);
		}

	}

	protected int[] parseGroupCodes(String groupCodes) throws Exception {
		if (groupCodes.trim().length() > 0) {
			String[] elements = groupCodes.split(",");
			int[] codes = new int[elements.length];
			for (int i = 0; i < elements.length; i++) {
				codes[i] = Integer.parseInt(elements[i]);
			}

			return codes;
		} else {
			return new int[0];
		}
	}


	protected boolean isReferenced(Attributes atts) throws Exception{
		String id = atts.getValue(ATTRIBUTE_REF_ID);
		if (id != null) {
			if(this.references.containsKey(id)){
				return true;
			}else{
				throw new  SAXException("The reference:"+id+" must be declared before");
			}
		}
		return false;
	}

	protected void updateReference(Attributes atts, Object source) {
		String id = atts.getValue(ATTRIBUTE_ID);
		if (id != null) {
			this.references.put(id, source);
		}
	}

	protected Object getReferencedObject(Attributes atts) {
		String id = atts.getValue(ATTRIBUTE_REF_ID);

		if (id != null) {
			return this.references.get(id);
		}
		return null;
	}

	
   protected void initialize(){
	   this.manager = new DXFGeneratorManagerImpl();  	 
   }
	
}
