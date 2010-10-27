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

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.kabeja.DraftDocument;
import org.kabeja.common.Layer;
import org.kabeja.common.Type;
import org.kabeja.dxf.parser.DXFParserBuilder;
import org.kabeja.entities.Solid3D;
import org.kabeja.parser.Parser;

public class Demo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		 Parser parser = DXFParserBuilder.createDefaultParser();
		 try {
			
			DraftDocument doc =parser.parse(new FileInputStream("/home/simon/Desktop/kabeja/test4martin.dxf"), new HashMap());
			Layer layer = doc.getLayer("0");
			List solids = layer.getEntitiesByType(Type.TYPE_3DSOLID);
			Iterator i = solids.iterator();
			while(i.hasNext()){
				Solid3D solid = (Solid3D)i.next();
				List acisData = solid.getACISDATA();
				Iterator lines = acisData.iterator();
				System.out.println("ACISData from="+solid.getID()+">>>>>>>>");
				while(lines.hasNext()){
					System.out.println(lines.next());
				}
				System.out.println("<<<<<<<<<<<<<<");
				
			}
	
			
		} catch (Exception e) {
			 
		}

	}

}
