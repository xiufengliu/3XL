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

import xlsystem.common.Utility;
import xlsystem.common.XLException;

public class NTripleLineParser {

	private String _subject;
	private URI _predicate;
	private Value _object;
	private Loader _loader;
	private int _len;

	public void setLoader(Loader loader) {
		_loader = loader;
	}

	public void parse(String line) throws IOException, XLException {
		if (line == null)
			return;
		_len = line.length();
		char ch[] = line.toCharArray();
		int i = _skipWhitespace(ch, 0);
		while (i < _len) {
			if (ch[i] == '#') {
				return;
			} else {
				i = _parseTriple(ch, i);
			}
		}
	}

	private int _skipWhitespace(char[] ch, int i) throws IOException {
		while (ch[i] == ' ' || ch[i] == '\t') {
			++i;
		}
		return i;
	}

	private int _parseTriple(char[] ch, int i) throws IOException, XLException {
		i = _parseSubject(ch, i);

		i = _skipWhitespace(ch, i);

		i = _parsePredicate(ch, i);

		i = _skipWhitespace(ch, i);

		i = _parseObject(ch, i);

		i = _skipWhitespace(ch, i);

		if (i == _len) {
			throw new XLException("EOL");
		} else if (ch[i] != '.') {
			throw new XLException("Expected '.', found: " + (char) ch[i]);
		}

		_loader.handleStatement(_subject, _predicate, _object);
		_subject = null;
		_predicate = null;
		_object = null;
		return i;
	}

	private int _parseSubject(char[] ch, int i) throws IOException, XLException {
		if (i < _len) {
			if (ch[i] == '<') {
				StringBuilder buf = new StringBuilder(100);
				i = _parseUriRef(ch, i, buf);
				_subject = Utility.unescapeString(buf.toString());
			} else {
				throw new XLException("Expected '<' or '_', found: " + (char) ch[i]);
			}
		} else {
			throw new XLException("EOL");
		}
		return i++;
	}

	private int _parsePredicate(char[] ch, int i) throws IOException, XLException {
		if (i < _len) {
			if (ch[i] == '<') {
				StringBuilder buf = new StringBuilder(100);
				i = _parseUriRef(ch, i, buf);
				_predicate = _createURI(buf.toString());
			} else {
				throw new XLException("Expected '<' or '_', found: " + (char) ch[i]);
			}
		} else {
			throw new XLException("EOL");
		}
		return i++;
	}

	private int _parseObject(char[] ch, int i) throws IOException, XLException {
		if (i < _len) {
			StringBuilder buf = new StringBuilder(100);
			if (ch[i] == '<') {
				i = _parseUriRef(ch, i, buf);
				_object = _createURI(buf.toString());
			} else if (ch[i] == '"') {
				StringBuilder lang = new StringBuilder(8);
				StringBuilder datatype = new StringBuilder(40);
				i = _parseLiteral(ch, i, buf, lang, datatype);
				_object = _createLiteral(buf.toString(), lang.toString(), datatype.toString());
			} else {
				throw new XLException("Expected '<' or  '\"', found: " + (char) ch[i]);
			}
		} else {
			throw new XLException("EOL");
		}
		return i++;
	}

	private int _parseUriRef(char[] ch, int i, StringBuilder uriRef) throws IOException, XLException {
		// Supplied char is '<', ignore it.
		// Read up to the next '>' character
		while (ch[i] != '>') {
			if (i == _len - 1) {
				throw new XLException("EOF");
			}
			uriRef.append((char) ch[++i]);
		}
		// c == '>', read next char
		return i;
	}

	private int _parseLiteral(char[] ch, int i, StringBuilder value, StringBuilder lang, StringBuilder datatype) throws XLException, IOException {
		// Supplied char is '"', ignore it.
		// Read up to the next '"' character
		++i;
		while (ch[i] != '"') {
			if (i == _len) {
				throw new XLException("EOL");
			}
			value.append((char) ch[i]);

			if (ch[i] == '\\') {
				// This escapes the next character, which might be a double
				// quote
				++i;
				if (ch[i] == _len) {
					throw new XLException("EOL");
				}
				value.append((char) ch[i]);
			}
			++i;
		}

		// c == '"', read next char
		++i;

		if (ch[i] == '@') {
			// Read language
			++i;
			while (i < _len && ch[i] != '.' && ch[i] != '^' && ch[i] != ' ' && ch[i] != '\t') {
				lang.append((char) ch[i]);
				++i;
			}
		} else if (ch[i] == '^') {
			// Read datatype
			++i;

			// c should be another '^'
			if (i == _len) {
				throw new XLException("EOL");
			} else if (ch[i] != '^') {
				throw new XLException("Expected '^', found: " + (char) ch[i]);
			}

			++i;

			// c should be a '<'
			if (i == _len) {
				throw new XLException("EOL");
			} else if (ch[i] != '<') {
				throw new XLException("Expected '<', found: " + (char) ch[i]);
			}
			i = _parseUriRef(ch, i, datatype);
		}

		return i;
	}

	private URI _createURI(String uri) throws XLException {
		try {
			uri = Utility.unescapeString(uri);
			return new URI(uri, null);
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
