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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.apache.commons.dbutils.ResultSetHandler;

import xlsystem.ontology.OwlOntology;

public class ValueHolderResultSetHandler implements ResultSetHandler<ValueHolder> {

	OwlOntology ont;
	String tableName;

	public ValueHolderResultSetHandler(OwlOntology ont, String tableName) {
		this.ont = ont;
		this.tableName = tableName;
	}

	@Override
	public ValueHolder handle(ResultSet rs) throws SQLException {
		if (!rs.next()) {
			return null;
		}

		ValueHolder vh = new ValueHolder(tableName);
		vh.setId(rs.getInt("id"));
		vh.setUri(rs.getString("uri"));

		ResultSetMetaData meta = rs.getMetaData();
		int cols = meta.getColumnCount();
		for (int i = 2; i < cols; ++i) {
			String propertyName = meta.getColumnName(i);
			String value = rs.getString(propertyName);

			int len = 0;
			if (value == null || (len = value.length()) == 0)
				continue;
			char[] ch = value.toCharArray();
			
			if (len > 1 && ch[0] == '{' && ch[len - 1] == '}') {
				if ((len - 2) == 0)
					continue;
				char[] mp = new char[len - 2];
				int cnt = 0, j = 1;
				while (j < len - 1) {
					if (ch[j] == '\'') {
						j++;
						while (ch[j] != '\'') {
							mp[cnt++] = ch[j++];
							if (j == len - 1) {
								throw new SQLException("Malformat!");
							}
						}
						vh.put(propertyName, new String(mp, 0, cnt));
						cnt = 0;
						j++;
					} else if (ch[j] != ',') {
						mp[cnt++] = ch[j++];
						if (j == len - 1) {
							vh.put(propertyName, new String(mp, 0, cnt));
						}
					} else if (ch[j] == ',' && cnt > 0) {
						vh.put(propertyName, new String(mp, 0, cnt));
						j++;
						cnt = 0;
					} else {
						j++;
					}
				}
			} else {
				vh.put(propertyName, value);
			}
		}
		return vh;
	}

}
