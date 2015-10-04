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

package xlclient.gui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import xlclient.common.SocketCmd;
import xlclient.common.Utility;
import xlclient.gui.CompOperations;
import xlclient.gui.SwingUtil;
import xlclient.gui.socket.Communicator;
import xlclient.gui.socket.SocketPool;

public class ConfigJMenuBar extends JMenuBar implements ActionListener, CompOperations {

	private JMenuItem connetServerItem;
	private String ipAddr;
	private int port;
	private JMenuItem newItem;
	private JMenuItem openItem;
	private JMenuItem saveItem;
	private JMenuItem exitItem;

	public ConfigJMenuBar() {

		JMenu config = new JMenu("Config");
		config.setMnemonic('F');

		connetServerItem = new JMenuItem("Connect server...");
		connetServerItem.setMnemonic('C');
		connetServerItem.addActionListener(this);
		config.add(connetServerItem);

		newItem = new JMenuItem("New");
		newItem.setMnemonic('N');
		newItem.addActionListener(this);
		config.add(newItem);

		openItem = new JMenuItem("Load from server");
		openItem.setMnemonic('L');
		openItem.addActionListener(this);
		config.add(openItem);

		saveItem = new JMenuItem("Save to server");
		saveItem.setMnemonic('S');
		saveItem.addActionListener(this);
		config.add(saveItem);

		exitItem = new JMenuItem("Exit");
		exitItem.setMnemonic('x');
		exitItem.addActionListener(this);
		config.add(exitItem);

		add(config);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == connetServerItem) {
			connectToServer();
		} else if (e.getSource() == saveItem) {
			saveConfig();
		} else if (e.getSource() == openItem) {
			openConfig();
		} else if (e.getSource() == newItem) {
			newConfig();
		} else if (e.getSource() == exitItem) {
			exit();
		}
	}

	private void connectToServer() {
		String newUrl = JOptionPane.showInputDialog(this, "IPAddress:Port", ipAddr == null ? "" : ipAddr + ":" + port);
		if (!Utility.isEmpty(newUrl)&&!newUrl.equals(ipAddr + ":" + port)) {
			String[] str = newUrl.split(":");
			Communicator communicator = Communicator.newInstance(str[0], Integer.parseInt(str[1]), 1, 1);
			if (communicator != null) {
				ipAddr = str[0];
				port = Integer.parseInt(str[1]);
				System.out.printf("Connect to %s successfully!\n", newUrl);
			}
		}
	}

	private void openConfig() {
		Communicator communicator = Communicator.getInstance();
		if (communicator != null) {
			if (communicator.sendMessage(new String[]{SocketCmd.MSG_OPEN_CONFIG}, new boolean[]{true}, null))
				SwingUtil.writeAll(this); // Write configuration into the fields in GUI.
		}
	}

	private void saveConfig() {
		Communicator communicator = Communicator.getInstance();
		if (communicator != null && SwingUtil.readAll(this)) {
			int n = JOptionPane.showConfirmDialog(SwingUtil.getTopLevelAncestor(this), "The configration and the ontology files will be overwriten. Will you proceed?", "Save configuration & ontology", JOptionPane.YES_NO_OPTION);
			if (n == JOptionPane.YES_OPTION) {
				communicator.sendMessage(new String[]{SocketCmd.MSG_SAVE_CONFIG}, new boolean[]{false}, null); // Read fields from GUI to configuration.
			}
		}
	}

	private void newConfig() {
		SwingUtil.clearAll(this);
		config.clear();
	}

	private void exit() {
		Communicator communicator = Communicator.getInstance();
		if (communicator != null && SwingUtil.readAll(this)) {
			int n = JOptionPane.showConfirmDialog(SwingUtil.getTopLevelAncestor(this), "Would you like save the configuration and ontology?", "Save configuration & ontology", JOptionPane.YES_NO_OPTION);
			if (n == JOptionPane.YES_OPTION) {
				communicator.sendMessage(new String[]{SocketCmd.MSG_SAVE_CONFIG}, new boolean[]{true}, null); // Read fields from GUI to configuration.
			}
		}

		
		if (communicator != null) {
			communicator.sendMessage(new String[]{SocketCmd.MSG_BYE}, new boolean[]{true}, null); 
		}
		System.exit(0);
	}

	@Override
	public boolean read() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void write() {
		// TODO Auto-generated method stub

	}

	@Override
	public void clear() {
	}
}
