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

package xlclient.gui.setting;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import xlclient.common.Utility;
import xlclient.gui.CompOperations;

public class PostgresqlJPanel extends JPanel implements CompOperations {

	private JTextField portTextField;
	private JTextField hostTextField;
	private JTextField dbTextField;
	private JTextField usernameTextField;
	private JPasswordField passwordField;

	public PostgresqlJPanel() {
		this.setBorder(BorderFactory.createTitledBorder("PostgreSQL"));
		this.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1000));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 120, 30, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 1.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		GridBagConstraints gbc_verticalGlue = new GridBagConstraints();
		gbc_verticalGlue.insets = new Insets(0, 0, 0, 5);
		gbc_verticalGlue.gridx = 0;
		gbc_verticalGlue.gridy = 0;
		add(Box.createRigidArea(new Dimension(0, 10)), gbc_verticalGlue);

		JLabel label_1 = new JLabel("Host:");
		GridBagConstraints gbc_label_1 = new GridBagConstraints();
		gbc_label_1.insets = new Insets(0, 0, 5, 5);
		gbc_label_1.anchor = GridBagConstraints.EAST;
		gbc_label_1.gridx = 0;
		gbc_label_1.gridy = 1;
		add(label_1, gbc_label_1);

		hostTextField = new JTextField();
		GridBagConstraints gbc_hostTextField = new GridBagConstraints();
		gbc_hostTextField.insets = new Insets(0, 0, 5, 5);
		gbc_hostTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_hostTextField.gridx = 1;
		gbc_hostTextField.gridy = 1;
		add(hostTextField, gbc_hostTextField);
		hostTextField.setColumns(6);

		JLabel label = new JLabel("Port:");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.anchor = GridBagConstraints.EAST;
		gbc_label.insets = new Insets(0, 0, 5, 5);
		gbc_label.gridx = 2;
		gbc_label.gridy = 1;
		add(label, gbc_label);

		portTextField = new JTextField();
		GridBagConstraints gbc_portTextField = new GridBagConstraints();
		gbc_portTextField.anchor = GridBagConstraints.NORTH;
		gbc_portTextField.insets = new Insets(0, 0, 5, 0);
		gbc_portTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_portTextField.gridx = 3;
		gbc_portTextField.gridy = 1;
		add(portTextField, gbc_portTextField);
		portTextField.setColumns(4);

		JLabel label_2 = new JLabel("Database:");
		GridBagConstraints gbc_label_2 = new GridBagConstraints();
		gbc_label_2.insets = new Insets(0, 0, 5, 5);
		gbc_label_2.anchor = GridBagConstraints.EAST;
		gbc_label_2.gridx = 0;
		gbc_label_2.gridy = 2;
		add(label_2, gbc_label_2);

		dbTextField = new JTextField();
		GridBagConstraints gbc_dbTextField = new GridBagConstraints();
		gbc_dbTextField.insets = new Insets(0, 0, 5, 5);
		gbc_dbTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_dbTextField.gridx = 1;
		gbc_dbTextField.gridy = 2;
		add(dbTextField, gbc_dbTextField);
		dbTextField.setColumns(6);

		JLabel label_3 = new JLabel("Username:");
		GridBagConstraints gbc_label_3 = new GridBagConstraints();
		gbc_label_3.anchor = GridBagConstraints.EAST;
		gbc_label_3.insets = new Insets(0, 0, 5, 5);
		gbc_label_3.gridx = 0;
		gbc_label_3.gridy = 3;
		add(label_3, gbc_label_3);

		usernameTextField = new JTextField();
		GridBagConstraints gbc_usernameTextField = new GridBagConstraints();
		gbc_usernameTextField.insets = new Insets(0, 0, 5, 5);
		gbc_usernameTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_usernameTextField.gridx = 1;
		gbc_usernameTextField.gridy = 3;
		add(usernameTextField, gbc_usernameTextField);
		usernameTextField.setColumns(6);

		JLabel label_4 = new JLabel("Password:");
		GridBagConstraints gbc_label_4 = new GridBagConstraints();
		gbc_label_4.insets = new Insets(0, 0, 5, 5);
		gbc_label_4.anchor = GridBagConstraints.EAST;
		gbc_label_4.gridx = 0;
		gbc_label_4.gridy = 4;
		add(label_4, gbc_label_4);

		passwordField = new JPasswordField();
		GridBagConstraints gbc_passwordField = new GridBagConstraints();
		gbc_passwordField.insets = new Insets(0, 0, 5, 5);
		gbc_passwordField.fill = GridBagConstraints.HORIZONTAL;
		gbc_passwordField.gridx = 1;
		gbc_passwordField.gridy = 4;
		add(passwordField, gbc_passwordField);

		GridBagConstraints gbc_verticalGluebottom = new GridBagConstraints();
		gbc_verticalGlue.insets = new Insets(0, 0, 0, 5);
		gbc_verticalGlue.gridx = 0;
		gbc_verticalGlue.gridy = 5;
		add(Box.createRigidArea(new Dimension(0, 10)), gbc_verticalGlue);
	}

	@Override
	public boolean read() {
		boolean isFilled = checkAndReadValue(config.DB_HOST, hostTextField, "Postgresql host");
		
		isFilled = isFilled & checkAndReadValue(config.DB_PORT, this.portTextField, "Postgresql port");
		
		isFilled = isFilled & checkAndReadValue(config.DB_NAME, this.dbTextField, "Postgresql dbname");
		
		isFilled = isFilled & checkAndReadValue(config.DB_USERNAME, this.usernameTextField, "Postgresql username");
		
		isFilled = isFilled & checkAndReadValue(config.DB_PASSWORD, this.passwordField, "Postgresql password");
		if (isFilled){
			config.setDbDriver("org.postgresql.Driver");
			config.setDbUrl("jdbc:postgresql://"+config.getDbHost()+":"+config.getDbPort()+"/"+config.getDbName());
		}
		return isFilled;
	}
	
	private boolean checkAndReadValue(String name, JTextField txtField, String message){
		if (Utility.isEmpty(txtField.getText())){
			System.err.printf("%s is not set!\n", message);
			return false;
		} else {
			config.setProperty(name, txtField.getText());
		}
		return true;
	}

	@Override
	public void write() {
		this.hostTextField.setText(config.getDbHost());
		this.portTextField.setText(config.getDbPort());
		this.dbTextField.setText(config.getDbName());
		this.usernameTextField.setText(config.getDbUsername());
		this.passwordField.setText(config.getDbPassword());
	}

	@Override
	public void clear() {
		this.hostTextField.setText("");
		this.portTextField.setText("");
		this.dbTextField.setText("");
		this.usernameTextField.setText("");
		this.passwordField.setText("");
	}

}
