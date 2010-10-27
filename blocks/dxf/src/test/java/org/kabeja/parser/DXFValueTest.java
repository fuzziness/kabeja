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
package org.kabeja.parser;

import org.junit.Test;
import static org.junit.Assert.*;

import org.kabeja.dxf.parser.DXFValue;

public class DXFValueTest {

    @Test
    public void parseDouble() {
        double d = 3.1314151617;
        DXFValue value = new DXFValue("  " + d + "   ");
        double r = value.getDoubleValue();
        assertEquals(d, r, 0.000001);
    }

    @Test
    public void parseInteger() {
        int i = 4;
        DXFValue value = new DXFValue("  " + i + "   ");
        int r = value.getIntegerValue();
        assertEquals(i, r);
    }

    @Test
    public void parseBoolean() {
        int i = 1;
        DXFValue value = new DXFValue("  " + i + "   ");
        boolean b = value.getBooleanValue();
        assertFalse(b);
    }
}
