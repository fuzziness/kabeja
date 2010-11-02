/*
 * Created on Dec 8, 2008
 *
 */
package org.kabeja.ui;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.kabeja.processing.ProcessingManager;
import org.kabeja.tools.Application;
import org.kabeja.tools.SAXProcessingManagerBuilder;
import org.kabeja.ui.impl.ServiceContainer;
import org.kabeja.ui.xml.SAXServiceContainerBuilder;

public class UIApplication implements Application {

    public void start() {
        try {
            ProcessingManager processorManager = SAXProcessingManagerBuilder
                    .buildFromStream(this.getClass().getResourceAsStream(
                            "/conf/process.xml"));

            ServiceContainer sc = SAXServiceContainerBuilder
                    .buildFromStream(new FileInputStream("conf/ui.xml"));
            sc.setProcessingManager(processorManager);
            sc.start();
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }

    }

    public void stop() {
       

    }

}
