package org.kabeja.dxf.generator.entities;

import org.kabeja.common.DraftEntity;
import org.kabeja.dxf.generator.DXFGenerationContext;
import org.kabeja.dxf.generator.DXFOutput;
import org.kabeja.dxf.generator.conf.DXFSubType;
import org.kabeja.entities.LW2DVertex;
import org.kabeja.entities.LWPolyline;
import org.kabeja.entities.Vertex;
import org.kabeja.io.GenerationException;
import org.kabeja.util.Constants;

public class DXFLWPolylineGenerator extends AbstractDXFEntityGenerator {

	@Override
	protected void generateSubType(DXFSubType subtype, DraftEntity entity, DXFOutput output, DXFGenerationContext context) throws GenerationException {

		if (subtype.getName().equals(Constants.SUBCLASS_MARKER_ENTITY_LWPOLYLINE)) {
			LWPolyline lw = (LWPolyline) entity;
			for (int groupCode : subtype.getGroupCodes()) {
				switch (groupCode) {
				case 38:
					//elevation
					output.output(90,0);
					break;
				case 39:
					output.output(39, lw.getThickness());
					break;
				case 43:
					if(lw.isConstantWidth()){
					  output.output(43, lw.getStartWidth());
					}
					break;
				case 70:
					output.output(70,(lw.isClosed()?1:0));
					break;			
				case 90:
					output.output(90,lw.getVertexCount());
					break;	
					
				default:
					super.outputCommonGroupCode(groupCode, lw, output);
				}

			}

		} else if (subtype.getName().equals(Constants.SUBCLASS_MARKER_ENTITY_VERTEX)) {
			LWPolyline lw = (LWPolyline) entity;
			boolean customWidth=!lw.isConstantWidth();
			
			for (LW2DVertex vertex : lw.getVertices()) {
				for (int groupCode : subtype.getGroupCodes()) {
					switch (groupCode) {
					case 10:
						output.output(10, vertex.getPoint().getX());
						break;
					case 20:
						output.output(20, vertex.getPoint().getY());
						break;
					case 40:
						if(customWidth && vertex.getStartWidth()!=0.0){
						output.output(40, vertex.getStartWidth());
						}
						break;
					case 41:
						if(customWidth && vertex.getEndWidth() != 0.0){
						  output.output(41, vertex.getEndWidth());
						}
						break;
					case 42:
						if(vertex.getBulge()!=0.0){
						  output.output(42, vertex.getBulge());
						}
						break;
					}
				}
			}
		}
	}

}
