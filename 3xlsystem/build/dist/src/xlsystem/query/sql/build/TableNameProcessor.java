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

import xlsystem.common.RDF;
import xlsystem.common.Utility;
import xlsystem.common.XLException;
import xlsystem.query.sql.comp.FieldValue;
import xlsystem.query.sql.comp.SQLSelect;
import xlsystem.query.sql.comp.TableName;

public class TableNameProcessor extends ProcessorHelper implements Processor {
	// Add the new TableName to the sql
	
	public void process(RDF rdf, SQLSelect sqlSelect) throws XLException {
		RDF nrdf = Utility.strip(rdf);
		TableName tableName = new TableName(nrdf.getObj(), nrdf.getSub());
		sqlSelect.addTableName(tableName);
		sqlSelect.addFieldValue(new FieldValue(tableName, "uri", nrdf.getSub()));
	}
}
