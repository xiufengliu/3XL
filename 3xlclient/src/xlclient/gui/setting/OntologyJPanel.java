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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import xlclient.common.Utility;
import xlclient.gui.CompOperations;
import xlclient.gui.OWLRDFFilter;
import xlclient.gui.SwingUtil;
import xlclient.gui.opt.dbgen.OntologyJTextPane;

public class OntologyJPanel extends JPanel implements ActionListener, CompOperations {

	private final static long serialVersionUID = 1L;
	private JTextField owlfileTextField;
	private JFileChooser fc;
	public final static String NAME = "OntologyJPanel";

	public OntologyJPanel() {
		this.setName(NAME);
		this.setBorder(BorderFactory.createTitledBorder("OWL Lite Ontology"));
		this.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));

		setLayout(new BorderLayout());

		owlfileTextField = new JTextField();

		add(owlfileTextField, BorderLayout.CENTER);

		JButton openOWLButton = new JButton("Load...");
		openOWLButton.addActionListener(this);
		add(openOWLButton, BorderLayout.EAST);

		Component verticalStrutNorth = Box.createVerticalStrut(10);
		add(verticalStrutNorth, BorderLayout.NORTH);
		Component verticalStrut = Box.createVerticalStrut(10);
		add(verticalStrut, BorderLayout.SOUTH);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if (fc == null) {
				fc = new JFileChooser();
				fc.addChoosableFileFilter(new OWLRDFFilter());
				fc.setAcceptAllFileFilterUsed(false);
			}
			// Show it.
			if (fc.showDialog(this, "Select") == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				BufferedReader bufReader = new BufferedReader(new FileReader(file));
				StringBuilder ontTxtBld = new StringBuilder();
				String line = "";
				while ((line = bufReader.readLine()) != null) {
					ontTxtBld.append(line).append("\n");
				}
				bufReader.close();
				config.setOntText(ontTxtBld.toString());
				OntologyJTextPane ontTextPane = (OntologyJTextPane) SwingUtil.findComponent(OntologyJTextPane.NAME, this);
				ontTextPane.write();
				owlfileTextField.setText(file.getCanonicalPath());
			}
			fc.setSelectedFile(null);
		} catch (IOException e1) {
			System.err.print(e1.getMessage());
		}
	}

	@Override
	public boolean read() {
		return true;
	}

	@Override
	public void write() {

	}

	@Override
	public void clear() {
		owlfileTextField.setText("");
	}

	public String getOntSavingPath() {
		return owlfileTextField.getText();
	}
}
