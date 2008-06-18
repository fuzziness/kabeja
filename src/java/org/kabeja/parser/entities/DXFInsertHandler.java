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
package org.kabeja.parser.entities;

import org.kabeja.dxf.DXFAttrib;
import org.kabeja.dxf.DXFConstants;
import org.kabeja.dxf.DXFEntity;
import org.kabeja.dxf.DXFInsert;
import org.kabeja.parser.DXFEntitiesSectionHandler;
import org.kabeja.parser.DXFValue;

/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 * 
 */
public class DXFInsertHandler extends AbstractEntityHandler {
	public static final int SCALE_X = 41;
	public static final int SCALE_Y = 42;
	public static final int SCALE_Z = 43;
	public static final int ROTATE = 50;
	public static final int COLUMN_COUNT = 70;
	public static final int ROW_COUNT = 71;
	public static final int COLUMN_SPACING = 44;
	public static final int ROW_SPACING = 45;
	public static final int BLOCK_NAME = 2;
	public static final int HAS_ATTRIBUTES = 66;

	private DXFInsert insert;
	private boolean attributesFollow = false;
	private boolean delegateAttribute = false;
	private DXFAttribHandler attribHandler;
	private DXFAttribDefinitionHandler attDefHandler;
	private DXFEntityHandler handler;

	/**
	 * 
	 */
	public DXFInsertHandler() {
		super();
	}

	public void endDXFEntity() {
	}

	public DXFEntity getDXFEntity() {
		return insert;
	}

	public String getDXFEntityName() {
		return DXFConstants.ENTITY_TYPE_INSERT;
	}

	public boolean isFollowSequence() {
		return this.attributesFollow;
		//return false;
	}

	public void parseGroup(int groupCode, DXFValue value) {
		
		if ((groupCode == DXFConstants.END_SEQUENCE_CODE)
				|| DXFConstants.END_SEQUENCE.equals(value.getValue())) {

			// add the last attribute to the block reference (insert or minsert)
			this.addParsedAttribute();
			this.attributesFollow = false;
			this.delegateAttribute=false;
			return;
		} else  if (groupCode == DXFEntitiesSectionHandler.ENTITY_START) {
			
			if (this.delegateAttribute) {
				this.addParsedAttribute();
			} else {
				//initialize the Handlers
				this.attribHandler = new DXFAttribHandler();
				this.attribHandler.setDXFDocument(this.doc);
				this.attDefHandler = new DXFAttribDefinitionHandler();
				this.attDefHandler.setDXFDocument(this.doc);
				this.delegateAttribute=true;
			}
			if(value.getValue().equals(this.attribHandler.getDXFEntityName())){
				this.handler = this.attribHandler;
			}else{
				//is attribute definition
				this.handler = this.attDefHandler;
			}
			
			this.handler.startDXFEntity();
            return;
		} else if (this.delegateAttribute) {
			this.handler.parseGroup(groupCode, value);
			return;
		}

		switch (groupCode) {

		case GROUPCODE_START_X:
			insert.getPoint().setX(value.getDoubleValue());

			break;

		case GROUPCODE_START_Y:
			insert.getPoint().setY(value.getDoubleValue());

			break;

		case GROUPCODE_START_Z:
			insert.getPoint().setZ(value.getDoubleValue());

			break;

		case SCALE_X:
			insert.setScaleX(value.getDoubleValue());

			break;

		case SCALE_Y:
			insert.setScaleY(value.getDoubleValue());

			break;

		case SCALE_Z:
			insert.setScaleZ(value.getDoubleValue());

			break;

		case ROTATE:
			insert.setRotate(value.getDoubleValue());

			break;

		case COLUMN_COUNT:
			insert.setColumns(value.getIntegerValue());

			break;

		case ROW_COUNT:
			insert.setRows(value.getIntegerValue());

			break;

		case COLUMN_SPACING:
			insert.setColumnSpacing(value.getDoubleValue());

			break;

		case ROW_SPACING:
			insert.setRowSpacing(value.getDoubleValue());

			break;

		case BLOCK_NAME:
			insert.setBlockID(value.getValue());

			break;

		case HAS_ATTRIBUTES:
			if ((value.getIntegerValue() & 1) == 1) {
				this.attributesFollow = true;
			}

		default:
			super.parseCommonProperty(groupCode, value, insert);
		}
	}

	public void startDXFEntity() {
		insert = new DXFInsert();
	}

	protected void addParsedAttribute() {
		
		this.attribHandler.endDXFEntity();
		DXFAttrib attrib = (DXFAttrib) this.attribHandler.getDXFEntity();
		attrib.setDXFDocument(this.doc);
		attrib.setBlockAttribute(true);
		this.insert.addAttribute(attrib);
	}
}
