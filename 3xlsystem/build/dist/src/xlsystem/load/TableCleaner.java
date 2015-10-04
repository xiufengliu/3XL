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

import java.util.concurrent.ConcurrentLinkedQueue;

public class TableCleaner extends Thread {

	private long _cleanInterval;

	private boolean _sleep = false;

	private ConcurrentLinkedQueue<Delete> cleanQueue;
	

	public TableCleaner(long cleanInterval) {
		_cleanInterval = cleanInterval;
		cleanQueue = new ConcurrentLinkedQueue<Delete>();
		setName(this.getClass().getName());
		setDaemon(true);

		// setPriority(Thread.MIN_PRIORITY);
	}

	public void setCleanInterval(long cleanInterval) {
		_cleanInterval = cleanInterval;

		synchronized (this) {
			if (_sleep) {
				interrupt();
			}
		}
	}

	public void run() {
		while (true) {
			while (!cleanQueue.isEmpty()) {
				try {
					Delete del = cleanQueue.poll();
					del.delete();
					// yield();
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}

			_sleep = true;
			try {
				sleep(_cleanInterval);
			} catch (Throwable t) {
			} finally {
				_sleep = false;
			}
		}
	}

	public void add(Delete del) {
		cleanQueue.add(del);

		synchronized (this) {
			if (_sleep) {
				interrupt();
			}
		}
	}

	public boolean isClean() {
		while (!cleanQueue.isEmpty())
			;
		return true;
	}
}
