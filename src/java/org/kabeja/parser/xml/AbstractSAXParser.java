/*
   Copyright 2009 Simon Mieth

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
package org.kabeja.parser.xml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.kabeja.dxf.DXFDocument;
import org.kabeja.parser.DXFParser;
import org.kabeja.parser.Handler;
import org.kabeja.parser.HandlerManager;
import org.kabeja.parser.ParseException;
import org.kabeja.parser.Parser;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public abstract class AbstractSAXParser extends DefaultHandler implements
        Parser, HandlerManager {

    protected DXFDocument doc;

    protected Map nsHandlers = new HashMap();

    protected Map noNSHandlers = new HashMap();

    protected XMLHandler currentHandler;
    protected boolean throughXMLHandler = false;

    protected ParsingContext context;

    protected boolean captureEvent = false;

    public void addHandler(XMLHandler xmlHandler) {

        Map elementHandlers = null;

        if (xmlHandler.getNamespaceHandle().length() == 0) {
            elementHandlers = noNSHandlers;
        } else if (this.nsHandlers.containsKey(xmlHandler.getNamespaceHandle())) {
            elementHandlers = (Map) this.nsHandlers.get(xmlHandler
                    .getNamespaceHandle());
        } else {
            elementHandlers = new HashMap();
            this.nsHandlers.put(xmlHandler.getNamespaceHandle(),
                    elementHandlers);

        }
        elementHandlers.put(xmlHandler.getElementNameHandle(), xmlHandler);

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kabeja.parser.Handler#releaseDXFDocument()
     */
    public void releaseDXFDocument() {

        this.doc = null;

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kabeja.parser.Handler#setDXFDocument(org.kabeja.dxf.DXFDocument)
     */
    public void setDXFDocument(DXFDocument doc) {
        this.doc = doc;

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kabeja.parser.Parser#getDocument()
     */
    public DXFDocument getDocument() {

        return this.doc;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kabeja.parser.Parser#parse(java.io.InputStream,
     * java.lang.String)
     */
    public void parse(InputStream input, String encoding) throws ParseException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            factory.setNamespaceAware(true);
            SAXParser parser = factory.newSAXParser();
            // start parsing
            parser.parse(input, this);

        } catch (Exception e) {
            throw new ParseException(e);

        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kabeja.parser.Parser#parse(java.lang.String, java.lang.String)
     */
    public void parse(String file, String encoding) throws ParseException {
        try {
            parse(new FileInputStream(file), encoding);
        } catch (FileNotFoundException e) {
            throw new ParseException(e);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kabeja.parser.Parser#parse(java.lang.String)
     */
    public void parse(String file) throws ParseException {
        parse(file, DXFParser.DEFAULT_ENCODING);

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        if (this.throughXMLHandler) {
            this.currentHandler.characters(ch, start, length);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String localName, String name)
            throws SAXException {
        if (this.captureEvent) {
            this.currentHandler.endParseElement(uri, localName, this.context);
            if (this.currentHandler.getNamespaceHandle().equals(uri) && this.currentHandler.getElementNameHandle().equals(localName)) {
                this.captureEvent = false;
                this.throughXMLHandler=false;
            }

        } else if(uri == null||uri.length()==0) {
            if(noNSHandlers.containsKey(localName)){
                this.currentHandler = (XMLHandler) noNSHandlers.get(localName);
                this.currentHandler.endParseElement(uri,localName, context);
                this.throughXMLHandler=false;
            }
            
        }if (nsHandlers.containsKey(uri)) {
            Map handlers = (Map) nsHandlers.get(uri);
            if (handlers.containsKey(localName)) {
                this.currentHandler = (XMLHandler) handlers.get(localName);
                this.currentHandler.endParseElement(uri, localName, context);
                this.throughXMLHandler=false;
            }
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
     * java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String localName, String name,
            Attributes attributes) throws SAXException {

        if (this.captureEvent) {
            this.fireStartParseElement(uri, localName, attributes);
        } else {
            if (uri == null || uri.length() == 0) {
                if (noNSHandlers.containsKey(localName)) {
                    this.currentHandler = (XMLHandler) noNSHandlers
                            .get(localName);
                    fireStartParseElement(uri, localName, attributes);
                }
            } else {
                if (this.nsHandlers.containsKey(uri)) {
                    Map handlers = (Map) nsHandlers.get(uri);
                    if (handlers.containsKey(localName)) {
                        this.currentHandler = (XMLHandler) handlers
                                .get(localName);
                        fireStartParseElement(uri, localName, attributes);
                    }
                }
            }
        }
    }

    protected void fireStartParseElement(String namespace, String name,
            Attributes attributes) throws SAXException {
        this.throughXMLHandler=true;
        this.captureEvent = this.currentHandler.startParseElement(namespace,
                name, attributes, this.context);
    }
    
    

    public void startDocument() throws SAXException {
        // check if a DXFDocument was given
        // before
        if (this.getDocument() == null) {
            this.setDXFDocument(new DXFDocument());
        }
        this.context = new ParsingContext(this.doc);
    }

}
