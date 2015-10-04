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
import java.util.Hashtable;
import java.util.List;

import xlsystem.common.Constants;
import xlsystem.common.RDF;
import xlsystem.common.XLException;
import xlsystem.ontology.Property;
import xlsystem.query.sql.comp.AnyKeyMap;
import xlsystem.query.sql.comp.BooleanAnyCondition;
import xlsystem.query.sql.comp.BooleanCondition;
import xlsystem.query.sql.comp.Condition;
import xlsystem.query.sql.comp.ConditionList;
import xlsystem.query.sql.comp.FieldValue;
import xlsystem.query.sql.comp.Joiner;
import xlsystem.query.sql.comp.KeyMap;
import xlsystem.query.sql.comp.NumberValue;
import xlsystem.query.sql.comp.SQLSelect;
import xlsystem.query.sql.comp.StringValue;
import xlsystem.query.sql.comp.TableName;

public class QuerySO extends QueryDAO implements Query {

	@Override
	public void query(RDF rdf, Writer writer) throws XLException {
		Connection con = null;
		try {
			boolean isOverflow = Metadata.isOverflow();
			con = conPool.getConnection();
			String s = rdf.getSub();
			String o = rdf.getObj();
			xlsystem.common.Map map = super.getMap(s, con);
			if (map != null) {
				String classTableName = map.getCt();
				writer.write(String.format("(%s%s%s%s%s)\n", s, delim, Metadata.getFullName("type"), delim, classTableName));

				Collection<Property> properties = ont.getPropsByOwlClassName(classTableName);
				List<Property> litProps = new ArrayList<Property>();
				String sql = buildSQL1(rdf, map, properties, litProps);
				
				//System.out.println(sql);
				
				if (litProps.size() > 0) {
					PreparedStatement pstmt = con.prepareStatement(sql);
					ResultSet rs = pstmt.executeQuery();
					while (rs.next()) {
						for (Property p : litProps) {
							Object obj = rs.getObject(p.getPGName());
							if (obj != null) {
								String v = String.valueOf(obj);
								if (p.isMultiProp()) {
									if (v.indexOf(o) != -1)
										writer.write(String.format("(%s%s%s%s%s)\n", s, delim, Metadata.getFullName(p.toString()), delim, o));
								} else {
									if (o.equals(v)) {
										writer.write(String.format("(%s%s%s%s%s)\n", s, delim, Metadata.getFullName(p.toString()), delim, o));
									}
								}
							}
						}
					}
					rs.close();
					pstmt.close();
				}

				java.util.Map<Property, String> propSqlMap = buildSQL2(rdf, map, properties);
				if (!propSqlMap.isEmpty()) {
					for (Property property : propSqlMap.keySet()) {
						sql = propSqlMap.get(property);
						//System.out.println(sql);
						PreparedStatement pstmt = con.prepareStatement(sql);
						ResultSet rs = pstmt.executeQuery();
						if (rs.next()) {
							String v = rs.getString(property.getPGName());
							if (o.equals(v)) {
								writer.write(String.format("(%s%s%s%s%s)\n", s, delim, Metadata.getFullName(property.getPGName()), delim, o));
							}
						}
						rs.close();
						pstmt.close();
					}
				}
			}

			if (isOverflow) {
				PreparedStatement pstmt = con.prepareStatement("select pre from xl_overflow where sub=? and obj=?");
				pstmt.setString(1, s);
				pstmt.setString(2, o);
				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					writer.write(String.format("(%s%s%s%s%s)\n", s, delim, rs.getString(1), delim, o));
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

	public List<RDF> query(RDF rdf) throws XLException {
		return null;
	}

	@Override
	public List<RDF> query(List<RDF> rdfs) throws XLException {
		return null;
	}

	@Override
	public void query(List<RDF> rdfs, Writer writer) throws XLException {
		this.query(rdfs.get(0), writer);
	}

	protected String buildSQL1(RDF rdf, xlsystem.common.Map map, Collection<Property> properties, List<Property> litProps) throws XLException {
		SQLSelect sqlSelect = new SQLSelect();
		int id = map.getId();
		String classTableName = map.getCt();
		TableName tableName = new TableName(classTableName, null);
		sqlSelect.addTableName(tableName);
		
		FieldValue leftValue = new FieldValue(tableName, "id");
		NumberValue<Integer> rightValue = new NumberValue<Integer>(id);
		sqlSelect.addWhereCondition(new BooleanCondition(leftValue, "=", rightValue));
		
		List<Condition> conditions = new ArrayList<Condition>();
		for (Property property : properties) {
			int type = ont.getPropType(property);
			if (type==Constants.LIT_MP_ARR){
				litProps.add(property);
				conditions.add(new BooleanAnyCondition(new FieldValue(tableName, property.getPGName()), "=",  new StringValue(rdf.getObj())));
			} else if (type==Constants.LIT_SIG){
				litProps.add(property);
				conditions.add(new BooleanCondition(new FieldValue(tableName, property.getPGName()), "=",  new StringValue(rdf.getObj())));
				FieldValue selectedField = new FieldValue(tableName, property.getPGName(), null);
				sqlSelect.addFieldValue(selectedField);
			}
		}
		sqlSelect.setConditionList(new ConditionList(Joiner.OR, conditions));
		return sqlSelect.toString();
	}

	
	
	
	protected java.util.Map<Property, String> buildSQL2(RDF rdf, xlsystem.common.Map map, Collection<Property> properties) throws XLException {
		
		java.util.Map<Property, String> propSqlMap = new Hashtable<Property, String>();
		
		for (Property property : properties) {
			SQLSelect sqlSelect = new SQLSelect();
			int id = map.getId();
			String classTableName = map.getCt();
			TableName tableName = new TableName(classTableName, null);
			sqlSelect.addTableName(tableName);
			
			FieldValue leftValue = new FieldValue(tableName, "id");
			NumberValue<Integer> rightValue = new NumberValue<Integer>(id);
			sqlSelect.addWhereCondition(new BooleanCondition(leftValue, "=", rightValue));
			if (property.isObjProp()){//OBJ
				if (property.isMultiProp()){//OBJ_MP
					if (super.isUsingMultiPropTable){//OBJ_MP_TAB
						String mpTableName = property.getDomain().getName() + "_" + property.getPGName();
						TableName mpTable = new TableName(mpTableName, null);
						sqlSelect.addTableName(mpTable);
						sqlSelect.addKeyMap(new KeyMap(tableName, "id", mpTable, "id"));

						String rangeTableName = property.getRange().getName();
						TableName rangeTable = new TableName(rangeTableName, null);
						sqlSelect.addTableName(rangeTable);
						sqlSelect.addKeyMap(new KeyMap(mpTable, property.getPGName(), rangeTable, "id"));

						FieldValue lv = new FieldValue(rangeTable, "uri");
						StringValue rv = new StringValue(rdf.getObj());
						sqlSelect.addWhereCondition(new BooleanCondition(lv, "=", rv));

						FieldValue selectedField = new FieldValue(rangeTable, "uri", property.getPGName());
						sqlSelect.addFieldValue(selectedField);
						propSqlMap.put(property, sqlSelect.toString());	
					} else {//OBJ_MP_ARR
						String rangeTableName = property.getRange().getName();
						TableName rangeTable = new TableName(rangeTableName, null);
						sqlSelect.addTableName(rangeTable);
						sqlSelect.addKeyMap(new AnyKeyMap(tableName, property.getPGName(), rangeTable, "id"));

						FieldValue lv = new FieldValue(rangeTable, "uri");
						StringValue rv = new StringValue(rdf.getObj());
						sqlSelect.addWhereCondition(new BooleanCondition(lv, "=", rv));

						FieldValue selectedField = new FieldValue(rangeTable, "uri", property.getPGName());
						sqlSelect.addFieldValue(selectedField);
						propSqlMap.put(property, sqlSelect.toString());
					}
				} else {//OBJ_SIG
					String rangeTableName = property.getRange().getName();
					TableName rangeTable = new TableName(rangeTableName, null);
					sqlSelect.addTableName(rangeTable);
					sqlSelect.addKeyMap(new KeyMap(tableName, property.getPGName(), rangeTable, "id"));

					FieldValue lv = new FieldValue(rangeTable, "uri");
					StringValue rv = new StringValue(rdf.getObj());
					sqlSelect.addWhereCondition(new BooleanCondition(lv, "=", rv));

					FieldValue selectedField = new FieldValue(rangeTable, "uri", property.getPGName());
					sqlSelect.addFieldValue(selectedField);
					propSqlMap.put(property, sqlSelect.toString());	
				}
			} else {//LIT
				if (property.isMultiProp()){//LIT_MP
					if (super.isUsingMultiPropTable){//LIT_MP_TAB
						String mpTableName = property.getDomain().getName() + "_" + property.getPGName();
						TableName mpTable = new TableName(mpTableName, null);
						sqlSelect.addTableName(mpTable);
						sqlSelect.addKeyMap(new KeyMap(tableName, "id", mpTable, "id"));

						FieldValue lv = new FieldValue(mpTable, property.getPGName());
						StringValue rv = new StringValue(rdf.getObj());
						sqlSelect.addWhereCondition(new BooleanCondition(lv, "=", rv));

						FieldValue selectedField = new FieldValue(mpTable, property.getPGName(), null);
						sqlSelect.addFieldValue(selectedField);
						propSqlMap.put(property, sqlSelect.toString());	
					}
				}
				
			}
		}
		 return propSqlMap;
	}

	
	
	public static void main(String[] args) {
		RDF rdf = new RDF("http://www.Department0.University0.edu/FullProfessor6", "*", "FullProfessor6@Department0.University0.edu");
		Query q = new QuerySO();
		try {
			q.query(rdf, null);
		} catch (XLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
