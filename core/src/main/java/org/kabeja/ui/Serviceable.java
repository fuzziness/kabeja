/*
   Copyright 2008 Simon Mieth

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package org.kabeja.ui;
/**
 * A component which implement this interface get access to
 * the services which are provided by this application.
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 *
 */
public interface Serviceable {
	/**
	 * Set the @see ServiceManager of the application.
	 * @param manager
	 */
    public void setServiceManager(ServiceManager manager);
}
