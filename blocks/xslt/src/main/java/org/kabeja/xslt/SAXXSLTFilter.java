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

import java.io.FileReader;
import java.util.Iterator;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;

import org.kabeja.xml.AbstractSAXFilter;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 *
 */
public class SAXXSLTFilter extends AbstractSAXFilter {
    public static final String PROPERTY_XSLTSTYLESHEET = "stylesheet";
    protected ContentHandler handler;
    protected String xsltSource;

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#startDocument()
     */
    public void startDocument() throws SAXException {
        if (this.properties.containsKey(PROPERTY_XSLTSTYLESHEET)) {
            try {
                SAXTransformerFactory factory = (SAXTransformerFactory) SAXTransformerFactory.newInstance();

                FileReader reader = new FileReader((String) this.properties.get(
                            PROPERTY_XSLTSTYLESHEET));

                SAXSource src = new SAXSource(new InputSource(reader));

                TransformerHandler f = factory.newTransformerHandler(src);
                setParameters(f);

                // put the the transformer in the chain
                f.setResult(new SAXResult(this.handler));
                super.setContentHandler(f);
            } catch (Exception e) {
                throw new SAXException(e);
            }
        }

        super.startDocument();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.XMLReader#setContentHandler(org.xml.sax.ContentHandler)
     */
    public void setContentHandler(ContentHandler handler) {
        this.handler = handler;
        super.setContentHandler(handler);
    }

    protected void setParameters(TransformerHandler h) {
        Transformer tf = h.getTransformer();
        Iterator i = this.properties.keySet().iterator();

        while (i.hasNext()) {
            String name = (String) i.next();

            if (!PROPERTY_XSLTSTYLESHEET.equals(i)) {
                tf.setParameter(name, this.properties.get(name));
            }
        }
    }

    public Map getProperties() {
        return this.properties;
    }
}
