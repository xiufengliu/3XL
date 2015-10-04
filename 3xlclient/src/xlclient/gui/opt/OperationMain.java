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

package xlclient.gui.opt;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import xlclient.gui.opt.dbgen.DBGenJPanel;
import xlclient.gui.opt.load.LoadJPanel;
import xlclient.gui.opt.query.QueryJPanel;

import java.awt.BorderLayout;

public class OperationMain extends JPanel {
	public OperationMain() {
		setLayout(new BorderLayout(0, 0));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		add(tabbedPane);
		
		JPanel dbGenTabbedPane = new DBGenJPanel();
		tabbedPane.addTab("DB Gen", null, dbGenTabbedPane, null);
		
		JPanel loadTabbedPane = new LoadJPanel();
		JScrollPane loadScrollPane = new JScrollPane(loadTabbedPane);
		tabbedPane.addTab("Load", null, loadScrollPane, null);
		
		JPanel queryJTabbedPane = new QueryJPanel();
		JScrollPane queryScrollPane = new JScrollPane(queryJTabbedPane);
		tabbedPane.addTab("Query", null, queryScrollPane, null);
	}

}
