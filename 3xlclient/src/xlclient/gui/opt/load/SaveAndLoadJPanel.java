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

package xlclient.gui.opt.load;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


import xlclient.common.SocketCmd;
import xlclient.common.Utility;
import xlclient.gui.SwingUtil;
import xlclient.gui.socket.Communicator;
import xlclient.gui.socket.SendConfig;

public class SaveAndLoadJPanel extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JButton startDBButton;

	public SaveAndLoadJPanel() {

		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		Component horizontalGlue = Box.createHorizontalGlue();
		add(horizontalGlue);

		startDBButton = new JButton("Start");
		startDBButton.addActionListener(this);
		add(startDBButton);

		Component horizontalGlue_1 = Box.createHorizontalGlue();
		add(horizontalGlue_1);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == startDBButton) {
			Communicator communicator = Communicator.getInstance();
			if (communicator != null && JOptionPane.showConfirmDialog(this, "Are you sure to load the data?", "Load OWL/RDF data", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				communicator.sendMessage(new String[]{SocketCmd.MSG_LOAD}, new boolean[]{false}, null);
			}
		}
	}
}
