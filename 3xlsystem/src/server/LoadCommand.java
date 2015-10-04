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
import java.io.IOException;

import api.Load;

import xlsystem.common.SocketCmd;
import xlsystem.common.Configure;
import xlsystem.common.XLException;
import xlsystem.query.BufferedWriterWrapper;

public class LoadCommand implements Command {

	@Override
	public Object execute(BufferedReader reader, BufferedWriter writer) throws XLException {

		try {
			BufferedWriterWrapper bufWriter = (BufferedWriterWrapper)writer;
			Configure config = Configure.getInstance();
			Load load = new Load(config);
			String message = load.start(config.getDataSourcePath(), true);
			bufWriter.write("Finish loading\n");
			bufWriter.writeT(message+"\n");
		} catch (Exception e) {
			try {
				writer.write(SocketCmd.MSG_ERROR+"\n");
				writer.write(e.getMessage());
			} catch (IOException e1) {
			}
		} finally {
			try {
				writer.write(SocketCmd.MSG_END + "\n");
				writer.flush();
			} catch (IOException e) {
				System.out.println("Connection broken!");
				e.printStackTrace();
			}
		}
		return null;
	}


}
