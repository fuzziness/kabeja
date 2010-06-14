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



import org.kabeja.entities.Arc;
import org.kabeja.entities.Attrib;
import org.kabeja.entities.AttribDefinition;
import org.kabeja.entities.Body;
import org.kabeja.entities.Circle;
import org.kabeja.entities.Dimension;
import org.kabeja.entities.Ellipse;
import org.kabeja.entities.Entity;
import org.kabeja.entities.Face3D;
import org.kabeja.entities.Hatch;
import org.kabeja.entities.Image;
import org.kabeja.entities.Insert;
import org.kabeja.entities.LWPolyline;
import org.kabeja.entities.Leader;
import org.kabeja.entities.Line;
import org.kabeja.entities.MLine;
import org.kabeja.entities.MText;
import org.kabeja.entities.Point;
import org.kabeja.entities.Polyline;
import org.kabeja.entities.Ray;
import org.kabeja.entities.Region;
import org.kabeja.entities.Shape;
import org.kabeja.entities.Solid;
import org.kabeja.entities.Solid3D;
import org.kabeja.entities.Spline;
import org.kabeja.entities.Text;
import org.kabeja.entities.Tolerance;
import org.kabeja.entities.Trace;
import org.kabeja.entities.Vertex;
import org.kabeja.entities.Viewport;
import org.kabeja.entities.XLine;



public class Type<T extends DraftEntity> {
	
	public static final Type<Face3D> TYPE_3DFACE = new Type<Face3D>("3DFace");
	public static final Type<Solid3D> TYPE_3DSOLID = new Type<Solid3D>("3DSolid");
	public static final Type<Arc> TYPE_ARC = new Type<Arc>("Arc");
	public static final Type<AttribDefinition> TYPE_ATTDEF = new Type<AttribDefinition>("Attdef");
	public static final Type<Attrib> TYPE_ATTRIB= new Type<Attrib>("Attrib");
	public static final Type<Body> TYPE_BODY = new Type<Body>("Body");
	public static final Type<Circle> TYPE_CIRCLE = new Type<Circle>("Circle");
	public static final Type<Dimension> TYPE_DIMENSTION = new Type<Dimension>("Dimension");	
	public static final Type<Entity> TYPE_ENTITY=new Type<Entity>("Entity");
	public static final Type<Ellipse> TYPE_ELLIPSE= new Type<Ellipse>("Ellipse");
	public static final Type<Hatch> TYPE_HATCH= new Type<Hatch>("Hatch");
	public static final Type<Image> TYPE_IMAGE= new Type<Image>("Image");
	public static final Type<Insert> TYPE_INSERT= new Type<Insert>("Insert");
	public static final Type<Leader> TYPE_LEADER= new Type<Leader>("Leader");
	public static final Type<Line> TYPE_LINE = new Type<Line>("Line");
	public static final Type<LWPolyline> TYPE_LWPOLYLINE= new Type<LWPolyline>("LWPolyline");
	public static final Type<MLine> TYPE_MLINE= new Type<MLine>("MLine");
	public static final Type<MText> TYPE_MTEXT= new Type<MText>("MText");
	public static final Type<Point> TYPE_POINT= new Type<Point>("Point");
	public static final Type<Polyline> TYPE_POLYLINE= new Type<Polyline>("Polyline");
	public static final Type<Ray> TYPE_RAY= new Type<Ray>("Ray");
	public static final Type<Region> TYPE_REGION= new Type<Region>("Region");
	public static final Type<Shape> TYPE_SHAPE= new Type<Shape>("Shape");
	public static final Type<Solid> TYPE_SOLID= new Type<Solid>("Solid");
	public static final Type<Spline> TYPE_SPLINE= new Type<Spline>("Spline");
	public static final Type<Text> TYPE_TEXT = new Type<Text>("Text");
	public static final Type<Tolerance> TYPE_TOLERANCE= new Type<Tolerance>("Tolerance");
	public static final Type<Trace> TYPE_TRACE= new Type<Trace>("Trace");
	public static final Type<Vertex> TYPE_VERTEX= new Type<Vertex>("Vertex");
	public static final Type<Viewport> TYPE_VIEWPORT= new Type<Viewport>("Viewport");
	public static final Type<XLine> TYPE_XLINE= new Type<XLine>("XLine");
	

	
	
	private String handle;
	private String typeName;
	
	
	
	public Type(String typeName){
		this.typeName = typeName;
		this.handle=typeName.toUpperCase();
	}
	
	
	public Type(String typeName,String handle){
		this.typeName=typeName;
		this.handle=handle;
	}
	
	
	public String getHandle(){
		return handle;
	}
	
	
	public String getName(){
		return this.typeName;
	}
	
}
