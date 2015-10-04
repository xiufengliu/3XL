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
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


import xlclient.common.Utility;
import xlclient.gui.CompOperations;
import xlclient.gui.SwingUtil;

public class BDBJPanel extends JPanel implements ActionListener, CompOperations, ChangeListener {

	private JTextField bdbTextField;
	private JSlider bdbSizeSlider;
	private Dictionary sliderLabelDict;
	private int oldKey;
	private int heapMaxSizeInMB;

	public BDBJPanel() {
		this.setBorder(BorderFactory.createTitledBorder("BerkeleyDB"));
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

		JLabel label = new JLabel("DB Path:");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.insets = new Insets(0, 0, 5, 5);
		gbc_label.anchor = GridBagConstraints.EAST;
		gbc_label.gridx = 0;
		gbc_label.gridy = 1;
		add(label, gbc_label);

		bdbTextField = new JTextField();
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 0);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 1;
		add(bdbTextField, gbc_textField);
		bdbTextField.setColumns(6);

		JButton bdbPathButton = new JButton("...");
		bdbPathButton.addActionListener(this);
		GridBagConstraints gbc_bdbPathButton = new GridBagConstraints();
		gbc_bdbPathButton.insets = new Insets(0, 0, 5, 0);
		gbc_bdbPathButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_bdbPathButton.gridx = 2;
		gbc_bdbPathButton.gridy = 1;
		add(bdbPathButton, gbc_bdbPathButton);

		JLabel label_1 = new JLabel("Cache Size (MB):");
		GridBagConstraints gbc_label_1 = new GridBagConstraints();
		gbc_label_1.insets = new Insets(0, 0, 5, 5);

		gbc_label_1.gridx = 0;
		gbc_label_1.gridy = 2;
		add(label_1, gbc_label_1);

		long heapMaxSize = Runtime.getRuntime().maxMemory();
		heapMaxSizeInMB = (int) (heapMaxSize / (1024.0 * 1024.0));
		//heapMaxSizeInMB = 764;
		
		bdbSizeSlider = new JSlider(JSlider.HORIZONTAL, 64, heapMaxSizeInMB, 128);

		sliderLabelDict = new Hashtable();
		for (int i=1; i<=heapMaxSizeInMB/128; ++i){
			sliderLabelDict.put(128*i, new JLabel(Integer.toString(128*i)));	
		}
		sliderLabelDict.put(heapMaxSizeInMB, new JLabel(Integer.toString(heapMaxSizeInMB)));
		bdbSizeSlider.setLabelTable(sliderLabelDict);
		
		//bdbSizeSlider.setMinorTickSpacing(64);
		bdbSizeSlider.setMajorTickSpacing(256);
		
		bdbSizeSlider.setPaintTicks(true);
		bdbSizeSlider.setPaintLabels(true);
		bdbSizeSlider.setSnapToTicks(true);

		bdbSizeSlider.addChangeListener(this);

		GridBagConstraints gbc_slider = new GridBagConstraints();
		gbc_slider.insets = new Insets(0, 0, 5, 0);
		gbc_slider.fill = GridBagConstraints.HORIZONTAL;
		gbc_slider.gridx = 1;
		gbc_slider.gridy = 2;
		add(bdbSizeSlider, gbc_slider);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			File dir = SwingUtil.getDirectoryChoice(this, "", "Select BerkeleyDB Saved Path");
			if (dir != null) {
				config.setBdbPath(dir.getCanonicalPath());
				this.write();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	@Override
	public boolean read() {
		String bdbPath = bdbTextField.getText();
		if (Utility.isEmpty(bdbPath)) {
			System.err.printf("%s is not set!\n", "BDB path");
			return false;
		} else {
			config.setBdbPath(bdbPath);
		}

		int bdbSize = bdbSizeSlider.getValue();
		if (bdbSize < 3) {
			System.err.printf("%s is too small!\n", "BDB cache size");
			return false;
		} else {
			config.setBdbBufSize(bdbSize);
		}
		return true;
	}

	@Override
	public void write() {
		bdbTextField.setText(config.getBdbPath());
		bdbSizeSlider.setValue(config.getBdbBufSize());
		
	}

	@Override
	public void clear() {
		bdbTextField.setText("");
		bdbSizeSlider.setValue(128);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider) e.getSource();
		int fps = (int) source.getValue();
		if (source.getValueIsAdjusting()) { // value is adjusting; just set the
											// text
//			sliderLabelDict.remove(oldKey);
//			int newKey = bdbSizeSlider.getValue();
//			sliderLabelDict.put(newKey, new JLabel(Integer.toString(newKey)));
//			sliderLabelDict.put(64, new JLabel(Integer.toString(64)));
//			sliderLabelDict.put(heapMaxSizeInMB, new JLabel(Integer.toString(heapMaxSizeInMB)));
//			bdbSizeSlider.setLabelTable(sliderLabelDict);
//			oldKey = newKey;
		}
	}
}
