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

import java.sql.SQLException;
import java.util.List;

import xlsystem.common.Logger;
import xlsystem.common.Map;
import xlsystem.common.TimeTracer;
import xlsystem.common.XLException;
import xlsystem.ontology.OwlOntology;

public class TripleProcessor implements StmtProcessor {

	private Logger logger = Logger.getLogger("3xlsystem");
	
	private int id;
	private Cache cache;
	private KVStore kvStore;
	private OwlOntology ont;
	private DAO dao;

	private int tripleCount = 0;

	

	public TripleProcessor(OwlOntology ont, KVStore kvStore, Cache cache, DAO dao) {
		this.dao = dao;
		this.cache = cache;
		this.kvStore = kvStore;
		this.ont = ont;
		Map map = (xlsystem.common.Map) kvStore.get("nextid");
		if (map != null) {
			id = map.getId();
		}
	}

	@Override
	public void handleStatement(String subject, URI predicate, Value object) {
		try {
			ValueHolder vh = (ValueHolder) cache.get(subject);

			if (vh != null) {
				valueHolderInCache(vh, subject, predicate, object);
			} else {
				Map map = (xlsystem.common.Map) kvStore.get(subject);
				if (map != null && !"xl_overflow".equals(map.getCt())) {
					valueHolderInDB(map, subject, predicate, object);
				} else {
					newValueHolder(subject, predicate, object);
				}
			}
			
			if (++tripleCount % 5000000 == 0) {
				TimeTracer timeTracer = TimeTracer.thiz();
				timeTracer.end("loading", String.valueOf(tripleCount));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void valueHolderInCache(ValueHolder vh, String subject, URI predicate, Value object) throws CacheException, XLException {
		if (predicate.isRdfType()) {
			if (!vh.isNameLocked()) {
				vh.setName(((URI) object).getPGName());
			}
		} else {
			vh.put(predicate.getPGName(), value(object));
		}
	}

	private void valueHolderInDB(Map map, String subject, URI predicate, Value object) throws CacheException, XLException, SQLException {
		if (predicate.isRdfType()) {
			if (!map.isCtLocked()) {
				ValueHolder vh = (ValueHolder) dao.query(map.getId(), map.getCt());
				if (vh != null) {
					vh.setName(((URI) object).getPGName());
					vh.setNameLocked(true);
					cache.put(vh);
				} else {
					throw new XLException("The database was currupted!");
				}
			}
		} else {
			ValueHolder vh = (ValueHolder) dao.query(map.getId(), map.getCt());
			if (vh != null) {
				vh.setNameLocked(map.isCtLocked());
				vh.put(predicate.getPGName(), value(object));
				cache.put(vh);
			} else {
				throw new XLException("The database was currupted!");
			}
		}
	}

	private void newValueHolder(String subject, URI predicate, Value object) throws CacheException, XLException {
		ValueHolder vh = new ValueHolder();
		vh.setId(++id);
		vh.setUri(subject);
		if (predicate.isRdfType()) {
			String className = ((URI) object).getPGName();
			if (ont.isOwlClassDefined(className)) {
				vh.setName(className);
				vh.setNameLocked(true);
			} else {
				vh.setName("xl_overflow");
				vh.put("pre", predicate.getURI());
				vh.put("obj", object.toString());
				vh.setNameLocked(true);
			}
		} else {
			String pgName = predicate.getPGName();
			List<String> domainNames = ont.getClassTableNames(pgName);
			if (domainNames != null) {
				String name = domainNames.size() == 1 ? domainNames.get(0) : "thing";
				vh.setName(name);
				vh.put(pgName, value(object));
			} else {
				vh.setName("xl_overflow");
				vh.put("pre", predicate.getURI());
				vh.put("obj", object.toString());
				vh.setNameLocked(true);
			}
		}
		cache.put(vh);
	}

	private String value(Value object) throws CacheException, XLException {
		if (object.isLiteral()) {
			return object.toString();
		} else {
			String uri = ((URI) object).getURI();
			ValueHolder vh = (ValueHolder) cache.get(uri);
			if (vh != null) {
				return String.valueOf(vh.getId());
			} else {
				Map map = (xlsystem.common.Map) kvStore.get(uri);
				if (map != null) {
					return String.valueOf(map.getId());
				} else {
					vh = new ValueHolder("thing");
					vh.setUri(uri);
					vh.setId(++id);
					cache.put(vh);
					return String.valueOf(vh.getId());
				}
			}
		}
	}

	@Override
	public void end() {
		try {
			cache.flush();
			kvStore.put(new Map("nextid", id, null));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
