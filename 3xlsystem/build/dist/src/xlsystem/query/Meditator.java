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

import java.io.Writer;
import java.util.EnumMap;
import java.util.List;

import xlsystem.common.Configure;
import xlsystem.common.RDF;
import xlsystem.common.XLException;

/**
 * 
 * @author xiliu
 */
public class Meditator {

	enum QueryType {
		S, SPO, SP, SO, P, PO, O, ALL, COMP
	}

	static EnumMap<QueryType, Query> queryMap = new EnumMap<QueryType, Query>(QueryType.class);
	static {
		queryMap = new EnumMap<QueryType, Query>(QueryType.class);
		queryMap.put(QueryType.SPO, new QuerySPO());
		queryMap.put(QueryType.S, new QueryS());
		queryMap.put(QueryType.SP, new QuerySP());
		queryMap.put(QueryType.SO, new QuerySO());
		queryMap.put(QueryType.P, new QueryP());
		queryMap.put(QueryType.PO, new QueryPO());
		queryMap.put(QueryType.O, new QueryO());
		queryMap.put(QueryType.ALL, new QueryAll());
		queryMap.put(QueryType.COMP, new CompositeQuery());
	}

	public Meditator(){
		
	}


	public void invoke(List<RDF> rdfs, Writer writer) throws XLException {
		QueryType queryType = getQueryType(rdfs);
		Query query = queryMap.get(queryType);
		query.query(rdfs, writer);	
	}

	public QueryType getQueryType(List<RDF> rdfs) {
		if (rdfs.size() == 1) {
			RDF rdf = rdfs.get(0);
			String s = rdf.getSub();
			String p = rdf.getPre();
			String o = rdf.getObj();
			
			if ("*".equals(s) || "*".equals(p) || "*".equals(o)) {
				if ("*".equals(s) && "*".equals(p) && "*".equals(o)) {
					return QueryType.ALL;
				} else if (!"*".equals(s) && "*".equals(p) && "*".equals(o)) {
					return QueryType.S;
				} else if ("*".equals(s) && !"*".equals(p) && "*".equals(o)) {
					return QueryType.P;
				} else if ("*".equals(s) && "*".equals(p) && !"*".equals(o)) {
					return QueryType.O;
				} else if (!"*".equals(s) && !"*".equals(p) && "*".equals(o)) {
					return QueryType.SP;
				} else if ("*".equals(s) && !"*".equals(p) && !"*".equals(o)) {
					return QueryType.PO;
				} else if (!"*".equals(s) && "*".equals(p) && !"*".equals(o)) {
					return QueryType.SO;
				} else if (!"*".equals(s) && !"*".equals(p) && !"*".equals(o)) {
					return QueryType.SPO;
				} else
					return null;
			} else {
				return QueryType.COMP;
			}
		} else {
			return QueryType.COMP;
		}
	}
}
