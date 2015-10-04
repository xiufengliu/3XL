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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

public class BufferedWriterWrapper extends BufferedWriter {

	private boolean timing = false;

	public BufferedWriterWrapper(Writer writer, boolean timing) {
		super(writer);
		this.timing = timing;
	}

	public void write(String str) throws IOException {
		//if (!timing) {
			super.write(str);
		//}
	}

	public void writeT(String str) throws IOException {
		if (timing) {
			super.write(str);
		}
	}
	
	public void writeStatistics(String str)throws IOException {
		if (timing) {
			super.write(str);
		}
	}
	
	public void flush() throws IOException {
		//if (!timing) {
			super.flush();
		//}
	}
}
