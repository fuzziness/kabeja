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
package org.kabeja.cocoon.generation;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.caching.CacheableProcessingComponent;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.generation.AbstractGenerator;
import org.apache.cocoon.util.HashUtil;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceValidity;
import org.apache.excalibur.source.impl.validity.TimeStampValidity;
import org.kabeja.dxf.DXFDocument;
import org.kabeja.parser.DXFParser;
import org.kabeja.parser.Handler;
import org.kabeja.parser.ParseException;
import org.kabeja.parser.Parser;
import org.kabeja.parser.ParserBuilder;
import org.kabeja.parser.SAXParserBuilder;
import org.kabeja.svg.SVGGenerator;
import org.kabeja.xml.SAXGenerator;
import org.xml.sax.SAXException;


/**
 * This generator parses DXF-Sources and transform the data to SVG.
 *
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 *
 */
public class DXF2SVGGenerator extends AbstractGenerator
    implements CacheableProcessingComponent, Parameterizable {
    public final static String PARAMETER_CONFIG = "configfile";
    public final static String PARAMETER_ENCODING = "encoding";
    private SourceResolver resolver;
    private String src;
    protected Source inputSource;
    protected String config = null;
    protected boolean configured = false;
    protected Logger log;
    protected Parser parser;
    protected String encoding;

    /*
     * (non-Javadoc)
     *
     * @see org.apache.cocoon.sitemap.SitemapModelComponent#setup(org.apache.cocoon.environment.SourceResolver,
     *      java.util.Map, java.lang.String,
     *      org.apache.avalon.framework.parameters.Parameters)
     */
    public void setup(SourceResolver resolver, Map objectModel, String src,
        Parameters par) throws ProcessingException, SAXException, IOException {
        super.setup(resolver, objectModel, src, par);

        // this is what we need
        this.resolver = resolver;
        this.src = src;
        this.inputSource = resolver.resolveURI(src);

        if (par.isParameter(PARAMETER_ENCODING)) {
            try {
                this.encoding = par.getParameter(PARAMETER_ENCODING);
            } catch (ParameterException e) {
                log.error("Setting up encoding error:" + e.getMessage());
                this.encoding = DXFParser.DEFAULT_ENCODING;
            }
        } else {
            this.encoding = DXFParser.DEFAULT_ENCODING;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.cocoon.generation.Generator#generate()
     */
    public void generate()
        throws IOException, SAXException, ProcessingException {
        try {
            this.parser.parse(this.inputSource.getInputStream(), this.encoding);

            DXFDocument doc = parser.getDocument();

            // the xmlConsumer the next component in
            // the pipeline from parent class
            SAXGenerator generator = new SVGGenerator();
            generator.setProperties(new HashMap());
            generator.generate(doc, this.xmlConsumer, null);

            // all is done release the source
            this.resolver.release(this.inputSource);

            // a little help for the GC
            ((Handler) this.parser).releaseDXFDocument();

            doc = null;
        } catch (ParseException e) {
            throw new ProcessingException(e);
        } catch (IOException e) {
            throw new ProcessingException(e);
        }
    }

    public Serializable getKey() {
        // the key the sourc-uri this is unique
        if (this.inputSource.getLastModified() != 0) {
            return new Long(HashUtil.hash(this.inputSource.getURI()));
        }

        return new Long(0);
    }

    public SourceValidity getValidity() {
        // the validity is simple the last modified timestamp
        long modified = this.inputSource.getLastModified();

        return new TimeStampValidity(modified);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.avalon.framework.logger.LogEnabled#enableLogging(org.apache.avalon.framework.logger.Logger)
     */
    public void enableLogging(Logger log) {
        this.log = log;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.avalon.framework.parameters.Parameterizable#parameterize(org.apache.avalon.framework.parameters.Parameters)
     */
    public void parameterize(Parameters params) throws ParameterException {
        if (params.isParameter(PARAMETER_CONFIG)) {
            try {
                this.initParser(params.getParameter(PARAMETER_CONFIG));
            } catch (Exception e) {
                log.error("Configuration failed:" + e.getMessage());
                throw new ParameterException("DXFParserConfiguration");
            }
        } else {
            initParser();
        }
    }

    protected void initParser() {
        this.parser = ParserBuilder.createDefaultParser();
    }

    protected void initParser(String config) throws Exception {
        // TODO use the sourceresolver
        this.parser = SAXParserBuilder.buildFromStream(new FileInputStream(
                    config));
    }
}
