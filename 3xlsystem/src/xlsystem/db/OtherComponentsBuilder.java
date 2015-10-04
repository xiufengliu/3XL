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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import xlsystem.common.Constants;
import xlsystem.common.Utility;
import xlsystem.ontology.OwlOntology;

public class OtherComponentsBuilder implements Builder {

	List<DbComponent> miscComponentsMap;
	OwlOntology ont;

	public OtherComponentsBuilder(OwlOntology ont) {
		this.miscComponentsMap = new ArrayList<DbComponent>();
		this.ont = ont;
	}

	public Collection<DbComponent> buildComponents() {
		addMetadataTable();
		addOverFlowTable();
		addMapTable();
		addBuildMap();
		addDelTable();
		return miscComponentsMap;
	}

	private void addMetadataTable() {
		OtherTable table = new OtherTable("xl_metadata");
		table.addColumn(new Column("value", DataTypeDict.PG_VARCHAR));
		table.addColumn(new Column("param", DataTypeDict.PG_VARCHAR));
		Collection<String> propertyNames = ont.getPropertyNames();
		for (String p : propertyNames) {
			table.addInitializationSql("INSERT INTO xl_metadata(param, value) VALUES ('" + Utility.fmt2PGName(p) + "', '')");
		}
		table.addInitializationSql("INSERT INTO xl_metadata(param, value) VALUES ('type', '')");
		table.addInitializationSql("INSERT INTO xl_metadata(param, value) VALUES ('" + Constants.OVERFLOW + "', '0')");
		miscComponentsMap.add(table);
	}

	private void addOverFlowTable() {
		OtherTable table = new OtherTable("xl_overflow");
		table.addColumn(new Column("id", DataTypeDict.PG_BIGINT));
		table.addColumn(new Column("sub", DataTypeDict.PG_VARCHAR));
		table.addColumn(new Column("pre", DataTypeDict.PG_VARCHAR));
		table.addColumn(new Column("obj", DataTypeDict.PG_VARCHAR));

		table.addCreateIndexSql("CREATE INDEX xl_overflow_id ON xl_overflow(id)");
		table.addDropIndexSql("DROP INDEX IF EXISTS xl_overflow_id");
		miscComponentsMap.add(table);
	}

	private void addDelTable() {
		OtherTable table = new OtherTable("xl_delete");
		table.addColumn(new Column("id", DataTypeDict.PG_BIGINT, "PRIMARY KEY"));
		miscComponentsMap.add(table);
	}

	
	private void addMapTable() {
		OtherTable table = new OtherTable("Map");
		table.addColumn(new Column("id", DataTypeDict.PG_BIGINT));
		table.addColumn(new Column("uri", DataTypeDict.PG_VARCHAR));
		table.addColumn(new Column("ct", DataTypeDict.PG_VARCHAR));
		miscComponentsMap.add(table);
	}

	private void addBuildMap() {
		Procedure proc = new Procedure("build_map");
		List<String> stmtList = new ArrayList<String>();
		stmtList.add("TRUNCATE Map");
		stmtList.add("DROP INDEX IF EXISTS idx_map_id");
		stmtList.add("DROP INDEX IF EXISTS idx_map_uri");

		/*Collection<OwlClass> classTables = ont.getClassTables();
		for (OwlClass classTable : classTables) {
			if (classTable.getParent() != null) {
				String tableName = classTable.getName();
				stmtList.add("INSERT INTO map(id, uri, ct) select id, uri, '" + tableName + "' from only " + tableName);
			}
		}*/
		stmtList.add("INSERT INTO map(id, uri, ct) SELECT id, uri, tableoid::regclass FROM thing");
		stmtList.add("CREATE UNIQUE INDEX idx_map_id ON Map(id)");
		stmtList.add("CREATE INDEX idx_map_uri ON Map USING BTREE (decode(md5(uri::text), 'hex'::text))");
		proc.setStmtList(stmtList);
		miscComponentsMap.add(proc);
	}
}
