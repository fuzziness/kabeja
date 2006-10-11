package org.kabeja.parser;

import java.io.InputStream;

import org.kabeja.dxf.DXFDocument;

public interface Parser extends Handler{

	public abstract void parse(String file) throws DXFParseException;

	public abstract void parse(String file, String encoding)
			throws DXFParseException;

	public abstract void parse(InputStream input, String encoding)
			throws DXFParseException;

	public abstract DXFDocument getDocument();


	public abstract boolean supportedExtension(String extension);
}