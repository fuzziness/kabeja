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

package org.kabeja.dxf.parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.kabeja.dxf.parser.entities.DXF3DFaceHandler;
import org.kabeja.dxf.parser.entities.DXF3DSolidHandler;
import org.kabeja.dxf.parser.entities.DXFArcHandler;
import org.kabeja.dxf.parser.entities.DXFAttribDefinitionHandler;
import org.kabeja.dxf.parser.entities.DXFAttribHandler;
import org.kabeja.dxf.parser.entities.DXFBodyHandler;
import org.kabeja.dxf.parser.entities.DXFCircleHandler;
import org.kabeja.dxf.parser.entities.DXFDimensionHandler;
import org.kabeja.dxf.parser.entities.DXFEllipseHandler;
import org.kabeja.dxf.parser.entities.DXFHatchHandler;
import org.kabeja.dxf.parser.entities.DXFImageHandler;
import org.kabeja.dxf.parser.entities.DXFInsertHandler;
import org.kabeja.dxf.parser.entities.DXFLWPolylineHandler;
import org.kabeja.dxf.parser.entities.DXFLeaderHandler;
import org.kabeja.dxf.parser.entities.DXFLineHandler;
import org.kabeja.dxf.parser.entities.DXFMLineHandler;
import org.kabeja.dxf.parser.entities.DXFMTextHandler;
import org.kabeja.dxf.parser.entities.DXFPointHandler;
import org.kabeja.dxf.parser.entities.DXFPolylineHandler;
import org.kabeja.dxf.parser.entities.DXFRayHandler;
import org.kabeja.dxf.parser.entities.DXFRegionHandler;
import org.kabeja.dxf.parser.entities.DXFSolidHandler;
import org.kabeja.dxf.parser.entities.DXFSplineHandler;
import org.kabeja.dxf.parser.entities.DXFTextHandler;
import org.kabeja.dxf.parser.entities.DXFToleranceHandler;
import org.kabeja.dxf.parser.entities.DXFTraceHandler;
import org.kabeja.dxf.parser.entities.DXFViewportHandler;
import org.kabeja.dxf.parser.entities.DXFXLineHandler;
import org.kabeja.dxf.parser.objects.DXFDictionaryHandler;
import org.kabeja.dxf.parser.objects.DXFImageDefHandler;
import org.kabeja.dxf.parser.objects.DXFLayoutHandler;
import org.kabeja.dxf.parser.objects.DXFMLineStyleHandler;
import org.kabeja.dxf.parser.objects.DXFPlotsettingsHandler;
import org.kabeja.dxf.parser.table.DXFDimensionStyleTableHandler;
import org.kabeja.dxf.parser.table.DXFLayerTableHandler;
import org.kabeja.dxf.parser.table.DXFLineTypeTableHandler;
import org.kabeja.dxf.parser.table.DXFStyleTableHandler;
import org.kabeja.dxf.parser.table.DXFVPortTableHandler;
import org.kabeja.dxf.parser.table.DXFViewTableHandler;
import org.kabeja.parser.Parser;


/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 *
 *
 */
