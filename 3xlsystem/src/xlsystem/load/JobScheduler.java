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

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import xlsystem.common.XLException;

class JobScheduler {

	public static final int POOLSIZE = 5;
	
	public void exec(Collection<Runnable> tasks) {
		ExecutorService executorService = Executors.newFixedThreadPool(POOLSIZE);
		try {
			for (Runnable task : tasks) {
				executorService.execute(task);
			}
			
			executorService.shutdown();
			if (!executorService.awaitTermination(60 * 20, TimeUnit.SECONDS)) {
				throw new XLException("Timeout: cannot finish the jobs within 10 min!");
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			e.printStackTrace();
		} catch (XLException e) {
			e.printStackTrace();
		} finally {
			executorService.shutdownNow();
		}
	}

	public static synchronized void run(Collection<Runnable> tasks) {
		if (jobScheduler == null) {
			jobScheduler = new JobScheduler();
		}
		jobScheduler.exec(tasks);
	}

	private static JobScheduler jobScheduler;
}
