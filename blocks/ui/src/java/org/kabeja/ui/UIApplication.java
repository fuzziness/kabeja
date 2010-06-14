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
 * Created on Dec 8, 2008
 *
 */
package org.kabeja.ui;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

import org.kabeja.Application;
import org.kabeja.processing.ProcessingManager;
import org.kabeja.processing.xml.SAXProcessingManagerBuilder;
import org.kabeja.tools.ConfigHelper;
import org.kabeja.ui.impl.ServiceContainer;
import org.kabeja.ui.xml.SAXServiceContainerBuilder;

public class UIApplication implements Application {

    public void start(Map properties) {
        try {
            ProcessingManager processorManager = SAXProcessingManagerBuilder
                    .buildFromStream(ConfigHelper.getConfigAsStream(this.getClass().getClassLoader(),"conf/process.xml"));
            ServiceContainer sc = SAXServiceContainerBuilder
                    .buildFromStream(new FileInputStream("conf/ui.xml"));
            sc.setProcessingManager(processorManager);
            sc.start(null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void stop() {
       

    }

}
