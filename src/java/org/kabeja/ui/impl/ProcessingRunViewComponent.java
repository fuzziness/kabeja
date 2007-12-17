package org.kabeja.ui.impl;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.kabeja.dxf.DXFDocument;
import org.kabeja.parser.DXFParseException;
import org.kabeja.parser.DXFParser;
import org.kabeja.parser.Parser;
import org.kabeja.parser.ParserBuilder;
import org.kabeja.processing.ProcessingManager;
import org.kabeja.processing.ProcessorException;
import org.kabeja.ui.ApplicationToolBar;
import org.kabeja.ui.DXFDocumentViewComponent;
import org.kabeja.ui.ServiceManager;
import org.kabeja.ui.Serviceable;
import org.kabeja.ui.UIException;
import org.kabeja.ui.ViewComponent;

import de.miethxml.toolkit.ui.PanelFactory;
import de.miethxml.toolkit.ui.UIUtils;

public class ProcessingRunViewComponent implements ViewComponent, Serviceable,
		ActionListener {

	protected JTabbedPane tabbedPane;
	protected JComponent view;
	protected JPanel pipelinePanel;
	protected boolean locked = false;
	protected boolean initialized = false;
	protected List viewComponents = new ArrayList();
	protected String processingPipeline;
	protected JTextArea logView;
	protected ProcessingManager manager;
	protected String baseDir = "";
	protected File sourceFile;
	protected DXFDocument doc;
	protected boolean autogenerateOutput = false;

	public String getTitle() {

		return "Run Processing";
	}

	public JComponent getView() {
		this.initialize();
		return this.view;

	}

	protected void initialize() {
		if (!this.initialized) {
			JPanel panel = PanelFactory.createTitledPanel(new JPanel(),
					"ProcessingPipelines", new ImageIcon(UIUtils
							.resourceToBytes(this.getClass(),
									"/icons/project.gif")));
			pipelinePanel = new JPanel(new GridLayout(0, 1, 0, 3));

			pipelinePanel
					.setBorder(BorderFactory.createEmptyBorder(4, 5, 4, 5));
			JScrollPane scroll = new JScrollPane(pipelinePanel);
			scroll.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
			panel.add(scroll);

			JSplitPane sp = PanelFactory.createOneTouchSplitPane();
			sp.setLeftComponent(panel);
			tabbedPane = new JTabbedPane();
			tabbedPane.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

			sp.setRightComponent(tabbedPane);
			sp.setDividerLocation(150);
			JSplitPane sp2 = PanelFactory
					.createOneTouchSplitPane(JSplitPane.VERTICAL_SPLIT);
			sp2.setTopComponent(sp);

			this.logView = new JTextArea();
			scroll = new JScrollPane(this.logView);
			scroll.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
			JPanel p = PanelFactory.createTitledPanel(scroll,
					"ProcessingOutput");
			sp2.setBottomComponent(p);
			sp2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

			sp2.setDividerLocation(350);
			this.view = sp2;
			this.initialized = true;
		}
	}

	public void setProcessingManager(ProcessingManager manager) {
		this.manager = manager;
		this.initialize();
		// setup the pipelines
		pipelinePanel.removeAll();
		Iterator i = manager.getProcessPipelines().keySet().iterator();
		while (i.hasNext()) {
			String pipelineName = (String) i.next();
			JButton button = new JButton(pipelineName);
			button.setActionCommand(pipelineName);
			button.addActionListener(this);
			pipelinePanel.add(button);

		}
		pipelinePanel.revalidate();

	}

	protected void addDXFDocumentViewComponent(
			DXFDocumentViewComponent component) {
		this.tabbedPane.add(component.getTitle(), component.getView());
		this.viewComponents.add(component);

	}

	public void setServiceManager(ServiceManager manager) {
		Object[] objects = manager
				.getServiceComponents(DXFDocumentViewComponent.SERVICE);
		for (int i = 0; i < objects.length; i++) {
			this
					.addDXFDocumentViewComponent((DXFDocumentViewComponent) objects[i]);
		}
		objects = manager.getServiceComponents(ApplicationToolBar.SERVICE);
		for (int i = 0; i < objects.length; i++) {
			((ApplicationToolBar) objects[i]).addAction(new AbstractAction(
					"Open") {

				public void actionPerformed(ActionEvent e) {
					Runnable r = new Runnable() {
						public void run() {
							chooseInput();
						}

					};
					Thread t = new Thread(r);
					t.start();
				}

			});
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (!this.locked) {
			this.processingPipeline = e.getActionCommand();
			this.locked = true;
			Runnable r = new Runnable() {

				public void run() {
					process();

				}
			};
			Thread t = new Thread(r);
			t.start();

		}

	}

	protected void process() {

		try {

			if (this.sourceFile.isDirectory()) {
				File[] files = this.sourceFile.listFiles();
				Parser parser = ParserBuilder.createDefaultParser();
				// we have to parse every file
				for (int i = 0; i < files.length; i++) {
					this.parseFile(files[i], parser);
					this.processFile(this.doc, files[i]);
				}
				// file already parsed
			} else if (this.sourceFile.isFile()) {
				this.processFile(this.doc, this.sourceFile);
			}

		} catch (Exception e) {
			this.logException(e);
		}

		this.locked = false;
	}

	protected void processFile(DXFDocument doc, File f) {
		try {
			this.logView.append("Processing:" + f.getAbsolutePath() + "\n");
			File out = null;
			if (this.autogenerateOutput) {
				this.manager.getProcessPipeline(this.processingPipeline);
				String suffix = this.manager.getProcessPipeline(
						this.processingPipeline).getSAXSerializer().getSuffix();
				String n = f.getAbsolutePath();
				n = n.substring(0, n.lastIndexOf('.')) + "." + suffix;
				out = new File(n);
			} else {
				JFileChooser fc = new JFileChooser(this.baseDir);
				int value = fc.showSaveDialog(null);
				if (value == JFileChooser.APPROVE_OPTION) {
					out = fc.getSelectedFile();
				}
			}
			if (out != null) {
				this.manager.process(doc, new HashMap(),
						this.processingPipeline, new FileOutputStream(out));
				this.logView.append("Finished:" + out.getAbsolutePath() + "\n");
			} else {
				this.logView.append("No output set, do nothing.\n");
			}
		} catch (Exception e) {
			this.logException(e);
		}
	}

	protected void chooseInput() {
		JFileChooser fc = new JFileChooser(this.baseDir);
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		JCheckBox cb = new JCheckBox();
		cb.setSelected(true);
		JPanel p = new JPanel(new FlowLayout());
		p.add(cb);
		p.add(new JLabel("Autogenerate outputfile"));
		fc.setAccessory(p);

		int value = fc.showOpenDialog(null);

		if (value == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			this.autogenerateOutput = cb.isSelected();
			if (file.isFile()) {
				this.baseDir = file.getParent();
				Parser parser = ParserBuilder.createDefaultParser();
				try {

					this.parseFile(file, parser);
					this.propagateDXFDocument(this.doc);
				} catch (Exception e) {
					this.logException(e);
				}
			} else if (file.isDirectory()) {
				this.baseDir = file.getAbsolutePath();
				this.logView.append("Selected directory:"
						+ file.getAbsolutePath() + "\n");
				this.logView.append("No preview\n");

			}
			this.sourceFile = file;
		}
	}

	protected void parseFile(File f, Parser parser) throws Exception {
		this.logView.append("Parsing:" + f.getAbsolutePath() + "\n");
		parser.parse(new FileInputStream(f), DXFParser.DEFAULT_ENCODING);
		this.doc = parser.getDocument();
	}

	protected void propagateDXFDocument(DXFDocument doc) {
		Iterator i = this.viewComponents.iterator();
		while (i.hasNext()) {
			DXFDocumentViewComponent view = (DXFDocumentViewComponent) i.next();
			try {
				view.showDXFDocument(doc);
			} catch (UIException e) {
			   e.printStackTrace();
			   this.logException(e);
			}

		}
	}

	protected void logException(Exception e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		this.logView.append(sw.toString());
	}

}
