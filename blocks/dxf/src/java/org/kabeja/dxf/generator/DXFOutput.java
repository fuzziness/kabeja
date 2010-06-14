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
package org.kabeja.dxf.generator;

import java.io.IOException;
import java.io.OutputStream;

import org.kabeja.io.GenerationException;

public class DXFOutput {

	protected OutputStream out;
	protected String encoding;

	public DXFOutput(OutputStream out, String encoding) {
		this.out =out;
		this.encoding = encoding;
	}

	public void output(int groupCode, int value) throws GenerationException {
		try {
			this.outputGroupCode(groupCode);
			this.out.write(Integer.toString(value).getBytes(encoding));
			this.newLine();
		} catch (Exception e) {
			throw new GenerationException("Error on groupCode:" + groupCode
					+ " integer value:" + value, e);
		}
	}

	public void output(int groupCode, double value)
			throws GenerationException {
		try {
			this.outputGroupCode(groupCode);
			this.out.write(Double.toString(value).getBytes(encoding));
			this.newLine();
		} catch (Exception e) {
			throw new GenerationException("Error on groupCode:" + groupCode
					+ " double value:" + value, e);
		}
	}

	public void output(int groupCode, String value)
			throws GenerationException {
		try {
			this.outputGroupCode(groupCode);
			this.out.write(value.getBytes(encoding));
			this.newLine();
		} catch (Exception e) {
			throw new GenerationException("Error on groupCode:" + groupCode
					+ " string value:" + value, e);
		}
	}

	protected void outputGroupCode(int groupCode) throws IOException {
		this.out.write(' ');
		this.out.write(Integer.toString(groupCode).getBytes(encoding));
		this.newLine();
	}

	protected void newLine() throws IOException {
		this.out.write('\n');
	}
}
