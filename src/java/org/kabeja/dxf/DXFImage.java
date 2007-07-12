/*
 * Created on 28.06.2005
 *
 */
package org.kabeja.dxf;

import java.util.ArrayList;
import java.util.Map;

import org.kabeja.dxf.helpers.Point;
import org.kabeja.dxf.objects.DXFImageDefObject;
import org.kabeja.svg.SVGConstants;
import org.kabeja.svg.SVGUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 */
public class DXFImage extends DXFEntity {

	protected Point insertPoint = new Point();

	protected Point vectorV = new Point();

	protected Point vectorU = new Point();

	protected double imageSizeAlongU;

	protected double imageSizeAlongV;

	protected String imageDefID = "";

	protected double brightness;

	protected double contrast;

	protected double fade;

	protected ArrayList clipBoundary = new ArrayList();

	protected boolean clipping = false;

	protected boolean rectangularClipping = false;

	protected boolean polygonalClipping = false;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.miethxml.kabeja.dxf.DXFEntity#getBounds()
	 */
	public Bounds getBounds() {
		Bounds b = new Bounds();
		b.addToBounds(insertPoint.getX(), insertPoint.getY());
		b.addToBounds(insertPoint.getX() + imageSizeAlongU, insertPoint.getY()
				+ imageSizeAlongV);
		return b;

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.miethxml.kabeja.dxf.DXFEntity#getType()
	 */
	public String getType() {

		return DXFConstants.ENTITY_TYPE_IMAGE;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.miethxml.kabeja.svg.SVGGenerator#toSAX(org.xml.sax.ContentHandler)
	 */
	public void toSAX(ContentHandler handler, Map svgContext) throws SAXException {

		// TODO add clipping here with clipPath

		AttributesImpl attr = new AttributesImpl();
		super.setCommonAttributes(attr, svgContext);

		SVGUtils.addAttribute(attr, "x", "" + insertPoint.getX());
		SVGUtils.addAttribute(attr, "y", "" + insertPoint.getY());
		SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_WIDTH, ""
				+ imageSizeAlongU);
		SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_HEIGHT, ""
				+ imageSizeAlongV);

		// get the image path from the referenced IMAGEDEF object
		DXFImageDefObject imageDef = (DXFImageDefObject) doc.getDXFObject(imageDefID);

		// convert the file to uri


		attr.addAttribute(SVGConstants.XMLNS_NAMESPACE, "xlink", "xmlns:xlink", "CDATA",
                SVGConstants.XLINK_NAMESPACE);
		attr.addAttribute(SVGConstants.XLINK_NAMESPACE, "href", "xlink:href",
				"CDATA", SVGUtils.pathToURI(imageDef.getFilename()));

		// We have a main transformation on the complete draft.
		// So we need here the rotate of image to get the right
		// view back.

		StringBuffer transform = new StringBuffer();
		transform.append("rotate(180 ");
		transform.append((insertPoint.getX()+ imageSizeAlongU / 2));
		transform.append(" ");
		transform.append((insertPoint.getY() + imageSizeAlongV / 2));
		transform.append(")");

		SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_TRANSFORM,
				transform.toString());
		SVGUtils.emptyElement(handler, SVGConstants.SVG_IMAGE, attr);

	}

	public Point getInsertPoint() {
		return insertPoint;
	}

	public void setInsertPoint(Point p) {
		this.insertPoint = p;
	}

	public void setImageDefObjectID(String id) {
		this.imageDefID = id;
	}

	public String getImageDefObjectID() {
		return this.imageDefID;
	}

	/**
	 * @return Returns the imageSizeAlongU.
	 */
	public double getImageSizeAlongU() {
		return imageSizeAlongU;
	}

	/**
	 * @param imageSizeAlongU
	 *            The imageSizeAlongU to set.
	 */
	public void setImageSizeAlongU(double imageSizeAlongU) {
		this.imageSizeAlongU = imageSizeAlongU;
	}

	/**
	 * @return Returns the imageSizeAlongV.
	 */
	public double getImageSizeAlongV() {
		return imageSizeAlongV;
	}

	/**
	 * @param imageSizeAlongV
	 *            The imageSizeAlongV to set.
	 */
	public void setImageSizeAlongV(double imageSizeAlongV) {
		this.imageSizeAlongV = imageSizeAlongV;
	}

	/**
	 * @return Returns the vectorU.
	 */
	public Point getVectorU() {
		return vectorU;
	}

	/**
	 * @param vectorU
	 *            The vectorU to set.
	 */
	public void setVectorU(Point vectorU) {
		this.vectorU = vectorU;
	}

	/**
	 * @return Returns the vectorV.
	 */
	public Point getVectorV() {
		return vectorV;
	}

	/**
	 * @param vectorV
	 *            The vectorV to set.
	 */
	public void setVectorV(Point vectorV) {
		this.vectorV = vectorV;
	}

	/**
	 * @return Returns the brightness.
	 */
	public double getBrightness() {
		return brightness;
	}

	/**
	 * @param brightness
	 *            The brightness to set.
	 */
	public void setBrightness(double brightness) {
		this.brightness = brightness;
	}

	/**
	 * @return Returns the clipping.
	 */
	public boolean isClipping() {
		return clipping;
	}

	/**
	 * @param clipping
	 *            The clipping to set.
	 */
	public void setClipping(boolean clipping) {
		this.clipping = clipping;
	}

	/**
	 * @return Returns the contrast.
	 */
	public double getContrast() {
		return contrast;
	}

	/**
	 * @param contrast
	 *            The contrast to set.
	 */
	public void setContrast(double contrast) {
		this.contrast = contrast;
	}

	/**
	 * @return Returns the fade.
	 */
	public double getFade() {
		return fade;
	}

	/**
	 * @param fade
	 *            The fade to set.
	 */
	public void setFade(double fade) {
		this.fade = fade;
	}

	/**
	 * @return Returns the clipBoundary.
	 */
	public ArrayList getClipBoundary() {
		return clipBoundary;
	}

	public void addClippingPoint(Point p) {
		clipBoundary.add(p);
	}

	/**
	 * @return Returns the polygonalClipping.
	 */
	public boolean isPolygonalClipping() {
		return polygonalClipping;
	}

	/**
	 * @param polygonalClipping
	 *            The polygonalClipping to set.
	 */
	public void setPolygonalClipping(boolean polygonalClipping) {
		this.polygonalClipping = polygonalClipping;
		this.rectangularClipping = !polygonalClipping;
	}

	/**
	 * @return Returns the rectangularClipping.
	 */
	public boolean isRectangularClipping() {
		return rectangularClipping;
	}

	/**
	 * @param rectangularClipping
	 *            The rectangularClipping to set.
	 */
	public void setRectangularClipping(boolean rectangularClipping) {
		this.rectangularClipping = rectangularClipping;
		this.polygonalClipping = !rectangularClipping;
	}

	public double getLength() {
	
		return 0;
	}
	

}
