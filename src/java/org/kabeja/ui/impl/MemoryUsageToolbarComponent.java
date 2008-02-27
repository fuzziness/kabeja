/*
   Copyright 2005 Simon Mieth

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
package org.kabeja.ui.impl;

import java.awt.Dimension;
import java.awt.Font;

import org.kabeja.ui.ApplicationToolBar;
import org.kabeja.ui.ServiceManager;
import org.kabeja.ui.Serviceable;
import org.kabeja.ui.Startable;

import de.miethxml.toolkit.ui.MemoryUsageComponent;


/**
 * @author simon
 *
 *
 *
 */
public class MemoryUsageToolbarComponent implements Serviceable, Startable {
    private boolean interrupted = false;
    private MemoryUsageComponent view;

    public void initialize() {
        view = new MemoryUsageComponent();
        view.setFont(new Font("Serif", Font.PLAIN, 9));
        view.setPreferredSize(new Dimension(60, 24));
        view.setMaximumSize(new Dimension(60, 24));
    }

    public void start() {
        Thread t = new Thread(new Runnable() {
                    public void run() {
                        while (!interrupted) {
                            long total = Runtime.getRuntime().maxMemory();

                            if (total == Long.MAX_VALUE) {
                                total = Runtime.getRuntime().totalMemory();
                            }

                            view.setTotalSize(total);
                            view.setCurrentSize(total -
                                Runtime.getRuntime().freeMemory());

                            try {
                                Thread.sleep(2000);
                            } catch (Exception e) {
                            }
                        }
                    }
                });
        t.start();
    }

    public void stop() {
        interrupted = true;
    }

    public void setServiceManager(ServiceManager manager) {
        this.initialize();

        Object[] objects = manager.getServiceComponents(ApplicationToolBar.SERVICE);

        for (int i = 0; i < objects.length; i++) {
            ((ApplicationToolBar) objects[i]).addAction(view);
        }
    }
}
