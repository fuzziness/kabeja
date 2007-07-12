/*
 * Created on Jun 29, 2004
 *
 */
package org.kabeja.dxf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kabeja.dxf.helpers.MathUtils;
import org.kabeja.dxf.helpers.Point;
import org.kabeja.dxf.helpers.PolylineSegment;
import org.kabeja.dxf.helpers.Vector;
import org.kabeja.svg.SVGConstants;
import org.kabeja.svg.SVGPathBoundaryElement;
import org.kabeja.svg.SVGUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth </a>
 * 
 */
public class DXFPolyline extends DXFEntity implements SVGPathBoundaryElement {

	protected ArrayList vertices = new ArrayList();

	protected double startWidth = 0.0;

	protected double endWidth = 0.0;

	protected boolean constantWidth = true;

	protected int surefaceType = 0;

	protected int surefaceDensityRows = 0;

	protected int surefaceDensityColumns = 0;

	protected int rows = 0;

	protected int columns = 0;
	
	protected static final double QUARTER_CIRCLE_ANGLE = Math.tan(0.39269908169872414D);

	/**
	 * 
	 */
	public DXFPolyline() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dxf2svg.dxf.DXFEntity#toSAX(org.xml.sax.ContentHandler)
	 */
	public void toSAX(ContentHandler handler, Map svgContext)
			throws SAXException {

		// the polyine will emit as a svg:path
		// note the dxf polyline has more
		// option as the svg:polyline

		if (this.vertices.size() > 0) {
			if (is3DPolygonMesh()) {
				meshToSAX(handler, svgContext);
			} else if (isPolyfaceMesh()) {
				polyfaceToSAX(handler, svgContext);
			} else if (isCurveFitVerticesAdded()) {
				// splineFitToSAX(handler, svgContext);
			} else if (isSplineFitVerticesAdded()) {
				splineFitToSAX(handler, svgContext);
			} else if (is3DPolygon()) {
				splineFitToSAX(handler, svgContext);
			} else {
				polylineToSAX(handler, svgContext);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dxf2svg.dxf.DXFEntity#updateViewPort()
	 */
	public Bounds getBounds() {
		Bounds bounds = new Bounds();
		
		Iterator i = vertices.iterator();
		if (i.hasNext()) {
			DXFVertex last, first, v = null;

			last = first = (DXFVertex) i.next();
			bounds.addToBounds(last.getPoint());
			while (i.hasNext()) {

				v = (DXFVertex) i.next();
				addToBounds(last, v, bounds);
				last = v;
			}
			if (v != null && v.getBulge() != 0.0) {
				addToBounds(v, first, bounds);
			}
		} else {
			bounds.setValid(false);
		}
		return bounds;
	}

	public void addVertex(DXFVertex vertex) {
		vertices.add(vertex);
		if (!vertex.isConstantWidth()) {
			constantWidth = false;
		}
	}

	public int getVertexCount() {
		return vertices.size();
	}

	public void removeVertex(DXFVertex vertex) {
		// remove and check the constantwidth
		constantWidth = true;
		Iterator i = vertices.iterator();
		while (i.hasNext()) {
			DXFVertex v = (DXFVertex) i.next();
			if (v == vertex) {
				i.remove();
			} else if (!v.isConstantWidth()) {
				constantWidth = false;
			}
		}
	}

	public void removeVertex(int index) {
		constantWidth = true;

		for (int i = 0; i < vertices.size(); i++) {
			DXFVertex v = (DXFVertex) vertices.get(i);
			if (index == i) {
				vertices.remove(i);
			} else if (!v.isConstantWidth()) {
				constantWidth = false;
			}
		}

	}

	public DXFVertex getVertex(int i) {
		return (DXFVertex) vertices.get(i);
	}

	/**
	 * Returns the distance between 2 DXFPoints
	 * 
	 * @param start
	 * @param end
	 * @return the length between the two points
	 */
	protected double getLength(DXFPoint start, DXFPoint end) {
		double value = Math.sqrt(Math.pow(end.getX() - start.getX(), 2)
				+ Math.pow(end.getY() - start.getY(), 2));

		return value;
	}

	/**
	 * Caculate the radius of a cut circle segment between 2 DXFVertex
	 * 
	 * @param bulge
	 *            the vertex bulge
	 * @param length
	 *            the length of the circle cut
	 */

	protected double getRadius(double bulge, double length) {

		double h = bulge * length / 2;
		double value = h / 2 + Math.pow(length, 2) / (8 * h);
		return Math.abs(value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.miethxml.kabeja.dxf.DXFEntity#getType()
	 */
	public String getType() {

		return DXFConstants.ENTITY_TYPE_POLYLINE;
	}

	/**
	 * @return Returns the endWidth.
	 */
	public double getEndWidth() {
		return endWidth;
	}

	/**
	 * @param endWidth
	 *            The endWidth to set.
	 */
	public void setEndWidth(double endWidth) {
		this.endWidth = endWidth;
	}

	/**
	 * @return Returns the startWidth.
	 */
	public double getStartWidth() {
		return startWidth;
	}

	/**
	 * @param startWidth
	 *            The startWidth to set.
	 */
	public void setStartWidth(double startWidth) {
		this.startWidth = startWidth;
	}

	protected void meshToSAX(ContentHandler handler, Map svgContext) throws SAXException {

		// TODO check first the points and put the output together

		StringBuffer d = new StringBuffer();
		if (isSimpleMesh()) {
			d = new StringBuffer();
			Point[][] points = new Point[this.rows][this.columns];
			Iterator it = this.vertices.iterator();

			// create a line for each row
			for (int i = 0; i < this.rows; i++) {
				d.append("M ");
				for (int x = 0; x < this.columns; x++) {
					DXFVertex v = (DXFVertex) it.next();
					points[i][x] = v.getPoint();
					d.append(v.getX());
					d.append(' ');
					d.append(v.getY());
					if (x < this.columns - 1) {
						d.append(" L ");
					}
				}
				if (isClosedMeshNDirection()) {
					d.append('L');
					d.append(' ');
					d.append(points[i][0].getX());
					d.append(' ');
					d.append(points[i][0].getY());
					d.append(' ');
				}
			}
			// create a line for each column
			for (int i = 0; i < this.columns; i++) {
				d.append(" M ");
				for (int x = 0; x < this.rows; x++) {
					d.append(points[x][i].getX());
					d.append(' ');
					d.append(points[x][i].getY());
					if (x < this.rows - 1) {
						d.append(" L ");
					}
				}
				if (isClosedMeshMDirection()) {
					d.append('L');
					d.append(' ');
					d.append(points[0][i].getX());
					d.append(' ');
					d.append(points[0][i].getY());
					d.append(' ');
				}

			}
		} else {

			Point[][] points = new Point[this.surefaceDensityRows][this.surefaceDensityColumns];
			Iterator vi = this.vertices.iterator();
			List appVertices = new ArrayList();
			while (vi.hasNext()) {
				DXFVertex v = (DXFVertex) vi.next();
				if (v.isMeshApproximationVertex()) {
					appVertices.add(v);
				}
			}

			Iterator it = appVertices.iterator();

			// create a line for each row
			for (int i = 0; i < this.surefaceDensityRows; i++) {
				d.append("M ");
				for (int x = 0; x < this.surefaceDensityColumns; x++) {
					DXFVertex v = (DXFVertex) it.next();
					points[i][x] = v.getPoint();
					d.append(v.getX());
					d.append(' ');
					d.append(v.getY());
					if (x < this.surefaceDensityColumns - 1) {
						d.append(" L ");
					}
				}
				if (isClosedMeshNDirection()) {
					d.append('L');
					d.append(' ');
					d.append(points[i][0].getX());
					d.append(' ');
					d.append(points[i][0].getY());
					d.append(' ');
				}
			}
			// create a line for each column
			for (int i = 0; i < this.surefaceDensityColumns; i++) {
				d.append(" M ");
				for (int x = 0; x < this.surefaceDensityRows; x++) {
					d.append(points[x][i].getX());
					d.append(' ');
					d.append(points[x][i].getY());
					if (x < this.surefaceDensityRows - 1) {
						d.append(" L ");
					}
				}
				if (isClosedMeshMDirection()) {
					d.append('L');
					d.append(' ');
					d.append(points[0][i].getX());
					d.append(' ');
					d.append(points[0][i].getY());
					d.append(' ');
				}

			}
		}
		AttributesImpl attr = new AttributesImpl();
		SVGUtils.addAttribute(attr, "d", d.toString());
		super.setCommonAttributes(attr, svgContext);
		SVGUtils.emptyElement(handler, SVGConstants.SVG_PATH, attr);

	}

	public boolean isClosed() {
		// the closed Flag
		return (this.flags & 1) == 1;
	}

	public boolean isCurveFitVerticesAdded() {
		return (this.flags & 2) == 2;
	}

	public boolean isSplineFitVerticesAdded() {
		return (this.flags & 4) == 4;
	}

	public boolean is3DPolygon() {
		return (this.flags & 8) == 8;
	}

	public boolean is3DPolygonMesh() {
		return (this.flags & 16) == 16;
	}

	public boolean isPolyfaceMesh() {
		return (this.flags & 64) == 64;
	}

	public boolean isClosedMeshNDirection() {
		return (this.flags & 32) == 32;
	}

	public boolean isClosedMeshMDirection() {
		return (this.flags & 1) == 1;
	}

	public boolean isQuadSpline() {
		if (isSplineFitVerticesAdded()) {
			return this.surefaceType == 5;
		}

		return false;
	}

	public boolean isCubicSpline() {
		if (isSplineFitVerticesAdded()) {
			return this.surefaceType == 6;
		}

		return false;
	}

	protected String getVertexPath(DXFVertex start, DXFVertex end) {

		StringBuffer d = new StringBuffer();

		if (start.getBulge() != 0) {
			// from the DXF-Specs.
			double l = MathUtils.distance(start.getPoint(), end.getPoint());
			// do nothing if the points are the same
			if (l > 0.0) {
				double r = getRadius(Math.abs(start.getBulge()), l);
				double h = start.getBulge() * l / 2;

				// converting to an elipse with the same rx=ry
				d.append("A " + r + " " + r + " 0");
				if (Math.abs(start.getBulge()) > 1.0) {
					// large Arc-flag
					d.append(" 1 ");
				} else {
					d.append(" 0 ");
				}

				// if the bulge > 0 the center point is on the left side
				// if the bulge < 0 the center point is ont the right side

				if (start.getBulge() < 0) {
					// the sweep-flag
					d.append(" 0 ");
				} else {
					d.append(" 1 ");
				}
				d.append(end.getX() + " ");
				d.append(doc.translateY(end.getY()) + " ");

			}

		} else {
			d.append("L " + end.getX() + " ");

			d.append(doc.translateY(end.getY()) + " ");

		}

		return d.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.miethxml.kabeja.dxf.helpers.HatchBoundaryElement#getSVGPath()
	 */
	public String getSVGPath() {
		// create the path

		StringBuffer d = new StringBuffer();

		DXFVertex last, first;

		Iterator i = vertices.iterator();
		first = last = (DXFVertex) i.next();
		d.append("M " + last.getX() + " " + last.getY() + " ");

		while (i.hasNext()) {
			DXFVertex end = (DXFVertex) i.next();
			d.append(getVertexPath(last, end));
			last = end;
		}

		// bit coded values
		if (isClosed()) {
			if (last.getBulge() != 0) {
				d.append(getVertexPath(last, first));

			}
			d.append(" z");
		}

		return d.toString();
	}

	protected void polylineToSAX(ContentHandler handler, Map svgContext)
			throws SAXException {
		AttributesImpl attr = new AttributesImpl();

		if (this.startWidth != this.endWidth || !constantWidth) {

			// handle the different width
			polylinePartToSAX(handler, svgContext);

		} else {
			StringBuffer d = new StringBuffer();
			DXFVertex last, first;
			Iterator i = vertices.iterator();
			first = last = (DXFVertex) i.next();
			d.append("M " + last.getX() + " " + last.getY() + " ");

			while (i.hasNext()) {
				DXFVertex end = (DXFVertex) i.next();
				d.append(getVertexPath(last, end));
				last = end;
			}

			// bit coded values
			if (isClosed()) {
				if (last.getBulge() != 0) {
					d.append(getVertexPath(last, first));
				}
				d.append(" z");
			}

			SVGUtils.addAttribute(attr, "d", d.toString());
			super.setCommonAttributes(attr, svgContext);
			
			if (startWidth > 0.0) {
				SVGUtils.addAttribute(attr,
						SVGConstants.SVG_ATTRIBUTE_STROKE_WITDH, ""
								+ startWidth);
			}

			
			SVGUtils.emptyElement(handler, SVGConstants.SVG_PATH, attr);
		}

	}

	protected void checkConstantWidth() {
		constantWidth = true;
		Iterator i = vertices.iterator();
		while (i.hasNext()) {
			DXFVertex vertex = (DXFVertex) i.next();
			if (!vertex.isConstantWidth()) {
				constantWidth = false;
			}
		}
	}

	protected void polylinePartToSAX(ContentHandler handler, Map svgContext)
			throws SAXException {

		// output as group

		AttributesImpl attr = new AttributesImpl();
		super.setCommonAttributes(attr, svgContext);
		SVGUtils.startElement(handler, SVGConstants.SVG_GROUP, attr);

		String oldID = this.id;
		PolylineSegment segment = null;
		//boolean bulged = false;
		boolean process = true;
		DXFVertex start = (DXFVertex) vertices.get(0);
		DXFVertex end = (DXFVertex) vertices.get(1);
		segment = new PolylineSegment(start, end, this);

		int i = 1;
		while (i < vertices.size()) {

			// we need the next vertex to get the right endpoints
			PolylineSegment next = null;

			if (i + 1 < vertices.size()) {
				process = false;
				DXFVertex nextStart = end;
				end = (DXFVertex) vertices.get(i + 1);
				next = new PolylineSegment(nextStart, end, this);
				if (next.isBulged()) {
					segment.setPoint3(next.getPoint2());
					segment.setPoint4(next.getPoint1());
				} else {
					segment.connect(next);
				}

			}

			StringBuffer d = new StringBuffer();
			d.append("M ");
			if (segment.isBulged()) {
				// first the line from 1->2
				d.append(segment.getPoint1().getX());
				d.append(' ');
				d.append(segment.getPoint1().getY());
				d.append(" L ");
				d.append(segment.getPoint2().getX());
				d.append(' ');
				d.append(segment.getPoint2().getY());

				// next the arc from 2->3
				d.append(" A " + segment.getRadius() + " "
						+ segment.getRadius() + " 0");
				if (Math.abs(segment.getBulgeHeight()) > Math.abs(segment
						.getRadius())) {
					// large Arc-flag
					d.append(" 1 ");
				} else {
					d.append(" 0 ");
				}

				// if the bulge > 0 the center point is on the left side
				// if the bulge < 0 the center point is ont the right side

				if (segment.getBulge() < 0) {
					// the sweep-flag
					d.append(" 0 ");
				} else {
					d.append(" 1 ");
				}
				d.append(segment.getPoint3().getX());
				d.append(' ');
				d.append(segment.getPoint3().getY());

				// next the line from 2->3
				d.append(" L ");
				d.append(segment.getPoint4().getX());
				d.append(' ');
				d.append(segment.getPoint4().getY());

				// next the arc from 4->1

				double radius = segment.getInnerRadius();

				d.append(" A " + radius + " " + radius + " 0");
				if (Math.abs(segment.getBulgeHeight()) > Math.abs(segment
						.getRadius())) {
					// large Arc-flag
					d.append(" 0 ");
				} else {
					d.append(" 1 ");
				}

				// if the bulge > 0 the center point is on the left side
				// if the bulge < 0 the center point is ont the right side

				if (segment.getBulge() < 0) {
					// the sweep-flag
					d.append(" 1 ");
				} else {
					d.append(" 0 ");
				}
				d.append(segment.getPoint1().getX());
				d.append(' ');
				d.append(segment.getPoint1().getY());
				// and finally close the path
				d.append(" Z");
			} else {

				// ok output the trapezium
				// from p1 -> p2 -> p3 -> p4 and close

				d.append(segment.getPoint1().getX());
				d.append(' ');
				d.append(segment.getPoint1().getY());
				d.append(" L ");

				d.append(segment.getPoint2().getX());
				d.append(' ');
				d.append(segment.getPoint2().getY());
				d.append(" L ");

				d.append(segment.getPoint3().getX());
				d.append(' ');
				d.append(segment.getPoint3().getY());
				d.append(" L ");

				d.append(segment.getPoint4().getX());
				d.append(' ');
				d.append(segment.getPoint4().getY());
				d.append(" Z");

			}

			// output
			attr = new AttributesImpl();
			this.id = this.id + "__" + i;
			super.setCommonAttributes(attr, svgContext);
			if (doc.getDXFHeader().isFillMode()) {
				SVGUtils.addAttribute(attr, "fill", "currentColor");
			}

			SVGUtils.addAttribute(attr, "d", d.toString());

			// output now
			SVGUtils.emptyElement(handler, SVGConstants.SVG_PATH, attr);
			if (!process) {
				segment = next;
			}

			i++;
		}

		SVGUtils.endElement(handler, SVGConstants.SVG_GROUP);
		this.id = oldID;

	}

	/**
	 * @return Returns the surefaceType.
	 */
	public int getSurefaceType() {
		return surefaceType;
	}

	/**
	 * @param surefaceType
	 *            The surefaceType to set.
	 */
	public void setSurefaceType(int surefaceType) {
		this.surefaceType = surefaceType;
	}

	protected void splineFitToSAX(ContentHandler handler, Map svgContext)
			throws SAXException {

		// TODO we will first use the approximation of the spline
		// and later take e deeper look at SVG-bezier curves /DXF b-splines
		StringBuffer d = new StringBuffer();

		

		Iterator i = vertices.iterator();
		DXFVertex last = (DXFVertex) i.next();
		d.append("M " + last.getX() + " " + last.getY() + " ");

		while (i.hasNext()) {
			DXFVertex vertex = (DXFVertex) i.next();
			if (vertex.is2DSplineApproximationVertex()) {
				d.append("L ");
				d.append(vertex.getX());
				d.append(' ');
				d.append(vertex.getY());
				d.append(' ');
			}

		}

		AttributesImpl attr = new AttributesImpl();
		super.setCommonAttributes(attr, svgContext);

		SVGUtils.addAttribute(attr, "d", d.toString());

		// output now
		SVGUtils.emptyElement(handler, SVGConstants.SVG_PATH, attr);

	}

	protected void singleEdgeToSAX(ContentHandler handler, DXFVertex start,
			DXFVertex end, Map svgContext) throws SAXException {

		AttributesImpl attr = new AttributesImpl();
		super.setCommonAttributes(attr, svgContext);

		StringBuffer d = new StringBuffer();
		d.append("M ");
		d.append(start.getX());
		d.append(' ');
		d.append(start.getY());
		d.append(' ');

		d.append(getVertexPath(start, end));

		SVGUtils.addAttribute(attr, "d", d.toString());

		if (start.getStartWidth() > 0.0) {
			SVGUtils.addAttribute(attr,
					SVGConstants.SVG_ATTRIBUTE_STROKE_WITDH, ""
							+ start.getStartWidth());
		}

		SVGUtils.emptyElement(handler, SVGConstants.SVG_PATH, attr);
	}

	/**
	 * @return Returns the columns.
	 */
	public int getSurefaceDensityColumns() {
		return surefaceDensityColumns;
	}

	/**
	 * @param columns
	 *            The columns to set.
	 */
	public void setSurefaceDensityColumns(int columns) {
		this.surefaceDensityColumns = columns;
	}

	/**
	 * @return Returns the rows.
	 */
	public int getSurefaceDensityRows() {
		return surefaceDensityRows;
	}

	/**
	 * @param rows
	 *            The rows to set.
	 */
	public void setSurefaceDensityRows(int rows) {
		this.surefaceDensityRows = rows;
	}

	protected void addToBounds(DXFVertex start, DXFVertex end, Bounds bounds) {
		
		if (start.getBulge() != 0) {
			// calculte the height
			double l = MathUtils.distance(start.getPoint(), end.getPoint());
			// double h = Math.abs(last.getBulge()) * l / 2;
			double r = this.getRadius(start.getBulge(), l);
           
			
			double s = l / 2;
			Vector edgeDirection = MathUtils.getVector(start.getPoint(), end
					.getPoint());
			edgeDirection = MathUtils.normalize(edgeDirection);
			Point centerPoint = MathUtils.getPointOfStraightLine(start.getPoint(),
					edgeDirection, s);

			
			Vector centerPointDirection = MathUtils.crossProduct(edgeDirection, this.getExtrusion().getNormal());
			centerPointDirection = MathUtils.normalize(centerPointDirection);

			//double t = Math.sqrt(Math.pow(r, 2) - Math.pow(s, 2));
//			double t = 0;
			double h = Math.abs(start.getBulge()*l)/2;
//			if(Math.abs(start.getBulge())>=1.0){
//				 t = h-r;
//			}else{
//				//t = Math.sqrt(Math.pow(r, 2) - Math.pow(s, 2));
//				t=r-h;
//			}
			// the center point of the arc
			int startQ = 0;
			int endQ = 0;

	        double bulge = start.getBulge();
			if (bulge > 0) {
								
		    	//the arc goes over the right side, but where is the center point?
			    if(bulge>1.0){
			    	double t = h-r;
			    	centerPoint = MathUtils.getPointOfStraightLine(centerPoint, centerPointDirection,t);
			    }else{
			    	double t = r-h;
			    	centerPoint = MathUtils.getPointOfStraightLine(centerPoint, centerPointDirection,(-1*t));
			    }
					
				endQ = MathUtils.getQuadrant(end.getPoint(), centerPoint);
				startQ = MathUtils.getQuadrant(start.getPoint(), centerPoint);

			} else {
//				the arc goes over the left side, but where is the center point?
				if(bulge<-1.0){
					double t = h-r;
					centerPoint = MathUtils.getPointOfStraightLine(centerPoint, centerPointDirection, (-1*t));
				}else{
					double t = r-h;
					centerPoint = MathUtils.getPointOfStraightLine(centerPoint, centerPointDirection, t);
				}
				
				startQ = MathUtils.getQuadrant(end.getPoint(), centerPoint);
				endQ = MathUtils.getQuadrant(start.getPoint(), centerPoint);

			}

			if (endQ < startQ) {
				endQ += 4;
			}else if(endQ==startQ &&Math.abs(start.getBulge())>QUARTER_CIRCLE_ANGLE){
				endQ+=4;
			}

			while (endQ > startQ) {
				switch (startQ) {
				case 0:
					bounds.addToBounds(centerPoint.getX(), centerPoint.getY() + r);
					break;
				case 1:
					bounds.addToBounds(centerPoint.getX() - r, centerPoint.getY());
					break;
				case 2:
					bounds.addToBounds(centerPoint.getX(), centerPoint.getY() - r);
					break;
				case 3:
					bounds.addToBounds(centerPoint.getX() + r, centerPoint.getY());
					endQ -= 4;
					startQ -= 4;
					break;
				}
				startQ++;

			}

		}
		bounds.addToBounds(start.getPoint());
		bounds.addToBounds(end.getPoint());
		

	}

	protected void polyfaceToSAX(ContentHandler handler, Map svgContext) throws SAXException {

		Iterator i = this.vertices.iterator();
		StringBuffer buf = new StringBuffer();
		while (i.hasNext()) {
			DXFVertex v = (DXFVertex) i.next();
			if (v.isFaceRecord()) {

				DXFVertex v1 = getPolyFaceMeshVertex(v.getPolyFaceMeshVertex0());
				DXFVertex v2 = getPolyFaceMeshVertex(v.getPolyFaceMeshVertex1());
				DXFVertex v3 = getPolyFaceMeshVertex(v.getPolyFaceMeshVertex2());
				DXFVertex v4 = getPolyFaceMeshVertex(v.getPolyFaceMeshVertex3());
				if (v.isPolyFaceEdge0Visible()
						&& v.getPolyFaceMeshVertex0() != 0) {
					addEdgeToPath(v1, v2, buf);
				}
				if (v.isPolyFaceEdge1Visible()
						&& v.getPolyFaceMeshVertex1() != 0) {
					addEdgeToPath(v2, v3, buf);
				}
				if (v.isPolyFaceEdge2Visible()
						&& v.getPolyFaceMeshVertex2() != 0) {
					addEdgeToPath(v3, v4, buf);
				}
				if (v.isPolyFaceEdge3Visible()
						&& v.getPolyFaceMeshVertex3() != 0) {
					addEdgeToPath(v4, v1, buf);
				} else if (v4 == null && v3 != null) {
					// triangle
					addEdgeToPath(v3, v1, buf);
				}
				if (buf.length() > 0) {
					AttributesImpl attr = new AttributesImpl();
					SVGUtils.addAttribute(attr, "d", buf.toString());
					v.setCommonAttributes(attr, svgContext);
					// fillmode ????????
					SVGUtils.emptyElement(handler, SVGConstants.SVG_PATH, attr);
					buf.delete(0, buf.length());
				}

			}
		}
		if (buf.length() > 0) {
			AttributesImpl attr = new AttributesImpl();
			SVGUtils.addAttribute(attr, "d", buf.toString());
			super.setCommonAttributes(attr, svgContext);
			SVGUtils.emptyElement(handler, SVGConstants.SVG_PATH, attr);
		}

	}

	public DXFVertex getPolyFaceMeshVertex(int index) {
		Iterator i = this.vertices.iterator();
		int count = 1;

		while (i.hasNext()) {
			DXFVertex v = (DXFVertex) i.next();
			if (v.isPolyFaceMeshVertex()) {

				if (count == index) {

					return v;
				} else {
					count++;
				}
			}
		}

		return null;
	}

	protected void addEdgeToPath(DXFVertex start, DXFVertex end,
			StringBuffer buf) {
		buf.append('M');
		buf.append(' ');
		buf.append(start.getX());
		buf.append(' ');
		buf.append(start.getY());
		buf.append(' ');
		if (end != null) {
			buf.append('L');
			buf.append(' ');
			buf.append(end.getX());
			buf.append(' ');
			buf.append(end.getY());
			buf.append(' ');
		}

	}

	/**
	 * @return Returns the column.
	 */
	public int getColumns() {
		return columns;
	}

	/**
	 * @param column
	 *            The column to set.
	 */
	public void setColumns(int column) {
		this.columns = column;
	}

	/**
	 * @return Returns the rows.
	 */
	public int getRows() {
		return rows;
	}

	/**
	 * @param rows
	 *            The rows to set.
	 */
	public void setRows(int rows) {
		this.rows = rows;
	}

	public boolean isSimpleMesh() {
		return this.surefaceType == 0 && (this.flags & 4) == 0;
	}

	public boolean isQuadSurefaceMesh() {
		return this.surefaceType == 5 && (this.flags & 4) == 4;
	}

	public boolean isCubicSurefaceMesh() {
		return this.surefaceType == 6 && (this.flags & 4) == 4;
	}

	public boolean isBezierSurefaceMesh() {
		return this.surefaceType == 8 && (this.flags & 4) == 4;
	}

	public double getLength() {

		double length = 0.0;
		
		
		if (isCubicSpline() || isQuadSpline()) {
			return getSplineApproximationLength();
		} else if (isPolyfaceMesh()) {
			
			return getPolyfaceLength();

		} else if (is3DPolygonMesh() || isBezierSurefaceMesh() ||isCubicSurefaceMesh()) {
              
			return getMeshLength();
			 
		} else {

			
			//a normal polyline with or without bulges 
			Iterator i = this.vertices.iterator();
			DXFVertex first, last = first = (DXFVertex) i.next();
			while (i.hasNext()) {
		
				DXFVertex v = (DXFVertex) i.next();
				length += this.getSegmentLength(last, v);
				last = v;
			}

			if (this.isClosed()) {
				length += this.getSegmentLength(last, first);
			}
		}
		return length;
	}

	protected double getSegmentLength(DXFVertex start, DXFVertex end) {

		double l =  MathUtils.distance(start.getPoint(), end.getPoint());
		
		if (start.getBulge() == 0.0) {
			return l;
		} else {
			double alpha = 4 * Math.atan(Math.abs(start.getBulge()));
			
			double r = l / (2 * Math.sin(alpha / 2));
            double d = (Math.PI * Math.toDegrees(alpha) * r) / 180;
			return d;
		}
	}

	protected double getSplineApproximationLength() {
		double length = 0.0;
		// use the approximation
		Iterator i = this.vertices.iterator();
		DXFVertex first, last = first = null;
		while (i.hasNext()) {
			DXFVertex v = (DXFVertex) i.next();
			if (v.is2DSplineApproximationVertex()) {
				if (first == null) {
					first = last = v;
				} else {
					length += getSegmentLength(last, v);
					last = v;
				}
			}

		}
		if (this.isClosed()) {
			length += getSegmentLength(last, first);
		}
		return length;
	}

	protected double getPolyfaceLength() {
		double length = 0.0;
		Iterator i = this.vertices.iterator();
		while (i.hasNext()) {
			DXFVertex v = (DXFVertex) i.next();
			if (v.isFaceRecord()) {

				DXFVertex v1 = getPolyFaceMeshVertex(v.getPolyFaceMeshVertex0());
				DXFVertex v2 = getPolyFaceMeshVertex(v.getPolyFaceMeshVertex1());
				DXFVertex v3 = getPolyFaceMeshVertex(v.getPolyFaceMeshVertex2());
				DXFVertex v4 = getPolyFaceMeshVertex(v.getPolyFaceMeshVertex3());
				if (v.isPolyFaceEdge0Visible()
						&& v.getPolyFaceMeshVertex0() != 0) {
					length += getSegmentLength(v1, v2);
				}
				if (v.isPolyFaceEdge1Visible()
						&& v.getPolyFaceMeshVertex1() != 0) {
					length += getSegmentLength(v2, v3);

				}
				if (v.isPolyFaceEdge2Visible()
						&& v.getPolyFaceMeshVertex2() != 0) {
					length += getSegmentLength(v3, v4);

				}
				if (v.isPolyFaceEdge3Visible()
						&& v.getPolyFaceMeshVertex3() != 0) {
					length += getSegmentLength(v4, v1);

				} else if (v4 == null && v3 != null) {
					// triangle
					length += getSegmentLength(v3, v1);
				}

			}
		}
		return length;
	}

	protected double getMeshLength() {
		double length = 0.0;

		if (isSimpleMesh()) {

			DXFVertex[][] points = new DXFVertex[this.rows][this.columns];
			Iterator it = this.vertices.iterator();

			// create a line for each row
			for (int i = 0; i < this.rows; i++) {

				for (int x = 0; x < this.columns; x++) {
					DXFVertex v = (DXFVertex) it.next();
					points[i][x] = v;

					if (x > 0) {
						length += getSegmentLength(points[i][x - 1],
								points[i][x]);
					}
				}
				if (isClosedMeshNDirection()) {
					length += getSegmentLength(points[i][points[i].length - 1],
							points[i][0]);
				}
			}
			// create a line for each column
			for (int i = 0; i < this.columns; i++) {
				for (int x = 0; x < this.rows; x++) {
					if (x > 0) {
						length += getSegmentLength(points[x - 1][i],
								points[x][i]);
					}
				}
				if (isClosedMeshMDirection()) {
					length += getSegmentLength(points[points[i].length - 1][i],
							points[0][i]);
				}

			}
		} else {

			DXFVertex[][] points = new DXFVertex[this.surefaceDensityRows][this.surefaceDensityColumns];
			Iterator vi = this.vertices.iterator();
			List appVertices = new ArrayList();
			while (vi.hasNext()) {
				DXFVertex v = (DXFVertex) vi.next();
				if (v.isMeshApproximationVertex()) {
					appVertices.add(v);
				}
			}

			Iterator it = appVertices.iterator();

			// create a line for each row
			for (int i = 0; i < this.surefaceDensityRows; i++) {

				for (int x = 0; x < this.surefaceDensityColumns; x++) {
					DXFVertex v = (DXFVertex) it.next();
					points[i][x] = v;
					if (x > 0) {
						length += getSegmentLength(points[i][x - 1],
								points[i][x]);
					}
				}
				if (isClosedMeshNDirection()) {
					length += getSegmentLength(points[i][points[i].length - 1],
							points[i][0]);

				}
			}
			// create a line for each column
			for (int i = 0; i < this.surefaceDensityColumns; i++) {

				for (int x = 0; x < this.surefaceDensityRows; x++) {
					if (x > 0) {
						length += getSegmentLength(points[x - 1][i],
								points[x][i]);
					}
				}
				if (isClosedMeshMDirection()) {
					length += getSegmentLength(points[points[i].length - 1][i],
							points[0][i]);
				}

			}
		}

		return length;
	}

}