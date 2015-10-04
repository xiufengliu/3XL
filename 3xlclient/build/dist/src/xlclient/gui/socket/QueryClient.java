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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import xlclient.common.SocketCmd;
import xlclient.common.Utility;
import xlclient.common.XLException;

public class QueryClient extends Thread implements Command {

	SocketPool pool;
	String queryStr;
	String savePath;

	@Override
	public void execute(SocketPool pool, boolean isJoin, String[] params) throws XLException {
		try {
			this.pool = pool;
			this.queryStr = params[0];
			this.savePath = params[1];
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
		BufferedWriter bufWriter = null;
		File resultSavedPath = null;
		boolean isWriteToFile = false;
		try {
			client = pool.getSocket();
			Writer writer = client.getWriter();

			// Command to create schema
			writer.write(SocketCmd.MSG_QUERY + "\n");
			writer.write(this.queryStr + "\n");
			writer.write(SocketCmd.MSG_END + "\n");
			writer.flush();
			//System.out.println("Query is submitted!");
			

			// Wait the response
			
			try {
				resultSavedPath = new File(savePath);
				if (Utility.isEmpty(savePath)||resultSavedPath.isDirectory()){
					bufWriter = new BufferedWriter(new OutputStreamWriter(System.out));
				} else {
					bufWriter = new BufferedWriter(new FileWriter(savePath));
					isWriteToFile = true;
				}
			} catch (Exception e) {
				bufWriter = new BufferedWriter(new OutputStreamWriter(System.out));
			}

			BufferedReader reader = client.getReader();
			String line = "";
			boolean isEnd = false;
			while (!SocketCmd.MSG_END.equals(line = reader.readLine())) {
				if (SocketCmd.MSG_ERROR.equals(line)) {
					bufWriter.flush();
					bufWriter = new BufferedWriter(new OutputStreamWriter(System.err));
				}
				if (!isEnd && SocketCmd.MSG_EOT.equals(line)){
					isEnd = true;
				}
				if (isWriteToFile && isEnd){
					System.out.println(line);
				} else {
					bufWriter.write(line + "\n");
				}
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
			isWriteToFile = false;
		} finally {
			try {
				bufWriter.flush();
				pool.returnSocket(client);
				if (isWriteToFile)
					System.out.printf("The results were saved to %s\n", resultSavedPath.getCanonicalPath());
			} catch (Exception e) {
			}
		}
	}
}
