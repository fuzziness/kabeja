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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.zip.GZIPOutputStream;

import org.kabeja.dxf.DXFDocument;
import org.kabeja.parser.DXFParser;
import org.kabeja.parser.Parser;
import org.kabeja.parser.ParserBuilder;
import org.kabeja.processing.PostProcessManager;
import org.kabeja.processing.ProcessingManager;
import org.kabeja.tools.SAXProcessingManagerBuilder;
import org.kabeja.ui.ViewComponent;
import org.kabeja.ui.impl.ProcessingEditorViewComponent;
import org.kabeja.ui.impl.ProcessingRunViewComponent;
import org.kabeja.ui.impl.ProcessingUI;
import org.kabeja.ui.impl.ServiceContainer;
import org.kabeja.ui.xml.SAXServiceContainerBuilder;
import org.kabeja.xml.SAXPrettyOutputter;

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

	private boolean gzip = false;

	private boolean postProcess = false;

	private boolean outputDTD = false;

	private boolean process = false;

	private boolean directoryMode = true;

	private PostProcessManager ppManager;

	private ProcessingManager processorManager;

	private String pipeline;

	public Main() {
	}

	public static void main(String[] args) {
		
		
		
		
		
		if (args.length == 0) {
			printUsage();
			System.exit(0);
		}

		Main m = new Main();
		int i = 0;
		boolean source = true;

		while (i < args.length) {
			if (args[i].equals("-gz")) {
				m.setGZip(true);
				i++;
			} else if (args[i].equals("-e")) {
				m.setEncoding(args[i + 1]);
				i += 2;
			} else if (args[i].equals("-c")) {
				m.setParserConfigFile(args[i + 1]);
				i += 2;
			} else if (args[i].equals("-dtd")) {
				m.enableDTD(true);
				i++;
			} else if (args[i].equals("-pp")) {
				m.setProcessConfig(args[i + 1]);
				i += 2;
			} else if (args[i].equals("-pipeline")) {
				m.setPipeline(args[i + 1]);
				i += 2;
			} else if (source) {
				m.setSourceFile(args[i]);
				source = false;
				i++;
			} else {
				m.setDestinationFile(args[i]);
				i++;
			}
		}

		m.convert();
	}

	private static void printUsage() {
		System.out
				.println("\n Use: java -jar kabeja.jar <Options> source.dxf out.svg\n\nOptions:\n      -e encoding "
						+ "of the inputfile (default:ASCII) \n      -c parser-config.xml\n      "
						+ "-dtd write DTD to output file\n      -gz  compress outputfile (gzip)\n      "
						+ "-pp process.xml set processing file\n      "
						+ "-pipeline name  process the given pipeline\n\nIf the source is a directory,"
						+ " all containing dxf files will be converted.\n\n");
	}

	public void convert() {
		if (parser == null) {
			parser = ParserBuilder.createDefaultParser();
		}

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

						if (gzip) {
							extension = ".svgz";
						} else {
							extension = ".svg";
						}

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
			} else {
				OutputStream out = null;

				if (gzip) {
					out = new GZIPOutputStream(new FileOutputStream(output));
				} else {
					out = new FileOutputStream(output);
				}

				SAXPrettyOutputter writer = new SAXPrettyOutputter(out,
						SAXPrettyOutputter.DEFAULT_ENCODING);

				// if (this.outputDTD) {
				// writer.setDTD(SVGConstants.SVG_DTD_1_0);
				// }
				// SAXGenerator gen = new SVGGenerator();
				// gen.setProperties(new HashMap());
				// gen.generate(doc, writer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setParserConfigFile(String file) {
		parser = ParserBuilder.buildFromXML(file);
	}

	public void setGZip(boolean state) {
		this.gzip = state;
	}

	public void setPostProcessorFile(String file) {
		postProcess = true;

		ppManager = new PostProcessManager();

		File f = new File(file);

		try {
			BufferedReader in = new BufferedReader(new FileReader(f));
			String pp = null;

			while ((pp = in.readLine()) != null) {
				ppManager.addPostProcessor(pp);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public void enableDTD(boolean b) {
		this.outputDTD = b;
	}

	public void setProcessConfig(String file) {
		try {
			this.processorManager = SAXProcessingManagerBuilder
					.buildFromStream(new FileInputStream(file));
			
			ServiceContainer sc = SAXServiceContainerBuilder.buildFromStream(new FileInputStream("conf/ui.xml"));
			sc.setProcessingManager(this.processorManager);
			sc.start();
	

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void setPipeline(String name) {
		this.pipeline = name;
		this.process = true;
	}
}