public class DXFParserBuilder {
    public static Parser createDefaultParser() {
        DXFParser parser = new DXFParser();

        DXFHandler handler;

        handler = new DXFHeaderSectionHandler();

        parser.addHandler(handler);

        // the blocks handler
        DXFHandlerManager handlerManager = new DXFBlocksSectionHandler();
        parser.addHandler(handlerManager);

        DXFHandler h = new DXFLineHandler();
        handlerManager.addHandler(h);

        h = new DXFCircleHandler();
        handlerManager.addHandler(h);

        h = new DXFArcHandler();
        handlerManager.addHandler(h);
        
        h = new DXFAttribHandler();
        handlerManager.addHandler(h);
        
        h = new DXFAttribDefinitionHandler();
        handlerManager.addHandler(h);

        h = new DXFPolylineHandler();
        handlerManager.addHandler(h);

        h = new DXFLWPolylineHandler();
        handlerManager.addHandler(h);

        h = new DXFMTextHandler();
        handlerManager.addHandler(h);

        h = new DXFTextHandler();
        handlerManager.addHandler(h);

        h = new DXFInsertHandler();
        handlerManager.addHandler(h);

        h = new DXFEllipseHandler();
        handlerManager.addHandler(h);

        h = new DXFSolidHandler();
        handlerManager.addHandler(h);

        h = new DXFTraceHandler();
        handlerManager.addHandler(h);

        h = new DXFDimensionHandler();
        handlerManager.addHandler(h);

        h = new DXFHatchHandler();
        handlerManager.addHandler(h);

        h = new DXFImageHandler();
        handlerManager.addHandler(h);

        h = new DXF3DFaceHandler();
        handlerManager.addHandler(h);

        h = new DXFRayHandler();
        handlerManager.addHandler(h);

        h = new DXFXLineHandler();
        handlerManager.addHandler(h);

        h = new DXFRegionHandler();
        handlerManager.addHandler(h);
        
        h = new DXFPointHandler();
        handlerManager.addHandler(h);

        h = new DXFBodyHandler();
        handlerManager.addHandler(h);

        h = new DXF3DSolidHandler();
        handlerManager.addHandler(h);

        h = new DXFSplineHandler();
        handlerManager.addHandler(h);

        h = new DXFMLineHandler();
        handlerManager.addHandler(h);

        h = new DXFLeaderHandler();
        handlerManager.addHandler(h);

        h = new DXFToleranceHandler();
        handlerManager.addHandler(h);

        h = new DXFViewportHandler();
        handlerManager.addHandler(h);

        // the table handler
        handlerManager = new DXFTableSectionHandler();
        parser.addHandler(handlerManager);

        handler = new DXFLayerTableHandler();
        handlerManager.addHandler(handler);

        handler = new DXFLineTypeTableHandler();
        handlerManager.addHandler(handler);

        handler = new DXFDimensionStyleTableHandler();
        handlerManager.addHandler(handler);

        handler = new DXFStyleTableHandler();
        handlerManager.addHandler(handler);

        handler = new DXFVPortTableHandler();
        handlerManager.addHandler(handler);

        handler = new DXFViewTableHandler();
        handlerManager.addHandler(handler);

        // the entity section handler
        handlerManager = new DXFEntitiesSectionHandler();
        parser.addHandler(handlerManager);

        // the entity handlers
        h = new DXFLineHandler();
        handlerManager.addHandler(h);

        h = new DXFCircleHandler();
        handlerManager.addHandler(h);

        h = new DXFArcHandler();
        handlerManager.addHandler(h);

        h = new DXFAttribHandler();
        handlerManager.addHandler(h);
        
        h = new DXFAttribDefinitionHandler();
        handlerManager.addHandler(h);
          
        h = new DXFPolylineHandler();
        handlerManager.addHandler(h);

        h = new DXFLWPolylineHandler();
        handlerManager.addHandler(h);

        h = new DXFMTextHandler();
        handlerManager.addHandler(h);

        h = new DXFTextHandler();
        handlerManager.addHandler(h);

        h = new DXFInsertHandler();
        handlerManager.addHandler(h);

        h = new DXFEllipseHandler();
        handlerManager.addHandler(h);

        h = new DXFSolidHandler();
        handlerManager.addHandler(h);

        h = new DXFTraceHandler();
        handlerManager.addHandler(h);

        h = new DXFDimensionHandler();
        handlerManager.addHandler(h);

        h = new DXFHatchHandler();
        handlerManager.addHandler(h);

        h = new DXFImageHandler();
        handlerManager.addHandler(h);

        h = new DXF3DFaceHandler();
        handlerManager.addHandler(h);

        h = new DXFRayHandler();
        handlerManager.addHandler(h);

        h = new DXFXLineHandler();
        handlerManager.addHandler(h);

        h = new DXFRegionHandler();
        handlerManager.addHandler(h);

        h = new DXFBodyHandler();
        handlerManager.addHandler(h);

        h = new DXF3DSolidHandler();
        handlerManager.addHandler(h);

        h = new DXFSplineHandler();
        handlerManager.addHandler(h);

        h = new DXFMLineHandler();
        handlerManager.addHandler(h);

        h = new DXFLeaderHandler();
        handlerManager.addHandler(h);

        h = new DXFToleranceHandler();
        handlerManager.addHandler(h);

        h = new DXFViewportHandler();
        handlerManager.addHandler(h);

        h = new DXFPointHandler();
        handlerManager.addHandler(h);
        
        // the OBJECTS section
        handlerManager = new DXFObjectsSectionHandler();

        h = new DXFImageDefHandler();
        handlerManager.addHandler(h);

        h = new DXFDictionaryHandler();
        handlerManager.addHandler(h);

        h = new DXFPlotsettingsHandler();
        handlerManager.addHandler(h);

        h = new DXFLayoutHandler();
        handlerManager.addHandler(h);

        h = new DXFMLineStyleHandler();
        handlerManager.addHandler(h);

        //add the HandlerManager as Handler to the parser
        parser.addHandler(handlerManager);

        return parser;
    }

    /**
     * @see org.kabeja.dxf.parser.SAXParserBuilder the SAXParserBuilder for XML
     *      description
     *
     * @param file
     * @return the DXFParser build from the XML description file
     */
    public static Parser buildFromXML(String file) {
        try {
            return buildFromXML(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Parser buildFromXML(InputStream in) {
        return SAXParserBuilder.buildFromStream(in);
    }
}
