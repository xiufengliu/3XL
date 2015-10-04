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

package xlclient.gui.console;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import xlclient.gui.CompOperations;
import xlclient.gui.PopupMouseListener;

public class ConsoleMain extends JTextPane implements CompOperations, Runnable {
	public static String NAME = "ConsoleMain";
	StyledDocument doc;
	SimpleAttributeSet style;
	
	private Thread reader;
	private Thread reader2;
	private HashMap actions;

	private final PipedInputStream pin = new PipedInputStream();
	private final PipedInputStream pin2 = new PipedInputStream();

	public ConsoleMain() {
		setName(NAME);
		setEditable(false);
		doc = getStyledDocument();

		style = new SimpleAttributeSet();

		this.addPopupMenu();

		try {
			PipedOutputStream pout = new PipedOutputStream(this.pin);
			System.setOut(new PrintStream(pout, true));
		} catch (java.io.IOException io) {
			this.err("Couldn't redirect STDOUT to this console\n" + io.getMessage());

		} catch (SecurityException se) {
			this.err("Couldn't redirect STDOUT to this console\n" + se.getMessage());
		}

		try {
			PipedOutputStream pout2 = new PipedOutputStream(this.pin2);
			System.setErr(new PrintStream(pout2, true));
		} catch (java.io.IOException io) {
			this.err("Couldn't redirect STDERR to this console\n" + io.getMessage());
		} catch (SecurityException se) {
			this.err("Couldn't redirect STDERR to this console\n" + se.getMessage());
		}

		// Starting two seperate threads to read from the PipedInputStreams
		reader = new Thread(this);
		reader.setDaemon(true);
		reader.start();

		reader2 = new Thread(this);
		reader2.setDaemon(true);
		reader2.start();
	}

	public synchronized void run() {
		try {
			while (Thread.currentThread() == reader) {
				try {
					this.wait(100);
				} catch (InterruptedException ie) {
				}
				if (pin.available() != 0) {
					String message = this.readLine(pin);
					this.print(message);

				}
			}

			while (Thread.currentThread() == reader2) {
				try {
					this.wait(100);
				} catch (InterruptedException ie) {
				}
				if (pin2.available() != 0) {
					String message = this.readLine(pin2);
					this.err(message);

				}

			}
		} catch (Exception e) {
			this.err("\nConsole reports an Internal error.");
			this.err("The error is: " + e);
		}
	}

	public synchronized String readLine(PipedInputStream in) throws IOException {
		String input = "";
		do {
			int available = in.available();
			if (available == 0)
				break;
			byte b[] = new byte[available];
			in.read(b);
			input = input + new String(b, 0, b.length);
		} while (!input.endsWith("\n") && !input.endsWith("\r\n"));
		return input;
	}

	private synchronized void print(String message) {
		this.print(message, Color.BLACK);
	}

	private synchronized void err(String message) {
		this.print(message, Color.RED);
	}

	private synchronized void print(String message, Color color) {
		this.silentPrint(doc.getLength(), message, color);
	}

	private synchronized void silentPrint(int offset, String message, Color color) {
		if ("".equals(message))
			return;
		try {
			style.addAttribute(StyleConstants.CharacterConstants.Foreground, color);
			doc.insertString(offset, message, style);
		} catch (Exception e) {
		}
	}


	private void addPopupMenu() {
		createActionTable();

		JPopupMenu menu = new JPopupMenu();
		menu.add(getActionByName("Copy", "Copy"));
		menu.add(getActionByName("Clear", "Clear"));

		menu.add(new JSeparator());
		menu.add(getActionByName("Select All", "Select All"));
		add(menu);
		addMouseListener(new PopupMouseListener(menu, this));
		actions.clear();
	}

	private Action getActionByName(String name, String description) {
		Action a = (Action) (actions.get(name));
		a.putValue(Action.NAME, description);
		return a;
	}

	private void createActionTable() {
		actions = new HashMap();
		Action[] actionsArray = getActions();
		for (int i = 0; i < actionsArray.length; i++) {
			Action a = actionsArray[i];
			actions.put(a.getValue(Action.NAME), a);
		}
		actions.put("Clear", new ClearAction("Clear", "Clear the console"));
	}

	

	class ClearAction extends AbstractAction {
		public ClearAction(String text, String desc) {
			super(text);
			putValue(SHORT_DESCRIPTION, desc);

		}

		public void actionPerformed(ActionEvent e) {
			try {
				doc.remove(0, doc.getLength());
			} catch (BadLocationException e1) {

			}
		}
	}



	@Override
	public boolean read() {
		return true;
		
	}

	@Override
	public void write() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clear() {
		try {
			doc.remove(0, doc.getLength());
		} catch (BadLocationException e1) {}	
	}
}
