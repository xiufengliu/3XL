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

public class CachedObject {

	protected Object _obj;

	private long _accessCount;

	private long _createTime;

	private long _lastAccessTime;

	private long _id;

	private int _objSize;

	CachedObject() {
		_obj = null;
		_createTime = System.currentTimeMillis();
		_accessCount = 1;
		_lastAccessTime = _createTime;
		_id = nextId();
	}

	Object getObject() {
		return _obj;
	}

	void setObject(Object vh) {
		_obj = vh;
	}

	String getObjectId(){
		return _obj.toString();
	}

	long getAccessCount() {
		return _accessCount;
	}

	long getCreateTime() {
		return _createTime;
	}

	long getLastAccessTime() {
		return _lastAccessTime;
	}

	void updateStatistics() {
		_accessCount++;
		_lastAccessTime = System.currentTimeMillis();
		_id = nextId();
	}

	void reset() {
		_obj = null;
		_createTime = System.currentTimeMillis();
		_accessCount = 1;
		_lastAccessTime = _createTime;
		_objSize = 0;
		_id = nextId();
	}

	long getId() {
		return _id;
	}

	long getObjectSize() {
		return _objSize;
	}

	void setObjectSize(int objSize) {
		_objSize = objSize;
	}

	public String toString() {
		return "id:" + _obj.toString() + " createTime:" + _createTime + " lastAccess:" + _lastAccessTime + " accessCount:" + _accessCount + " object:" + _obj;
	}

	private static long ID = 0;

	private static synchronized long nextId() {
		return ID++;
	}

}
