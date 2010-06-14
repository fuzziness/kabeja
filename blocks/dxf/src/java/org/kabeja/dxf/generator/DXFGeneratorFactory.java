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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

import org.kabeja.dxf.generator.conf.SAXDXFGenerationContextBuilder;
import org.kabeja.processing.Generator;
import org.kabeja.processing.InstanceFactory;

public class DXFGeneratorFactory  implements InstanceFactory{

	private String DEFAULT_PROFILES_FILE="blocks/dxf/conf/profiles.xml";

	
   
    
    protected static DXFGenerationContext getDXFGenerationContext(
            InputStream in) {
        SAXDXFGenerationContextBuilder builder = new SAXDXFGenerationContextBuilder();
        return builder.buildDXFGenerationContext(in);
    }

    public static Generator createStreamGenerator(InputStream config) {

        DXFGenerationContext context = getDXFGenerationContext(config);

        DXFGenerator generator = new DXFGenerator(context);

        return generator;
    }


	public Object createInstance(Map properties) {
		  InputStream in = null;
	        File f = new File(DEFAULT_PROFILES_FILE);
	        try {
	            if (f.exists()) {
	                in = new FileInputStream(f);
	            
	            } else {
	                in = DXFGeneratorFactory.class.getResourceAsStream("/"
	                        + DEFAULT_PROFILES_FILE);
	            }
	        } catch (FileNotFoundException e) {

	            e.printStackTrace();
	        }

	        return createStreamGenerator(in);
	}

}
