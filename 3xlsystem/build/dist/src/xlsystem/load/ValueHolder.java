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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ValueHolder extends Thing {

	private static final long serialVersionUID = 1L;

	Map<String, Set<Object>> propValues;
	boolean nameLocked;

	public ValueHolder(String name) {
		this.name = name;
		propValues = new HashMap<String, Set<Object>>();
	}

	public ValueHolder() {
		this(null);
	}

	public boolean isNameLocked() {
		return nameLocked;
	}

	public void setNameLocked(boolean nameLocked) {
		this.nameLocked = nameLocked;
	}

	public void put(String p, Object value) {
		Set<Object> values = propValues.get(p);
		if (values == null) {
			values = new HashSet<Object>();
			propValues.put(p, values);
		}
		values.add(value);
	}

	public final Set<Object> getValues(String p) {
		return propValues.get(p);
	}

	public final Object getValue(String p) {
		Object obj = null;
		Set<Object> values = propValues.get(p);
		if (values != null) {
			Iterator<Object> i = values.iterator();
			if (i.hasNext()) {
				obj = i.next();
			}
		}
		return obj;
	}

	public void putAll(String p, List<Object> list) {
		Set<Object> values = propValues.get(p);
		if (values == null) {
			values = new HashSet<Object>();
			propValues.put(p, values);
		}
		values.addAll(list);
	}

	public boolean isOverflow() {
		return "xl_overflow".equals(this.name);
	}

	public String toString() {
		return this.uri; // Attention: It is used as Id in the CachedObject!!!

		/*
		 * StringBuilder strBuilder = new
		 * StringBuilder(this.name).append("(\n");
		 * strBuilder.append("id=").append(this.id).append("\n");
		 * strBuilder.append("uri=").append(this.uri).append("\n"); for (String
		 * prop : propValues.keySet()) {
		 * strBuilder.append(prop).append("=").append
		 * (propValues.get(prop).toString()).append("\n"); }
		 * strBuilder.append(")\n"); return strBuilder.toString();
		 */
	}
}
