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

public class Literal implements Value {

	private String _label;

	private String _language;

	private URI _datatype;

	public Literal(String label) {
		if (label == null) {
			throw new IllegalArgumentException("label must not be null");
		}
		_label = label;
	}

	public Literal(String label, String language) {
		this(label);
		_language = (language == null) ? null : language.toLowerCase();
	}

	public Literal(String label, URI datatype) {
		this(label);
		_datatype = datatype;
	}

	public String getLabel() {
		return _label;
	}

	// implements Literal.getLanguage()
	public String getLanguage() {
		return _language;
	}

	// implements Literal.getDatatype()
	public URI getDatatype() {
		return _datatype;
	}

	// Overrides Object.equals(Object)
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o instanceof Literal) {
			Literal other = (Literal) o;

			// Compare labels
			if (!_label.equals(other.getLabel())) {
				return false;
			}

			// Compare datatypes
			if (_datatype == null) {
				if (other.getDatatype() != null) {
					return false;
				}
			} else {
				if (!_datatype.equals(other.getDatatype())) {
					return false;
				}
			}

			// Compare language tags
			if (_language == null) {
				if (other.getLanguage() != null) {
					return false;
				}
			} else {
				if (!_language.equals(other.getLanguage())) {
					return false;
				}
			}

			return true;
		}

		return false;
	}

	// overrides Object.hashCode()
	public int hashCode() {
		return _label.hashCode();
	}

	// Implements Comparable.compareTo(Object)
	public int compareTo(Object o) {
		if (this == o) {
			return 0;
		}

		if (o instanceof Literal) {
			Literal other = (Literal) o;

			int result = _label.compareTo(other.getLabel());

			if (result == 0) {
				// labels are equal, compare datatypes. Literals without
				// datatypes are less than literals that do have one.

				if (_datatype == null) {
					if (other.getDatatype() != null) {
						result = -1;
					}
				} else if (other.getDatatype() == null) {
					result = 1;
				} else {
					result = _datatype.compareTo(other.getDatatype());
				}
			}

			if (result == 0) {
				// labels and datatypes are equal, compare languages. Literals
				// without languages are less than literals that do have one.

				if (_language == null) {
					if (other.getLanguage() != null) {
						result = -1;
					}
				} else if (other.getLanguage() == null) {
					result = 1;
				} else {
					result = _language.compareTo(other.getLanguage());
				}
			}

			return result;
		}

		// Force a ClassCastException if o is not a Value
		Value other = (Value) o;

		// Literal > URI and Literal > BNode
		return 1;
	}

	public String toString() {
		return _label;
	}

	@Override
	public boolean isLiteral() {
		return true;
	}

	@Override
	public boolean isURI() {
		return false;
	}

}
