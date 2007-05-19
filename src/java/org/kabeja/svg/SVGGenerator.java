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

	/**
	 * This property defines the way of calculation/setup the bounds of the
	 * Document. Possible values are:
	 * <ul>
	 * <li>kabeja: Bounds calculate on the geometries. (default)</li>
	 * <li>paperspace: extracts the values of the limits from paperspace</li>
	 * <li>modelspace: extracts the values of the limits from modelspace</li>
	 * </ul>
	 */

	public final static int PROPERTY_DOCUMENTBOUNDS_RULE_KABEJA = 1;

	public final static int PROPERTY_DOCUMENTBOUNDS_RULE_PAPERSPACE = 2;

	public final static int PROPERTY_DOCUMENTBOUNDS_RULE_MODELSPACE = 3;

	public final static String PROPERTY_DOCUMENTBOUNDS_RULE_KABEJA_VALUE = "kabeja";

	public final static String PROPERTY_DOCUMENTBOUNDS_RULE_PAPERSPACE_VALUE = "paperspace";

	public final static String PROPERTY_DOCUMENTBOUNDS_RULE_MODELSPACE_VALUE = "modelspace";

	public final static String PROPERTY_DOCUMENTBOUNDS_RULE = "bounds-rule";

	public final static String PROPERTY_WIDTH = "width";

	public final static String PROPERTY_HEIGHT = "height";

	public final static String PROPERTY_OVERFLOW = "svg.overflow";

	public static final double DEFAULT_MARGIN_PERCENT = 0.0;

	public final static String SUPPORTED_SVG_VERSION = "1.0";

	private boolean paperspace = true;

	private boolean modelspace = true;

	private Map context;

	private boolean overflow = true;

	private boolean useBounds = true;

	private int boundsRule = PROPERTY_DOCUMENTBOUNDS_RULE_KABEJA;

	private String marginSettings;

	protected void generate() throws SAXException {
		// TODO here should be the insert point for the
		// converstion in a later release
		// create for every DXF class a converter class
		this.setupProperties();
		this.generateSAX();
	}

	protected void setupProperties() {
		System.out.println("setup props=" + this.properties.size());
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
		if (this.properties.containsKey(PROPERTY_DOCUMENTBOUNDS_RULE)) {

			String value = ((String) this.properties
					.get(PROPERTY_DOCUMENTBOUNDS_RULE)).trim().toLowerCase();
			System.out.println("setup_rule=" + value);
			if (value.equals(PROPERTY_DOCUMENTBOUNDS_RULE_KABEJA_VALUE)) {
				this.boundsRule = PROPERTY_DOCUMENTBOUNDS_RULE_KABEJA;
			} else if (value
					.equals(PROPERTY_DOCUMENTBOUNDS_RULE_PAPERSPACE_VALUE)) {
				this.boundsRule = PROPERTY_DOCUMENTBOUNDS_RULE_PAPERSPACE;
			} else if (value
					.equals(PROPERTY_DOCUMENTBOUNDS_RULE_MODELSPACE_VALUE)) {
				this.boundsRule = PROPERTY_DOCUMENTBOUNDS_RULE_MODELSPACE;
			}
		}
	}

	private void generateSAX() throws SAXException {
		try {

			this.handler.startDocument();

			AttributesImpl attr = new AttributesImpl();

			// add the viewport
			// with margin
			// this is important otherwise in most cases
			// the SVG-Viewer will not show the content
			String viewport = "";
			Bounds bounds = this.getBounds();

			viewport = SVGUtils.formatNumberAttribute(bounds.getMinimumX())
					+ " "
					+ SVGUtils
							.formatNumberAttribute((-1 * bounds.getMaximumY()))
					+ "  " + SVGUtils.formatNumberAttribute(bounds.getWidth())
					+ " " + SVGUtils.formatNumberAttribute(bounds.getHeight());

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

	protected void blockToSAX(DXFBlock block) throws SAXException {

		AttributesImpl attr = new AttributesImpl();
		SVGUtils.addAttribute(attr, "id", SVGUtils.validateID(block.getName()));

		SVGUtils.startElement(handler, SVGConstants.SVG_GROUP, attr);

		Iterator i = block.getDXFEntitiesIterator();

		while (i.hasNext()) {
			DXFEntity entity = (DXFEntity) i.next();
			this.entityToSAX(entity);
		}

		SVGUtils.endElement(handler, SVGConstants.SVG_GROUP);

	}

	protected void entityToSAX(DXFEntity entity) throws SAXException {

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

	}

	protected void layerToSAX(DXFLayer layer) throws SAXException {

		layer.toSAX(this.handler, this.context);

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
						m = m.substring(0, m.length() - 1);

						if (i == 0 && i == 2) {

							margin[i] = (Double.parseDouble(m) / 100)
									* bounds.getHeight();

						} else {
							margin[i] = (Double.parseDouble(m) / 100)
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
					m = m.substring(0, m.length() - 1);
				}
				margin[0] = Double.parseDouble(m);
				margin[1] = margin[2] = margin[3] = margin[0];
				return margin;

			}
		}

		margin[0] = bounds.getHeight() * (DEFAULT_MARGIN_PERCENT / 100);
		margin[2] = margin[0];
		margin[1] = bounds.getWidth() * (DEFAULT_MARGIN_PERCENT / 100);
		margin[3] = margin[1];

		return margin;
	}

	protected Bounds getBounds() {

		Bounds bounds = null;

		if (this.boundsRule == PROPERTY_DOCUMENTBOUNDS_RULE_PAPERSPACE) {
			// first the user based limits of the paperspace

			bounds = new Bounds();
			if (this.doc.getDXFHeader().hasVariable("$PEXTMAX")
					&& this.doc.getDXFHeader().hasVariable("$PEXTMIN")) {
				DXFVariable min = this.doc.getDXFHeader().getVariable(
						"$PEXTMIN");
				DXFVariable max = this.doc.getDXFHeader().getVariable(
						"$PEXTMAX");

				bounds.setMinimumX(min.getDoubleValue("10"));
				bounds.setMinimumY(min.getDoubleValue("20"));
				bounds.setMaximumX(max.getDoubleValue("10"));
				bounds.setMaximumY(max.getDoubleValue("20"));

			}
			if (bounds.getWidth() == 0.0 || bounds.getHeight() == 0.0) {
				DXFVariable min = this.doc.getDXFHeader().getVariable(
						"$PLIMMIN");
				DXFVariable max = this.doc.getDXFHeader().getVariable(
						"$PLIMMAX");
				bounds = new Bounds();
				bounds.setMinimumX(min.getDoubleValue("10"));
				bounds.setMinimumY(min.getDoubleValue("20"));
				bounds.setMaximumX(max.getDoubleValue("10"));
				bounds.setMaximumY(max.getDoubleValue("20"));

			}

		} else if (this.boundsRule == PROPERTY_DOCUMENTBOUNDS_RULE_MODELSPACE) {
			// first the user based limits of the modelspace
			bounds = new Bounds();

			if (this.doc.getDXFHeader().hasVariable("$EXTMAX")

			&& this.doc.getDXFHeader().hasVariable("$EXTMIN")) {
				DXFVariable min = this.doc.getDXFHeader()
						.getVariable("$EXTMIN");
				DXFVariable max = this.doc.getDXFHeader()
						.getVariable("$EXTMAX");
				bounds = new Bounds();
				bounds.setMinimumX(min.getDoubleValue("10"));
				bounds.setMinimumY(min.getDoubleValue("20"));
				bounds.setMaximumX(max.getDoubleValue("10"));
				bounds.setMaximumY(max.getDoubleValue("20"));

			}
			if (bounds.getWidth() == 0.0 || bounds.getHeight() == 0.0) {
				DXFVariable min = this.doc.getDXFHeader()
						.getVariable("$LIMMIN");
				DXFVariable max = this.doc.getDXFHeader()
						.getVariable("$LIMMAX");

				bounds.setMinimumX(min.getDoubleValue("10"));
				bounds.setMinimumY(min.getDoubleValue("20"));
				bounds.setMaximumX(max.getDoubleValue("10"));
				bounds.setMaximumY(max.getDoubleValue("20"));

			}
		} else if (this.boundsRule == PROPERTY_DOCUMENTBOUNDS_RULE_KABEJA) {
			bounds = this.doc.getBounds();

		}
		if (bounds == null || bounds.getWidth() == 0.0
				|| bounds.getHeight() == 0.0) {
			bounds = this.doc.getBounds();

		}

		// set a margin
		double[] margin = this.getMargin(bounds);
		bounds.setMinimumX(bounds.getMinimumX() - margin[3]);
		bounds.setMaximumX(bounds.getMaximumX() + margin[1]);
		bounds.setMinimumY(bounds.getMinimumY() - margin[2]);
		bounds.setMaximumY(bounds.getMaximumY() + margin[0]);

		return bounds;
	}
}
