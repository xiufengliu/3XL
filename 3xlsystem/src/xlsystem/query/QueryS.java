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
import xlsystem.ontology.Property;

public class QueryS extends QueryDAO implements Query {

	@Override
	public void query(RDF rdf, Writer writer) throws XLException {
		Connection con = null;
		try {
			boolean isOverflow = Metadata.isOverflow();
			con = conPool.getConnection();
			String s = rdf.getSub();
			xlsystem.common.Map map = super.getMap(s, con);
			if (map != null) {
				int id = map.getId();
				String ct = map.getCt();
				writer.write(String.format("(%s%s%s%s%s)\n", s, delim, Metadata.getFullName("type"), delim, ct));

				PreparedStatement pstmt = con.prepareStatement("select * from only " + ct	+ " where id = ?");
				pstmt.setFetchSize(7000);
				pstmt.setInt(1, id);
				ResultSet rs = pstmt.executeQuery();
				if (rs.next()) {
					Collection<Property> propSet = ont.getPropsByOwlClassName(ct);
					for (Property property : propSet) {
						String p = property.getPGName();
						switch (ont.getPropType(ct, p)) {
						case Constants.OBJ_MP_TAB:
							String mp = ont.getMPTabName(ct, p);
							String r = ont.getRange(ct, p);
							PreparedStatement pst = con.prepareStatement("select t2.uri from " + mp
									+ " t1," + r + " t2 where t1." + p + "=t2.id and t1.id=?");
							pstmt.setFetchSize(7000);
							pst.setInt(1, id);
							ResultSet rst = pst.executeQuery();
							while (rst.next()) {
								String o = rst.getString(1);
								writer.write(String.format("(%s%s%s%s%s)\n", o, delim, Metadata.getFullName("type"), delim, r));
								writer.write(String.format("(%s%s%s%s%s)\n", s, delim, Metadata.getFullName(p), delim, o));
							}
							rst.close();
							pst.close();
							break;
						case Constants.OBJ_MP_ARR:
							Set<String> objList = Utility.mp2Set(rs.getString(p));
							int size = objList.size();
							if (size > 0) {
								r = ont.getRange(ct, p);
								String sql = "select uri from only " + r + " where id=?";
								for (int j = 1; j < size; j++) {
									sql += " or id=?";
								}
								pst = con.prepareStatement(sql);
								pst.setFetchSize(7000);
								int j = 0;
								for (String obj: objList) {
									pst.setInt(++j, Integer.parseInt(obj));
								}
								rst = pst.executeQuery();
								while (rst.next()) {
									String o = rst.getString(1);
									writer.write(String.format("(%s%s%s%s%s)\n", o, delim, Metadata.getFullName("type"), delim, r));
									writer.write(String.format("(%s%s%s%s%s)\n", s, delim, Metadata.getFullName(p), delim, o));
								}
								rst.close();
								pst.close();
							}
							break;
						case Constants.OBJ_SIG:
							r = ont.getRange(ct, p);
							pst = con.prepareStatement("select uri from only " + r + " where id=?");
							pst.setFetchSize(7000);
							pst.setInt(1, rs.getInt(p));
							rst = pst.executeQuery();
							if (rst.next()) {
								String o = rst.getString(1);
								writer.write(String.format("(%s%s%s%s%s)\n", o, delim, Metadata.getFullName("type"), delim, r));
								writer.write(String.format("(%s%s%s%s%s)\n", s, delim, Metadata.getFullName(p), delim, o));
							}
							rst.close();
							pst.close();
							break;
						case Constants.LIT_MP_TAB:
							mp = ont.getMPTabName(ct, p);
							pst = con.prepareStatement("select " + p + " from " + mp
									+ " where id=?");
							pst.setFetchSize(7000);
							pst.setInt(1, id);
							rst = pst.executeQuery();
							while (rst.next()) {
								String o = rst.getString(1);
								writer.write(String.format("(%s%s%s%s%s)\n", s, delim, Metadata.getFullName(p), delim, o));
							}
							rst.close();
							pst.close();
							break;
						case Constants.LIT_MP_ARR:
							Set<String> objSet = Utility.mp2Set(rs.getString(p));
							for (String o: objSet) {
								writer.write(String.format("(%s%s%s%s%s)\n", s, delim, Metadata.getFullName(p), delim, o));
							}
							break;
						case Constants.LIT_SIG:
							writer.write(String.format("(%s%s%s%s%s)\n", s, delim, Metadata.getFullName(p), delim, rs.getString(p)));
							break;
						}
					}
				}
			}

			if (isOverflow) {
				PreparedStatement pst = con.prepareStatement("select pre, obj from xl_Overflow where sub=?");
				pst.setFetchSize(7000);
				pst.setString(1, s);
				ResultSet rst = pst.executeQuery();
				while (rst.next()) {
					String p = rst.getString(1);
					String o = rst.getString(2);
					writer.write(String.format("(%s%s%s%s%s)\n", s, delim, p, delim, o));
				}
				rst.close();
				pst.close();
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

	@Override
	public List<RDF> query(RDF rdf) throws XLException {
		// TODO Auto-generated method stub
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
