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

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;


import xlclient.gui.CompOperations;

public class DataBufferJPanel extends JPanel implements CompOperations {
	private JSlider vhSlider;
	private JSlider partCmtslider;

	public DataBufferJPanel() {
		this.setBorder(BorderFactory.createTitledBorder("Data Buffer"));
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

		JLabel label = new JLabel("Buffer size (*100,000):");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.insets = new Insets(0, 0, 5, 5);
		gbc_label.anchor = GridBagConstraints.EAST;
		gbc_label.gridx = 0;
		gbc_label.gridy = 1;
		add(label, gbc_label);

		vhSlider = new JSlider(JSlider.HORIZONTAL, 0, 40, 10);
		vhSlider.setMinorTickSpacing(1);
		vhSlider.setMajorTickSpacing(10);
		vhSlider.setPaintTicks(true);
		vhSlider.setPaintLabels(true);
		vhSlider.setSnapToTicks(true);

		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 0);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 1;
		add(vhSlider, gbc_textField);

		JLabel label_1 = new JLabel("Partial Commit (%):");
		GridBagConstraints gbc_label_1 = new GridBagConstraints();
		gbc_label_1.insets = new Insets(0, 0, 5, 5);
		gbc_label_1.gridx = 0;
		gbc_label_1.gridy = 2;
		add(label_1, gbc_label_1);

		partCmtslider = new JSlider(JSlider.HORIZONTAL, 0, 100, 70);
		partCmtslider.setMinorTickSpacing(1);
		partCmtslider.setMajorTickSpacing(10);
		partCmtslider.setPaintTicks(true);
		partCmtslider.setPaintLabels(true);
		partCmtslider.setSnapToTicks(true);
		GridBagConstraints gbc_slider = new GridBagConstraints();
		gbc_slider.insets = new Insets(0, 0, 5, 0);
		gbc_slider.fill = GridBagConstraints.HORIZONTAL;
		gbc_slider.gridx = 1;
		gbc_slider.gridy = 2;
		add(partCmtslider, gbc_slider);
	}

	@Override
	public boolean read() {
		if (vhSlider.getValue() == 0) {
			System.err.printf("%s is too small!\n", "Data buffer size");
			return false;
		} else {
			config.setVhBufSize(vhSlider.getValue() * 100000);
		}
		if (partCmtslider.getValue() < 5) {
			System.err.printf("%s is too small!\n", "Partial commit percentage ");
			return false;
		} else {
			config.setVhCommitSize(partCmtslider.getValue() / 100.0);
		}
		return true;
	}

	@Override
	public void write() {
		vhSlider.setValue(config.getVhBufSize()/ 100000);
		partCmtslider.setValue((int) (config.getVhCommitSize() * 100));
	}

	@Override
	public void clear() {
		vhSlider.setValue(10);
		partCmtslider.setValue(70);
	}

}
