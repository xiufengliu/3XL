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

package xlsystem.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;

import xlsystem.common.Configure;
import xlsystem.ontology.Property;

public class ClassTable  implements DbComponent {

	String name;
	String parent;
	List<Property> properties;
	List<String> dropTableSqlList;
	List<String> createTableSqlList;
	List<String> createMpIndexSqlList;
	List<String> dropMpIndexSqlList;
	String createIndexSql;
	

	public ClassTable(String name, List<Property> properties, String parent) {
		this.name = name;
		this.parent = parent;
		this.properties = properties;
		this.dropTableSqlList = new ArrayList<String>();
		this.createTableSqlList = new ArrayList<String>();
		this.createMpIndexSqlList = new ArrayList<String>();
		this.dropMpIndexSqlList = new ArrayList<String>();
	}


	@Override
	public void initialize(Connection con) {
	}

	@Override
	public void create(Connection con) {
		java.sql.Statement stmt = null;
		try {
			stmt = con.createStatement();
			for (String sql : createTableSqlList) {
				stmt.addBatch(sql);
			}
			stmt.addBatch(createIndexSql);
			for (String sql : createMpIndexSqlList) {
				stmt.addBatch(sql);
			}
			stmt.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtils.closeQuietly(stmt);
		}
	}

	@Override
	public void drop(Connection con) {
		java.sql.Statement stmt = null;
		try {
			stmt = con.createStatement();
			for (String sql : dropTableSqlList) {
				stmt.addBatch(sql);
			}
			stmt.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtils.closeQuietly(stmt);
		}
	}

	@Override
	public void generateSQL() {
		boolean useMP = Configure.getInstance().isUseMP(); 
		StringBuilder createSqlBuilder = new StringBuilder("CREATE TABLE ").append(name).append("(");
		for (Property prop : properties) {
			String field = prop.getPGName();
			String dbDataType = prop.getDataType();
			if (prop.isMultiProp()) {
				if (useMP) {
					dropTableSqlList.add(new StringBuilder("DROP TABLE IF EXISTS ").append(prop.getMPTableName()).append(" CASCADE").toString());
					createTableSqlList.add(new StringBuilder("CREATE TABLE ").append(prop.getMPTableName()).append("(id ").append(DataTypeDict.PG_BIGINT).append(", ").append(field).append(" ").append(dbDataType).append(")").toString());
					dropMpIndexSqlList.add(new StringBuilder("DROP INDEX IF EXISTS idx_").append(prop.getMPTableName()).append("_id").toString());
					createMpIndexSqlList.add(new StringBuilder("CREATE INDEX idx_").append(prop.getMPTableName()).append("_id ON ").append(prop.getMPTableName()).append("(id)").toString());
					if (prop.isObjProp()) {
						dropMpIndexSqlList.add(new StringBuilder("DROP INDEX IF EXISTS idx_").append(prop.getMPTableName()).append("_").append(field).toString());
						createMpIndexSqlList.add(new StringBuilder("CREATE INDEX idx_").append(prop.getMPTableName()).append("_").append(field).append(" ON ").append(prop.getMPTableName()).append("(").append(field).append(")").toString());
					}					
				} else {
					createSqlBuilder.append(field).append(" ").append(dbDataType).append(" []").append(",");
				}
			} else {
				createSqlBuilder.append(field).append(" ").append(dbDataType).append(",");
			}
		}
		
			int len = createSqlBuilder.length();
			if (createSqlBuilder.lastIndexOf(",")==len-1)
				createSqlBuilder.deleteCharAt(len-1);
		
		createSqlBuilder.append(")");
		if (parent != null) {
			createSqlBuilder.append(" INHERITS (").append(parent).append(")");
		}	
		createTableSqlList.add(createSqlBuilder.toString());
		dropTableSqlList.add(new StringBuilder("DROP TABLE IF EXISTS ").append(name).append(" CASCADE").toString());
		createIndexSql = new StringBuilder("CREATE UNIQUE INDEX idx_").append(name).append("_id ON ").append(name).append("(id)").toString();
	}

	public String toString() {
		StringBuilder buf = new StringBuilder();
		for (String sql : this.dropTableSqlList){
			buf.append(sql).append(";\n");
		}
		for (String sql : this.createTableSqlList){
			buf.append(sql).append(";\n");
		}
		buf.append(createIndexSql).append(";\n");
		for (String sql : this.createMpIndexSqlList){
			buf.append(sql).append(";\n");
		}
		for (String sql : this.dropMpIndexSqlList){
			buf.append(sql).append(";\n");
		}
		return buf.toString();
	}


	@Override
	public Collection<String> get(int type) {
		switch (type){
		case DbComponent.INDEX_REF_CREATE:
			return this.createMpIndexSqlList;
		case DbComponent.INDEX_REF_DROP:
			return this.dropMpIndexSqlList;
		default: return null;
		}
	}
}
