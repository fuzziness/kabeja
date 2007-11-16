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
package org.kabeja.dxf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kabeja.dxf.helpers.DXFMLineSegment;
import org.kabeja.dxf.helpers.DXFMLineSegmentElement;
import org.kabeja.dxf.helpers.DXFMLineStyleElementDistanceComparator;
import org.kabeja.dxf.helpers.DXFUtils;
import org.kabeja.dxf.helpers.MathUtils;
import org.kabeja.dxf.helpers.ParametricLine;
import org.kabeja.dxf.helpers.Point;
import org.kabeja.dxf.helpers.Vector;
import org.kabeja.dxf.objects.DXFMLineStyle;
import org.kabeja.dxf.objects.DXFMLineStyleElement;
import org.kabeja.svg.SVGConstants;
import org.kabeja.svg.SVGUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 * 
 */
public class DXFMLine extends DXFEntity {

	public final static int JUSTIFICATION_TOP = 0;
	public final static int JUSTIFICATION_ZERO = 1;
	public final static int JUSTIFICATION_BOTTOM = 2;

	protected double scale = 1.0;
	protected Point startPoint = new Point();
	protected List mlineSegments = new ArrayList();
	protected int lineCount = 0;
	protected int justification = 0;

	protected String mLineStyleID = "";
	protected String mLineStyleName = "";

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.miethxml.kabeja.svg.SVGGenerator#toSAX(org.xml.sax.ContentHandler)
	 */
	public void toSAX(ContentHandler handler, Map svgContext)
			throws SAXException {

		DXFPolyline[] pl = this.toDXFPolylines();

		DXFMLineStyle style = (DXFMLineStyle) this.doc
				.getDXFObject(this.mLineStyleID);
		
		if (style.isFilled()) {
			//we create a filled polyline
			StringBuffer buf = new StringBuffer();
			DXFPolyline p1 = pl[0];
			buf.append(p1.getSVGPath());
			DXFPolyline p2 = pl[pl.length-1];
			DXFUtils.reverseDXFPolyline(p2);
			String str = p2.getSVGPath().trim();
			if(str.startsWith("M")){
				buf.append(" L ");
				buf.append(str.substring(1));
			}else{
				buf.append(str);
			}
			
			buf.append(" z");
			
			AttributesImpl atts = new AttributesImpl();
			SVGUtils.addAttribute(atts, "d", buf.toString());
			SVGUtils.addAttribute(atts, SVGConstants.SVG_ATTRIBUTE_STROKE, "none");
			SVGUtils.addAttribute(atts, SVGConstants.SVG_ATTRIBUTE_FILL, "rgb(" + DXFColor.getRGBString(style.getFillColor())+ ")");
			SVGUtils.emptyElement(handler, SVGConstants.SVG_PATH, atts);
			
		}

		for (int i = 0; i < pl.length; i++) {
			pl[i].toSAX(handler, svgContext);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.miethxml.kabeja.dxf.DXFEntity#getBounds()
	 */
	public Bounds getBounds() {
		Bounds b = new Bounds();
		DXFPolyline[] pl = this.toDXFPolylines();
		for (int i = 0; i < pl.length; i++) {
			b.addToBounds(pl[i].getBounds());
		}
		// b.setValid(false);

		return b;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.miethxml.kabeja.dxf.DXFEntity#getType()
	 */
	public String getType() {
		return DXFConstants.ENTITY_TYPE_MLINE;
	}

	public double getLength() {
   
	   DXFPolyline[] pl = toDXFPolylines();
       double l = 0;
       for(int i=0;i<pl.length;i++){
    	   l+=pl[i].getLength();
       }	
		return l;
	}

	public void addDXFMLineSegement(DXFMLineSegment seg) {
		this.mlineSegments.add(seg);
	}

	public int getDXFMLineSegmentCount() {
		return this.mlineSegments.size();
	}

	public DXFMLineSegment getDXFMLineSegment(int index) {
		return (DXFMLineSegment) this.mlineSegments.get(index);
	}

	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	public Point getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(Point startPoint) {
		this.startPoint = startPoint;
	}

	public int getLineCount() {
		return lineCount;
	}

	public void setLineCount(int lineCount) {
		this.lineCount = lineCount;
	}

	public String getMLineStyleID() {
		return mLineStyleID;
	}

	public void setMLineStyleID(String lineStyleID) {
		mLineStyleID = lineStyleID;
	}

	public int getJustification() {
		return justification;
	}

	public void setJustification(int justification) {
		this.justification = justification;
	}

	public String getMLineStyleName() {
		return mLineStyleName;
	}

	public void setMLineStyleName(String lineStyleName) {
		mLineStyleName = lineStyleName;
	}

	protected DXFPolyline[] toDXFPolylines() {
		DXFMLineStyle style = (DXFMLineStyle) this.doc
				.getDXFObject(this.mLineStyleID);
//		style
//				.sortDXFMLineStyleElements(new DXFMLineStyleElementDistanceComparator());

		// initialize polylines
		DXFPolyline[] pl = new DXFPolyline[style
				.getDXFMLineStyleLElementCount()];
		for (int x = 0; x < pl.length; x++) {
			DXFMLineStyleElement se = style.getDXFMLineStyleLElement(x);
			pl[x] = new DXFPolyline();
			pl[x].setDXFDocument(this.doc);
			pl[x].setLineType(se.getLineType());
			pl[x].setColor(se.getLineColor());
			if (this.isClosed()) {
				pl[x].setFlags(1);
			}
		}
		Vector v = new Vector();
		Vector d = new Vector();
		ParametricLine l = new ParametricLine();
		ParametricLine miter = new ParametricLine();
		for (int i = 0; i < this.mlineSegments.size(); i++) {
			DXFMLineSegment seg = (DXFMLineSegment) this.mlineSegments.get(i);

			v = seg.getDirection();
			d = seg.getMiterDirection();
			miter.setStartPoint(seg.getStartPoint());
			miter.setDirectionVector(d);
			for (int x = 0; x < seg.getDXFMLineSegmentElementCount(); x++) {
				DXFMLineSegmentElement segEl = seg.getDXFMLineSegmentElement(x);
				double[] le = segEl.getLengthParameters();
				Point s = miter.getPointAt(le[0]);
				l.setStartPoint(s);
				l.setDirectionVector(v);
				pl[x].addVertex(new DXFVertex(l.getPointAt(le[1])));

			}

		}
		if (style.hasEndRoundCaps()) {

			Point p1 = pl[0].getVertex(pl[0].getVertexCount() - 1).getPoint();
			Point p2 = pl[pl.length - 1].getVertex(
					pl[pl.length - 1].getVertexCount() - 1).getPoint();
			Vector v1 = MathUtils.getVector(p1, p2);
			double distance = v1.getLength();
			double r = distance / 2;
		
			double length = Math.sqrt(2) * r;
			double h = r - Math.sqrt(0.5) * r;
			double bulge = h * 2 / length;
			v1.normalize();
			ParametricLine line = new ParametricLine(p1, v1);
			Point center = line.getPointAt(r);
			line.setStartPoint(center);

			v.normalize();
			line.setDirectionVector(v);
			center = line.getPointAt(r);

			pl[0].getVertex(pl[0].getVertexCount() - 1).setBulge(-1 * bulge);
			pl[0].addVertex(new DXFVertex(center));

			pl[pl.length - 1].getVertex(pl[pl.length - 1].getVertexCount() - 1)
					.setBulge(bulge);
			pl[pl.length - 1].addVertex(new DXFVertex(center));

		}

		return pl;
	}

	public boolean isClosed() {
		return (this.flags & 2) == 2;
	}
}
