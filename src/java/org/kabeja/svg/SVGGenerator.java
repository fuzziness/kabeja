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
package org.kabeja.svg;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import org.kabeja.dxf.Bounds;
import org.kabeja.dxf.DXFBlock;
import org.kabeja.dxf.DXFEntity;
import org.kabeja.dxf.DXFLayer;
import org.kabeja.dxf.DXFStyle;
import org.kabeja.dxf.DXFVariable;
import org.kabeja.xml.AbstractSAXGenerator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class SVGGenerator extends AbstractSAXGenerator {

	public final static String PROPERTY_MARGIN = "margin";

	public final static String PROPERTY_PAPERSPACE = "paperspace";

	public final static String PROPERTY_MODELSPACE = "modelspace";

	public final static String PROPERTY_STROKE_WIDTH = "stroke-width";

	public final static String PROPERTY_DOCUMENTBOUNDS = "useBounds";

	public final static String PROPERTY_WIDTH = "width";

	public final static String PROPERTY_HEIGHT = "height";

	public final static String PROPERTY_OVERFLOW = "svg.overflow";

	public static final double DEFAULT_MARGIN_PERCENT = 1.0;

	public final static String SUPPORTED_SVG_VERSION = "1.0";

	private double margin;

	private boolean paperspace = true;

	private boolean modelspace = true;

	private Map context;

	private boolean overflow = true;

	private boolean useBounds = true;

	private String marginSettings;

	protected void generate() {
		// TODO here should be the insert point for the
		// converstion in a later release
		// create for every DXF class a converter class
		this.setupProperties();
		this.generateSAX();
	}

	protected void setupProperties() {

		if (this.context == null) {
			this.context = new HashMap();
		}

		// setup the properties

		// the margin
		if (this.properties.containsKey(PROPERTY_MARGIN)) {
			this.marginSettings = (String) this.properties.get(PROPERTY_MARGIN);
		}

		// use paperspace
		if (this.properties.containsKey(PROPERTY_PAPERSPACE)) {
			this.paperspace = Boolean.valueOf(
					(String) this.properties.get(PROPERTY_PAPERSPACE))
					.booleanValue();
		}
		// use modelspace
		if (this.properties.containsKey(PROPERTY_MODELSPACE)) {
			this.modelspace = Boolean.valueOf(
					(String) this.properties.get(PROPERTY_MODELSPACE))
					.booleanValue();
		}

		if (this.properties.containsKey(PROPERTY_OVERFLOW)) {
			this.overflow = Boolean.valueOf(
					(String) this.properties.get(PROPERTY_OVERFLOW))
					.booleanValue();
		}

		if (this.properties.containsKey(PROPERTY_DOCUMENTBOUNDS)) {
			this.useBounds = Boolean.valueOf(
					(String) this.properties.get(PROPERTY_DOCUMENTBOUNDS))
					.booleanValue();
		}
	}

	private void generateSAX() {
		try {
			
		
			
			this.handler.startDocument();

			
			
			
			
			AttributesImpl attr = new AttributesImpl();

			// add the viewport
			// with margin
			// this is important otherwise in most cases
			// the SVG-Viewer will not show the content
			String viewport = "";
			Bounds bounds = this.doc.getBounds();
			double width = 0.0;
			double height = 0.0;

			// TODO this should be configurable

			if (this.doc.getDXFHeader().hasVariable("$PEXTMAX")
					&& this.doc.getDXFHeader().hasVariable("$PEXTMMIN")
					&& !this.useBounds) {
				DXFVariable min = this.doc.getDXFHeader().getVariable(
						"$PEXTMIN");
				DXFVariable max = this.doc.getDXFHeader().getVariable(
						"$PEXTMAX");
				double x = min.getDoubleValue("10");
				double y = min.getDoubleValue("20");
				double max_y = max.getDoubleValue("20");
				width = max.getDoubleValue("10") - x;
				height = max_y - y;

				viewport = "" + x + " " + ((-1.0) * max_y) + " "
						+ Math.abs(width) + " " + Math.abs(height);

			} else {

				double[] margin = this.getMargin(bounds);
				width = bounds.getWidth() + (margin[1] + margin[3]);
				height = bounds.getHeight() + (margin[0] + margin[2]);

				viewport = "" + (bounds.getMinimumX() - margin[3]) + " "
						+ ((-1 * bounds.getMaximumY()) - margin[0]) + "  "
						+ SVGUtils.formatNumberAttribute(width) + " "
						+ SVGUtils.formatNumberAttribute(height);
			}

			SVGUtils.addAttribute(attr, "viewBox", viewport);

			if (this.properties.containsKey(PROPERTY_WIDTH)) {
				SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_WIDTH,
						(String) this.properties.get(PROPERTY_WIDTH));
			}

			if (this.properties.containsKey(PROPERTY_HEIGHT)) {
				SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_HEIGHT,
						(String) this.properties.get(PROPERTY_HEIGHT));
			}

			// set the default namespace
			SVGUtils.addAttribute(attr, "xmlns", SVGConstants.SVG_NAMESPACE);

			// the version of SVG we generate now
			SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_VERSION,
					SUPPORTED_SVG_VERSION);

			// the overflow
			if (this.overflow) {
				SVGUtils.addAttribute(attr,
						SVGConstants.SVG_ATTRIBUTE_OVERFLOW,
						SVGConstants.SVG_ATTRIBUTEVALUE_VISIBLE);
			}

			SVGUtils.startElement(this.handler, SVGConstants.SVG_ROOT, attr);

			// the blocks as symbol in the defs-section of SVG
			attr = new AttributesImpl();
			SVGUtils.startElement(this.handler, SVGConstants.SVG_DEFS, attr);

			// set the context
			context.put(SVGContext.DRAFT_BOUNDS, bounds);

			double dotLength = 0.0;

			if (bounds.getWidth() > bounds.getHeight()) {
				dotLength = bounds.getHeight()
						* SVGConstants.DEFAULT_STROKE_WIDTH_PERCENT;
			} else {
				dotLength = bounds.getWidth()
						* SVGConstants.DEFAULT_STROKE_WIDTH_PERCENT;
			}

			context.put(SVGContext.DOT_LENGTH, new Double(dotLength));

			Iterator i = this.doc.getDXFBlockIterator();

			while (i.hasNext()) {
				DXFBlock block = (DXFBlock) i.next();
				this.blockToSAX(block);

			}

			// maybe there is a fontdescription available from DXFStyle
			i = this.doc.getDXFStyleIterator();

			while (i.hasNext()) {
				DXFStyle style = (DXFStyle) i.next();
				style.toSAX(handler, context);
			}

			// i = this.doc.getDXFHatchPatternIterator();
			//
			// while (i.hasNext()) {
			// DXFHatchPattern pattern = (DXFHatchPattern) i.next();
			// pattern.toSAX(handler, context);
			// }

			SVGUtils.endElement(handler, SVGConstants.SVG_DEFS);

			// the draft
			attr = new AttributesImpl();
			SVGUtils.addAttribute(attr, "id", "draft");

			// the globale coordinate system transformation
			// note: DXF has the y-axis positiv from bottom to top
			// SVG has the y-axis positiv from top to bottom
			SVGUtils.addAttribute(attr, "transform", "matrix(1 0 0 -1 0 0)");

			// the stroke-width
			if (this.context.containsKey(SVGContext.STROKE_WIDTH)) {
				SVGUtils.addAttribute(attr, "stroke-width", ""
						+ this.context.get(SVGContext.STROKE_WIDTH));
			} else {

				SVGUtils.addAttribute(attr, "stroke-width", ""
						+ SVGConstants.DEFAULT_STROKE_WIDTH_PERCENT + '%');
			}

			SVGUtils.startElement(handler, SVGConstants.SVG_GROUP, attr);

			// the layers as container g-elements
			i = this.doc.getDXFLayerIterator();

			while (i.hasNext()) {
				DXFLayer layer = (DXFLayer) i.next();
				this.layerToSAX(layer);
			}

			SVGUtils.endElement(handler, SVGConstants.SVG_GROUP);
			SVGUtils.endElement(handler, SVGConstants.SVG_ROOT);
			handler.endDocument();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	protected void blockToSAX(DXFBlock block) {
		try {
			AttributesImpl attr = new AttributesImpl();
			SVGUtils.addAttribute(attr, "id", SVGUtils.validateID(block
					.getName()));

			SVGUtils.startElement(handler, SVGConstants.SVG_GROUP, attr);

			Iterator i = block.getDXFEntitiesIterator();

			while (i.hasNext()) {
				DXFEntity entity = (DXFEntity) i.next();
				this.entityToSAX(entity);
			}

			SVGUtils.endElement(handler, SVGConstants.SVG_GROUP);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	protected void entityToSAX(DXFEntity entity) {
		try {
			if (this.paperspace) {
				if (!entity.isModelSpace()) {
					entity.toSAX(this.handler, this.properties);
				}
			}
			if (this.modelspace) {
				if (entity.isModelSpace()) {
					entity.toSAX(this.handler, this.properties);
				}
			}
		} catch (SAXException e) {

			e.printStackTrace();
		}
	}

	protected void layerToSAX(DXFLayer layer) {
		try {
			layer.toSAX(this.handler, this.context);
		} catch (SAXException e) {

			e.printStackTrace();
		}
	}

	/**
	 * Returns the margin-array where:
	 * <ul>
	 * <li>0 ->top margin</li>
	 * <li>1 ->right margin</li>
	 * <li>2 ->bottom margin</li>
	 * <li>3 ->left margin</li>
	 * </ul>
	 * 
	 * @param bounds
	 * @return
	 */

	protected double[] getMargin(Bounds bounds) {
		double[] margin = new double[4];
		if (this.marginSettings != null) {
		
			StringTokenizer st = new StringTokenizer(this.marginSettings);
			int count = st.countTokens();
			switch (count) {
			case 4:
				for (int i = 0; i < count; i++) {
					String m = st.nextToken().trim();
				
					if (m.endsWith("%")) {
                           m = m.substring(0,m.length()-1);
                         
						if (i == 0 && i == 2) {

							margin[i] = (Double.parseDouble(m)/100)
									* bounds.getHeight();

						} else {
							margin[i] = (Double.parseDouble(m)/100)
									* bounds.getWidth();
						}

					} else {
						margin[i] = Double.parseDouble(m);
					}
				}
				return margin;
			case 1:
				String m = st.nextToken().trim();
				if (m.endsWith("%")) {
                    m = m.substring(0,m.length()-1);
				}
				margin[0] = Double.parseDouble(m);
				margin[1] = margin[2] = margin[3] = margin[0];
				return margin;

			}
		}
		margin[0] = bounds.getHeight() * (DEFAULT_MARGIN_PERCENT/100);
		margin[2] = margin[0];
		margin[1] = bounds.getWidth() * (DEFAULT_MARGIN_PERCENT/100);
		margin[3] = margin[1];

		return margin;
	}
}
