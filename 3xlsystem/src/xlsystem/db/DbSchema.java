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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import xlsystem.ontology.OwlOntology;

public class DbSchema implements DbComponent {
	String name = null;
	OwlOntology ont;
	List<DbComponent> dbComponents;

	String createSchemaSql;
	String dropSchemaSql;

	public DbSchema(OwlOntology ont) {
		this.ont = ont;
		this.dbComponents = new ArrayList<DbComponent>();
	}

	public void addDbComponents(Collection<DbComponent> dbComps) {
		this.dbComponents.addAll(dbComps);
	}

	@Override
	public void initialize(Connection con) {
		for (DbComponent dbComp : dbComponents) {
			dbComp.initialize(con);
		}
	}

	@Override
	public void create(Connection con) {
		for (DbComponent dbComp : dbComponents) {
			dbComp.create(con);
		}
	}

	@Override
	public void drop(Connection con) {
		for (DbComponent dbComp : dbComponents) {
			dbComp.drop(con);
		}
	}

	@Override
	public void generateSQL() {
		for (DbComponent dbComp : dbComponents) {
			dbComp.generateSQL();
		}
	}

	public String toString() {
		StringBuilder buf = new StringBuilder();
		for (DbComponent dbComp : dbComponents) {
			buf.append(dbComp.toString()).append("\n-----------------------------------\n");
		}
		return buf.toString();
	}

	@Override
	public Collection<String> get(int type) {
		List<String> sqlist = new ArrayList<String>();
		for (DbComponent dbComp : dbComponents) {
			Collection<String> sqlStatements = dbComp.get(type);
			if (sqlStatements!=null && sqlStatements.size()>0)
				sqlist.addAll(sqlStatements);
		}
		return sqlist;
	}
}
