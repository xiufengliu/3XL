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
import java.util.SortedMap;
import java.util.TreeMap;

import xlsystem.common.XLException;

public class SynchronizedCache implements Cache {

	private Map<String, CachedObject> _map;

	private SortedMap<CachedObject, CachedObject> _tmap;

	private CacheConfig _config;

	private long _memorySize;


	@Override
	public void put(Object obj) throws XLException  {
		if (obj != null) {
			
			/*int objSize = 0;
			try {
				objSize = _config.getMaxMemorySize() > 0 ? Utility.size(vh) : 0;
			} catch (IOException e) {
				throw new CacheException(e.getMessage());
			}*/

			CachedObject co = (CachedObject) _map.get(obj.toString());
			if (co != null) {
				_tmap.remove(co);
			//	_memorySize += objSize - co.getObjectSize();
				co.reset();

				co.setObject(obj);
			//	co.setObjectSize(objSize);
				_tmap.put(co, co);
			} else {
				checkOverflow();
				
				//checkOverflow(objSize);
				//_memorySize += objSize;
				co = newCacheObject();
				co.setObject(obj);
				//co.setObjectSize(objSize);
				_map.put(obj.toString(), co);
				_tmap.put(co, co);
			}
		}
	}

	/* (non-Javadoc)
	 * @see xlsystem.load.Cache#get(java.lang.String)
	 */
	@Override
	public Object get(String uri)  {
		if (uri != null) {
			CachedObject co = (CachedObject) _map.get(uri);
			Object o = co == null ? null : co.getObject();
			if (o != null) {
				_tmap.remove(co);
				co.updateStatistics();
				_tmap.put(co, co);
				return o;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see xlsystem.load.Cache#remove(java.lang.String)
	 */
	@Override
	public void remove(String uri) {
		if (uri == null) {
			throw new NullPointerException("uri is null");
		}
		CachedObject co = (CachedObject) _map.remove(uri);
		if (co != null) {
			_tmap.remove(co);
			co.reset();
		}
	}
	
	/* (non-Javadoc)
	 * @see xlsystem.load.Cache#poll()
	 */
	@Override
	public  Object poll() {
		if (_map.size() > ((1-_config.getCommitRatio()) * _config.getMaxSize())) {
			CachedObject co = _tmap.size() == 0 ? null : (CachedObject) _tmap.remove(_tmap.firstKey());
			if (co != null) {
				Object obj = co.getObject();
				_map.remove(co.getObjectId());
				co.reset();
				return obj;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see xlsystem.load.Cache#size()
	 */
	@Override
	public int size() {
		return _map.size();
	}

	/* (non-Javadoc)
	 * @see xlsystem.load.Cache#setCacheConfig(xlsystem.load.CacheConfig)
	 */
	@Override
	public void setCacheConfig(CacheConfig config) {
		if (config == null) {
			throw new NullPointerException("config is null");
		}
		_config = config;
		_map = _config.getMaxSize() > 1024 ? new HashMap<String, CachedObject>(1024) : new HashMap<String, CachedObject>();
		// _map = new HashMap<String,
		// CachedValueHolder>((int)(_config.getMaxSize()/0.75+1));
		// _map = new xlsystem.load.HashMap<String, CachedValueHolder>();
		_tmap = new TreeMap<CachedObject, CachedObject>(_config.getAlgorithmComparator());
	}

	


	
	
	private void checkOverflow() throws  XLException {
		if (_map.size() + 1 > _config.getMaxSize()) {
			Processor processor = _config.getProcessor();
			int commitSize = (int) (_config.getCommitRatio() * _config.getMaxSize());
			
			//long t1 = System.nanoTime();
			for (int i = 0; i < commitSize; ++i) {
				CachedObject co = _tmap.size() == 0 ? null : (CachedObject) _tmap.remove(_tmap.firstKey());
				if (co != null) {
					Object obj = co.getObject();
					_map.remove(co.getObjectId());			
					processor.process(obj);
					co.reset();
				}
			}
			//long t2 = System.nanoTime();
			//System.out.printf("Mem2CSV=%.2f; ", (t2-t1)/1000000000.0);
			processor.end();
		}
	}

	private void checkOverflow(long objSize) throws XLException {
		if (_memorySize + objSize > _config.getMaxMemorySize()) {
			Processor processor = _config.getProcessor();
			// System.out.println("Before: tmap.size()="+_tmap.size());
			while (_memorySize + objSize > _config.getMaxMemorySize() * (1.0 - _config.getCommitRatio())) {
				CachedObject co = _tmap.size() == 0 ? null : (CachedObject) _tmap.remove(_tmap.firstKey());
				if (co != null) {
					Object obj = co.getObject();
					_map.remove(co.getObjectId());
				
						processor.process(obj);
					
					_memorySize -= co.getObjectSize();
					co.reset();
				}
			}
			// System.out.println("after: tmap.size()="+_tmap.size());
				processor.end();
			
		}
	}

	/* (non-Javadoc)
	 * @see xlsystem.load.Cache#flush()
	 */
	@Override
	public void flush() throws XLException {
		Processor processor = _config.getProcessor();
		while (_map.size() > 0) {
			CachedObject co = _tmap.size() == 0 ? null : (CachedObject) _tmap.remove(_tmap.firstKey());
			if (co != null) {
				Object obj = co.getObject();
				_map.remove(co.getObjectId());
				processor.process(obj);
				co.reset();
			}
		}
		processor.end();
	}

	private CachedObject newCacheObject() {
		CachedObject co = _config.newCacheObject();
		return co;
	}
}
