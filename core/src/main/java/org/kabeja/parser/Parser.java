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

package org.kabeja.parser;

import java.io.InputStream;
import java.util.Map;

import org.kabeja.DraftDocument;


/**
 * This interface describes a Parser, which will parse a specific
 * format and create a @see org.kabeja.DraftDocument from this data.
 * <h2>Lifecycle</h2>
 * <ol>
 *   <li>supportedExtension()</li>
 *   <li>parse(...)</li>
 * </ol>
 *
 * <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 *
 */
public interface Parser{
	
	
	/**
	 * Parse the given file.
	 * @param file the file to parse
	 * @throws ParseException
	 */
	
    public abstract DraftDocument parse(InputStream in,Map<String,Object> properties) throws ParseException;

    
    
    /**
     * Parse the given inputstream
     * @param input
     * @param encoding
     * @throws ParseException
     */
    public abstract void parse(InputStream input, DraftDocument doc,Map<String,Object> properties)
        throws ParseException;
    
   
    /**
     * 
     * @param extension
     * @return
     */

    public abstract boolean supportedExtension(String extension);

    /**
     * Gets the name of the parser.
     * @return
     */
    public abstract String getName();
}
