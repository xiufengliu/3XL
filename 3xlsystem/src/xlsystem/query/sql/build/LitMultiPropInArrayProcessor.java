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
import xlsystem.query.sql.comp.BooleanAnyCondition;
import xlsystem.query.sql.comp.FieldValue;
import xlsystem.query.sql.comp.SQLSelect;
import xlsystem.query.sql.comp.StringValue;
import xlsystem.query.sql.comp.TableName;

public class LitMultiPropInArrayProcessor extends ProcessorHelper implements Processor {

	@Override
	public void process(RDF rdf, SQLSelect sqlSelect) throws XLException {
		RDF nrdf = Utility.strip(rdf);
		TableName classTable = sqlSelect.getTableName(nrdf.getSub());
		if (classTable == null) {
			throw new XLException(Constants.ILLEGAL_QUERY);
		}

		if (Utility.isParam(rdf.getObj())) {
			sqlSelect.addFieldValue(new FieldValue(classTable, nrdf.getPre(), nrdf.getObj()));
		} else { // Add WhereConditions
			FieldValue leftValue = new FieldValue(classTable, nrdf.getPre());
			StringValue rightValue = new StringValue(rdf.getObj());
			sqlSelect.addWhereCondition(new BooleanAnyCondition(leftValue, "=", rightValue));
		}
	}
}
