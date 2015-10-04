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

package xlclient.gui.socket;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map.Entry;

import xlclient.common.Configure;
import xlclient.common.SocketCmd;
import xlclient.common.XLException;

public class SaveConfigClient extends Thread implements Command {

	SocketPool pool;

	@Override
	public void execute(SocketPool pool, boolean isJoin, String[] params) throws XLException {
		try {
			this.pool = pool;
			if (pool.isBusy()) {
				throw new XLException("The server is busy!");
			}
			start();
			if (isJoin) {
				this.join();
			}
		} catch (Exception e) {
			throw new XLException(e);
		}
	}


	
	@Override
	public void run() {
		SocketEntry client = null;
		try {
			client = pool.getSocket();
						
			Writer writer = client.getWriter();
			// Command to save configure
			Configure config = Configure.getInstance();
			Iterator<Entry<Object, Object>> itr = config.iterator();
			writer.write(SocketCmd.MSG_SAVE_CONFIG + "\n");
			while (itr.hasNext()) {
				Entry<Object, Object> keyValue = itr.next();
				writer.write(keyValue.getKey() + "=" + keyValue.getValue() + "\n");
			}
			writer.write(SocketCmd.MSG_END + "\n");
			
			// Transmit ontology file
			writer.write(SocketCmd.MSG_SAVE_ONT + "\n");
			writer.write(config.getOntText());
			writer.write(SocketCmd.MSG_END + "\n");
			writer.flush();
			
			System.out.println("Save successfully!");
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}finally {
			pool.returnSocket(client);
		}

	}

}
