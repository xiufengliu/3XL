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

package xlsystem.query;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import xlsystem.common.Constants;
import xlsystem.common.XLException;

public class QueryServer {
	private ServerSocket serverSocket;
	private ExecutorService pool;
	private Query query;
	private boolean timing;
	

	public QueryServer(int port, int poolSize, boolean timing) {
		try {
			serverSocket = new ServerSocket(port);
			pool = Executors.newFixedThreadPool(poolSize);
			this.timing = timing;
			//this.query = new Query();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void serve() {
		try {
			System.out.print("Query server listens on port: 9000\n");
			for (;;) {
				pool.execute(new Handler(serverSocket.accept()));
			}
		} catch (IOException ex) {
			pool.shutdown();
		}
	}
	
	class Handler implements Runnable {
		private final BufferedReader reader;
		private final BufferedWriter writer;
		private String resultFilePath;

		private static final String usage = "Usage:\n" + "1. One point query: (s, p, o) \n" + "*- is used to match anything, for example: (http://www.University0.edu, *, *)\n"
				+ "2. Joined query, for example:\n" + "(Professor,type,?X)\n(?X,worksFor,http://www.Department0.University0.edu)\n" + "(?X,name,?Y1)\n(?X,emailAddress,?Y2)\n(?X,telephone,?Y3)\n";

		Handler(Socket client) throws IOException {
			this.reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			this.writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
			

			// 2. Write to file
			// File outputFile = File.createTempFile("xl_", ".txt");
			// resultFilePath = outputFile.getPath()
			// OutputStream fos = new FileOutputStream(outputFile);
			// writer = new BufferedWriterWrapper(new OutputStreamWriter(fos),  timing);

			// 3. Write to screen
			//this.writer = new BufferedWriterWrapper(new PrintWriter(System.out), timing);
		}

		public void run() {
			try {
				//query.query(reader, writer, timing);
				throw new XLException("");
			} catch (XLException xe) {
				try {
					writer.write(xe.getMessage());
					writer.write(usage);
				} catch (IOException e) {
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					writer.write(Constants.EOT);
					if (this.resultFilePath != null) {
						writer.write("Results: " + this.resultFilePath);
					}
					writer.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
}


