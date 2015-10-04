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
import java.io.StringReader;

import xlsystem.common.SocketCmd;
import xlsystem.common.XLException;
import xlsystem.query.BufferedWriterWrapper;
import api.Query;

public class QueryCommand implements Command {
	Query query = new Query();
	
	@Override
	public Object execute(BufferedReader reader, BufferedWriter writer) throws XLException {
		try {
			StringBuilder strBuilder = new StringBuilder();
			String line = "";
			while (!SocketCmd.MSG_END.equals(line = reader.readLine())) {
				strBuilder.append(line);
			}
			if (strBuilder.length()>0){
				System.out.println(strBuilder.toString());
				reader =  new BufferedReader(new StringReader(strBuilder.toString()));

				long start = System.currentTimeMillis();
				query.query(reader, writer);
				long queryTime = System.currentTimeMillis() - start;
				((BufferedWriterWrapper)writer).writeT(String.format("Query time: %d ms.\n", queryTime));
			}
		} catch (Exception e) {
			try {
				writer.write(SocketCmd.MSG_ERROR+"\n");
				writer.write(e.getMessage()+"\n");
			} catch (IOException e1) {
			}
		} finally{
			try {
				writer.write(SocketCmd.MSG_END+"\n");
				writer.flush();
			} catch (IOException e) {
			}
		}
		return null;
	}
}
