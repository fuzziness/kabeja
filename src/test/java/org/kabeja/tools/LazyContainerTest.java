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
package org.kabeja.tools;

import org.junit.Test;
import static org.junit.Assert.*;

public class LazyContainerTest {

    @Test
    public void add() {
        LazyContainer con = new LazyContainer();
        String s1 = "one";
        String s2 = "two";
        String s3 = "three";

        con.set(s1, 10);
        con.set(s2, 5);
        con.set(s3, 1);

        assertEquals(s1, (String) con.get(10));
        assertEquals(s3, (String) con.get(1));
        assertEquals(s2, (String) con.get(5));
        assertFalse(con.contains(6));

        con.set(s1, 1);
        assertEquals(s1, (String) con.get(1));
    }
}
