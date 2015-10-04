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

import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;


import xlclient.gui.CompOperations;

public class MultiPropertyPanel extends JPanel implements CompOperations {
	private JRadioButton mpInArrayButton;
	private JRadioButton mpInTableButton;
	ButtonGroup mpGroup;

	public MultiPropertyPanel() {
		setBorder(BorderFactory.createTitledBorder("Multiproperty Values in"));
		setLayout(new FlowLayout());

		mpInArrayButton = new JRadioButton("Array", true);

		mpInTableButton = new JRadioButton("MP Table");

		mpGroup = new ButtonGroup();

		mpGroup.add(mpInArrayButton);
		mpGroup.add(mpInTableButton);

		add(mpInArrayButton);
		add(mpInTableButton);
	}

	@Override
	public boolean read() {
		config.setUseMP(this.mpInTableButton.isSelected());
		return true;
	}

	@Override
	public void write() {
		if (config.isUseMP()) {
			mpInTableButton.setSelected(true);
		}
	}

	@Override
	public void clear() {
		mpInArrayButton.setSelected(true);
	}
}
