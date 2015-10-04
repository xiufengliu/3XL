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

package xlsystem.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Vector;


public class ConnectionPool implements Runnable {
  
  private static ConnectionPool instance = null; 
  
  public static synchronized ConnectionPool getInstance(int initSize, int maxSize, boolean autoCommit){
	  if (instance==null){
		  try {
			Configure config = Configure.getInstance();
			instance = new ConnectionPool(config.getDbDriver(), config.getDbUrl(), config.getDbUsername(), config.getDbPassword(), initSize, maxSize, true, autoCommit);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	  }
	  return instance;
  }
  

  private String driver, url, username, password;
  private int maxConnections;
  private boolean waitIfBusy;
  private boolean autoCommit;

  private Vector<Connection> availableConnections, busyConnections;
  
  private boolean connectionPending = false;



  public ConnectionPool(String driver, String url,
                        String username, String password,
                        int initialConnections,
                        int maxConnections,
                        boolean waitIfBusy,
                        boolean autoCommit)
      throws SQLException {
    this.driver = driver;
    this.url = url;
    this.username = username;
    this.password = password;
    this.maxConnections = maxConnections;
    this.waitIfBusy = waitIfBusy;
    this.autoCommit = autoCommit;
    if (initialConnections > maxConnections) {
      initialConnections = maxConnections;
    }
    availableConnections = new Vector<Connection>(initialConnections);
    busyConnections = new Vector<Connection>();
    for(int i=0; i<initialConnections; i++) {
      availableConnections.addElement(makeNewConnection());
    }
  }

  public synchronized void closeAllConnections() {
    closeConnections(availableConnections);
    availableConnections = new Vector<Connection>();
    closeConnections(busyConnections);
    busyConnections = new Vector<Connection>();
  }

 
  
  private void closeConnections(Vector<Connection> connections) {
    try {
      for(int i=0; i<connections.size(); i++) {
        Connection connection =
          (Connection)connections.elementAt(i);
        if (!connection.isClosed()) {
          this.commit(connection);
          connection.close();
        }
      }
    } catch(SQLException sqle) {
      // Ignore errors; garbage collect anyhow
    }
  }

  public synchronized void commit(Connection connection)  throws SQLException{
	  if (!autoCommit){
		  try {
			connection.commit();
		} catch (SQLException e) {
		    throw new SQLException("Failed to commit");
		}
	  }
 }
    
  
  public synchronized void free(Connection connection) {
    busyConnections.removeElement(connection);
    availableConnections.addElement(connection);
    // Wake up threads that are waiting for a connection
    notifyAll(); 
  }
	    
  
  public synchronized Connection getConnection()
      throws SQLException {
    if (!availableConnections.isEmpty()) {
      Connection existingConnection =
        (Connection)availableConnections.lastElement();
      int lastIndex = availableConnections.size() - 1;
      availableConnections.removeElementAt(lastIndex);

      if (existingConnection.isClosed()) {
        notifyAll(); // Freed up a spot for anybody waiting
        return(getConnection());
      } else {
        busyConnections.addElement(existingConnection);
        return(existingConnection);
      }
    } else {
      
   
      
      if ((totalConnections() < maxConnections) &&
          !connectionPending) {
        makeBackgroundConnection();
      } else if (!waitIfBusy) {
        throw new SQLException("Connection limit reached");
      }
      // Wait for either a new connection to be established
      // (if you called makeBackgroundConnection) or for
      // an existing connection to be freed up.
      try {
        wait();
      } catch(InterruptedException ie) {}
      // Someone freed up a connection, so try again.
      return(getConnection());
    }
  }



  private void makeBackgroundConnection() {
    connectionPending = true;
    try {
      Thread connectThread = new Thread(this);
      connectThread.start();
    } catch(OutOfMemoryError oome) {
      // Give up on new connection
    }
  }

  private Connection makeNewConnection()
      throws SQLException {
    try {
    	Class.forName(driver);
    	Connection connection =  DriverManager.getConnection(url, username, password);
        connection.setAutoCommit(autoCommit);
      return(connection);
    } catch(ClassNotFoundException cnfe) {
      // Simplify try/catch blocks of people using this by
      // throwing only one exception type.
      throw new SQLException("Can't find class for driver: " +
                             driver);
    }
  }
  
  public void run() {
    try {
      Connection connection = makeNewConnection();
      synchronized(this) {
        availableConnections.addElement(connection);
        connectionPending = false;
        notifyAll();
      }
    } catch(Exception e) { // SQLException or OutOfMemory
      // Give up on new connection and wait for existing one
      // to free up.
    }
  }
  
  public synchronized String toString() {
    String info =
      "ConnectionPool(" + url + "," + username + ")" +
      ", available=" + availableConnections.size() +
      ", busy=" + busyConnections.size() +
      ", max=" + maxConnections;
    return(info);
  }
  public synchronized int totalConnections() {
    return(availableConnections.size() +
           busyConnections.size());
  }
  
}
