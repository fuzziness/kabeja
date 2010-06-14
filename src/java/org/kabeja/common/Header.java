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

package org.kabeja.common;

import java.util.Hashtable;
import java.util.Iterator;

import org.kabeja.entities.util.Utils;
import org.kabeja.util.Constants;

/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 * 
 * 
 * 
 */
public class Header {

	private Hashtable variables = new Hashtable();
	private long lastID = -1;
	private Variable idHolder = null;

	public Header() {
		// init last free ID
		Variable v = new Variable(Constants.HEADER_VARIABLE_HANDSEED);
		v.setValue("5", "1");
		this.variables.put(v.getName(), v);
	}

	public void setVariable(Variable v) {
		variables.put(v.getName(), v);
	}

	public boolean hasVariable(String name) {
		return variables.containsKey(name);
	}

	public Variable getVariable(String name) {
		return (Variable) variables.get(name);
	}

	public Iterator getVarialbeIterator() {
		return variables.values().iterator();
	}

	public boolean isFillMode() {
		if (hasVariable(Constants.HEADER_VARIABLE_FILLMODE)
				&& (getVariable(Constants.HEADER_VARIABLE_FILLMODE)
						.getDoubleValue("70") > 0)) {
			return true;
		}

		return false;
	}

	/**
	 * Returns the global linetype scale factor.
	 * 
	 * @return the global scalefactor
	 */
	public double getLinetypeScale() {
		double gscale = 1.0;

		if (hasVariable("$LTSCALE")) {
			gscale = getVariable("$LTSCALE").getDoubleValue("40");
		}

		return gscale;
	}

	public long getLastID() {
		if (lastID == -1) {
			if (hasVariable(Constants.HEADER_VARIABLE_HANDSEED)) {
				idHolder = getVariable(Constants.HEADER_VARIABLE_HANDSEED);
				String id = idHolder.getValue("5");
				lastID = Utils.parseIDString(id);
			}
		}

		return lastID;
	}
	
	public void setLastID(long id){
		lastID = id;
	}
}
