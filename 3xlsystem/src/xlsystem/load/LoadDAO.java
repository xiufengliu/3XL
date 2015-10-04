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

package xlsystem.load;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;

import xlsystem.common.Configure;
import xlsystem.common.ConnectionPool;
import xlsystem.common.Constants;
import xlsystem.common.Utility;
import xlsystem.common.XLException;
import xlsystem.ontology.OwlOntology;

public class LoadDAO implements DAO {

	//ConnectionPool connPool;
	Connection conn = null;
	//boolean isUsingMultiPropTable = Configure.getBool("use.mp");
	
	OwlOntology ont;
	TableCleaner cleaner;
	Set<String> delTableNames;
	Set<Integer> delIDs;
	
	public LoadDAO(Connection conn, OwlOntology ont) {
		this.ont = ont;
		this.conn = conn;
		//this.cleaner = new TableCleaner(30000);//30 seconds
		//cleaner.start();
		delTableNames = new HashSet<String>();
		delIDs = new HashSet<Integer>();
	}
	
	public void delete(int id, String tableName) throws XLException {
		StringBuilder sqlBuilder = new StringBuilder("DELETE FROM  ONLY ").append(tableName).append(" WHERE id = ?");
		try {
			boolean oldCommitMode = conn.getAutoCommit();
			conn.setAutoCommit(false);
			PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString());
			try {
				pstmt.setInt(1, id);
				pstmt.executeUpdate();
			} catch (SQLException e) {
				DbUtils.rollback(conn);
				System.out.println(sqlBuilder.toString());
				throw new XLException(e.getMessage());
			} finally {
				DbUtils.closeQuietly(pstmt);
				try {
					conn.commit();
					conn.setAutoCommit(oldCommitMode);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new XLException(e.getMessage());
		} 
	}

	/*@Override
	public boolean isClean(){
		return cleaner.isClean();
	}*/
	
	
	@Override
	public boolean isClean() throws XLException {
		//long t1 = System.nanoTime();
		try {
			PreparedStatement pstmt = conn.prepareStatement("TRUNCATE xl_delete");
			pstmt.executeUpdate();
			pstmt.close();

			pstmt = conn.prepareStatement("INSERT INTO xl_delete(id) values(?)");
			for (Iterator<Integer> itr = this.delIDs.iterator(); itr.hasNext();) {
				pstmt.setInt(1, itr.next());
				pstmt.addBatch();
			}
			pstmt.executeBatch();
			pstmt.close();

			java.sql.Statement stmt = conn.createStatement();
			for (Iterator<String> itr = this.delTableNames.iterator(); itr.hasNext();) {
				StringBuilder sqlBuilder = new StringBuilder("DELETE FROM  ONLY ").append(itr.next()).append(" ct USING xl_delete del WHERE ct.id = del.id");
				stmt.addBatch(sqlBuilder.toString());
			}
			stmt.executeBatch();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new XLException(e.getMessage());
		} finally {
			this.delTableNames.clear();
			this.delIDs.clear();
			//connPool.free(conn);
		}
		//long t2 = System.nanoTime();
		//float time = (t2-t1)/1000000000.0f;
		//System.out.printf("Del=%.2f; ", time);
		return true;
	}

	
	@Override
	public void load(String path, String tableName, String[] columns) throws XLException {
		try {
			//conn = connPool.getConnection();
			String fields = Utility.array2StrByDelim(columns, ",");
			StringBuilder sqlBuilder = new StringBuilder("COPY ").append(tableName).append("(").append(fields).append(") FROM '").append(path).append(tableName).append(".csv")
					.append("' DELIMITER '|' CSV");
			java.sql.PreparedStatement	pstmt = conn.prepareStatement(sqlBuilder.toString());
			pstmt.execute();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new XLException(e.getMessage());
		} 
	}

	@Override
	public void create() throws XLException {
		try {
			PreparedStatement pstmt = conn.prepareStatement("select build_map()");
			pstmt.execute();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new XLException(e.getMessage());
		} 
	}

	@Override
	public void update(Map<String, String> metadataMap) throws  XLException {
		try {
			PreparedStatement stmt = conn.prepareStatement("UPDATE xl_metadata SET value= (SELECT  count(1)::Text FROM xl_overflow LIMIT 1) WHERE param=?");
			stmt.setString(1, Constants.OVERFLOW);
			stmt.executeUpdate();
			stmt.close();
			if (metadataMap.size() > 0) {
				for (Iterator<Map.Entry<String, String>> itr = metadataMap.entrySet().iterator(); itr.hasNext();) {
					Map.Entry<String, String> entry = itr.next();
					stmt = conn.prepareStatement("UPDATE xl_metadata SET value= ? WHERE param=?");
					stmt.setString(1, entry.getValue());
					stmt.setString(2, entry.getKey());
					stmt.executeUpdate();
					stmt.close();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new XLException(e.getMessage());
		} 
	}

	@Override
	public Object query(int id, String ct) throws SQLException {
		QueryRunner runner = new QueryRunner();
		StringBuilder sqlBuilder = new StringBuilder("SELECT *  FROM ONLY ").append(ct).append(" WHERE ID = ?");

		ValueHolderResultSetHandler rsh = new ValueHolderResultSetHandler(ont, ct);
		ValueHolder vh = runner.query(conn, sqlBuilder.toString(), rsh, id);
		if (vh != null) {
			//cleaner.add(new Delete(id, ct, this));
			this.delTableNames.add(ct);
			this.delIDs.add(id);
			
			/*if (isUsingMultiPropTable) {
				List<Property> props = ont.getPropsByOwlClassName(ct);
				for (Property prop : props) {
					if (prop.isMultiProp()) {
						String propertyName = prop.getPGName();
						StringBuffer sqlBuf = new StringBuffer("SELECT ");
						sqlBuf.append(propertyName).append(" FROM ").append(prop.getMPTableName()).append(" WHERE id=?");
						ColumnListHandler clh = new ColumnListHandler(propertyName);
						List values = runner.query(conn, sqlBuf.toString(), clh, id);
						if (values != null && values.size() > 0) {
							vh.putAll(propertyName, values);
							cleaner.add(new Delete(id, prop.getMPTableName(), this));
						}
					}
				}
			}*/
		} 
		return vh;
	}

	@Override
	public void batchSqlExcution(Collection<String> sqlStatements) throws XLException {
		if (sqlStatements != null && sqlStatements.size() > 0) {
			try {
				java.sql.Statement stmt = conn.createStatement();
				for (String sql : sqlStatements) {
					stmt.addBatch(sql);
				}
				stmt.executeBatch();
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new XLException(e.getMessage());
			} 
		}
	}
	
	@Override
	public void close() {
		this.delTableNames.clear();
		this.delIDs.clear();
	}
	
	// --------------------------------------- To be deleted ----------------------------------------------
	private Connection makeNewConnection() throws SQLException {
		Configure config = Configure.getInstance();
		DbUtils.loadDriver(config.getDbDriver());
		Connection connection = DriverManager.getConnection(config.getDbUrl(), config.getDbUsername(), config.getDbPassword());
		 connection.setAutoCommit(false);
		 return connection;
	}

}
