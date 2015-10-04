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

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.Set;

import xlclient.common.SocketCmd;

 public  class SocketClient {

	Socket requestSocket;
	BufferedReader reader;
	BufferedWriter writer;

	synchronized public boolean connect(String hostname, int port) {
		try {
			requestSocket = new Socket(hostname, port);
			System.out.printf("Connected to XLServer %s:%d successfully!\n", hostname, port);
			writer = new BufferedWriter(new OutputStreamWriter(requestSocket.getOutputStream()));
			reader = new BufferedReader(new InputStreamReader(requestSocket.getInputStream()));
			return true;
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return false;
	}

	synchronized public void read() {
		try {
			String line = "";
			PrintStream printStream = System.out;
			while (!SocketCmd.MSG_END.equals(line = reader.readLine())) {
				if (SocketCmd.MSG_ERROR.equals(line)) {
					printStream = System.err;
				}
				printStream.println(line);
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	synchronized public void read(Writer outputWriter) {
		try {
			String line = "";
			while (!SocketCmd.MSG_END.equals(line = reader.readLine())) {
				outputWriter.write(line + "\n");
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

  synchronized public void close() {
		try {
			reader.close();
			writer.close();
			requestSocket.close();
		} catch (IOException ioException) {
		}
	}

	synchronized public void write(String msg) {
		try {
			writer.write(msg);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	synchronized public void flush() {
		try {
			writer.flush();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	synchronized public void writeLine(String msg) {
		this.write(msg + "\n");
	}

	synchronized public boolean isConnected() {
		try {
			writeLine(SocketCmd.MSG_IS_ALIVE);
			writer.flush();
			String line = reader.readLine();
			return SocketCmd.MSG_I_AM_ALIVE.equals(line);
		} catch (Exception e) {
			return false;
		}
	}

	public static void main(String args[]) {
		SocketClient client = new SocketClient();
		client.connect("172.25.25.48", 9000);
		String line = "";
		Scanner scanner = new Scanner(System.in);
		while (scanner.hasNext()) {
			line = scanner.nextLine();
			System.out.println(">" + line);
		}
	}
}
