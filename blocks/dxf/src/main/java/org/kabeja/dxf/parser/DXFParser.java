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
package org.kabeja.dxf.parser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kabeja.DraftDocument;
import org.kabeja.dxf.parser.filter.DXFStreamFilter;
import org.kabeja.parser.ParseException;
import org.kabeja.parser.Parser;
import org.kabeja.tools.CodePageParser;
import org.kabeja.tools.IOUtils;


/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 *
 */
public class DXFParser implements DXFHandlerManager, Parser, DXFHandler {
    public final static String PARSER_NAME = "DXFParser";
    public final static String EXTENSION = "dxf";
    private final static String SECTION_START = "SECTION";
    private final static String SECTION_END = "ENDSEC";
    private final static String END_STREAM = "EOF";
    private final static int COMMAND_CODE = 0;
    public static final String DEFAULT_ENCODING = "";
    protected DraftDocument doc;
    protected Map<String,DXFSectionHandler> handlers = new HashMap<String,DXFSectionHandler>();
    protected DXFSectionHandler currentHandler;
    private String line;
    protected List<DXFStreamFilter> streamFilters = new ArrayList<DXFStreamFilter>();
    protected DXFHandler filter;

    // some parse flags
    private boolean key = false;
    private boolean sectionstarts = false;
    private int linecount;
    private boolean parse = false;




	public DraftDocument parse(InputStream in, Map properties)
			throws ParseException {
		
		parse(in,new DraftDocument(),properties);
		return this.doc;
	}


    public void parse(InputStream input, DraftDocument doc,Map properties)
        throws ParseException {
    	this.doc=doc;
        String currentKey = "";
        key = false;
        linecount = 0;
        parse = false;

        //initialize 
       String encoding = null;
       if(properties.containsKey(DraftDocument.PROPERTY_ENCODING)){
    	   encoding = (String)properties.get(DraftDocument.PROPERTY_ENCODING);
       }else{
    	   encoding = DEFAULT_ENCODING;
       }
        
        doc.setProperty(DraftDocument.PROPERTY_ENCODING, encoding);
        //the StreamFilters
        this.buildFilterChain();

        BufferedReader in = null;

        try {
            if ("".equals(encoding)) {
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
            DXFValue value= new DXFValue();
            while ((line = in.readLine()) != null) {
                linecount++;
                if (key) {
                    currentKey = line;
                    key = false;
                } else {
                    int keyCode = Integer.parseInt(currentKey.trim());
                    //the filter chain
                    value.setValue(line);
                    filter.parseGroup(keyCode,value);
                    // parseGroup(currentKey, line);
                    key = true;
                }
            }

            // finish last parsing
            if (parse) {
                currentHandler.endSection();
            }
        } catch (FileNotFoundException e) {
            throw new ParseException(e.toString());
        } catch (IOException ioe) {
            throw new ParseException(ioe.toString());
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                // Nothing to do
            }
        }
    }

    public void parseGroup(int keyCode, DXFValue value)
        throws ParseException {
        try {
            if (sectionstarts) {
                sectionstarts = false;

                if (handlers.containsKey(value.getValue())) {
                    currentHandler = (DXFSectionHandler) handlers.get(value.getValue());
                    parse = true;
                    currentHandler.setDocument(doc);
                    currentHandler.startSection();
                } else {
                    parse = false;
                }

                return;
            }else if ((keyCode == COMMAND_CODE) &&
                    SECTION_START.equals(value.getValue()) && !sectionstarts) {
                sectionstarts = true;
            }else  if ((keyCode == COMMAND_CODE) &&
                    SECTION_END.equals(value.getValue())) {
                if (parse) {
                    currentHandler.endSection();
                }
                parse = false;
                return;
            }

            if (parse) {
                currentHandler.parseGroup(keyCode, value);
            }
            
            return;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ParseException("Line: " + linecount +
                " unsupported groupcode: " + key + " for value:" + value, e);
        }
    }



    public void addDXFSectionHandler(DXFSectionHandler handler) {
        handler.setDocument(doc);
        handlers.put(handler.getSectionKey(), handler);
    }

    public void addHandler(DXFHandler handler) {
        addDXFSectionHandler((DXFSectionHandler) handler);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kabeja.parser.Parser#releaseDXFDocument()
     */
    public void releaseDocument() {
        this.doc = null;

        Iterator i = handlers.values().iterator();

        while (i.hasNext()) {
            DXFHandler handler = (DXFHandler) i.next();
            handler.releaseDocument();
        }
    }


    public boolean supportedExtension(String extension) {
        return extension.toLowerCase().equals(EXTENSION);
    }

    public void addDXFStreamFilter(DXFStreamFilter filter) {
        this.streamFilters.add(filter);
    }

    public void removeDXFStreamFilter(DXFStreamFilter filter) {
        this.streamFilters.remove(filter);
    }

    protected void buildFilterChain() {
        // build the chain from end to start
        // the parser itself is the last element
        // in the chain
        DXFHandler handler = this;

        for (int i = this.streamFilters.size() - 1; i >= 0; i--) {
            DXFStreamFilter f = (DXFStreamFilter) this.streamFilters.get(i);
            f.setDXFHandler(handler);
            handler = f;
        }

        // the first is used filter and if no filter
        // the parser itself is the filter
        this.filter = handler;
    }

    public String getName() {
        return PARSER_NAME;
    }



	public void setDocument(DraftDocument doc) {
		this.doc = doc;
		
	}






 
}
