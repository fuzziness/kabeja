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
 * Created on 29.09.2005
 *
 */
package org.kabeja.entities;

import org.kabeja.common.Type;
import org.kabeja.math.MathUtils;


/**
 * @author simon
 *
 */
public class Face3D extends Solid {
	
    public Type<?> getType() {
        return Type.TYPE_3DFACE;
    }

    public double getLength() {
        double length = 0.0;
        int flag = this.getFlags();

        if ((flag & 1) == 0) {
            length += MathUtils.distance(this.getPoint1(), this.getPoint2());
        }

        if ((flag & 2) == 0) {
            length += MathUtils.distance(this.getPoint2(), this.getPoint3());
        }

        if ((flag & 4) == 0) {
            length += MathUtils.distance(this.getPoint3(), this.getPoint4());
        }

        if ((flag & 8) == 0) {
            length += MathUtils.distance(this.getPoint4(), this.getPoint1());
        }

        return length;
    }
}
