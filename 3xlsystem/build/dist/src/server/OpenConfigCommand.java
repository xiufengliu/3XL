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

package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

import xlsystem.common.SocketCmd;
import xlsystem.common.Configure;
import xlsystem.common.XLException;

public class OpenConfigCommand implements Command {

	@Override
	public Object execute(BufferedReader reader, BufferedWriter writer) throws XLException {
		try {
			Configure config = Configure.getInstance();
			Iterator<Entry<Object, Object>> itr = config.iterator();
			while (itr.hasNext()) {
				Entry<Object, Object> keyValue = itr.next();
				writer.write(keyValue.getKey() + "=" + keyValue.getValue() + "\n");
			}
			writer.write(SocketCmd.MSG_END + "\n");
			
			// Transmit ontology file
			BufferedReader bufReader = new BufferedReader(new FileReader(config.getOntPath()));
			String line = "";
			while ((line = bufReader.readLine()) != null) {
				writer.write(line + "\n");
			}
			writer.write(SocketCmd.MSG_END + "\n");
			writer.flush();
		} catch (IOException e) {
			throw new XLException(e.getMessage());
		}
		return null;
	}

}
