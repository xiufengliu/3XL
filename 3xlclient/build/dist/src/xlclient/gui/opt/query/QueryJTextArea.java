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

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;

import xlclient.common.SocketCmd;
import xlclient.common.Utility;
import xlclient.gui.CompOperations;
import xlclient.gui.PopupMouseListener;
import xlclient.gui.SwingUtil;
import xlclient.gui.console.ConsoleMain;
import xlclient.gui.socket.Communicator;

public class QueryJTextArea extends JTextArea implements KeyListener, MouseListener, FocusListener, CompOperations {

	protected boolean printUsage = true;
	protected static final String[] queryformat_usage = new String[] { "Enter ';' to end a query, and press ENTER to execute. The formats are:", "1. Point-wise query: (s, p, o);",
			"    where s, p, o can be '*' to match anything, for example: (http://www.University0.edu, *, *);", "2. Composite queries, for example:", "    (?X type Professor)",
			"    (?X worksFor http://www.Department0.University0.edu)", "    (?X name ?Y1)", "    (?X emailAddress ?Y2)", "    (?X telephone ?Y3);" };

	protected final static String CMD_END = ";";
	protected final static char[] specialChars = { '\n', '\r', ' ', '\t', '>' };
	protected int prevPosition;

	protected String queryStr;
	protected Map actions;

	public QueryJTextArea() {
		this.addKeyListener(this);
		this.addMouseListener(this);
		this.addFocusListener(this);

		addShellSign();
		prevPosition = getCaretPosition();
		
		addPopupMenu();
	}


	private void addPopupMenu(){
		JPopupMenu popup =  new JPopupMenu();
		addMouseListener(new PopupMouseListener(popup, this));
		JMenuItem copy = new JMenuItem(new DefaultEditorKit.CopyAction());
		copy.setText("Copy");
		popup.add(copy);
		
		JMenuItem paste = new JMenuItem(new DefaultEditorKit.PasteAction());
		paste.setText("Paste");
		popup.add(paste);
		
		JMenuItem clear = new JMenuItem(new ClearAction());
		clear.setText("Clear");
		popup.add(clear);
		add(popup);
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		prevPosition = getCaretPosition();

	}

	@Override
	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		int curpos = getCaretPosition();
		String txt = getText();
		int pos = txt == null ? 0 : txt.lastIndexOf(CMD_END);
		if (curpos <= pos) {
			if (keyCode == KeyEvent.VK_LEFT) {
				try {
					setCaretPosition(pos + 1);
				} catch (Exception e1) {
				}

			} else if (keyCode == KeyEvent.VK_UP) {
				try {
					setCaretPosition(prevPosition);
				} catch (Exception e1) {
				}
			} else if (keyCode == KeyEvent.VK_BACK_SPACE) {

			}
		}

		if (keyCode == KeyEvent.VK_ENTER) {
			if (exec(getText())) {
				ExportResultsJPanel rxportResultsJPanel = (ExportResultsJPanel) SwingUtil.findComponent(ExportResultsJPanel.NAME, this);
				String savePath = rxportResultsJPanel.readPath();
				Communicator communicator = Communicator.getInstance();
				if (communicator != null) {// Read fields from GUI to
											// configuration.
					communicator.sendMessage(new String[] { SocketCmd.MSG_QUERY }, new boolean[] { false }, new String[] { queryStr, savePath });
				}
				addShellSign();
			}
		}
	}

	private void addShellSign() {
		try {
			Document doc = super.getDocument();
			doc.insertString(getCaretPosition(), ">", null);
		} catch (BadLocationException e1) {
		}
	}

	public boolean exec(String str) {
		char chs[] = str.toCharArray();
		int i = chs.length - 1;

		while (i >= 0 && (chs[i] == ' ' || chs[i] == '\n')) {
			i--;
		}
		if (i >= 0 && chs[i--] == ';') {
			char[] qch = new char[i + 1];
			int j = 0;
			while (i >= 0 && chs[i] != ';' && chs[i] != '>') {
				qch[j++] = chs[i--];
			}
			if (j < 7)
				return false; // length of "(*,*,*)"
			char[] aa = Arrays.copyOfRange(qch, 0, j);
			Utility.reverse(aa);
			queryStr = new String(aa);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void focusGained(FocusEvent e) {
		if (printUsage) {
			ConsoleMain console = (ConsoleMain) SwingUtil.findComponent(ConsoleMain.NAME, this);
			console.clear();
			for (String str : queryformat_usage) {
				System.out.println(str);
			}
			printUsage = false;
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean read() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void write() {
		// TODO Auto-generated method stub

	}

	@Override
	public void clear() {
		printUsage = true;
		setText(">");

	}

	@Override
	public void mouseClicked(MouseEvent e) {

		int curpos = getCaretPosition();
		String txt = getText();
		int pos = txt == null ? 0 : txt.lastIndexOf(CMD_END);
		if (curpos <= pos) {
			try {
				setCaretPosition(pos + 1);
			} catch (Exception e1) {
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	class ClearAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			setText(">");
		}
	}

}
