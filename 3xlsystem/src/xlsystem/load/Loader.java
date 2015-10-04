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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


import xlsystem.DBMain;
import xlsystem.common.Configure;
import xlsystem.common.ConnectionPool;
import xlsystem.common.Map;
import xlsystem.common.XLException;
import xlsystem.db.ClassTablesBuilder;
import xlsystem.db.DbComponent;
import xlsystem.db.DbSchema;
import xlsystem.db.OtherComponentsBuilder;
import xlsystem.load.bdb.BdbKVStore;
import xlsystem.ontology.OwlOntManager;
import xlsystem.ontology.OwlOntology;

public class Loader implements StmtProcessor {

	ConnectionPool connPool = null;
	KVStore kvStore = null;
	List<StmtProcessor> stmtProcessors;
	DAO dao;
	Collection<String> createIdxList;

	public Loader() {
		init();
	}

	private void init() {
		try {
			OwlOntology ont = OwlOntManager.getInstance();
			DbSchema dbSchema = new DbSchema(ont);
			Collection<DbComponent> owlTables = new ClassTablesBuilder(ont).buildComponents();
			Collection<DbComponent> miscComps = new OtherComponentsBuilder(ont).buildComponents();
			dbSchema.addDbComponents(owlTables);
			dbSchema.addDbComponents(miscComps);

			Collection<String> dropIdxList = dbSchema.get(DbSchema.INDEX_REF_DROP);
			createIdxList = dbSchema.get(DbSchema.INDEX_REF_CREATE);

			Configure config = Configure.getInstance();
			connPool = new ConnectionPool(config.getDbDriver(), config.getDbUrl(), config.getDbUsername(), config.getDbPassword(), 1, 1, true, true);
			Connection conn = connPool.getConnection();
			dao = new LoadDAO(conn, ont);
			dao.batchSqlExcution(dropIdxList);

			kvStore = new BdbKVStore();
			
			Cache cache = new SynchronizedCache();
			CacheConfig cacheConfig = new CacheConfig(config.getVhBufSize(), 1024, config.getVhCommitSize(),
					new ValueHolderMemToPgProcessor(ont, kvStore, dao));
			cache.setCacheConfig(cacheConfig);

			stmtProcessors = new ArrayList<StmtProcessor>();
			stmtProcessors.add(new TripleProcessor(ont, kvStore, cache, dao));
			stmtProcessors.add(new NameMappingProcessor(dao));
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	@Override
	public void handleStatement(String subject, URI predicate, Value object) {
		for (StmtProcessor processor : stmtProcessors) {
			processor.handleStatement(subject, predicate, object);
		}
	}

	@Override
	public void end() {
		for (StmtProcessor stmtProcessor : stmtProcessors) {
			stmtProcessor.end();
		}
		stmtProcessors.clear();

		try {
			dao.create(); // Create map table
			dao.batchSqlExcution(createIdxList); // recreate the index on the
								// Multiproperty tables
			//kvStore.put(new Map("nextid", id, null));
			kvStore.close();
		} catch (XLException e) {
			e.printStackTrace();
		} finally {
			dao.close();
			connPool.closeAllConnections();
		}
	}
}
