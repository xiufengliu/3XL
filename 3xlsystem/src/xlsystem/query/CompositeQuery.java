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

package xlsystem.query;

import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.List;

import org.postgresql.util.PSQLException;

import xlsystem.common.Constants;
import xlsystem.common.RDF;
import xlsystem.common.Utility;
import xlsystem.common.XLException;
import xlsystem.query.sql.SQLBuilder;

public class CompositeQuery extends QueryDAO implements Query {

	public void query(List<RDF> rdfs, Writer writer) throws XLException {
		Connection con = null;
		Savepoint querySavePoint = null;
		try {
			BufferedWriterWrapper bufWriter = (BufferedWriterWrapper)writer;
			String sql = SQLBuilder.buildSQL(rdfs);
			//System.out.println(sql);
			con = conPool.getConnection();
			querySavePoint = con.setSavepoint();
			PreparedStatement pstmt = con.prepareStatement(sql); //, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			pstmt.setFetchSize(7000);
			ResultSet rs = pstmt.executeQuery();
			int tripleCount = 0;
			while (rs.next()) {
				for (RDF rdf : rdfs) {
					RDF nrdf = Utility.strip(rdf);
					String s = rdf.getSub();
					String p = rdf.getPre();
					String o = rdf.getObj();
					String s_value = Utility.isParam(s) ? String.valueOf(rs.getObject(nrdf.getSub())) : s;
					String p_value = Utility.isParam(p) ? String.valueOf(rs.getObject(nrdf.getPre())) : p;
					String o_value = Utility.isParam(o) ? String.valueOf(rs.getObject(nrdf.getObj())) : o;
					bufWriter.write(String.format("(%s%s%s%s%s)\n", s_value, delim, p_value, delim, o_value));
					++tripleCount;
				}
			}
			rs.close();
			pstmt.close();
			writer.write(Constants.EOT);
			bufWriter.writeStatistics("Number of triples: " + tripleCount + "\n");
		} catch(PSQLException pe) {
			try {
				con.rollback(querySavePoint);
			} catch (SQLException e) {
			}
			throw new XLException(Constants.ILLEGAL_QUERY);
		} catch (Exception e) {
			e.printStackTrace();
			throw new XLException(e);
		} finally {
			conPool.free(con);
		}
	}

	@Override
	public List<RDF> query(RDF rdf) throws XLException {
		return null;
	}

	@Override
	public void query(RDF rdf, Writer writer) throws XLException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<RDF> query(List<RDF> rdfs) throws XLException {
		// TODO Auto-generated method stub
		return null;
	}
}
