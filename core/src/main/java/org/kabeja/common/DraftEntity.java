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
package org.kabeja.common;

import org.kabeja.DraftDocument;
import org.kabeja.math.Bounds;
import org.kabeja.math.Extrusion;


public interface DraftEntity {

	public abstract long getID();

	public abstract Type<?> getType();

	public abstract Bounds getBounds();

	public abstract void setColor(int i);

	public abstract boolean isVisibile();

	public abstract void setVisibile(boolean b);

	public abstract boolean isModelSpace();

	public abstract int getColor();

	public abstract byte[] getColorRGB();

	public abstract int getLineWeight();

	public abstract DraftDocument getDocument();

	public abstract double getLinetypeScaleFactor();

	public abstract boolean isOmitLineType();

	public abstract boolean hasLineType();

	public abstract Layer getLayer();

	public abstract LineType getLineType();

	public abstract Extrusion getExtrusion();

	public abstract long getOwnerID();

	public abstract int getFlags();

	public abstract void setDocument(DraftDocument doc);

	public abstract void setLayer(Layer layer);

	public abstract double getLength();

}
