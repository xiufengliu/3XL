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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;

public class OtherTable implements DbComponent {
	String name = null;
	//Map<String, String> propTypeMap;
	
	List<Column> columnList;
	List<String> createTableSqlList; // Used to save the table creation DDL sql
	List<String> createIndexSqlList; // Used to save the Index DDL sql
	List<String> dropTableSqlList; // Used to save the table creation DDL sql
	List<String> initializationSqlList;
	List<String> dropIndexSqlList;

	public OtherTable(String name) {
		this.name = name;
		this.columnList = new ArrayList<Column>();
		this.createTableSqlList = new ArrayList<String>();
		this.createIndexSqlList = new ArrayList<String>();
		this.dropIndexSqlList = new ArrayList<String>();
		this.dropTableSqlList = new ArrayList<String>();
		this.initializationSqlList = new ArrayList<String>();
	}

	public String getName() {
		return name;
	}

	public void addColumn(Column col){
		this.columnList.add(col);
	}

	public void addCreateIndexSql(String sql) {
		this.createIndexSqlList.add(sql);
	}

	public void addDropIndexSql(String sql) {
		this.dropIndexSqlList.add(sql);
	}
	
	public void addInitializationSql(String sql) {
		this.initializationSqlList.add(sql);
	}

	@Override
	public void initialize(Connection con) {
		Statement stmt = null;
		try {
			if (initializationSqlList.size() > 0) {
				stmt = con.createStatement();
				for (String sql : initializationSqlList) {
					stmt.addBatch(sql);
				}
				stmt.executeBatch();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtils.closeQuietly(stmt);
		}
	}

	@Override
	public void create(Connection con) {
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			for (String sql : createTableSqlList) {
				stmt.addBatch(sql);
			}
			for (String sql : createIndexSqlList) {
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
		Statement stmt = null;
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
		StringBuilder createSqlBuilder = new StringBuilder("CREATE TABLE ");
		createSqlBuilder.append(name).append("(");	
		int len = createSqlBuilder.length();
		for (Column col : this.columnList) {
			if (createSqlBuilder.length()>len) createSqlBuilder.append(",");
			createSqlBuilder.append(col.toString());
		}
		createSqlBuilder.append(")");
		this.createTableSqlList.add(createSqlBuilder.toString());
		this.dropTableSqlList.add(new StringBuilder("DROP TABLE IF EXISTS ").append(name).append(" CASCADE").toString());
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		for (String sql : this.dropTableSqlList)
			buf.append(sql).append("\n");
		for(String sql: this.createTableSqlList)
			buf.append(sql).append("\n");
		for(String sql: this.createIndexSqlList)
			buf.append(sql).append("\n");
		for(String sql : this.initializationSqlList)
			buf.append(sql).append("\n");
		return buf.toString();
	}

	@Override
	public Collection<String> get(int type) {
		switch (type){
		case DbComponent.INDEX_REF_CREATE:
			return this.createIndexSqlList;
		case DbComponent.INDEX_REF_DROP:
			return this.dropIndexSqlList;
		default: return null;
		}
	}
}
