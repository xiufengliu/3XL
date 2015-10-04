/*
 *
 * Copyright (c) 2011, Xiufeng Liu (xiliu@cs.aau.dk) and the eGovMon Consortium
 * Permission to use, copy, modify, and distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *
 *
 */

package xlsystem.load;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import xlsystem.common.Utility;
import xlsystem.common.XLException;

public class NTriplesParser {

	private Reader _reader;
	private String _subject;
	private URI _predicate;
	private Value _object;
	private Loader _loader;
	
	
	public void setLoader(Loader loader) {
		_loader = loader;
	}

	public  void parse(InputStream in) throws IOException, XLException {
		if (in == null) {
			throw new IllegalArgumentException("Input stream can not be 'null'");
		}
		try {
			parse(new InputStreamReader(in, "US-ASCII"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public  void parse(Reader reader) throws IOException, XLException {
		_reader = reader;
		int c = _reader.read();
		c = _skipWhitespace(c);

		
		while (c != -1) {
			if (c == '#') {
				// Comment, ignore
				c = _skipLine(c);
			} else if (c == '\r' || c == '\n') {
				// Empty line, ignore
				c = _skipLine(c);
			} else {
				c = _parseTriple(c);
			}
			c = _skipWhitespace(c);
		}
	}

	private int _skipWhitespace(int c) throws IOException {
		while (c == ' ' || c == '\t') {
			c = _reader.read();
		}

		return c;
	}

	private int _skipLine(int c) throws IOException {
		while (c != -1 && c != '\r' && c != '\n') {
			c = _reader.read();
		}

		// c is equal to -1, \r or \n. In case of a \r, we should
		// check whether it is followed by a \n.
		if (c == '\n') {
			c = _reader.read();
			//_lineNo++;
		} else if (c == '\r') {
			c = _reader.read();
			if (c == '\n') {
				c = _reader.read();
			}
			//_lineNo++;
		}
		return c;
	}

	private int _parseTriple(int c) throws IOException, XLException {
		c = _parseSubject(c);

		c = _skipWhitespace(c);

		c = _parsePredicate(c);

		c = _skipWhitespace(c);

		c = _parseObject(c);

		c = _skipWhitespace(c);

		if (c == -1) {
			throw new XLException("EOF");
		} else if (c != '.') {
			throw new XLException("Expected '.', found: " + (char) c);
		}

		c = _skipLine(c);

		
		 _loader.handleStatement(_subject, _predicate, _object);
		_subject = null;
		_predicate = null;
		_object = null;

		return c;
	}


	private int _parseSubject(int c) throws IOException, XLException {
		StringBuilder buf = new StringBuilder(100);
		if (c == '<') {
			c = _parseSubjectUriRef(c, buf);
			//_subject = _createURI(buf.toString());
			_subject = Utility.unescapeString(buf.toString());
		} else if (c == -1) {
			throw	new XLException("EOF");
		} else {
			throw	new XLException("Expected '<' or '_', found: " + (char) c);
		}
		return c;
	}

	private int _parsePredicate(int c) throws IOException, XLException {
		

		// predicate must be an uriref (<foo://bar>)
		if (c == '<') {
			StringBuilder buf = new StringBuilder(100);
			StringBuilder pgBuf = new StringBuilder(100);
			// predicate is an uriref
			c = _parseUriRef(c, buf, pgBuf);
			_predicate = _createURI(buf.toString(), pgBuf.toString());
		} else if (c == -1) {
			throw	new XLException("EOF");
		} else {
			throw	new XLException("Expected '<', found: " + (char) c);
		}
		return c;
	}

	private int _parseObject(int c) throws IOException, XLException {
		StringBuilder buf = new StringBuilder(100);
		StringBuilder pgBuf = new StringBuilder(100);
		// object is either an uriref (<foo://bar>), a nodeID (_:node1) or a
		// literal ("foo"-en or "1"^^<xsd:integer>).
		if (c == '<') {
			// object is an uriref
			c = _parseUriRef(c, buf, pgBuf);
			_object = _createURI(buf.toString(), pgBuf.toString());
		} else if (c == '"') {
			// object is a literal
			StringBuilder lang = new StringBuilder(8);
			StringBuilder datatype = new StringBuilder(40);
			c = _parseLiteral(c, buf, lang, datatype);
			_object = _createLiteral(buf.toString(), lang.toString(), datatype.toString());
		} else if (c == -1) {
			throw	new XLException("EOF");
		} else {
			throw	new XLException("Expected '<', '_' or '\"', found: " + (char) c);
		}
		return c;
	}

	private int _parseUriRef(int c, StringBuilder uriRef, StringBuilder pgUriRef) throws IOException, XLException {
		// Supplied char is '<', ignore it.
		// Read up to the next '>' character
		c = _reader.read();
		boolean isPglocalname = false;
		while (c != '>') {
			if (c == -1) {
			 throw	new XLException("EOF");
			}
			uriRef.append((char) c);
			
			if (isPglocalname && ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || c >= '0' && c <= '9')) {
				if (c >= 'A' && c <= 'Z'){
					c += 32;
				}
				pgUriRef.append((char) c);
			}
			
			if (c=='#'){
				isPglocalname = true;
			}
			c = _reader.read();
		}
		// c == '>', read next char
		c = _reader.read();

		return c;
	}

	private int _parseSubjectUriRef(int c, StringBuilder uriRef) throws IOException, XLException {
		// Supplied char is '<', ignore it.
		// Read up to the next '>' character
		c = _reader.read();
		while (c != '>') {
			if (c == -1) {
			 throw	new XLException("EOF");
			}
			uriRef.append((char) c);
			c = _reader.read();
		}
		// c == '>', read next char
		c = _reader.read();

		return c;
	}
	

	
	private int _parseLiteral(int c, StringBuilder value, StringBuilder lang, StringBuilder datatype) throws XLException, IOException {
		// Supplied char is '"', ignore it.
		// Read up to the next '"' character
		c = _reader.read();
		while (c != '"') {
			if (c == -1) {
				throw	new XLException("EOF");
			}
			value.append((char) c);

			if (c == '\\') {
				// This escapes the next character, which might be a double quote
				c = _reader.read();
				if (c == -1) {
					throw new XLException("EOF");
				}
				value.append((char) c);
			}
			c = _reader.read();
		}

		// c == '"', read next char
		c = _reader.read();

		if (c == '@') {
			// Read language
			c = _reader.read();
			while (c != -1 && c != '.' && c != '^' && c != ' ' && c != '\t') {
				lang.append((char) c);
				c = _reader.read();
			}
		} else if (c == '^') {
			// Read datatype
			c = _reader.read();

			// c should be another '^'
			if (c == -1) {
				throw	new XLException("EOF");
			} else if (c != '^') {
				throw	new XLException("Expected '^', found: " + (char) c);
			}

			c = _reader.read();

			// c should be a '<'
			if (c == -1) {
				throw new XLException("EOF");
			} else if (c != '<') {
				throw new XLException("Expected '<', found: " + (char) c);
			}
			c = _parseUriRef(c, datatype, new StringBuilder());
		}

		return c;
	}

	private URI _createURI(String uri, String pgLocalname) throws XLException {
		try {
			uri = Utility.unescapeString(uri);
			return new URI(uri, pgLocalname);
		} catch (Exception e) {
			throw new XLException(e);
		}
	}

	private Literal _createLiteral(String label, String lang, String datatype) {
		label = Utility.unescapeString(label);
		if (lang.length() == 0) {
			lang = null;
		}
		return new Literal(label, lang);
	}

}
