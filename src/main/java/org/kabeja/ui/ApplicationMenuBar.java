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

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
/**
 * Appliction menubar service provides access to the applications menubar.
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 * 
 *
 */

public interface ApplicationMenuBar {
    public static final String SERVICE = ApplicationMenuBar.class.getName();
    public static final String MENU_ID_FILE = "menu.file";
    public static final String MENU_ID_VIEW = "menu.view";
    public static final String MENU_ID_EDIT = "menu.edit";
    public static final String MENU_ID_HELP = "menu.help";

    public void setMenu(String menuID, JMenu menu);

    public boolean hasMenu(String id);

    public void setAction(String menuID, Action action);

    public void setJMenuItem(String menuID, JMenuItem item);
}
