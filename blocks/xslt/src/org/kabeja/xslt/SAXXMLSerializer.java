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
package org.kabeja.xslt;

import java.io.OutputStream;
import java.util.Map;

import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.kabeja.xml.SAXSerializer;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 *
 */
public class SAXXMLSerializer extends XMLFilterImpl implements SAXSerializer {
    public static final String MIME_TYPE = "text/xml";
    public static final String SUFFIX = "xml";
    private OutputStream out;
    private Map properties;

    /*
     * (non-Javadoc)
     *
     * @see org.kabeja.xml.SAXSerializer#getMimeType()
     */
    public String getMimeType() {
        // TODO Auto-generated method stub
        return MIME_TYPE;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kabeja.xml.SAXSerializer#getSuffix()
     */
    public String getSuffix() {
        // TODO Auto-generated method stub
        return SUFFIX;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kabeja.xml.SAXSerializer#setOutput(java.io.OutputStream)
     */
    public void setOutput(OutputStream out) {
        this.out = out;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kabeja.xml.SAXSerializer#setProperties(java.util.Map)
     */
    public void setProperties(Map properties) {
        this.properties = properties;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#startDocument()
     */
    public void startDocument() throws SAXException {
        try {
            SAXTransformerFactory factory = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
            TransformerHandler f = factory.newTransformerHandler();

            // put the the transformer in the chain
            f.setResult(new StreamResult(out));
            super.setContentHandler(f);
        } catch (Exception e) {
            throw new SAXException(e);
        }

        super.startDocument();
    }

    public Map getProperties() {
        return this.properties;
    }
}
