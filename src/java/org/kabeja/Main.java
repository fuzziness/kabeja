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
package org.kabeja;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;

import org.kabeja.dxf.DXFDocument;
import org.kabeja.parser.DXFParser;
import org.kabeja.parser.Parser;
import org.kabeja.parser.ParserBuilder;
import org.kabeja.processing.ProcessPipeline;
import org.kabeja.processing.ProcessingManager;
import org.kabeja.tools.SAXProcessingManagerBuilder;
import org.kabeja.ui.impl.ServiceContainer;
import org.kabeja.ui.xml.SAXServiceContainerBuilder;

/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth </a>
 * 
 * 
 */
public class Main {
	private String encoding = DXFParser.DEFAULT_ENCODING;
	private String sourceFile;
	private String destinationFile;
	private Parser parser;
	private boolean process = false;
	private boolean directoryMode = true;
	private ProcessingManager processorManager;
	private String pipeline;
	private boolean nogui = false;

	public Main() {
	}

	public static void main(String[] args) {
		Main main = new Main();
		int i = 0;
		boolean source = true;
		boolean help = false;

		while (i < args.length) {
			if (args[i].equals("-pp")) {
				try {
					main.setProcessConfig(new FileInputStream(args[i + 1]));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

				i += 2;
			} else if (args[i].equals("-pipeline")) {
				main.setPipeline(args[i + 1]);
				i += 2;
			} else if (args[i].equals("--help")) {
				i++;
				help = true;
			} else if (args[i].equals("-nogui")) {
				main.omitUI(true);
				i++;
			} else if (source) {
				main.setSourceFile(args[i]);
				source = false;
				i++;
			} else {
				main.setDestinationFile(args[i]);
				i++;
			}
		}

		main.initialize();

		if (help || (args.length == 1 && main.isNogui())) {
			printUsage();
			main.printPipelines();
		} else {
			main.process();
		}

	}

	private static void printUsage() {
		System.out
				.println("\n Use: java -jar kabeja.jar <Options> sourcefile  <outputfile>"
						+ "\n\nOptions:\n"
						+ "  --help shows this and exit\n"
						+ "  -nogui run only the cli, omit the user interface\n"
						+ "  -pp process.xml set processing file to use\n"
						+ "  -pipeline name  process the given pipeline\n\n"
						+ "If the source is a directory,"
						+ " all containing files will be converted.\n");
	}

	public void initialize() {
		if (this.processorManager == null) {
			this.setProcessConfig(this.getClass().getResourceAsStream(
					"/conf/process.xml"));
		}
	}

	public void process() {
		if (parser == null) {
			parser = ParserBuilder.createDefaultParser();
		}

		if (this.nogui) {
			File f = new File(this.sourceFile);

			if (f.exists() && f.isFile()) {
				parseFile(f, this.destinationFile);
			} else if (f.isDirectory()) {
				File[] files = f.listFiles();

				for (int i = 0; i < files.length; i++) {
					if (files[i].getName().toLowerCase().endsWith(".dxf")) {
						try {
							String source = files[i].getCanonicalPath();
							String extension = null;

							String result = source.substring(0, source
									.toLowerCase().lastIndexOf(".dxf"))
									+ extension;
							System.out.println("convert file:" + source);
							parseFile(files[i], result);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			} else {
				System.err.println("Cannot open " + this.sourceFile);
			}
		} else {
			// ServiceContainer sc = SAXServiceContainerBuilder
			// .buildFromStream(this.getClass().getResourceAsStream(
			// "/conf/ui.xml"));
			try {
				ServiceContainer sc = SAXServiceContainerBuilder
						.buildFromStream(new FileInputStream("conf/ui.xml"));
				sc.setProcessingManager(this.processorManager);
				sc.start();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
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
			this.parser.parse(new FileInputStream(f), encoding);

			DXFDocument doc = parser.getDocument();

			if (this.process) {
				if (this.directoryMode) {
					this.processorManager.process(doc, new HashMap(),
							this.pipeline, f.getAbsolutePath());
				} else {
					// user set name
					this.processorManager.process(doc, new HashMap(),
							this.pipeline, new FileOutputStream(output));
				}
			}

			// TODO move this into the svg block + gzip
			// else {
			// OutputStream out = null;
			//
			// out = new FileOutputStream(output);
			//
			// SAXPrettyOutputter writer = new SAXPrettyOutputter(out,
			// SAXPrettyOutputter.DEFAULT_ENCODING);

			// if (this.outputDTD) {
			// writer.setDTD(SVGConstants.SVG_DTD_1_0);
			// }
			// SAXGenerator gen = new SVGGenerator();
			// gen.setProperties(new HashMap());
			// gen.generate(doc, writer);
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setParserConfigFile(String file) {
		parser = ParserBuilder.buildFromXML(file);
	}

	public void setProcessConfig(InputStream in) {
		this.processorManager = SAXProcessingManagerBuilder.buildFromStream(in);
	}

	public void setPipeline(String name) {
		this.pipeline = name;
		this.process = true;
	}

	public void omitUI(boolean b) {
		this.nogui = b;
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

	public boolean isNogui() {
		return nogui;
	}
}
