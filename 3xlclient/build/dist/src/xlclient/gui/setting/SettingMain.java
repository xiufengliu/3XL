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

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;

public class SettingMain extends JPanel {


	public SettingMain() {
		
		//setBorder();
		TitledBorder border = new TitledBorder(
                new LineBorder(Color.blue),
                "3XL Triple-Store Settings",
                TitledBorder.CENTER,
                TitledBorder.BELOW_TOP);
		border.setTitleColor(Color.black);
		//Border border = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		this.setBorder(border);
		
		
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));


		add(Box.createRigidArea(new Dimension(0, 20)));
		JPanel ontJPanel = new OntologyJPanel();
		add(ontJPanel);

		add(Box.createRigidArea(new Dimension(0, 5)));
		JPanel dbJPanel = new PostgresqlJPanel();
		add(dbJPanel);
		add(Box.createRigidArea(new Dimension(0, 5)));
		JPanel bdbJPanel = new MultiPropertyPanel();
		add(bdbJPanel);
		
	}

}
