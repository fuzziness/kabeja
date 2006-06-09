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
package org.kabeja.parser;

import org.kabeja.dxf.DXFDocument;

import org.kabeja.tools.CodePageParser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.Hashtable;
import java.util.Iterator;


/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 *
 */
public class DXFParser implements HandlerManager, Handler {
    private final static String SECTION_START = "SECTION";
    private final static String SECTION_END = "ENDSEC";
    private final static String END_STREAM = "EOF";
    private final static int COMMAND_CODE = 0;
    public static final String DEFAULT_ENCODING = "";
    private DXFDocument doc;
    private Hashtable handlers = new Hashtable();
    private DXFSectionHandler currentHandler;
    private String line;

    // some parse flags
    private boolean key = false;
    private boolean sectionstarts = false;
    private int linecount;
    private boolean parse = false;

    public DXFParser() {
    }

    public void parse(String file) throws DXFParseException {
        parse(file, DEFAULT_ENCODING);
    }

    public void parse(String file, String encoding) throws DXFParseException {
        try {
            parse(new FileInputStream(file), encoding);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void parse(InputStream input, String encoding)
        throws DXFParseException {
        String currentKey = "";
        key = false;
        linecount = 0;
        parse = false;

        doc = new DXFDocument();
        doc.setProperty(DXFDocument.PROPERTY_ENCODING, encoding);

        BufferedReader in = null;

        try {
            if ("".equals( encoding )) {
                BufferedInputStream buf = new BufferedInputStream(input);
                buf.mark(9000);

                try {
                    BufferedReader r = new BufferedReader(new InputStreamReader(
                                buf));
                    CodePageParser p = new CodePageParser();
                    encoding = p.parseEncoding(r);
                    buf.reset();

                    in = new BufferedReader(new InputStreamReader(buf, encoding));
                } catch (IOException e1) {
                    buf.reset();
                    in = new BufferedReader(new InputStreamReader(buf));
                }
            } else {
                in = new BufferedReader(new InputStreamReader(input, encoding));
            }

            key = true;
            sectionstarts = false;

            while ((line = in.readLine()) != null) {
                linecount++;

                if (key) {
                    currentKey = line;
                    key = false;
                } else {
                    parseGroup(currentKey, line);
                    key = true;
                }

            }

            in.close();

            in = null;

            // finish last parsing
            if (parse) {
                currentHandler.endSection();
            }
        } catch (FileNotFoundException e) {
        	 throw new DXFParseException(e.toString());
        } catch (IOException ioe) {
           throw new DXFParseException(ioe.toString());
        }

    }

    private void parseGroup(String key, String value) throws DXFParseException {

        try {
            int keyCode = Integer.parseInt(key.trim());

            if (sectionstarts) {
                sectionstarts = false;

                if (handlers.containsKey(value)) {
                    currentHandler = (DXFSectionHandler) handlers.get(value);
                    parse = true;
                    currentHandler.setDXFDocument(doc);
                    currentHandler.startSection();
                } else {
                    parse = false;
                }

                return;
            }

            if ((keyCode == COMMAND_CODE) &&
                    SECTION_START.equals( value.trim() ) && !sectionstarts) {
                sectionstarts = true;
            }

            if ((keyCode == COMMAND_CODE) && SECTION_END.equals( value.trim() )) {
                if (parse) {
                    currentHandler.endSection();
                }

                parse = false;

                return;
            }

            if (parse) {
                DXFValue v = new DXFValue(value);
                currentHandler.parseGroup(keyCode, v);
            }

            return;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            throw new DXFParseException("Line: " + linecount +
                " unsupported groupcode: " + key + " for value:" + value, e);
        }
    }

    public DXFDocument getDocument() {
        return doc;
    }

    public void addDXFSectionHandler(DXFSectionHandler handler) {
        handler.setDXFDocument(doc);
        handlers.put(handler.getSectionKey(), handler);
    }

    public void addHandler(Handler handler) {
        addDXFSectionHandler((DXFSectionHandler) handler);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.kabeja.parser.Handler#releaseDXFDocument()
     */
    public void releaseDXFDocument() {
        this.doc = null;

        Iterator i = handlers.values().iterator();

        while (i.hasNext()) {
            Handler handler = (Handler) i.next();
            handler.releaseDXFDocument();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.miethxml.kabeja.parser.Handler#setDXFDocument(de.miethxml.kabeja.dxf.DXFDocument)
     */
    public void setDXFDocument(DXFDocument doc) {
        this.doc = doc;
    }
}
