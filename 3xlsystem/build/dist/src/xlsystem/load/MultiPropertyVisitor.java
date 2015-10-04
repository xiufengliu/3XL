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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.supercsv.io.ICsvMapWriter;


import xlsystem.common.Utility;
import xlsystem.common.XLException;
import xlsystem.ontology.Property;

public class MultiPropertyVisitor implements PropertyVisitor {

	private Map<String, Object> row;
	private ValueHolder vh;
	private Map<String, ICsvMapWriter> csvWriterMap;
	private Map<String, String[]>headerMap;
	private String csvPath;

	@Override
	public void setConfig(String path, ValueHolder vh, Map<String, Object> row, Map<String, ICsvMapWriter> csvWriterMap, Map<String, String[]>headerMap) {
		this.csvPath = path;
		this.vh = vh;
		this.row = row;
		this.csvWriterMap = csvWriterMap;
		this.headerMap = headerMap;
	}

	@Override
	public void visit(Object obj) throws XLException {
		Property property = (Property) obj;
		try {
			if (property.isMultiProp()) {
				writeMP2CSV(property);
			} else {
				writeSP2CSV(property);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new XLException(e.getMessage());
		}
	}

	private void writeSP2CSV(Property property) throws IOException {
		Object value = vh.getValue(property.getPGName());
		row.put(property.getPGName(), value == null ? "" : value);
	}

	private void writeMP2CSV(Property property) throws IOException {
		Set<Object> values = vh.getValues(property.getPGName());
		if (values != null) {
			String mpTableName = property.getMPTableName();
			ICsvMapWriter mpWriter = csvWriterMap.get(mpTableName);
			if (mpWriter == null) {
				StringBuffer buf = new StringBuffer(this.csvPath).append(mpTableName).append(".csv");
				mpWriter = Utility.getMapWriter(buf.toString());
				csvWriterMap.put(mpTableName, mpWriter);
			}

			String[] header = headerMap.get(mpTableName);
			if (header == null) {
				header = new String[] { "id", property.getPGName() };
				headerMap.put(mpTableName, header);
			}

			HashMap<String, Object> mpValueMap = new HashMap<String, Object>();
			for (Iterator<Object> i = values.iterator(); i.hasNext();) {
				Object value = i.next();
				if (value != null) {
					mpValueMap.put(header[0], vh.getId());
					mpValueMap.put(header[1], value);
					mpWriter.write(mpValueMap, header);
				}
			}
		}
	}

	@Override
	public void end() {
		this.vh = null;
		this.row = null;
		this.csvWriterMap = null;
		this.headerMap = null;
	}
}
