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
package org.kabeja.acis;

import java.io.InputStream;
import java.util.Map;

import org.kabeja.DraftDocument;
import org.kabeja.parser.ParseException;
import org.kabeja.parser.Parser;

public class SATParser implements Parser{

	protected DraftDocument doc;

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public DraftDocument parse(InputStream in, Map properties)
			throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	public void parse(InputStream input, DraftDocument doc, Map properties)
			throws ParseException {
		// TODO Auto-generated method stub
		
	}

	public boolean supportedExtension(String extension) {
		// TODO Auto-generated method stub
		return false;
	}
	

	
}
