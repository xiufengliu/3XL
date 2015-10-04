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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import xlsystem.common.Constants;
import xlsystem.common.RDF;
import xlsystem.common.Utility;
import xlsystem.common.XLException;
import xlsystem.db.DataTypeDict;
import xlsystem.ontology.Property;

public class QueryO extends QueryDAO implements Query {

	@Override
	public void query(RDF rdf, Writer writer) throws XLException {
		Connection con = null;
		String sql = null;
		try {
			boolean isOverflow = Metadata.isOverflow();
			con = conPool.getConnection();
			
			String o = rdf.getObj();
			Collection<String> ctSet = ont.getClassTableNames();
			
			
			for (String ct : ctSet) {
				// 1. LIT_SIG and LIT_MP_ARR
				List<Integer> types = new ArrayList<Integer>();
				types.add(Constants.LIT_SIG);
				types.add(Constants.LIT_MP_ARR);
				Collection<Property> litPropSet = ont.getPropertiesByType(ct, types);
				
				if (litPropSet.size()<1) continue;
				
				String selectFields = "";
				String conditions = "";
				for (Property p : litPropSet) {
					String field = p.getPGName();
					if (p.isMultiProp()) {
						conditions += "? = any(" + field  + ") or ";
					} else {
						conditions += field + "=? or ";
					}
					selectFields += field + ",";
				}
				selectFields = Utility.rtrim(selectFields, ",");
				conditions = Utility.rtrim(conditions, "or ");
				sql = "select uri, " + selectFields + " from only " + ct + " where " + conditions;
				

				PreparedStatement pstmt = con.prepareStatement(sql);
				pstmt.setFetchSize(7000);
				
				for (int i = 1; i <= litPropSet.size(); ++i) {
					pstmt.setString(i, o);
				}
				int idx =0;
				for (Property p : litPropSet){
					if (DataTypeDict.isNumbericType(p.getDataType())) {
						if (!Utility.isNumeric(o)){
							pstmt.setFloat(++idx, 99999.99999f);
						} else {
							pstmt.setFloat(++idx, Float.parseFloat(o));
						}
					} else {
						pstmt.setString(++idx, o);
					}
				}
				
				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					String s = rs.getString(1);
					for (Property p : litPropSet) {
						Object obj = rs.getObject(p.getPGName());
						if (obj != null) {
							String v = String.valueOf(obj);
							if (p.isMultiProp()) {
								if (v.indexOf(o) != -1)
									writer.write(String.format("(%s%s%s%s%s)\n", s, delim, Metadata.getFullName(p.getPGName()), delim, o));
							} else {
								if (o.equals(v)) {
									writer.write(String.format("(%s%s%s%s%s)\n", s, delim, Metadata.getFullName(p.getPGName()), delim, o));
								}
							}
						}
					}
				}
				rs.close();
				pstmt.close();
				
				// 2. LIT_MP_TAB
				
				types.clear();
				types.add(Constants.LIT_MP_TAB);
				Collection<Property> litMPTabMap = ont.getPropertiesByType(ct, types);
				if (litMPTabMap.size() > 0) {
					for (Property property:litMPTabMap ) {
						String p = property.getPGName();
						String mpt = property.getDomain().getName()+"_"+p;
						sql = "select t1.uri from " + ct + " t1, " + mpt
								+ " t2 where t1.id=t2.id and t2." + p + "=?";

						pstmt = con.prepareStatement(sql);
						pstmt.setFetchSize(7000);
						if (DataTypeDict.isNumbericType(property.getDataType())) {
							if (!Utility.isNumeric(o)){
								pstmt.close();
								continue;
							} else {
								pstmt.setFloat(1, Float.parseFloat(o));
							}
						} else {
							pstmt.setString(1, o);
						}
						rs = pstmt.executeQuery();
						while (rs.next()) {
							String s = rs.getString(1);
							writer.write(String.format("(%s%s%s%s%s)\n", s, delim, Metadata.getFullName(p), delim, o));
						}
						rs.close();
						pstmt.close();
					}
				}
			}

			xlsystem.common.Map map = super.getMap(o, con);
			if (map != null) {
				int id = map.getId();
				for (String ct : ctSet) {
					// 3. OBJ_SIG and OBJ_MP_ARR
					List<Integer> types = new ArrayList<Integer>();
					types.add(Constants.OBJ_SIG);
					types.add(Constants.OBJ_MP_ARR);
					Collection<Property> objTabSet = ont.getPropertiesByType(ct, types);
					if (objTabSet.size()<1) continue;
					String selectFields = "";
					String conditions = "";
					for (Property p : objTabSet) {
						if (p.isMultiProp()) {
							conditions += "? = any(" + p + ") or ";
						} else {
							conditions += p + "=? or ";
						}
						selectFields += p + ",";
					}
					selectFields = Utility.rtrim(selectFields, ",");
					conditions = Utility.rtrim(conditions, "or ");
					sql = "select uri, " + selectFields + " from only " + ct + " where "
							+ conditions;

					PreparedStatement pstmt = con.prepareStatement(sql);
					pstmt.setFetchSize(7000);
					for (int i = 1; i <= objTabSet.size(); ++i) {
						pstmt.setInt(i, id);
					}
					
					ResultSet rs = pstmt.executeQuery();
					while (rs.next()) {
						String s = rs.getString(1);
						for (Property p : objTabSet) {
							if (p.isMultiProp()) {
								Object obj = rs.getObject(p.getPGName());
								if (obj != null
										&& (String.valueOf(obj)).indexOf(String.valueOf(id)) != -1) {
									writer.write(String.format("(%s%s%s%s%s)\n", s, delim, Metadata.getFullName(p.toString()), delim, o));
								}
							} else {
								int v = rs.getInt(p.getPGName());
								if (id == v) {
									writer.write(String.format("(%s%s%s%s%s)\n", s, delim, Metadata.getFullName(p.toString()), delim, o));
								}
							}
						}
					}
					rs.close();
					pstmt.close();

					// 4. OBJ_MP_TAB
					types.clear();
					types.add(Constants.OBJ_MP_TAB);
					Collection<Property> objMPTabMap = ont.getPropertiesByType(ct, types);
					if (objMPTabMap.size() > 0) {
						for (Property property : objMPTabMap) {
							String p = property.getPGName();
							String mpt = property.getDomain().getName()+"_"+p;
							sql = "select t1.uri from " + ct + " t1, " + mpt
									+ " t2 where t1.id=t2.id and t2." + p + "=?";

							pstmt = con.prepareStatement(sql);
							pstmt.setFetchSize(7000);
							pstmt.setInt(1, id);
							rs = pstmt.executeQuery();
							while (rs.next()) {
								String s = rs.getString(1);
								writer.write(String.format("(%s%s%s%s%s)\n", s, delim, Metadata.getFullName(p), delim, o));
							}
							rs.close();
							pstmt.close();
						}
					}
				}
			}

			if (isOverflow) {
				PreparedStatement pstmt = con.prepareStatement("select sub, pre from xl_Overflow where obj=?");
				pstmt.setFetchSize(7000);
				pstmt.setString(1, o);
				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					writer.write(String.format("(%s%s%s%s%s)\n", rs.getString(1), delim, rs.getString(2), delim, o));
				}
				rs.close();
				pstmt.close();
			}

			writer.write(Constants.EOT);
			//writer.flush();
		} catch (Exception e) {
			System.out.printf("SQL=%s\n", sql);
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
