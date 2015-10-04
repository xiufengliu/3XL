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

package xlclient.gui.opt.dbgen;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import xlclient.common.SocketCmd;
import xlclient.gui.CompOperations;
import xlclient.gui.SwingUtil;
import xlclient.gui.socket.Communicator;

public class DBGenJPanel extends JPanel implements CompOperations, ActionListener {

	private JButton saveOntButton;
	private JButton genDBButton;
	Thread schemaGenThread;

	public DBGenJPanel() {
		setLayout(new BorderLayout(0, 0));

		JTextPane ontTextPane = new OntologyJTextPane();
		JScrollPane ontScrollPane = new JScrollPane(ontTextPane);
		add(ontScrollPane, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		add(panel, BorderLayout.SOUTH);

		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		Component horizontalGlue = Box.createHorizontalGlue();
		panel.add(horizontalGlue);

		saveOntButton = new JButton("Save OWL");
		saveOntButton.addActionListener(this);
		panel.add(saveOntButton);

		Component horizontalStrut = Box.createHorizontalStrut(30);
		panel.add(horizontalStrut);

		genDBButton = new JButton("Gen DBSchema");
		genDBButton.addActionListener(this);
		panel.add(genDBButton);

		Component horizontalGlue_1 = Box.createHorizontalGlue();
		panel.add(horizontalGlue_1);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == saveOntButton) {
			OntologyJTextPane ontJTextPane = (OntologyJTextPane) SwingUtil.findComponent(OntologyJTextPane.NAME, this);
			ontJTextPane.save();
		} else if (e.getSource() == genDBButton) {
			Communicator communicator = Communicator.getInstance();
			if (communicator!= null && JOptionPane.showConfirmDialog(this, "Gen DBSchema will erase the existing data. Will you continue?", "Generate database schema", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {// Read fields from GUI to configuration.
				communicator.sendMessage(new String[]{SocketCmd.MSG_GEN_SCHEMA}, new boolean[]{false}, null);
			}
		}
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
		// TODO Auto-generated method stub

	}

}
