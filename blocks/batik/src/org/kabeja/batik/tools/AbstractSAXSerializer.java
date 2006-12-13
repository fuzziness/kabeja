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
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.kabeja.xml.AbstractSAXFilter;
import org.kabeja.xml.SAXSerializer;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public abstract class AbstractSAXSerializer extends AbstractSAXFilter implements
		SAXSerializer {
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

	protected abstract Transcoder createTranscoder();

	protected Document document;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kabeja.xml.SAXSerializer#setProperties(java.util.Map)
	 */
	public void setProperties(Map properties) {
		if (properties.containsKey(PROPERTY_WIDTH)) {
			this.width = Float.parseFloat((String) properties
					.get(PROPERTY_WIDTH));
		}

		if (properties.containsKey(PROPERTY_HEIGHT)) {
			this.height = Float.parseFloat((String) properties
					.get(PROPERTY_HEIGHT));
		}

		if (properties.containsKey(PROPERTY_QUALITY)) {
			this.quality = Double.parseDouble((String) properties
					.get(PROPERTY_QUALITY));
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
			TranscoderInput transInput = new TranscoderInput(this.document);

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#startDocument()
	 */
	public void startDocument() throws SAXException {

		try {
			SAXTransformerFactory factory = (SAXTransformerFactory) SAXTransformerFactory
					.newInstance();
			TransformerHandler f = factory.newTransformerHandler();
			this.document = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().newDocument();

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

}
