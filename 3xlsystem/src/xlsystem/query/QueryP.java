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
import java.util.Collection;
import java.util.List;
import java.util.Set;

import xlsystem.common.Constants;
import xlsystem.common.RDF;
import xlsystem.common.Utility;
import xlsystem.common.XLException;

public class QueryP extends QueryDAO implements Query{

	@Override
	public void query(RDF rdf, Writer writer) throws XLException {
		Connection con = null;
		try {
			boolean isOverflow = Metadata.isOverflow();
			con = conPool.getConnection();
			String p = rdf.getPre();
			String _p = Utility.removeNS(p);
			
			if ("type".equals(_p)) {
				PreparedStatement pstmt = con.prepareStatement("select uri, ct from map"); //, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				pstmt.setFetchSize(7000);
				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					writer.write(String.format("(%s%s%s%s%s)\n", rs.getString(1), delim, p, delim, Metadata.getFullName(rs.getString(2))));
				}
				rs.close();
				pstmt.close();
			} else if (ont.isDefined(_p)) {
				Collection<String> domSet = ont.getDomains(_p);
				for (String ct : domSet) {
					query(con, writer, ct, _p);
				}
			}

			if (isOverflow) {
				PreparedStatement pstmt = con.prepareStatement("select sub, obj from xl_Overflow where pre=?");
				pstmt.setFetchSize(200);
				pstmt.setString(1, p);
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

	private void query(Connection con, Writer writer, String ct, String p) throws  XLException {
		try{
		String sql = null;
		switch (ont.getPropType(ct, p)) {
		case Constants.OBJ_MP_TAB:
			String mp = ont.getMPTabName(ct, p);
			String r = ont.getRange(ct, p);
			sql = "select t1.uri, t3.uri from " + ct + " t1," + mp + " t2, " + r
					+ " t3 where t1.id=t2.id and t2." + p + "=t3.id";
			break;
		case Constants.OBJ_MP_ARR:
			r = ont.getRange(ct, p);
			sql = "select t1.uri, t2.uri from " + ct + " t1, " + r + " t2 where t2.id=any(t1." + p
					+ ")";
			break;
		case Constants.OBJ_SIG:
			r = ont.getRange(ct, p);
			sql = "select t1.uri, t2.uri from " + ct + " t1, " + r + " t2 where t1." + p
					+ " = t2.id";
			break;
		case Constants.LIT_MP_TAB:
			mp = ont.getMPTabName(ct, p);
			sql = "select t1.uri, t2." + p + "::Text from " + ct + " t1, " + mp
					+ " t2 where t1.id=t2.id";
			break;
		case Constants.LIT_MP_ARR:
			sql = "select uri, " + p + " from only " + ct;
			break;
		case Constants.LIT_SIG:
			sql = "select uri, " + p + "::Text from only " + ct;
			break;
		}
		PreparedStatement pstmt = con.prepareStatement(sql); //, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		pstmt.setFetchSize(7000);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			String s = rs.getString(1);
			Set<String> oList = Utility.mp2Set(rs.getString(2));
			for (String o : oList) {
				writer.write(String.format("(%s%s%s%s%s)\n", s, delim, Metadata.getFullName(p), delim, o));
			}
		}
		rs.close();
		pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new XLException(e);
		} finally {
			conPool.free(con);
		}
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
