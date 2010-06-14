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
 Copyright 2008 Simon Mieth

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */package org.kabeja.util;

import java.util.Comparator;

import org.kabeja.common.Layer;
/**
 * Compares two Layers by the z-index. The layer with z-index '0' is
 * on top all other are  underneath.
 *@author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 */
public class LayerComparator implements Comparator<Layer>{

	public int compare(Layer layer1, Layer layer2) {

		if(layer1.getZIndex()<layer2.getZIndex()){
			return -1;
		}else if(layer1.getZIndex()>layer2.getZIndex()){
			return 1;
		}
		return 0;
	}

}
