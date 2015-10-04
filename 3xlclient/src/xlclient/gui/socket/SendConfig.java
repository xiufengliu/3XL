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
import java.io.FileReader;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.JComponent;

import xlclient.common.Configure;
import xlclient.common.SocketCmd;
import xlclient.gui.SwingUtil;

public class SendConfig implements Command {

	public  boolean ensureIsSent(JComponent jcomp) {
		Configure config = Configure.getInstance();
		if (true) {
			if (SwingUtil.readAll(jcomp)) {

				try {
					SocketEntry client=null;
					Writer writer = client.getWriter();

					// Transmit configuration parameters
					writer.write(SocketCmd.MSG_TRAN_CONFIG + "\n");
					Iterator<Entry<Object, Object>> itr = config.iterator();
					while (itr.hasNext()) {
						Entry<Object, Object> keyValue = itr.next();
						writer.write(keyValue.getKey() + "=" + keyValue.getValue() + "\n");
					}
					writer.write(SocketCmd.MSG_END + "\n");

					// Transmit ontology file
					BufferedReader bufReader = new BufferedReader(new FileReader(config.getOntPath()));
					writer.write(SocketCmd.MSG_TRAN_ONTOLOGY + "\n");
					String line = "";
					while ((line = bufReader.readLine()) != null) {
						writer.write(line + "\n");
					}
					writer.write(SocketCmd.MSG_END + "\n");
					writer.flush();

					// Wait the response
					BufferedReader reader = client.getReader();
					line = reader.readLine();
					
				} catch (Exception e1) {
					e1.printStackTrace();
					return false;
				} 
				return true;
			} else
				return false;
		} else {
			return true;
		}
	}



	@Override
	public void execute(SocketPool pool, boolean isJoin, String[] params) {
		// TODO Auto-generated method stub
		
	}

}
