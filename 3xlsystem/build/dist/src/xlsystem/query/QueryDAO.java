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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;


import xlsystem.common.ConnectionPool;
import xlsystem.common.Constants;
import xlsystem.common.Map;
import xlsystem.common.Configure;
import xlsystem.common.XLException;
import xlsystem.ontology.OwlOntManager;
import xlsystem.ontology.OwlOntology;

public class QueryDAO {
	boolean isUsingMultiPropTable = Configure.getInstance().isUseMP();
	String delim = Configure.getInstance().getRdfDelim();

	ConnectionPool conPool = ConnectionPool.getInstance(5, 10, false);
	OwlOntology ont = OwlOntManager.getInstance();

	//static java.util.Map<String, String> fullNameMap;
	//static boolean isOverflow = false;

	public QueryDAO() {
	//	getMetadata();
	}

	public Map getMap(String uri, Connection con) throws XLException {
		Map map = null;
		try {
			PreparedStatement pstmt = con.prepareStatement("SELECT id, ct FROM Map WHERE  DECODE(md5(uri), 'hex') =DECODE(md5(?), 'hex')");
			pstmt.setString(1, uri);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				map = new Map();
				map.setUri(uri);
				map.setId(rs.getInt("id"));
				map.setCt(rs.getString("ct"));
			}
			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new XLException(e);
		}
		return map;
	}


	
	public void close() {
		conPool.closeAllConnections();
	}

}
