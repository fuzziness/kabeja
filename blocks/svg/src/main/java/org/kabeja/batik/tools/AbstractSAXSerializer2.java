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
package org.kabeja.batik.tools;

import java.io.OutputStream;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.kabeja.xml.SAXSerializer;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLReaderFactory;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 *
 */
public abstract class AbstractSAXSerializer2 extends SAXSVGDocumentFactory
    implements SAXSerializer {
    public static final String SUFFIX_JPEG = "jpg";
    public static final String SUFFIX_PNG = "png";
    public static final String SUFFIX_TIFF = "tif";
    public static final String SUFFIX_PDF = "pdf";
    public static final String MIME_TYPE_JPEG = "image/jepg";
    public static final String MIME_TYPE_PNG = "image/png";
    public static final String MIME_TYPE_TIFF = "image/tiff";
    public static final String MIME_TYPE_PDF = "application/pdf";
    public static final String PROPERTY_WIDTH = "width";
    public static final String PROPERTY_HEIGHT = "height";
    public static final String PROPERTY_QUALITY = "quality";
    protected OutputStream out;
    protected float width;
    protected float height;
    protected double quality = 0.9;
    protected Transcoder transcoder;

    public AbstractSAXSerializer2() {
        super(null);
        init();
        this.transcoder = createTranscoder();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kabeja.xml.SAXSerializer#getMimeType()
     */

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
        if (properties.containsKey(PROPERTY_WIDTH)) {
            this.width = Float.parseFloat((String) properties.get(
                        PROPERTY_WIDTH));
        }

        if (properties.containsKey(PROPERTY_HEIGHT)) {
            this.height = Float.parseFloat((String) properties.get(
                        PROPERTY_HEIGHT));
        }

        if (properties.containsKey(PROPERTY_QUALITY)) {
            this.quality = Double.parseDouble((String) properties.get(
                        PROPERTY_QUALITY));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#endDocument()
     */
    public void endDocument() throws SAXException {
        try {
            super.endDocument();

            TranscoderInput transInput = new TranscoderInput(super.document);

            // Buffering is done by the pipeline (See shouldSetContentLength)
            TranscoderOutput transOutput = new TranscoderOutput(this.out);

            if ((this.width > 0.0) && (this.height > 0.0)) {
                transcoder.addTranscodingHint(JPEGTranscoder.KEY_WIDTH,
                    new Float(this.width));

                transcoder.addTranscodingHint(JPEGTranscoder.KEY_HEIGHT,
                    new Float(this.height));
            }

            transcode(transInput, transOutput);
        } catch (TranscoderException e) {
            throw new SAXException(e);
        }
    }

    protected abstract Transcoder createTranscoder();

    protected void transcode(TranscoderInput input, TranscoderOutput output)
        throws TranscoderException {
        transcoder.transcode(input, output);
    }

    protected void init() {
        try {
            if (parserClassName != null) {
                parser = XMLReaderFactory.createXMLReader(parserClassName);
            } else {
                SAXParser saxParser = null;

                try {
                    saxParser = SAXParserFactory.newInstance().newSAXParser();
                } catch (ParserConfigurationException pce) {
                    pce.printStackTrace();
                }

                parser = saxParser.getXMLReader();
            }

            parser.setContentHandler(this);
            parser.setDTDHandler(this);
            parser.setEntityResolver(this);
            parser.setErrorHandler((errorHandler == null) ? this : errorHandler);

            parser.setFeature("http://xml.org/sax/features/namespaces", true);
            parser.setFeature("http://xml.org/sax/features/namespace-prefixes",
                true);
            parser.setFeature("http://xml.org/sax/features/validation",
                isValidating);
            parser.setProperty("http://xml.org/sax/properties/lexical-handler",
                this);
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }
}
