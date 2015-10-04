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

package xlclient;

import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;

import xlclient.common.SocketCmd;
import xlclient.gui.CompOperations;
import xlclient.gui.SwingUtil;
import xlclient.gui.console.ConsoleMain;
import xlclient.gui.menu.ConfigJMenuBar;
import xlclient.gui.opt.OperationMain;
import xlclient.gui.setting.SettingMain;
import xlclient.gui.socket.Communicator;

public class Main extends JFrame implements WindowListener, CompOperations {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			Main dialog = new Main();
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public Main() {
		// setTitle("3XL - An OWL/RDF triple store");
		// setBounds(0, 0, 1000, 700);
		// Configure.newConfig(null);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(this);

		setSize(new Dimension(1000, 700));
		JScrollPane settingScrollPane = new JScrollPane(new SettingMain());

		// Create a split pane with the two scroll panes in it.
		JSplitPane topSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, settingScrollPane, new OperationMain());
		topSplitPane.setOneTouchExpandable(true);
		topSplitPane.setDividerLocation(300);

		// Provide minimum sizes for the two components in the split pane.
		Dimension minimumSize = new Dimension(100, 50);
		settingScrollPane.setMinimumSize(minimumSize);

		JTextPane consolePanel = new ConsoleMain();
		JScrollPane consoleScrollPane = new JScrollPane(consolePanel);

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topSplitPane, consoleScrollPane);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(550);

		// Provide minimum sizes for the two components in the split pane
		topSplitPane.setMinimumSize(new Dimension(100, 50));
		consolePanel.setMinimumSize(new Dimension(100, 30));

		// Add the split pane to this frame
		getContentPane().add(splitPane);

		setJMenuBar(new ConfigJMenuBar());

	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosing(WindowEvent e) {
		Communicator communicator = Communicator.getInstance();
		if (communicator != null && SwingUtil.readAll(this)) {
			int n = JOptionPane.showConfirmDialog(this, "Would you like save the configuration and ontology?", "Save configuration & ontology", JOptionPane.YES_NO_OPTION);
			if (n == JOptionPane.YES_OPTION) {
				communicator.sendMessage(new String[]{SocketCmd.MSG_SAVE_CONFIG}, new boolean[]{true}, null); // Read fields from GUI to configuration.
			}
		}
		if (communicator!=null){
			communicator.sendMessage(new String[]{SocketCmd.MSG_BYE}, new boolean[]{true}, null);
		}
		System.exit(0);
	}

	@Override
	public void windowClosed(WindowEvent e) {

	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean read() {
		config.setConfigPath(this.getTitle());
		// config.set
		return true;
	}

	@Override
	public void write() {
		this.setTitle(config.getConfigPath());

	}

	@Override
	public void clear() {
		this.setTitle("");

	}
}
