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

import xlclient.common.SocketCmd;
import xlclient.common.XLException;

public class Communicator {

	SocketPool socketPool = null;

	Communicator(String address, int port, int minPoolSize, int maxPoolSize) throws XLException {
		socketPool = new SocketPool(address, port, minPoolSize, maxPoolSize);
	}

	public boolean sendMessage(String[] message, boolean[] joins, String[] params) {
		try {
			for (int i = 0; i < message.length; ++i) {
				Command command = lookup(message[i]);
				command.execute(socketPool, joins[i], params);
			}
			return true;
		} catch (XLException e) {
			System.err.println(e.getMessage());
			return false;
		}
		
	}

	private Command lookup(String cmd) {
		if (SocketCmd.MSG_GEN_SCHEMA.equals(cmd))
			return new SchemaGenClient();
		else if (SocketCmd.MSG_LOAD.equals(cmd))
			return new LoadClient();
		else if (SocketCmd.MSG_QUERY.equals(cmd))
			return new QueryClient();
		else if (SocketCmd.MSG_SAVE_CONFIG.equals(cmd))
			return new SaveConfigClient();
		else if (SocketCmd.MSG_OPEN_CONFIG.equals(cmd))
			return new OpenConfigClient();
		else if (SocketCmd.MSG_BYE.equals(cmd))
			return new CloseClient();
		else
			return null;
	}

	static Communicator instance;

	public static Communicator getInstance() {
		if (instance == null) {
			System.err.println("3XL Server is not connected!");
		}
		return instance;
	}

	public static Communicator newInstance(String address, int port, int minPoolSize, int maxPoolSize) {
		try {
			instance = new Communicator(address, port, minPoolSize, maxPoolSize);
		} catch (XLException e) {
			System.err.println(e.getMessage());
			System.err.printf("Please check if the connect string is correct and if the server was started!\n");
		}
		return instance;
	}
	
}
