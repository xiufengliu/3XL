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

package xlsystem.load.bdb;

import java.io.File;

import xlsystem.common.Configure;
import xlsystem.common.Map;
import xlsystem.load.KVStore;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.LockMode;
import com.sleepycat.persist.EntityCursor;

public class BdbKVStore implements KVStore {

	//private static File myDbEnvPath = new File(Configure.get("bdb.path"));
	

	private DataAccessor da;

	// Encapsulates the database environment.
	private MyDbEnv myDbEnv;

	public BdbKVStore() {
		try {
			File envPath = new File(Configure.getInstance().getBdbPath());
			envPath.mkdirs();
			myDbEnv = new MyDbEnv();
			myDbEnv.setup(envPath);
			da = new DataAccessor(myDbEnv.getEntityStore());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public EntityCursor<?> getCursor() throws DatabaseException {

		return da.mapByPK.entities();

	}

	@Override
	public void put(Object map) throws DatabaseException {
		da.mapByPK.put((xlsystem.common.Map)map);
	}

	@Override
	public Object get(String key) throws DatabaseException {
		return da.mapByPK.get(null, key, LockMode.READ_UNCOMMITTED);
	}

	@Override
	public void close() {
		
		myDbEnv.close();
	}
}
