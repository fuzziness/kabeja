/*******************************************************************************
 * Copyright 2010 Simon Mieth
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
/*
 * Created on 09.09.2009
 *
 */
package org.kabeja.entities;

import org.kabeja.math.Point3D;

public class LW2DVertex {

	protected double x;

	protected double y;

	protected ExtendedData extendedData;

	public LW2DVertex(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public LW2DVertex() {
		this(0.0, 0.0);
	}

	/**
	 * @return the x
	 */
	public double getX() {
		return x;
	}

	/**
	 * @param x
	 *            the x to set
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public double getY() {
		return y;
	}

	/**
	 * @param y
	 *            the y to set
	 */
	public void setY(double y) {
		this.y = y;
	}

	/**
	 * @return the startWidth
	 */
	public double getStartWidth() {
		if (extendedData != null) {
			extendedData.getStartWidth();
		}
		return 0.0;
	}

	/**
	 * @param startWidth
	 *            the startWidth to set
	 */
	public void setStartWidth(double startWidth) {
		if (startWidth != 0.0) {
			if (extendedData == null) {
				extendedData = new ExtendedData();
			}
			extendedData.setStartWidth(startWidth);
		}
	}

	/**
	 * @return the endWidth
	 */
	public double getEndWidth() {
		if (extendedData != null) {
			return extendedData.getEndWidth();
		}
		return 0.0;
	}

	/**
	 * @param endWidth
	 *            the endWidth to set
	 */
	public void setEndWidth(double endWidth) {
		if (endWidth != 0.0) {
			if (extendedData == null) {
				extendedData = new ExtendedData();
			}
			extendedData.setEndWidth(endWidth);
		}
	}

	/**
	 * @return the bulge
	 */
	public double getBulge() {
		if (extendedData != null) {
			return extendedData.getBulge();
		}
		return 0.0;
	}

	/**
	 * @param bulge
	 *            the bulge to set
	 */
	public void setBulge(double bulge) {
		if (bulge != 0.0) {
			if (extendedData == null) {
				extendedData = new ExtendedData();
			}
			extendedData.setBulge(bulge);
		}
	}

	public boolean isConstantWidth() {
		if (extendedData == null) {
			return true;
		} else {
			return extendedData.getStartWidth() == extendedData.getEndWidth();
		}
	}

	public Point3D getPoint() {
		return new Point3D(x, y, 0.0);
	}

	protected class ExtendedData {

		protected double startWidth = 0.0;
		protected double endWidth = 0.0;
		protected double bulge = 0.0;

		public double getStartWidth() {
			return startWidth;
		}

		public void setStartWidth(double startWidth) {
			this.startWidth = startWidth;
		}

		public double getEndWidth() {
			return endWidth;
		}

		public void setEndWidth(double endWidth) {
			this.endWidth = endWidth;
		}

		public double getBulge() {
			return bulge;
		}

		public void setBulge(double bulge) {
			this.bulge = bulge;
		}

	}

}
