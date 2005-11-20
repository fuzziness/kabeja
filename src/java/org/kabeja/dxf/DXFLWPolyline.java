/*
 * Created on 13.04.2005
 *
 */
package org.kabeja.dxf;

import java.util.Map;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;


/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 */
public class DXFLWPolyline extends DXFPolyline {

	private double constantwidth = 0.0;

	private double elevation = 0.0;

	public DXFLWPolyline() {

	}

	public void setConstantWidth(double width) {
		this.constantwidth = width;
	}

	public double getContstantWidth() {
		return this.constantwidth;
	}

	/**
	 * @return Returns the elevation.
	 */
	public double getElevation() {
		return elevation;
	}

	/**
	 * @param elevation
	 *            The elevation to set.
	 */
	public void setElevation(double elevation) {
		this.elevation = elevation;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.miethxml.kabeja.svg.SVGGenerator#toSAX(org.xml.sax.ContentHandler)
	 */
	public void toSAX(ContentHandler handler, Map svgContext) throws SAXException {

		// the lwpolyine will emit as a svg:path
		// note the dxf polyline has more
		// option as the svg:polygon


		super.toSAX(handler,svgContext);
	}



	/* (non-Javadoc)
	 * @see de.miethxml.kabeja.dxf.DXFEntity#getType()
	 */
	public String getType() {

		return DXFConstants.ENTITY_TYPE_LWPOLYLINE;
	}
}
