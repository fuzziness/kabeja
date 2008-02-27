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
package org.kabeja.batik.tools;

import java.io.OutputStream;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;

import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.kabeja.xml.AbstractSAXFilter;
import org.kabeja.xml.SAXSerializer;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;


public abstract class AbstractSAXSerializer extends AbstractSAXFilter
    implements SAXSerializer {
    public static final String SUFFIX_JPEG = "jpg";
    public static final String SUFFIX_PNG = "png";
    public static final String SUFFIX_TIFF = "tif";
    public static final String SUFFIX_PDF = "pdf";
    public static final String MIME_TYPE_JPEG = "image/jepg";
    public static final String MIME_TYPE_PNG = "image/png";
    public static final String MIME_TYPE_TIFF = "image/tiff";
    public static final String MIME_TYPE_PDF = "application/pdf";

    /**
     * Allows you to setup a width in px,inch,mm,cm,pt. This property overrides the PAPER_PROPERTY.
     */
    public static final String PROPERTY_WIDTH = "width";

    /**
     * Allows you to setup a height in px,inch,mm,cm,pt. This property overrides the PAPER_PROPERTY.
     */
    public static final String PROPERTY_HEIGHT = "height";

    /**
     * Allows you to setup the dpi
     */
    public static final String PROPERTY_DPI = "dpi";
    public static final String PROPERTY_QUALITY = "quality";

    /**
     * You can choose A0-A6 papers and Letter.
     */
    public static final String PROPERTY_PAPER = "paper";

    /**
     * Lets you choose the orientation of a paper (landscape).
     */
    public static final String PROPERTY_ORIENTATION = "orientation";
    public static final float INCH_TO_MM = 25.4f;
    public static final float PT_TO_MM = 0.3527777777777f;
    public int DPI = 96;
    public float PIXEL_UNIT_TO_MM = INCH_TO_MM / DPI;
    protected OutputStream out;
    protected float width;
    protected float height;
    protected double quality = 0.9;
    protected Transcoder transcoder;
    protected boolean scaleToFit = true;
    protected Document document;

    protected abstract Transcoder createTranscoder();

    /*
     * (non-Javadoc)
     *
     * @see org.kabeja.xml.SAXSerializer#setProperties(java.util.Map)
     */
    public void setProperties(Map properties) {
        if (properties.containsKey(PROPERTY_DPI)) {
            this.DPI = Integer.parseInt((String) properties.get(PROPERTY_DPI));
            this.PIXEL_UNIT_TO_MM = INCH_TO_MM / this.DPI;
        }

        if (properties.containsKey(PROPERTY_PAPER)) {
            this.parsePaper(((String) properties.get(PROPERTY_PAPER)).toLowerCase());
        }

        if (properties.containsKey(PROPERTY_QUALITY)) {
            this.quality = Double.parseDouble((String) properties.get(
                        PROPERTY_QUALITY));
        }

        if (properties.containsKey(PROPERTY_WIDTH)) {
            this.width = unitsToPixel(((String) properties.get(PROPERTY_WIDTH)).trim());
        }

        if (properties.containsKey(PROPERTY_HEIGHT)) {
            this.height = unitsToPixel(((String) properties.get(PROPERTY_HEIGHT)).trim());
        }

        if (properties.containsKey(PROPERTY_ORIENTATION)) {
            String orientation = ((String) properties.get(PROPERTY_ORIENTATION)).toLowerCase();

            if (orientation.equals("landscape")) {
                float w = this.width;
                this.width = this.height;
                this.height = w;
            }
        }
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
     * @see org.xml.sax.ContentHandler#endDocument()
     */
    public void endDocument() throws SAXException {
        super.endDocument();

        try {
            transcoder = createTranscoder();
            setupTranscoder(transcoder);

            TranscoderInput transInput = new TranscoderInput(this.document);

            // Buffering is done by the pipeline (See shouldSetContentLength)
            TranscoderOutput transOutput = new TranscoderOutput(this.out);
            setupTranscoder(transcoder);

            transcode(transInput, transOutput);
        } catch (TranscoderException e) {
            throw new SAXException(e);
        }
    }

    /**
     * Override this if your Transcoder support more
     * options.
     * @param t
     */
    protected void setupTranscoder(Transcoder t) {
        transcoder.addTranscodingHint(ImageTranscoder.KEY_PIXEL_UNIT_TO_MILLIMETER,
            new Float(this.PIXEL_UNIT_TO_MM));

        if (this.width > 0.0) {
            transcoder.addTranscodingHint(ImageTranscoder.KEY_WIDTH,
                new Float(this.width));
        }

        if (this.height > 0.0) {
            transcoder.addTranscodingHint(ImageTranscoder.KEY_HEIGHT,
                new Float(this.height));
        }
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
            this.document = DocumentBuilderFactory.newInstance()
                                                  .newDocumentBuilder()
                                                  .newDocument();

            // put the the transformer in the chain
            f.setResult(new DOMResult(document));

            super.setContentHandler(f);
        } catch (TransformerConfigurationException e) {
            throw new SAXException(e);
        } catch (IllegalArgumentException e) {
            throw new SAXException(e);
        } catch (TransformerFactoryConfigurationError e) {
            throw new SAXException(e.getException());
        } catch (ParserConfigurationException e) {
            throw new SAXException(e);
        }

        super.startDocument();
    }

    protected void transcode(TranscoderInput input, TranscoderOutput output)
        throws TranscoderException {
        transcoder.transcode(input, output);
    }

    protected float unitsToPixel(String size) {
        if (size.endsWith("px")) {
            return Float.parseFloat(size.substring(0, size.length() - 2));
        } else if (size.endsWith("in")) {
            return (Float.parseFloat(size.substring(0, size.length() - 2)) * INCH_TO_MM) / PIXEL_UNIT_TO_MM;
        } else if (size.endsWith("pt")) {
            return (Float.parseFloat(size.substring(0, size.length() - 2)) * PT_TO_MM) / PIXEL_UNIT_TO_MM;
        } else if (size.endsWith("cm")) {
            float units = Float.parseFloat(size.substring(0, size.length() - 2));
            float pixel = (float) ((units * 100) / PIXEL_UNIT_TO_MM);

            return pixel;
        } else if (size.endsWith("mm")) {
            float units = Float.parseFloat(size.substring(0, size.length() - 2));
            float pixel = (float) (units / PIXEL_UNIT_TO_MM);

            return pixel;
        } else if (size.endsWith("m")) {
            float units = Float.parseFloat(size.substring(0, size.length() - 1));
            float pixel = (float) ((units * 1000) / PIXEL_UNIT_TO_MM);

            return pixel;
        } else {
            return Float.parseFloat(size);
        }
    }

    protected void parsePaper(String paper) {
        if (paper.equals("a0")) {
            this.width = 841 / PIXEL_UNIT_TO_MM;
            this.height = 1189 / PIXEL_UNIT_TO_MM;
        } else if (paper.equals("a1")) {
            this.width = 594 / PIXEL_UNIT_TO_MM;
            this.height = 841 / PIXEL_UNIT_TO_MM;
        } else if (paper.equals("a2")) {
            this.width = 420 / PIXEL_UNIT_TO_MM;
            this.height = 594 / PIXEL_UNIT_TO_MM;
        } else if (paper.equals("a3")) {
            this.width = 297 / PIXEL_UNIT_TO_MM;
            this.height = 420 / PIXEL_UNIT_TO_MM;
        } else if (paper.equals("a4")) {
            this.width = 210 / PIXEL_UNIT_TO_MM;
            this.height = 297 / PIXEL_UNIT_TO_MM;
        } else if (paper.equals("a5")) {
            this.width = 148 / PIXEL_UNIT_TO_MM;
            this.height = 210 / PIXEL_UNIT_TO_MM;
        } else if (paper.equals("a6")) {
            this.width = 105 / PIXEL_UNIT_TO_MM;
            this.height = 148 / PIXEL_UNIT_TO_MM;
        } else if (paper.equals("letter")) {
            this.width = 216 / PIXEL_UNIT_TO_MM;
            this.height = 279 / PIXEL_UNIT_TO_MM;
        }

        //add more papers here
    }

    public Map getProperties() {
        return this.properties;
    }
}
