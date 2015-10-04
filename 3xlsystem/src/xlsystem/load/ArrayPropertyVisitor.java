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

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.supercsv.io.ICsvMapWriter;

import xlsystem.common.Utility;
import xlsystem.db.DataTypeDict;
import xlsystem.ontology.Property;

public class ArrayPropertyVisitor implements PropertyVisitor {

	private Map<String, Object> row;
	private ValueHolder vh;

	@Override
	public void setConfig(String path, ValueHolder vh, Map<String, Object> row, Map<String, ICsvMapWriter> csvWriterMap, Map<String, String[]> headerMap) {
		this.vh = vh;
		this.row = row;
	}

	@Override
	public void visit(Object obj) {
		Property property = (Property) obj;
		if (property.isMultiProp()) {
			Set<Object> values = vh.getValues(property.getPGName());
			String strMPValue = mp2Array(property, values);
			row.put(property.getPGName(), strMPValue);
		} else {
			Object value = vh.getValue(property.getPGName());
			row.put(property.getPGName(), value == null ? "" : value);
		}
	}

	private String mp2Array(Property property, Set<Object> values) {
		if (values == null) {
			return "{}";
		} else {
			Iterator<Object> i = values.iterator();
			StringBuilder buf = new StringBuilder("{");
			if (property.isObjProp()) {
				for (; i.hasNext(); ) {
					if (buf.length() > 1)	buf.append(",");
						buf.append(i.next());
				}
			} else {
				boolean isStr = DataTypeDict.PG_VARCHAR.equals(property.getDataType());
				for (; i.hasNext(); ) {
					Object value = i.next();
					String v = Utility.removeSpecialChar(value, new char[] { '"', '\'', '/', '\\', '{', '}' });
					if (v == null) continue;
					if (buf.length() > 1)	buf.append(",");
					if (isStr) {
						buf.append("'").append(v).append("'");
					} else {
						buf.append(v);
					}
				}
			}
			buf.append("}");
			return buf.toString();
		}
	}

	public void end() {
		this.vh = null;
		this.row = null;
	}
}
