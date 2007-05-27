package org.kabeja.parser.dxf.filter;

import org.kabeja.parser.DXFParseException;
import org.kabeja.parser.DXFValue;

abstract class DXFStreamSectionFilter extends AbstractDXFStreamFilter {

	private final static String SECTION_START = "SECTION";

	private final static String SECTION_END = "ENDSEC";

	private final static int COMMAND_CODE = 0;

	protected boolean sectionStarts = false;

	protected String section;

	public void parseGroup(int groupCode, DXFValue value)
			throws DXFParseException {
		if ((groupCode == COMMAND_CODE)
				&& SECTION_START.equals(value.getValue())) {
			sectionStarts = true;
		} else if (sectionStarts) {
			sectionStarts = false;
			section = value.getValue();
			sectionStart(section);
			parseSection(COMMAND_CODE, new DXFValue(SECTION_START));
			parseSection(groupCode, value);

		} else {

			parseSection(groupCode, value);
		}
		if ((groupCode == COMMAND_CODE) && SECTION_END.equals(value.getValue())) {
			sectionEnd(section);
		}

	}

	protected abstract void parseSection(int groupCode, DXFValue value)
			throws DXFParseException;

	protected abstract void sectionStart(String Section)
			throws DXFParseException;

	protected abstract void sectionEnd(String Section) throws DXFParseException;
}
