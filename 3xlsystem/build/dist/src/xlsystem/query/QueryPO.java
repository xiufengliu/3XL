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

import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import xlsystem.common.Constants;
import xlsystem.common.RDF;
import xlsystem.common.Utility;
import xlsystem.common.XLException;

public class QueryPO extends QueryDAO implements Query {

	@Override
	public void query(RDF rdf, Writer writer) throws XLException {
		Connection con = null;
		try {
			boolean isOverflow = Metadata.isOverflow();
			con = conPool.getConnection();
			String p = rdf.getPre();
			String _p = Utility.removeNS(p);
			String o = rdf.getObj();
			if ("type".equals(_p)) {
				PreparedStatement pstmt = con.prepareStatement("select uri, ct from map"); //, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				pstmt.setFetchSize(7000);
				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					writer.write(String.format("(%s%s%s%s%s)\n", rs.getString(1), delim, p, delim, rs.getString(2)));
				}
				rs.close();
				pstmt.close();
			} else if (ont.isDefined(_p)) {
				Collection<String> domSet = ont.getDomains(_p);
				for (String ct : domSet) {
					query(con, writer, ct, _p, o);
				}
			}

			if (isOverflow) {
				PreparedStatement pstmt = con.prepareStatement("select sub from xl_Overflow where pre=? and obj=?");
				pstmt.setFetchSize(7000);
				pstmt.setString(1, p);
				pstmt.setString(2, o);
				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					writer.write(String.format("(%s%s%s%s%s)\n", rs.getString(1), delim, p, delim, rs.getString(2)));
				}
				rs.close();
				pstmt.close();
			}
			writer.write(Constants.EOT);
			//writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
			throw new XLException(e);
		} finally {
			conPool.free(con);
		}
	}

	private void query(Connection con, Writer writer, String ct, String p, String o) throws SQLException, IOException {
		String sql = null;
		switch (ont.getPropType(ct, p)) {
		case Constants.OBJ_MP_TAB:
			String mp = ont.getMPTabName(ct, p);
			sql = "select t1.uri from " + ct + " t1," + mp + " t2,"
					+ "map t3 where t1.id=t2.id and t2." + p
					+ "=t3.id and decode(md5(t3.uri),'hex')=decode(md5(?),'hex')";
			break;
		case Constants.OBJ_MP_ARR:
			sql = "select t1.uri from " + ct + " t1, map t2 where t2.id=any(t1." + p
					+ ") and decode(md5(t2.uri),'hex')=decode(md5(?),'hex')";
			break;
		case Constants.OBJ_SIG:
			sql = "select t1.uri from " + ct + " t1, map t2 where t1." + p
					+ " = t2.id and decode(md5(t2.uri),'hex')=decode(md5(?),'hex')";
			break;
		case Constants.LIT_MP_TAB:
			mp = ont.getMPTabName(ct, p);
			sql = "select t1.uri from " + ct + " t1, " + mp + " t2 where t1.id=t2.id and t2." + p
					+ "::Text=?";
			break;
		case Constants.LIT_MP_ARR:
			sql = "select uri from " + ct + " where ?=any(" + p + ")";
			break;
		case Constants.LIT_SIG:
			sql = "select uri from " + ct + " where " + p + "::Text=?";
			break;
		}
		PreparedStatement pstmt = con.prepareStatement(sql); //, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		pstmt.setFetchSize(7000);
		pstmt.setString(1, o);
		ResultSet rs = pstmt.executeQuery();

		while (rs.next()) {
			String s = rs.getString(1);
			writer.write(String.format("(%s%s%s%s%s)\n", s, delim, Metadata.getFullName(p), delim, o));
		}
		rs.close();
		pstmt.close();
		//writer.flush();
	}

	public List<RDF> query(RDF rdf) throws XLException {
		return null;
	}

	@Override
	public List<RDF> query(List<RDF> rdfs) throws XLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void query(List<RDF> rdfs, Writer writer) throws XLException {
		this.query(rdfs.get(0), writer);
	}

}
