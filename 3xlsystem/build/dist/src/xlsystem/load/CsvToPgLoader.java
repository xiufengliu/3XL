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

import xlsystem.common.XLException;

public class CsvToPgLoader implements Runnable {

	DAO dao;
	String tableName;
	String[] columns;

	public CsvToPgLoader(DAO dao, String tableName, String[] columns) {
		this.dao = dao;
		this.tableName = tableName;
		this.columns = columns;
	}

	@Override
	public void run() {
		try {
			long t1 = System.nanoTime();
			this.dao.load(null, tableName, columns);
			long t2 = System.nanoTime();
			
			float total = (t2 - t1) / 1000000000.0f;
			if (total > 3.0f) {
				System.out.printf("%s: %s=%.2f \n", Thread.currentThread().getName(), tableName, total);
			}
		} catch (XLException e) {
			e.printStackTrace();
		}
	}

}
