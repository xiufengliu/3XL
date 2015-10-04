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

package xlsystem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import server.Command;
import server.GenSchemaCommand;
import server.HeartBeatTestingCommand;
import server.LoadCommand;
import server.OpenConfigCommand;
import server.QueryCommand;
import server.SaveConfigCommand;
import server.SaveOntologyCommand;
import xlsystem.common.Arg;
import xlsystem.common.Configure;
import xlsystem.common.SocketCmd;
import xlsystem.common.Utility;
import xlsystem.common.XLcmd;
import xlsystem.query.BufferedWriterWrapper;

public class Server extends XLcmd {
	private ServerSocket serverSocket;
	private ExecutorService pool;
	private boolean timing;


	private static final String[] usage = new String[] { "Usage: XLServer [--port 9000] [--timing]" };
	private final Arg portArg = new Arg(true, "port", "9000");
	private final Arg timingArg = new Arg("timing");
	

	private final Map<String, Command> exeCommandMaps;


	public Server() {
		setUsage(usage);
		getCommandLine().add(portArg);
		getCommandLine().add(timingArg);
	
		exeCommandMaps = new HashMap<String, Command>();
		Configure.newInstance("conf/config.xml");
		exeCommandMaps.put(SocketCmd.MSG_IS_ALIVE, new HeartBeatTestingCommand());
		exeCommandMaps.put(SocketCmd.MSG_SAVE_ONT, new SaveOntologyCommand());
		exeCommandMaps.put(SocketCmd.MSG_OPEN_CONFIG, new OpenConfigCommand());
		exeCommandMaps.put(SocketCmd.MSG_SAVE_CONFIG, new SaveConfigCommand());
		exeCommandMaps.put(SocketCmd.MSG_GEN_SCHEMA, new GenSchemaCommand());
		exeCommandMaps.put(SocketCmd.MSG_LOAD, new LoadCommand());
		exeCommandMaps.put(SocketCmd.MSG_QUERY, new QueryCommand());
	}

	public void serve() {
		try {
			System.out.printf("XLServer is listening on %s:%s\n", Utility.getLocalIPAddr(), getCommandLine().getArg(portArg).getValue());
			for (;;) {
				pool.execute(new Handler(serverSocket.accept()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.shutdown();
		}
	}

	@Override
	protected void exec() {
		try {
			int port = Integer.parseInt(getCommandLine().getArg(portArg).getValue());
			this.timing = getCommandLine().contains(timingArg);
			this.serverSocket = new ServerSocket(port);
			this.pool = Executors.newFixedThreadPool(6);
			this.serve();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Server xlserver = new Server();
		xlserver.init(args);
		xlserver.exec();
	}

	class Handler implements Runnable {
		private BufferedReader reader;
		private BufferedWriter writer;
		
		Handler(Socket client) {
			try {
				reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
				writer = new BufferedWriterWrapper(new OutputStreamWriter(client.getOutputStream()), timing);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void run() {
			String cmd = "";
			try {
				while (!SocketCmd.MSG_BYE.equals(cmd = reader.readLine())) {
					if (cmd!=null){
						System.out.println("cmd=" + cmd);
						Command command = exeCommandMaps.get(cmd);
						command.execute(reader, writer);
					} 
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					writer.close();
					reader.close();
				} catch (Exception e) {
				}
			}
		}

	}
}
