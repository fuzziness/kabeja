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
*/
package org.kabeja.svg;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.kabeja.dxf.DXFConstants;
import org.kabeja.svg.generators.SVG3DFaceGenerator;
import org.kabeja.svg.generators.SVG3DSolidGenerator;
import org.kabeja.svg.generators.SVGArcGenerator;
import org.kabeja.svg.generators.SVGAttribGenerator;
import org.kabeja.svg.generators.SVGCircleGenerator;
import org.kabeja.svg.generators.SVGDimensionGenerator;
import org.kabeja.svg.generators.SVGEllipseGenerator;
import org.kabeja.svg.generators.SVGHatchGenerator;
import org.kabeja.svg.generators.SVGImageGenerator;
import org.kabeja.svg.generators.SVGInsertGenerator;
import org.kabeja.svg.generators.SVGLWPolylineGenerator;
import org.kabeja.svg.generators.SVGLeaderGenerator;
import org.kabeja.svg.generators.SVGLineGenerator;
import org.kabeja.svg.generators.SVGMLineGenerator;
import org.kabeja.svg.generators.SVGMTextGenerator;
import org.kabeja.svg.generators.SVGPointGenerator;
import org.kabeja.svg.generators.SVGPolylineGenerator;
import org.kabeja.svg.generators.SVGRayGenerator;
import org.kabeja.svg.generators.SVGShapeGenerator;
import org.kabeja.svg.generators.SVGSolidGenerator;
import org.kabeja.svg.generators.SVGSplineGenerator;
import org.kabeja.svg.generators.SVGTextGenerator;
import org.kabeja.svg.generators.SVGToleranceGenerator;
import org.kabeja.svg.generators.SVGTraceGenerator;
import org.kabeja.svg.generators.SVGViewportGenerator;
import org.kabeja.svg.generators.SVGXLineGenerator;


public class SVGSAXGeneratorManager {
    protected Map generators = new HashMap();
    protected Map pathBoundaryGenerator = new HashMap();

    public SVGSAXGeneratorManager() {
        this.initialize();
    }

    public SVGSAXGenerator getSVGGenerator(String entityType)
        throws SVGGenerationException {
        SVGSAXGenerator gen = (SVGSAXGenerator) this.generators.get(entityType);

        if (gen == null) {
            throw new SVGGenerationException("EntityType:" + entityType +
                " not supported");
        } else {
            return gen;
        }
    }

    public SVGPathBoundaryGenerator getSVGPathBoundaryGenerator(String type) {
        return (SVGPathBoundaryGenerator) this.pathBoundaryGenerator.get(type);
    }

    public void setSVGSAXGenerator(SVGSAXGenerator generator, String entityType) {
        this.generators.put(entityType, generator);
    }

    protected void initialize() {
        // setup all generators here
        this.generators.put(DXFConstants.ENTITY_TYPE_3DFACE,
            new SVG3DFaceGenerator());
        this.generators.put(DXFConstants.ENTITY_TYPE_3DSOLID,
            new SVG3DSolidGenerator());
        this.generators.put(DXFConstants.ENTITY_TYPE_ARC, new SVGArcGenerator());
        this.generators.put(DXFConstants.ENTITY_TYPE_ATTRIB,
            new SVGAttribGenerator());

        this.generators.put(DXFConstants.ENTITY_TYPE_CIRCLE,
            new SVGCircleGenerator());
        this.generators.put(DXFConstants.ENTITY_TYPE_DIMENSION,
            new SVGDimensionGenerator());
        this.generators.put(DXFConstants.ENTITY_TYPE_ELLIPSE,
            new SVGEllipseGenerator());
        this.generators.put(DXFConstants.ENTITY_TYPE_HATCH,
            new SVGHatchGenerator());
        this.generators.put(DXFConstants.ENTITY_TYPE_IMAGE,
            new SVGImageGenerator());
        this.generators.put(DXFConstants.ENTITY_TYPE_INSERT,
            new SVGInsertGenerator());
        this.generators.put(DXFConstants.ENTITY_TYPE_LEADER,
            new SVGLeaderGenerator());
        this.generators.put(DXFConstants.ENTITY_TYPE_LINE,
            new SVGLineGenerator());

        this.generators.put(DXFConstants.ENTITY_TYPE_LWPOLYLINE,
            new SVGLWPolylineGenerator());
        this.generators.put(DXFConstants.ENTITY_TYPE_MLINE,
            new SVGMLineGenerator());
        this.generators.put(DXFConstants.ENTITY_TYPE_MTEXT,
            new SVGMTextGenerator());
        this.generators.put(DXFConstants.ENTITY_TYPE_POINT,
            new SVGPointGenerator());
        this.generators.put(DXFConstants.ENTITY_TYPE_POLYLINE,
            new SVGPolylineGenerator());
        this.generators.put(DXFConstants.ENTITY_TYPE_RAY, new SVGRayGenerator());
        this.generators.put(DXFConstants.ENTITY_TYPE_SHAPE,
            new SVGShapeGenerator());
        this.generators.put(DXFConstants.ENTITY_TYPE_SOLID,
            new SVGSolidGenerator());
        this.generators.put(DXFConstants.ENTITY_TYPE_SPLINE,
            new SVGSplineGenerator());
        this.generators.put(DXFConstants.ENTITY_TYPE_TEXT,
            new SVGTextGenerator());
        this.generators.put(DXFConstants.ENTITY_TYPE_TOLERANCE,
            new SVGToleranceGenerator());
        this.generators.put(DXFConstants.ENTITY_TYPE_TRACE,
            new SVGTraceGenerator());

        this.generators.put(DXFConstants.ENTITY_TYPE_VIEWPORT,
            new SVGViewportGenerator());
        this.generators.put(DXFConstants.ENTITY_TYPE_XLINE,
            new SVGXLineGenerator());

        //filter the boundarypathgenerators
        Iterator i = this.generators.keySet().iterator();

        while (i.hasNext()) {
            String type = (String) i.next();
            Object obj = this.generators.get(type);

            if (obj instanceof SVGPathBoundaryGenerator) {
                this.pathBoundaryGenerator.put(type, obj);
            }
        }
    }
}
