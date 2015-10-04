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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import xlclient.common.SocketCmd;
import xlclient.common.XLException;

public class SocketEntry {

	private Socket socket;

	private long connectedTime;

	private int handedOutCount;

	private long handedOutSince;

	private boolean handedOut;
	
	BufferedReader reader;
	BufferedWriter writer;
	private String address;
	private int port;
	
	public int hashCode() {
		return socket.hashCode();
	}

	public synchronized BufferedWriter getWriter() throws IOException {
		if( writer == null ) {
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		}
		return writer;
	}
	
	
	public synchronized BufferedReader getReader() throws IOException {
		if( reader == null ) {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}
		return reader;
	}
	
	public SocketEntry(String address, int port) throws XLException {
		handedOutCount = 0;
		handedOutSince = -1;
		handedOut = false;
		this.address = address;
		this.port = port;
		try {
			socket = new Socket(address, port);
			connectedTime = System.currentTimeMillis();
		} catch (Exception e) {
			throw new XLException("Failed to connect to the 3XL server. Please check the server if active!");
		}
	}
	

	
	public long getConnectedTime() {
		return connectedTime;
	}

	public void setConnectedTime(long connectedTime) {
		this.connectedTime = connectedTime;
	}

	public boolean isHandedOut() {
		return handedOut;
	}

	public void setHandedOut(boolean handedOut) {
		this.handedOut = handedOut;
		if( handedOut) {
			handedOutCount++;
			handedOutSince = System.currentTimeMillis();			
		}
	}

	public int getHandedOutCount() {
		return handedOutCount;
	}

	public long getHandedOutSince() {
		return handedOutSince;
	}


	public boolean isValid(){
		try{
			BufferedWriter writer = this.getWriter();
			writer.write(SocketCmd.MSG_IS_ALIVE+"\n");
			writer.flush();
			
			BufferedReader reader = this.getReader();
			String line = reader.readLine();
			return SocketCmd.MSG_I_AM_ALIVE.equals(line);
		} catch (Exception e){
			return false;
		}
	}
	
	public Socket getSocket() throws XLException {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

}
