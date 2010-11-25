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
package org.kabeja.processing.scripting.impl;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.text.BadLocationException;

import org.kabeja.dxf.DXFDocument;
import org.kabeja.processing.AbstractPostProcessor;
import org.kabeja.processing.ProcessorException;
import org.kabeja.ui.DXFDocumentViewComponent;
import org.kabeja.ui.UIException;
import org.kabeja.ui.event.DXFDocumentChangeEventProvider;
import org.kabeja.ui.event.DXFDocumentChangeListener;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import de.miethxml.toolkit.ui.UIUtils;


public class JavaScriptShell extends AbstractPostProcessor
    implements DXFDocumentViewComponent, DXFDocumentChangeEventProvider {
    protected final static String COMMAND_PREFIX = "js>";
    protected JFrame frame;
    protected JTextArea textArea;
    protected HashMap actions = new HashMap();
    protected ArrayList history = new ArrayList();
    protected int historyPos = 0;
    protected ScriptWorker worker;
    protected String title = "JSShell";
    protected ArrayList listeners = new ArrayList();
    protected DXFDocument doc;

    public void process(DXFDocument doc, Map context) throws ProcessorException {
        worker = new ScriptWorker(doc);
        worker.start();
        this.init();

        // wait for finished shell editing
        while (worker.isAlive()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setProperties(Map properties) {
    }

    protected void init() {
        frame = new JFrame(getTitle());
        frame.setJMenuBar(this.createMenuBar());
        frame.getContentPane().setLayout(new BorderLayout());

        frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    // capture the second close-event and ignore
                    dispose();
                }
            });

        frame.getContentPane().add(getView(), BorderLayout.CENTER);

        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 2));
        JButton button = new JButton((Action) actions.get("close"));
        p.add(button);
        frame.getContentPane().add(p, BorderLayout.SOUTH);

        frame.setSize(new Dimension(640, 480));
        frame.setVisible(true);

        newShellLine();
    }

    protected JToolBar createToolbar() {
        JToolBar toolbar = new JToolBar();

        JButton button = new JButton((Action) actions.get("copy"));
        button.setToolTipText(button.getText());
        button.setText("");
        toolbar.add(button);
        button = new JButton((Action) actions.get("paste"));
        button.setToolTipText(button.getText());
        button.setText("");
        toolbar.add(button);
        button = new JButton((Action) actions.get("cut"));
        button.setToolTipText(button.getText());
        button.setText("");
        toolbar.add(button);

        button = new JButton((Action) actions.get("reload"));
        button.setToolTipText(button.getText());
        button.setText("");
        toolbar.add(button);

        return toolbar;
    }

    protected JMenuBar createMenuBar() {
        JMenuBar menubar = new JMenuBar();
        JMenu menu = new JMenu("File");

        JMenuItem item = new JMenuItem((Action) actions.get("open"));
        item.setToolTipText(item.getText());

        menu.add(item);
        item = new JMenuItem((Action) actions.get("save"));
        menu.add(item);
        menu.add(new JSeparator());
        item = new JMenuItem((Action) actions.get("close"));
        menu.add(item);

        menubar.add(menu);

        // menu = new JMenu("Edit");
        // item = new JMenuItem((Action) actions.get("copy"));
        // menu.add(item);
        // item = new JMenuItem((Action) actions.get("paste"));
        // menu.add(item);
        // item = new JMenuItem((Action) actions.get("cut"));
        // menu.add(item);
        // menubar.add(menu);
        return menubar;
    }

    protected void initActions() {
        Action action = new AbstractAction("Cut",
                new ImageIcon(UIUtils.resourceToBytes(this.getClass(),
                        "/icons/cut_edit.gif"))) {
                public void actionPerformed(ActionEvent e) {
                    textArea.cut();
                }
            };

        actions.put("cut", action);

        action = new AbstractAction("Paste",
                new ImageIcon(UIUtils.resourceToBytes(this.getClass(),
                        "/icons/paste_edit.gif"))) {
                    public void actionPerformed(ActionEvent e) {
                        textArea.paste();
                    }
                };
        actions.put("paste", action);

        action = new AbstractAction("Copy",
                new ImageIcon(UIUtils.resourceToBytes(this.getClass(),
                        "/icons/copy_edit.gif"))) {
                    public void actionPerformed(ActionEvent e) {
                        textArea.copy();
                    }
                };
        actions.put("copy", action);

        action = new AbstractAction("Close") {
                    public void actionPerformed(ActionEvent e) {
                        dispose();
                    }
                };
        actions.put("close", action);

        action = new AbstractAction("Save") {
                    public void actionPerformed(ActionEvent e) {
                        JFileChooser fs = new JFileChooser();
                        int r = fs.showSaveDialog(frame);

                        if (r == JFileChooser.APPROVE_OPTION) {
                            try {
                                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                                            new FileOutputStream(
                                                fs.getSelectedFile())));
                                Iterator i = history.iterator();

                                while (i.hasNext()) {
                                    out.write((String) i.next());
                                    out.newLine();
                                }

                                out.flush();
                                out.close();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                };
        actions.put("save", action);

        action = new AbstractAction("Open") {
                    public void actionPerformed(ActionEvent e) {
                        textArea.setText(COMMAND_PREFIX);
                        history.clear();

                        JFileChooser fs = new JFileChooser();
                        int r = fs.showOpenDialog(frame);

                        if (r == JFileChooser.APPROVE_OPTION) {
                            try {
                                BufferedReader in = new BufferedReader(new InputStreamReader(
                                            new FileInputStream(
                                                fs.getSelectedFile())));
                                String line;

                                while ((line = in.readLine()) != null) {
                                    textArea.append(line);
                                    evalString(COMMAND_PREFIX + line);
                                }

                                in.close();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                };
        actions.put("open", action);
        action = new AbstractAction("Update views",
                new ImageIcon(UIUtils.resourceToBytes(this.getClass(),
                        "/icons/reload.gif"))) {
                    public void actionPerformed(ActionEvent e) {
                        fireDXFDocumentChangeEvent();
                    }
                };
        actions.put("reload", action);
    }

    protected String getLineAtCaretPosition() {
        try {
            int lineNumber = this.textArea.getLineOfOffset(this.textArea.getCaretPosition());
            int startOffset = this.textArea.getLineStartOffset(lineNumber);
            int length = this.textArea.getCaretPosition() - startOffset;
            String line = this.textArea.getText(startOffset, length);

            return line;
        } catch (BadLocationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "";
    }

    protected int getStartOffsetAtCaretPosition() {
        try {
            int lineNumber = textArea.getLineOfOffset(textArea.getCaretPosition());
            int startOffset = textArea.getLineStartOffset(lineNumber);

            return startOffset;
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        return 0;
    }

    protected void evalString(String line) {
        textArea.append("\n");

        if (line.startsWith(COMMAND_PREFIX)) {
            String script = line.substring(COMMAND_PREFIX.length());

            history.add(script);
            worker.executeScript(script);
        } else {
            newShellLine();
        }
    }

    protected void newShellLine() {
        textArea.append(COMMAND_PREFIX);
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }

    protected void write(String str) {
        try {
            this.textArea.getDocument()
                         .insertString(this.textArea.getCaretPosition(), str,
                null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    protected void dispose() {
        frame.setVisible(false);
        frame.dispose();
        worker.dispose();
    }

    public void showDXFDocument(DXFDocument doc) throws UIException {
    	
        this.doc = doc;
        worker = new ScriptWorker(doc);
        worker.start();
    }

    public String getTitle() {
        return this.title;
    }

    public JComponent getView() {
        textArea = new JTextArea();

        textArea.addKeyListener(new CommandKeyListener());

        this.initActions();

        JPanel panel = new JPanel(new BorderLayout());

        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);

        panel.add(createToolbar(), BorderLayout.NORTH);
        panel.setPreferredSize(new Dimension(640, 480));

        newShellLine();

        worker = new ScriptWorker(null);
        worker.start();

        return panel;
    }

    public void addDXFDocumentChangeListener(DXFDocumentChangeListener listener) {
        this.listeners.add(listener);
    }

    public void removeDXFDocumentChangeListener(
        DXFDocumentChangeListener listener) {
        this.listeners.remove(listener);
    }

    protected void fireDXFDocumentChangeEvent() {
        if (this.doc != null) {
            Iterator i = ((ArrayList) this.listeners.clone()).iterator();

            while (i.hasNext()) {
                DXFDocumentChangeListener l = (DXFDocumentChangeListener) i.next();
                l.changed(this.doc);
            }
        }
    }

    public class CommandKeyListener extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                evalString(getLineAtCaretPosition());
                e.consume();
                historyPos = history.size() - 1;
            } else if ((e.getKeyCode() == KeyEvent.VK_BACK_SPACE) ||
                    (e.getKeyCode() == KeyEvent.VK_LEFT)) {
                try {
                    int lineNumber = textArea.getLineOfOffset(textArea.getCaretPosition());
                    int startOffset = textArea.getLineStartOffset(lineNumber);
                    int l = textArea.getCaretPosition() - startOffset;

                    if (l <= COMMAND_PREFIX.length()) {
                        e.consume();
                    }
                } catch (BadLocationException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                if ((historyPos >= 0) && (history.size() > 0)) {
                    if ((historyPos >= history.size()) && (history.size() > 1)) {
                        historyPos = history.size() - 2;
                    }

                    int start = getStartOffsetAtCaretPosition() +
                        COMMAND_PREFIX.length();
                    textArea.replaceRange((String) history.get(historyPos),
                        start, textArea.getDocument().getLength());
                    historyPos--;
                }

                e.consume();
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                if ((historyPos < history.size()) && (history.size() > 0)) {
                    if ((historyPos < 0) && (history.size() > 1)) {
                        historyPos = 1;
                    }

                    int start = getStartOffsetAtCaretPosition() +
                        COMMAND_PREFIX.length();
                    textArea.replaceRange((String) history.get(historyPos),
                        start, textArea.getDocument().getLength());
                    historyPos++;
                }

                e.consume();
            }
        }
    }

    protected class ScriptWorker extends Thread {
        protected Context ctx;
        protected Scriptable scope;
        protected boolean execute = false;
        protected boolean dispose = false;
        protected String script;
        protected DXFDocument doc;
        PrintStream err;
        PrintStream out;
        protected PrintStream output = new PrintStream(new JTextAreaPrintWriter());

        public ScriptWorker(DXFDocument doc) {
            this.doc = doc;
        }

        protected void init() {
            // this.ctx = ContextFactory.getGlobal().enter();
            ContextFactory f = new ContextFactory();

            this.ctx = f.enter();
            err = System.err;
            out = System.out;
            // System.setOut(output);
            // System.setErr(output);
            this.scope = ctx.initStandardObjects(null, false);

            try {
                ScriptableObject.defineClass(this.scope, Global.class);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            this.scope = new Global();
            Global.setOutput(output);
            ctx.setErrorReporter(new ErrorReporter() {
                    public void error(String arg0, String arg1, int arg2,
                        String arg3, int arg4) {
                        textArea.append(arg0 + " from line:" + arg3 + "\n");
                    }

                    public EvaluatorException runtimeError(String arg0,
                        String arg1, int arg2, String arg3, int arg4) {
                        textArea.append(arg0 + " from line:" + arg3 + "\n");

                        return new EvaluatorException(arg0);
                    }

                    public void warning(String arg0, String arg1, int arg2,
                        String arg3, int arg4) {
                        textArea.append(arg0 + " from line:" + arg3 + "\n");
                    }
                });

            Object jsOut = Context.javaToJS(doc, scope);
            ScriptableObject.putProperty(scope, "dxf", jsOut);
            textArea.setText("");
            if(doc!=null){
            textArea.append("DXFDocument available as 'dxf'\n");
            }
            newShellLine();
        }

        public void run() {
            init();

            while (!dispose) {
                if (execute) {
                    try {
                        Object result = this.ctx.evaluateString(scope, script,
                                "<cmd>", 1, null);
                        String r = Context.toString(result);

                        if (!r.equals("undefined")) {
                            synchronized (textArea) {
                                textArea.append(r);
                                textArea.append("\n");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace(output);
                        init();
                    }

                    execute = false;
                    newShellLine();
                } else {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            Context.exit();
        }

        public void executeScript(String str) {
            this.script = str;
            this.execute = true;

            while (execute) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void dispose() {
            System.setErr(err);
            System.setOut(out);
            this.dispose = true;
        }
    }

    protected class JTextAreaPrintWriter extends OutputStream {
        StringBuffer buf = new StringBuffer();

        public void write(int b) throws IOException {
            if (b == '\r') {
                return;
            } else if (b == '\n') {
                synchronized (textArea) {
                    textArea.append(buf.toString() + "\n");
                }

                buf.setLength(0);
            } else {
                buf.append((char) b);
            }
        }
    }
}
