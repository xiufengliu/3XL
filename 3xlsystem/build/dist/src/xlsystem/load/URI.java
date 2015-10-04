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

import xlsystem.common.Constants;
import xlsystem.common.Utility;

public class URI implements Value {

	private String _namespace;

	private String _localName;
	
	private String _pgName;
	
	private boolean _rdfType = false;

	public URI(String uri, String pgName) {
		// Find the place to split the uri
		int i = uri.length() - 1;
		while (i >= 0) {
			char c = uri.charAt(i);
			if (c == '#' || c == ':' || c == '/') {
				break;
			}
			i--;
		}
		if (i > 0) {
			// Split the uri
			_namespace = uri.substring(0, i + 1);
			_localName = uri.substring(i + 1);
			
			if (!Constants.NS_RDF.equals(_namespace)){
				if (pgName==null || pgName.length()==0){
					pgName = _localName.toLowerCase();
				}
				_pgName = Utility.processPgKeyWorkConfliction(pgName);
			} else {
				_pgName = pgName;
				_rdfType = true;
			}
		} else {
			throw new IllegalArgumentException("'" + uri + "' is not a legal (absolute) URI");
		}
	}


	// inherit comments
	public String getNamespace() {
		return _namespace;
	}

	// inherit comments
	public String getLocalName() {
		return _localName;
	}

	public String getPGName(){
		return _pgName;
	}
	// inherit comments
	public String getURI() {
		// This code is (much) more efficient then just concatenating the two
		// strings.
		char[] result = new char[_namespace.length() + _localName.length()];
		_namespace.getChars(0, _namespace.length(), result, 0);
		_localName.getChars(0, _localName.length(), result, _namespace.length());
		return new String(result);
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o instanceof URI) {
			URI other = (URI) o;
			return _localName.equals(other.getLocalName()) && _namespace.equals(other.getNamespace());
		}
		return false;
	}

	// Implements Object.hashCode()
	public int hashCode() {
		return _namespace.hashCode() ^ _localName.hashCode();
	}

	public int compareTo(Object o) {
		if (this == o) {
			return 0;
		}

		Value other = (Value) o;

		if (other instanceof URI) {
			return getURI().compareTo(((URI) other).getURI());
		} else {
			// URI < BNode and URI < Literal
			return -1;
		}
	}

	public String toString() {
		/*StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("fullURI=").append(getURI()).append("\n");
		strBuilder.append("pgName=").append(_pgName).append("\n");
		strBuilder.append("rdfType=").append(_rdfType).append("\n");
		return strBuilder.toString();*/
		return this.getURI();
	}

	@Override
	public boolean isLiteral() {
		return false;
	}

	@Override
	public boolean isURI() {
		return true;
	}



	/**
	 * @return the _rdfType
	 */
	public boolean isRdfType() {
		return _rdfType;
	}

}
