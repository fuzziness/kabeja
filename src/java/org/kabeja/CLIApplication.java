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

package org.kabeja;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.kabeja.processing.ProcessPipeline;
import org.kabeja.processing.ProcessingManager;
import org.kabeja.processing.xml.SAXProcessingManagerBuilder;

public class CLIApplication implements Application{

	
	private String sourceFile;
	private String destinationFile;

	private boolean process = false;
	private boolean directoryMode = true;
	private ProcessingManager processorManager;
	private String pipeline;
	

	
	
	
    public void start(Map properties) {
    	//setup application
    	if(properties.containsKey("pp")){
    		try {
				this.setProcessConfig(new FileInputStream((String)properties.get("pp")));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
    	}
    	
    	if(properties.containsKey("pipeline")){
    	    this.setPipeline((String)properties.get("pipeline"));	
    	}
        if(properties.containsKey("in")){
             this.setSourceFile((String)properties.get("in"));
        }
    	if(properties.containsKey("o")){
    	   this.setDestinationFile((String)properties.get("o"));	
    	}
        


		this.initialize();

		if (properties.containsKey("help")) {
			printUsage();
			this.printPipelines();
		} else {
			this.process();
		}
        
    }

    public void stop() {
      //not needed
        
    }
    
    
	private static void printUsage() {
		System.out
				.println("\n Use: java -jar kabeja.jar <Options> "
						+ "\n\nOptions:\n"
						+ "  --help shows this and exit\n"
						+ "  -pp process.xml set processing file to use\n"
						+ "  -pipeline name  process the given pipeline\n\n"
						+ "  -o  <Output file or directory>\n"
						+ "  -in <Input file or directroy>\n");
	}

	public void initialize() {
		if (this.processorManager == null) {
			this.setProcessConfig(this.getClass().getResourceAsStream(
					"/conf/process.xml"));
		}
	}

	public void process() {

			File f = new File(this.sourceFile);

			if (f.exists() && f.isFile()) {
				parseFile(f, this.destinationFile);
			} else if (f.isDirectory()) {
				File[] files = f.listFiles();
                File destination = null; 
				if(this.destinationFile != null){
                	 destination = new File(this.destinationFile);
                	 destination = destination.isDirectory() ? destination : destination.getParentFile();
                 }else{
                	 destination = f;
                 }
				 for (int i = 0; i < files.length; i++) {
					       String file = files[i].getName();
					       String[] parts = file.split("."); 
					       File result = new File(destination.getAbsolutePath(),parts[0]+"."+this.processorManager.getProcessPipeline(pipeline).getGenerator().getSuffix());
							parseFile(files[i], result.getAbsolutePath());
						
					}
				}
	}



	public String getSourceFile() {
		return sourceFile;
	}

	public void setSourceFile(String sourceFile) {
		this.sourceFile = sourceFile;
	}

	public String getDestinationFile() {
		return destinationFile;
	}

	public void setDestinationFile(String destinationFile) {
		this.destinationFile = destinationFile;
		this.directoryMode = false;
	}

	private void parseFile(File f, String output) {
		try {
		
            String extension = f.getName().toLowerCase();
            int index =extension.lastIndexOf('.');
            if(index >-1&& index+1<extension.length()){
            	extension = extension.substring(index+1);
            }
		    this.processorManager.process(new FileInputStream(f), extension, new HashMap(), pipeline, new FileOutputStream(output));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	public void setProcessConfig(InputStream in) {
		this.processorManager = SAXProcessingManagerBuilder.buildFromStream(in);
	}

	public void setPipeline(String name) {
		this.pipeline = name;
		this.process = true;
	}



	public void printPipelines() {
		Iterator i = this.processorManager.getProcessPipelines().keySet()
				.iterator();
		System.out.println("\n Available pipelines:\n----------\n");

		while (i.hasNext()) {
			String pipeline = (String) i.next();
			ProcessPipeline pp = this.processorManager
					.getProcessPipeline(pipeline);
			System.out.print(" " + pipeline);
			if (pp.getDescription().length() > 0) {
				System.out.print("\t" + pp.getDescription());

			}
			System.out.println();
		}
	}

	

}
