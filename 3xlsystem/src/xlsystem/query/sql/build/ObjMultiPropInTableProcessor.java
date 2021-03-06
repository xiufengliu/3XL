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

package xlsystem.query.sql.build;

import xlsystem.common.Constants;
import xlsystem.common.RDF;
import xlsystem.common.Utility;
import xlsystem.common.XLException;
import xlsystem.query.sql.comp.BooleanCondition;
import xlsystem.query.sql.comp.FieldValue;
import xlsystem.query.sql.comp.KeyMap;
import xlsystem.query.sql.comp.SQLSelect;
import xlsystem.query.sql.comp.StringValue;
import xlsystem.query.sql.comp.TableName;

public class ObjMultiPropInTableProcessor extends ProcessorHelper implements Processor {

	@Override
	public void process(RDF rdf, SQLSelect sqlSelect) throws XLException {
		RDF nrdf = Utility.strip(rdf);
		TableName classTable = sqlSelect.getTableName(nrdf.getSub());
		if (classTable == null) {
			throw new XLException(Constants.ILLEGAL_QUERY);
		}
		
		String ct = classTable.getTableName();
		String name = ont.getMPTabName(ct, nrdf.getPre());

		TableName mpTable = new TableName(name, null);
		sqlSelect.addTableName(mpTable);
		sqlSelect.addKeyMap(new KeyMap(classTable, "id", mpTable, "id"));

		TableName rangeTable = null;
		if (Utility.isParam(rdf.getObj())) {
			rangeTable = sqlSelect.getTableName(nrdf.getObj());
			if (rangeTable == null) {
				name = ont.getRange(ct, nrdf.getPre());
				rangeTable = new TableName(name, nrdf.getObj());
				sqlSelect.addTableName(rangeTable);
			}
			sqlSelect.addKeyMap(new KeyMap(mpTable, nrdf.getPre(), rangeTable, "id"));
			sqlSelect.addFieldValue(new FieldValue(rangeTable, "uri", nrdf.getObj()));
		} else {
			name = ont.getRange(ct, nrdf.getPre());
			rangeTable = sqlSelect.getTableName(name);
			if (rangeTable == null) {
				rangeTable = new TableName(name, null);
				sqlSelect.addTableName(rangeTable);
				sqlSelect.addKeyMap(new KeyMap(mpTable, nrdf.getPre(), rangeTable, "id"));
			}
			FieldValue leftValue = new FieldValue(rangeTable, "uri");
			StringValue rightValue = new StringValue(rdf.getObj());
			sqlSelect.addWhereCondition(new BooleanCondition(leftValue, "=", rightValue));
		}
	}
}
