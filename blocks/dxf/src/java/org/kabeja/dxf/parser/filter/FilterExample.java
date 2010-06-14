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

package org.kabeja.dxf.parser.filter;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import org.kabeja.DraftDocument;
import org.kabeja.dxf.parser.DXFParser;
import org.kabeja.dxf.parser.DXFParserBuilder;


public class FilterExample {
    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            DXFParser parser = (DXFParser) DXFParserBuilder.createDefaultParser();

            //test 
            DXFStreamFilter filter = new DXFStreamLayerFilter();
            Map p = new HashMap();
            p.put("layers.include", args[0]);
            filter.setProperties(p);
            parser.addDXFStreamFilter(filter);
          

            DraftDocument doc =  parser.parse(new FileInputStream(args[1]),new HashMap());

            //do something with the doc
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
