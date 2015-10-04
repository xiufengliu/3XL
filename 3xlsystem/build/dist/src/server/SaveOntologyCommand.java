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
import java.io.FileWriter;
import java.io.IOException;

import xlsystem.common.Configure;
import xlsystem.common.SocketCmd;
import xlsystem.common.XLException;

public class SaveOntologyCommand implements Command {

	@Override
	public Object execute(BufferedReader reader, BufferedWriter writer) throws XLException {
		try {
			StringBuilder ontTextBld = new StringBuilder();
			String line = "";
			while (!SocketCmd.MSG_END.equals(line = reader.readLine())) {
				ontTextBld.append(line).append("\n");
			}
			
			String ontPath = Configure.getInstance().getOntPath();
			
			BufferedWriter owlWriter = new BufferedWriter(new FileWriter(ontPath));
			owlWriter.write(ontTextBld.toString());
			owlWriter.close();
			
			//System.out.println(Configure.getInstance());
			//System.out.println(ontTextBld.toString());
			//writer.write(SocketCmd.MSG_ONT_IS_SAVED+"\n");
			writer.flush();
			System.out.printf("OWL Lite Ontology is saved to %s\n", ontPath);
		} catch (IOException e) {
			throw new XLException(e.getMessage());
		}
		return null;
	}

}
