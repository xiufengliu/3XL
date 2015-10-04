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

package xlclient.gui.opt.query;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import xlclient.gui.CompOperations;
import xlclient.gui.SwingUtil;

public class ExportResultsJPanel extends JPanel implements ActionListener, CompOperations {

	JTextField exportPathTextField;
	JButton exportButton;
	public final static String NAME = "ExportResultsJPanel";
	public ExportResultsJPanel() {
		setName(NAME);
		setLayout(new BorderLayout());
		this.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		exportPathTextField = new JTextField();
		exportButton = new JButton("Save Results...");
		add(new JLabel("Save results to:"), BorderLayout.WEST);
		add(exportPathTextField, BorderLayout.CENTER);
		add(exportButton, BorderLayout.EAST);
		exportButton.addActionListener(this);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getSource() == exportButton) {
				File exportFile = SwingUtil.getDirectoryChoice(this, "", "Export triples to file");
				if (exportFile != null) {
					exportPathTextField.setText(exportFile.getCanonicalPath());
				}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public boolean read() {
		// TODO Auto-generated method stub
		return true;
	}
	
	public String readPath() {
		return exportPathTextField.getText();
	}

	@Override
	public void write() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clear() {
		exportPathTextField.setText("");
	}
}
