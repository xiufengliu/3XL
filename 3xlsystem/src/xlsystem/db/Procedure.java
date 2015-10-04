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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class Procedure implements DbComponent {
	 String name;
	 List<String> stmtList;
	 String createSql;
	
    public Procedure (String name) {
    	this.name = name;
    }

	public String getName() {
		return this.name;
	}
    
	@Override
	public void initialize(Connection con) {
	}
	
    @Override
    public void create (Connection con) {
    	PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(createSql);
			pstmt.execute();
		} catch (SQLException e) {
			try {
				if (pstmt != null)
					pstmt.close();
			} catch (SQLException e1) {
			}
			e.printStackTrace();
		}
    }

    @Override
    public void drop (Connection con) {
    }
    
    @Override
    public void generateSQL(){
    	StringBuilder sqlBuilder = new StringBuilder("CREATE OR REPLACE FUNCTION ")
    	.append(name)
    	.append("()\n").append("  ").append("RETURNS integer AS\n")
    	.append("$$\nBEGIN\n");
    	for (String stmt: stmtList){
    		sqlBuilder.append("  ").append(stmt).append(";\n");
    	}
    	sqlBuilder.append("  ").append("RETURN 1;\nEND;\n$$\nLANGUAGE plpgsql");
    	createSql = sqlBuilder.toString();
    }
    
	public void setStmtList(List<String> stmtList) {
		this.stmtList = stmtList;
	}


	public String toString(){
		StringBuffer buf = new StringBuffer();
		buf.append(createSql).append(";\n");
		return buf.toString();
	}

	@Override
	public Collection<String> get(int type) {
		// TODO Auto-generated method stub
		return null;
	}
}

