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

import java.util.Comparator;

public class CacheConfig {
	private int _maxSize;
	private long _maxMemorySize;
	private String _algorithm;
	Processor _processor;
	private double _commitRatio;
	



	
	public CacheConfig(int maxSize, int maxMemorySize, double commitRatio, Processor processor) {
		_maxSize = maxSize < 1024 ? 1024 : maxSize;
		_maxMemorySize = maxMemorySize *1024*1024;
		_commitRatio = commitRatio<0.2 ? 0.2:commitRatio;
		_commitRatio = _commitRatio>1.0 ? 1.0:_commitRatio;
		_processor = processor;
	}

	
	public int getMaxSize() {
		return _maxSize;
	}
	
	public void setMaxSize(int maxSize){
		_maxSize = maxSize;
	}
	
	public void reduceMaxSize(){
		_maxSize = _maxSize > 4096 ? _maxSize-50:4096;
	}

	public String getAlgorithm() {
		return _algorithm;
	}

	public Processor getProcessor() {
		return _processor;
	}

	CachedObject newCacheObject() {
		return new CachedObject();
	}

	public double getCommitRatio() {
		return _commitRatio;
	}

	Comparator getAlgorithmComparator() {
		return new LRUComparator();
	}

	public long getMaxMemorySize() {
		return _maxMemorySize;
	}
}
