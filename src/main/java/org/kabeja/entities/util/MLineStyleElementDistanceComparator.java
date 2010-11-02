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

package org.kabeja.entities.util;

import java.util.Comparator;

import org.kabeja.objects.MLineStyleElement;


public class MLineStyleElementDistanceComparator implements Comparator {
    public int compare(Object arg0, Object arg1) {
        MLineStyleElement el1 = (MLineStyleElement) arg0;
        MLineStyleElement el2 = (MLineStyleElement) arg1;

        if (el1.getOffset() > el2.getOffset()) {
            return 1;
        } else if (el1.getOffset() < el2.getOffset()) {
            return -1;
        }

        return 0;
    }
}
