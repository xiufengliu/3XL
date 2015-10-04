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
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import xlsystem.common.Configure;
import xlsystem.common.Constants;

public class Metadata {

	Map<String, String> fullNameMap = new HashMap<String, String>();

	Metadata() {
		Configure config = Configure.getInstance();
		Connection connection = null;
		try {
			Class.forName(config.getDbDriver());
			connection = DriverManager.getConnection(config.getDbUrl(), config.getDbUsername(), config.getDbPassword());
			PreparedStatement stmt = connection.prepareStatement("SELECT param, value FROM xl_metadata");
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				fullNameMap.put(rs.getString("param"), rs.getString("value"));
			}
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
			}
		}
	}

	public String get(String name) {
		return fullNameMap.get(name);
	}

	public static String getFullName(String name) {
		Metadata metadata = getInstance();
		return metadata.get(name);
	}

	public static boolean isOverflow() {
		Metadata metadata = getInstance();
		return !"0".equals(metadata.get(Constants.OVERFLOW));
	}

	static Metadata instance;

	public static Metadata getInstance() {
		if (instance == null) {
			instance = new Metadata();
		}
		return instance;
	}
}
