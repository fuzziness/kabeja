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
package org.kabeja.ui.viewer;

import de.miethxml.toolkit.ui.SmallShadowBorder;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.JSVGScrollPane;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.util.XMLResourceDescriptor;

import org.w3c.dom.svg.SVGDocument;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;


/**
 * This is a very simple SVGViewer.
 *
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 *
 */
public class SVGViewer {
    private JFrame frame;
    private JTextField uriField;
    private JSVGCanvas svgCanvas;
    private JFileChooser fc;
    private JLabel infoLabel;
    private CardLayout cards;
    private JPanel parentPanel;
    private SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(
            "org.kabeja.svg.DXF2SVGReader",true);
    private double scaleXY = 1.0;

    /**
     *
     */
    public SVGViewer() {
        super();
    }

    public void initialize() {
        frame = new JFrame("SVGViewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // the uri-panel
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 3));
        panel.add(new JLabel("Url:"));
        uriField = new JTextField(30);
        uriField.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Runnable r = new Runnable() {
                            public void run() {
                                load(new File(uriField.getText()));
                            }
                        };

                    Thread t = new Thread(r);
                    t.start();
                }
            });
        panel.add(uriField);
        fc = new JFileChooser();

        JButton button = new JButton("Open");
        button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int value = fc.showOpenDialog(frame);

                    if (value == JFileChooser.APPROVE_OPTION) {
                        svgCanvas.setSVGDocument(null);

                        Runnable r = new Runnable() {
                                public void run() {
                                    infoLabel.setText("Parsing and Rendering");
                                    cards.show(parentPanel, "info");

                                    File file = fc.getSelectedFile();
                                    uriField.setText(file.getAbsolutePath());
                                    load(file);
                                    cards.show(parentPanel, "view");
                                }
                            };

                        Thread t = new Thread(r);
                        t.start();
                    }
                }
            });
        panel.add(button);

        button = new JButton("Zoom in");
        button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                AffineTransform at = new AffineTransform();
                                scaleXY += .3;
                                at.setToScale(scaleXY, scaleXY);

                                svgCanvas.setRenderingTransform(at);
                            }
                        });
                }
            });
        panel.add(button);
        button = new JButton("Zoom out");
        button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                AffineTransform at = new AffineTransform();
                                scaleXY -= .3;
                                at.setToScale(scaleXY, scaleXY);
                                svgCanvas.setRenderingTransform(at);
                            }
                        });
                }
            });
        panel.add(button);

        button = new JButton("Export JPEG");
        button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int value = fc.showSaveDialog(frame);

                    if (value == JFileChooser.APPROVE_OPTION) {
                        Runnable r = new Runnable() {
                                public void run() {
                                    File file = fc.getSelectedFile();

                                    try {
                                        saveToJPEG(new FileOutputStream(file));
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };

                        Thread t = new Thread(r);
                        t.start();
                    }
                }
            });
        panel.add(button);

        frame.getContentPane().add(panel, BorderLayout.NORTH);

        cards = new CardLayout();
        parentPanel = new JPanel(cards);

        svgCanvas = new JSVGCanvas();

        JSVGScrollPane sp = new JSVGScrollPane(svgCanvas);
        sp.setBorder(new SmallShadowBorder());

        parentPanel.add(sp, "view");
        parentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        infoLabel = new JLabel("DXF2SVGViewer", JLabel.CENTER);
        parentPanel.add(infoLabel, "info");
        parentPanel.setPreferredSize(new Dimension(640, 480));
        frame.getContentPane().add(parentPanel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }

    public void load(File file) {
        try {
            if (file.exists()) {
                FileInputStream in = new FileInputStream(file);

                load(file.toURI().toASCIIString(), in);
            } else {
                System.out.println("no file:" + file);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void load(String uri, InputStream in) {
        try {
            System.gc();

            SAXSVGDocumentFactory f = null;

            if (uri.toLowerCase().endsWith(".dxf")) {
                f = factory;
            } else {
                // check for version 1.4 and above
                String ver = System.getProperty("java.version");

                if (ver.startsWith("1.2") || ver.startsWith("1.3")) {
                    String parser = XMLResourceDescriptor.getXMLParserClassName();
                    f = new SAXSVGDocumentFactory(parser);
                } else if (ver.startsWith("1.4")) {
                    // jdk 1.4 uses crimson
                    f = new SAXSVGDocumentFactory(
                            "org.apache.crimson.parser.XMLReaderImpl");
                } else if (ver.startsWith("1.5")) {
                    f = new SAXSVGDocumentFactory(
                            "com.sun.org.apache.xerces.internal.parsers.SAXParser");
                }
            }

            SVGDocument doc = f.createSVGDocument(uri, in);
            this.svgCanvas.setDocumentState(JSVGCanvas.ALWAYS_DYNAMIC);

            // add a script to doc here by adding the script element
            this.svgCanvas.setSVGDocument(doc);
        } catch (Exception e) {
        	e.printStackTrace();
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);

        } catch (Error e) {
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public void saveToJPEG(OutputStream out) {
        ImageTranscoder trans = new JPEGTranscoder();

        try {
            trans.transcode(new TranscoderInput(this.svgCanvas.getSVGDocument()),
                new TranscoderOutput(out));
        } catch (TranscoderException e) {
            e.printStackTrace();
        }
    }
}
