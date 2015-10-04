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

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

import xlclient.gui.opt.load.BDBJPanel;
import xlclient.gui.opt.load.DataBufferJPanel;
import xlclient.gui.opt.load.DataSourceJPanel;

import java.awt.BorderLayout;
import java.awt.Dimension;

public class QueryJPanel extends JPanel {

	public QueryJPanel() {
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		add(Box.createRigidArea(new Dimension(0, 20)));
		
		//JPanel runningModeJPanel = new QuerySettingJPanel();
		//add(runningModeJPanel);

		JPanel exportResultsJPanel = new ExportResultsJPanel();
		add(exportResultsJPanel);
		
		
		JScrollPane queryScrollPane = new JScrollPane(new QueryJTextArea());
		//add(queryScrollPane, BorderLayout.CENTER);
		
		
		add(queryScrollPane);

	}
}
