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
import java.util.List;
import java.util.Set;

import xlsystem.common.Constants;
import xlsystem.common.RDF;
import xlsystem.common.Utility;
import xlsystem.common.XLException;

public class QuerySP extends QueryDAO implements Query {

	@Override
	public void query(RDF rdf, Writer writer) throws XLException {
		Connection con = null;
		try {
			boolean isOverflow = Metadata.isOverflow();
			con = conPool.getConnection();
			String s = rdf.getSub();
			String p = rdf.getPre();
			String _p = Utility.removeNS(p);
			xlsystem.common.Map map = super.getMap(s, con);
			String sql = null;
			
			if (map != null) {
				if ("type".equals(_p)) {
					String ct = map.getCt();
					writer.write(String.format("(%s%s%s%s%s)\n", s, delim, Metadata.getFullName("type"), delim, ct));
				} else if (ont.isDefined(_p)) {
					String ct = map.getCt();
					int id = map.getId();
					switch (ont.getPropType(ct, _p)) {
					case Constants.OBJ_MP_TAB:
						String mp = ont.getMPTabName(ct, _p);
						String r = ont.getRange(ct, _p);
						sql = "select t2.uri from " + mp + " t1, " + r + " t2 where t1." + _p
								+ "=t2.id and t1.id=?";
						break;
					case Constants.OBJ_MP_ARR:
						r = ont.getRange(ct, _p);
						sql = "select t2.uri from " + ct + " t1, " + r
								+ " t2 where t1.id=? and t2.id=any(t1." + _p + ")";
						break;
					case Constants.OBJ_SIG:
						r = ont.getRange(ct, _p);
						sql = "select t2.uri from " + ct + " t1, " + r + " t2 where t1." + _p
								+ " = t2.id and t1.id=?";
						break;
					case Constants.LIT_MP_TAB:
						mp = ont.getMPTabName(ct, _p);
						sql = "select " + _p + "::Text from " + mp + " where id=?";
						break;
					case Constants.LIT_MP_ARR:
						sql = "select " + _p + "::Text from " + ct + " where id=?";
						break;
					case Constants.LIT_SIG:
						sql = "select " + _p + "::Text from " + ct + " where id=?";
						break;
					}

					if (isOverflow) {
						sql += " union select obj from xl_overflow where sub=? and pre=?";
					}
					PreparedStatement pstmt = con.prepareStatement(sql);
					pstmt.setInt(1, id);
					if (isOverflow) {
						pstmt.setString(2, s);
						pstmt.setString(3, p);
					}
					ResultSet rs = pstmt.executeQuery();
					while (rs.next()) {
						Set<String> oList = Utility.mp2Set(rs.getString(1));
						for (String o : oList) {
							writer.write(String.format("(%s%s%s%s%s)\n", s, delim,  Metadata.getFullName(_p), delim, o));
						}
					}
					rs.close();
					pstmt.close();
				}
			} else if (isOverflow) {
				sql = "select obj from xl_overflow where sub=? and pre=?";
				PreparedStatement pstmt = con.prepareStatement(sql);
				pstmt.setString(1, s);
				pstmt.setString(2, p);
				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					writer.write(String.format("(%s%s%s%s%s)\n", s, delim, p, delim, rs.getString(1)));
				}
				rs.close();
				pstmt.close();
			}
			writer.write(Constants.EOT);
		//	writer.flush();
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
