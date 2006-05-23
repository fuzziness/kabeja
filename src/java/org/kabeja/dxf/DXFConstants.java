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
package org.kabeja.dxf;

import org.kabeja.dxf.helpers.Vector;


/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 */
public class DXFConstants {
    public static final String DEFAULT_LAYER = "0";
    public final static String HEADER_ACAD_VERSION = "$ACADVER";
    public final static String HEADER_ACAD_MAINT_VERSION = "$ACADMAINTVER";
    public final static String HEADER_POINT_DISPLAY_MODE = "$PDMODE";
    public final static int GROUPCODE_STANDARD_LAYER = 8;
    public final static int GROUPCODE_STANDARD_FLAGS = 70;
    public final static int GROUPCODE_SUBCLASS_MARKER = 100;
    public final static String ENTITY_TYPE_ARC = "ARC";
    public final static String ENTITY_TYPE_LINE = "LINE";
    public final static String ENTITY_TYPE_MLINE = "MLINE";
    public final static String ENTITY_TYPE_XLINE = "XLINE";
    public final static String ENTITY_TYPE_RAY = "RAY";
    public final static String ENTITY_TYPE_CIRCLE = "CIRCLE";
    public final static String ENTITY_TYPE_ELLIPSE = "ELLIPSE";
    public final static String ENTITY_TYPE_IMAGE = "IMAGE";
    public final static String ENTITY_TYPE_POLYLINE = "POLYLINE";
    public final static String ENTITY_TYPE_LWPOLYLINE = "LWPOLYLINE";
    public final static String ENTITY_TYPE_HATCH = "HATCH";
    public final static String ENTITY_TYPE_TEXT = "TEXT";
    public final static String ENTITY_TYPE_MTEXT = "MTEXT";
    public final static String ENTITY_TYPE_VERTEX = "VERTEX";
    public final static String ENTITY_TYPE_POINT = "POINT";
    public final static String ENTITY_TYPE_SOLID = "SOLID";
    public final static String ENTITY_TYPE_TRACE = "TRACE";
    public final static String ENTITY_TYPE_INSERT = "INSERT";
    public final static String ENTITY_TYPE_ATTRIB = "ATTRIB";
    public final static String ENTITY_TYPE_3DFACE = "3DFACE";
    public final static String ENTITY_TYPE_3DSOLID = "3DSOLID";
    public final static String ENTITY_TYPE_BODY = "BODY";
    public final static String ENTITY_TYPE_REGION = "REGION";
    public final static String ENTITY_TYPE_SPLINE = "SPLINE";
    public final static String ENTITY_TYPE_SHAPE = "SHAPE";
    public final static String ENTITY_TYPE_LEADER = "LEADER";
    public final static String ENTITY_TYPE_TOLERANCE = "TOLERANCE";
    public final static String ENTITY_TYPE_TABLE = "TABLE";
    public final static String ENTITY_TYPE_DIMENSION = "DIMENSION";
    public final static String OBJECT_TYPE_IMAGEDEF = "IMAGEDEF";
    public final static Vector DEFAULT_X_AXIS_VECTOR = new Vector(1.0, 0.0, 0.0);
    public final static Vector DEFAULT_Y_AXIS_VECTOR = new Vector(0.0, 1.0, 0.0);
    public final static Vector DEFAULT_Z_AXIS_VECTOR = new Vector(0.0, 0.0, 1.0);
    public final static String TABLE_KEY_VPORT = "VPORT";
    public final static String TABLE_KEY_VIEW = "VIEW";
    public final static String TABLE_KEY_DIMSTYLE = "DIMSTYLE";
    public final static String TABLE_KEY_LAYER = "LAYER";
    public final static String TABLE_KEY_LTYPE = "LTYPE";
    public final static String TABLE_KEY_STYLE = "STYLE";
    public final static String TABLE_KEY_UCS = "UCS";
    public final static String TABLE_KEY_APPID = "APPID";
    public final static String TABLE_KEY_BLOCK_RECORD = "BLOCK_RECORD";
    public final static String SECTION_START = "SECTION";
    public final static String SECTION_END = "ENDSEC";
    public final static String END_STREAM = "EOF";
    public final static int COMMAND_CODE = 0;
    public final static String SECTION_HEADER = "HEADER";
    public final static String SECTION_CLASSES = "CLASSES";
    public final static String SECTION_TABLES = "TABLES";
    public final static String SECTION_BLOCKS = "BLOCKS";
    public final static String SECTION_ENTITIES = "ENTITIES";
    public final static String SECTION_OBJECTS = "OBJECTS";
    public final static String SECTION_THUMBNAILIMAGE = "THUMBNAILIMAGE";
    
    public static double POINT_CONNECTION_RADIUS=0.0001;
    
}
