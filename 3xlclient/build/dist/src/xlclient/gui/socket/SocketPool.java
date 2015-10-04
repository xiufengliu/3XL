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

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import xlclient.common.SocketCmd;
import xlclient.common.XLException;

/**
 * This class manages multiple socket connections and the process of handing out
 * and returning these sockets to other resources.
 * 
 * 
 * 
 */
public class SocketPool {

	private String address;
	private int port;
	private int minPoolSize;
	private int maxPoolSize;
	private int currentPoolSize;

	private Map<Socket, SocketEntry> usedSocketPool;
	private LinkedList<SocketEntry> socketPool;

	/**
	 * Creates a socket pool
	 * 
	 * @param address
	 * @param port
	 * @throws IOException
	 */
	public SocketPool(String address, int port, int minPoolSize, int maxPoolSize) throws XLException {
		this.address = address;
		this.port = port;

		this.minPoolSize = minPoolSize;
		this.maxPoolSize = maxPoolSize;
		initPool();
	}

	/**
	 * Hands out a connected socket from the pool. The socket will not be handed
	 * out to anyone else before it has been returned using the
	 * <code>returnSocket()</code> method.
	 * <p>
	 * If there are no sockets available this thread will block until one is.
	 * 
	 * @return a connected socket
	 * @throws IOException
	 */
	
	public String getAddress(){
		return this.address;
	} 
	
	public int getPort(){
		return this.port;
	}
	
	public boolean isBusy(){
		return socketPool.size()==0 && currentPoolSize>=maxPoolSize;
	}
	
	public synchronized SocketEntry getSocket() throws  XLException {
		/*
		 * Block thread and wait for new sockets to be returned if none
		 * available
		 */
		
			if (socketPool.size()==0) {
				if (currentPoolSize < maxPoolSize){
					socketPool.add(new SocketEntry(address, port));
					++currentPoolSize;
				} else {
					return null;
				}
			} 
			
			/*System.err.println("Socket pool empty");
			try {
				wait();
			} catch (InterruptedException e) {
				System.err.printf("Interrupted while sleeping %s\n", e.getMessage());
			}*/
			
		

		SocketEntry entry = socketPool.poll();
		if (!entry.isValid()){
			try{
				entry = new SocketEntry(address, port);
			} catch (XLException e){
				--currentPoolSize;
				throw e;
			}
		}
		
		Socket socket = entry.getSocket();
		
		entry.setHandedOut(true);
		usedSocketPool.put(socket, entry);

		//System.out.println("Socket " + entry + " handed out from pool");
		//System.out.println(socketPool.size() + " sockets are left in pool");
		return entry;
	}

	/**
	 * Puts a socket back in the pool.
	 * 
	 * @param s
	 */
	public synchronized void returnSocket(SocketEntry entry) {
		if (entry != null) {
			entry.setHandedOut(false);
			socketPool.add(entry);
			notify();
			//System.out.println("Socket returned: " + entry.getSocket() + ". " + socketPool.size() + " sockets in pool");
		} 
	}

	private void initPool() throws XLException {
		if (currentPoolSize != 0) {
			throw new XLException("pool already initialized");
		}
		socketPool = new LinkedList<SocketEntry>();
		usedSocketPool = new HashMap<Socket, SocketEntry>();
		currentPoolSize = 0;

		while (currentPoolSize < minPoolSize) {
			socketPool.add(new SocketEntry(address, port));
			++currentPoolSize;
		}
	}

	

	public void closeAll() {
		BufferedWriter writer;
		for (SocketEntry s : socketPool) {
			try {
				writer = s.getWriter();
				writer.write(SocketCmd.MSG_BYE+"\n");
				writer.flush();
				writer.close();
				s.getSocket().close();
			} catch (Exception e) {
				//System.err.printf("Error closing socket %s\n", e.getMessage());
			}
		}
		socketPool.clear();
	}
}
