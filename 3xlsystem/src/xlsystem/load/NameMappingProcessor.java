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

import java.util.HashMap;
import java.util.Map;

import xlsystem.common.XLException;

public class NameMappingProcessor implements StmtProcessor {

	private DAO dao;
	Map<String, String> metaMap;
	public NameMappingProcessor(DAO dao) {
		this.dao = dao;
		metaMap = new HashMap<String, String>();
	}


	@Override
	public void handleStatement(String subject, URI predicate, Value object) {
		String pgPre = predicate.getPGName();
		if (!metaMap.containsKey(pgPre)) {
			metaMap.put(pgPre, predicate.toString());
		}
	}


	@Override
	public void end() {
		try {
			dao.update(metaMap);
			metaMap.clear();
		} catch (XLException e) {
			e.printStackTrace();
		}
	}

}
