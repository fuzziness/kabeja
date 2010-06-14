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
package org.kabeja.entities.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kabeja.entities.Hatch;


/**
 * This class represent a single line family of a hatch pattern set.
 *
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth </a>
 *
 */
public class HatchPattern {
    private static int idCount = 0;
    private String id = null;
    private List patterns = new ArrayList();
    private Hatch hatch;

    /**
     * @return Returns the id.
     */
    public String getID() {
        if (this.id == null) {
            this.id = "HATCH_PATTERN_ID_" + HatchPattern.idCount;
            HatchPattern.idCount++;
        }

        return id;
    }

    /**
     * @param id
     *            The id to set.
     */
    public void setID(String id) {
        this.id = id;
    }

    public void addLineFamily(HatchLineFamily pattern) {
        patterns.add(pattern);
    }

    public Iterator getLineFamilyIterator() {
        return patterns.iterator();
    }

    /**
     * The associated hatch for this pattern.
     *
     * @return Returns the hatch.
     */
    public Hatch getHatch() {
        return this.hatch;
    }

    /**
     * The associated hatch for this pattern.
     *
     * @param hatch
     *            The hatch to set.
     */
    public void setHatch(Hatch hatch) {
        this.hatch = hatch;
    }

    /**
     *
     * @return the count of the used line families
     */
    public int getLineFamilyCount() {
        return this.patterns.size();
    }
}
