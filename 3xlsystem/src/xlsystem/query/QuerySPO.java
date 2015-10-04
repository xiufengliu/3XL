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

import xlsystem.common.Constants;
import xlsystem.common.Map;
import xlsystem.common.RDF;
import xlsystem.common.Utility;
import xlsystem.common.XLException;

public class QuerySPO extends QueryDAO implements Query {

	@Override
	public List<RDF> query(RDF rdf) throws XLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void query(RDF rdf, Writer writer) throws XLException {
		Connection con = null;
		try {
			boolean isOverflow = Metadata.isOverflow(); 
			con = conPool.getConnection();
			String s = rdf.getSub();
			String p = rdf.getPre();
			String _p = Utility.removeNS(rdf.getPre());
			String o = rdf.getObj();
			String sql = null;
			Map map = super.getMap(s, con);
			int paraNum = 0;
			int id = 0;
			if (map != null) {
				 id = map.getId();
				String ct = map.getCt();
				if ("type".equals(_p)) {
					if (ct.equals(Utility.removeNS(o))) {
						writer.write(String.format("(%s%s%s%s%s)\n", s, delim, Metadata.getFullName("type"), delim, o));
					}
				} else if (ont.isDefined(_p)) {
					switch (ont.getPropType(ct, _p)) {
					case Constants.OBJ_MP_TAB:
						String mp = ont.getMPTabName(ct, _p);
						sql = "SELECT 1 FROM " + mp + " t1, Map t2 WHERE t1.id=? AND t1." + _p
								+ "=t2.id AND DECODE(md5(t2.uri),'HEX')=DECODE(md5(?),'HEX')";
						break;
					case Constants.OBJ_MP_ARR:
						sql = "SELECT 1 FROM " + ct + " t1, Map t2 WHERE t1.id=? AND t2.id=ANY(t1."
								+ _p + ") AND DECODE(md5(t2.uri),'HEX')=DECODE(md5(?),'HEX')";
						break;
					case Constants.OBJ_SIG:
						sql = "select 1 from " + ct + " t1, map t2 where t1.id=? and t1." + _p
								+ "=t2.id and decode(md5(t2.uri),'HEX')=decode(md5(?),'HEX')";
						break;
					case Constants.LIT_MP_TAB:
						mp = ont.getMPTabName(ct, _p);
						sql = "select 1 from " + mp + " where id=? and " + _p + "::Text=?";
						break;
					case Constants.LIT_MP_ARR:
						sql = "select 1 from " + ct + " where id=? and ?=any(" + _p + ")";
						break;
					case Constants.LIT_SIG:
						sql = "select 1 from " + ct + " where id=? and " + _p + "::Text=?";
						break;
					}
					paraNum = 2;
					if (isOverflow) {
						sql += " union select 1 from xl_overflow where sub=? and pre=? and obj=?";
						paraNum = 5;
					}
				}
			} else {
				if (isOverflow) {
					sql += "select 1 from xl_overflow where sub=? and pre=? and obj=?";
					paraNum = 3;
				}
			}
			PreparedStatement pstmt = null;
			if (sql != null) {
				pstmt = con.prepareStatement(sql);
				switch (paraNum) {
				case 2:
					pstmt.setInt(1, id);
					pstmt.setString(2, o);
					break;
				case 3:
					pstmt.setString(1, s);
					pstmt.setString(2, p);
					pstmt.setString(3, o);
					break;
				case 5:
					pstmt.setInt(1, id);
					pstmt.setString(2, o);
					pstmt.setString(3, s);
					pstmt.setString(4, p);
					pstmt.setString(5, o);
					break;
				}

				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					if (rs.getInt(1) == 1) {
						writer.write(String.format("(%s%s%s%s%s)\n", s, delim, p, delim, o));
						break;
					}
				}
				rs.close();
				pstmt.close();
			}
			writer.write(Constants.EOT);
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
			throw new XLException(e);
		} finally {
			conPool.free(con);
		}
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
