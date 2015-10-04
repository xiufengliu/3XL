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
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


import xlclient.common.Utility;
import xlclient.gui.CompOperations;
import xlclient.gui.SwingUtil;

public class CSVJPanel extends JPanel implements ActionListener, CompOperations {

	private JTextField csvTextField;

	public CSVJPanel() {
		this.setBorder(BorderFactory.createTitledBorder("Temporary directory for CSV in bulk-load"));
		this.setMaximumSize(new Dimension(Integer.MAX_VALUE, 600));

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		Component verticalStrut = Box.createVerticalStrut(5);
		GridBagConstraints gbc_verticalStrut = new GridBagConstraints();
		gbc_verticalStrut.insets = new Insets(0, 0, 5, 5);
		gbc_verticalStrut.gridx = 0;
		gbc_verticalStrut.gridy = 0;
		add(verticalStrut, gbc_verticalStrut);

		JLabel label = new JLabel("Path:");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.insets = new Insets(0, 0, 5, 5);
		gbc_label.anchor = GridBagConstraints.EAST;
		gbc_label.gridx = 0;
		gbc_label.gridy = 1;
		add(label, gbc_label);

		csvTextField = new JTextField();
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 0);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 1;
		add(csvTextField, gbc_textField);
		csvTextField.setColumns(6);

		JButton bdbPathButton = new JButton("...");
		bdbPathButton.addActionListener(this);
		GridBagConstraints gbc_bdbPathButton = new GridBagConstraints();
		gbc_bdbPathButton.insets = new Insets(0, 0, 5, 0);
		gbc_bdbPathButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_bdbPathButton.gridx = 2;
		gbc_bdbPathButton.gridy = 1;
		add(bdbPathButton, gbc_bdbPathButton);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			File dir = SwingUtil.getDirectoryChoice(this, "", "Select a temporary directory for CSV files in bulk-load");
			if (dir != null) {
				config.setCsvPath(dir.getCanonicalPath());
				csvTextField.setText(dir.getCanonicalPath());
				this.write();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public boolean read() {
		String csvPath = csvTextField.getText();
		if (Utility.isEmpty(csvPath)){
			System.err.printf("%s is not set!\n", "CSV path");
			return false;
		} else {
			config.setCsvPath(csvPath);
		}
		return true;
	}


	@Override
	public void write() {
		csvTextField.setText(config.getCsvPath());
	}

	@Override
	public void clear() {
		csvTextField.setText("");
	}
}
