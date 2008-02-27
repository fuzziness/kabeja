/*
   Copyright 2006 Simon Mieth

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
package org.kabeja.cocoon.transformation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.transformation.AbstractTransformer;
import org.apache.cocoon.util.ClassUtils;
import org.kabeja.xml.SAXFilter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;


/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 */
public class SAXFilter2TransformerAdapter extends AbstractTransformer
    implements Parameterizable {
    public static final String PARAMETER_SAXFILTER_CLASS = "filter.class";
    protected SAXFilter filter;

    public void parameterize(Parameters arg0) throws ParameterException {
        try {
            String clazz = arg0.getParameter(PARAMETER_SAXFILTER_CLASS);

            if (clazz != null) {
                this.filter = (SAXFilter) ClassUtils.newInstance(clazz);
            } else {
                throw new ParameterException("Missing filter.class parameter");
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.getLogger().error(e.getLocalizedMessage());
            throw new ParameterException(e.getLocalizedMessage());
        }
    }

    public void setup(SourceResolver arg0, Map arg1, String arg2,
        Parameters arg3) throws ProcessingException, SAXException, IOException {
        this.filter.setContentHandler(this.xmlConsumer);

        Map properties = new HashMap();
        String[] parameters = arg3.getNames();

        try {
            for (int i = 0; i < parameters.length; i++) {
                properties.put(parameters[i], arg3.getParameter(parameters[i]));
            }
        } catch (ParameterException e) {
            this.getLogger().error(e.getLocalizedMessage());
        }

        this.filter.setProperties(properties);
    }

    public void characters(char[] arg0, int arg1, int arg2)
        throws SAXException {
        this.filter.characters(arg0, arg1, arg2);
    }

    public void endDocument() throws SAXException {
        this.filter.endDocument();
    }

    public void endElement(String arg0, String arg1, String arg2)
        throws SAXException {
        this.filter.endElement(arg0, arg1, arg2);
    }

    public void endPrefixMapping(String arg0) throws SAXException {
        this.filter.endPrefixMapping(arg0);
    }

    public void ignorableWhitespace(char[] arg0, int arg1, int arg2)
        throws SAXException {
        this.filter.ignorableWhitespace(arg0, arg1, arg2);
    }

    public void processingInstruction(String arg0, String arg1)
        throws SAXException {
        this.filter.processingInstruction(arg0, arg1);
    }

    public void skippedEntity(String arg0) throws SAXException {
        this.filter.skippedEntity(arg0);
    }

    public void startElement(String arg0, String arg1, String arg2,
        Attributes arg3) throws SAXException {
        this.filter.startElement(arg0, arg1, arg2, arg3);
    }

    public void startPrefixMapping(String arg0, String arg1)
        throws SAXException {
        this.filter.startPrefixMapping(arg0, arg1);
    }

    public void startDocument() throws SAXException {
        this.filter.setContentHandler(this.xmlConsumer);
        this.filter.startDocument();
    }
}
