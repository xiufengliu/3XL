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

package xlclient.gui.opt.dbgen;

import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.text.DefaultEditorKit;

import xlclient.common.Configure;
import xlclient.common.Utility;
import xlclient.gui.CompOperations;
import xlclient.gui.PopupMouseListener;
import xlclient.gui.SwingUtil;
import xlclient.gui.setting.OntologyJPanel;

public class OntologyJTextPane extends JTextPane implements CompOperations {
	private static final long serialVersionUID = 6270183148379328084L;

	// private StyledDocument doc;
	private HashMap actions;
	public final static String NAME = "OntologyJTextPane";

	public OntologyJTextPane() {
		this.setName(NAME);
		this.setEditorKitForContentType("text/xml", new XmlEditorKit());
		this.setContentType("text/xml");
		// doc = getStyledDocument();
		this.addPopupMenu();
	}

	private void addPopupMenu() {
		createActionTable();

		JPopupMenu menu = new JPopupMenu();
		menu.add(getActionByName(DefaultEditorKit.copyAction, "Copy"));
		menu.add(getActionByName(DefaultEditorKit.cutAction, "Cut"));
		menu.add(getActionByName(DefaultEditorKit.pasteAction, "Paste"));
		menu.add(getActionByName("Clear", "Clear"));
		menu.add(new JSeparator());
		menu.add(getActionByName(DefaultEditorKit.selectAllAction, "Select All"));

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
		actions.put("Clear", new ClearAction("Clear", "Clear"));
	}

	class ClearAction extends AbstractAction {
		public ClearAction(String text, String desc) {
			super(text);
			putValue(SHORT_DESCRIPTION, desc);

		}

		public void actionPerformed(ActionEvent e) {
			clear();
		}
	}

	@Override
	public void clear() {
		this.setText("");

	}

	@Override
	public boolean read() {
		if (Utility.isEmpty(this.getText())) {
			return false;
		} else {
			Configure config = Configure.getInstance();
			config.setOntText(this.getText());
			return true;
		}
	}

	@Override
	public void write() {
		this.setText(Configure.getInstance().getOntText());
	}

	public void save() {
		if (!Utility.isEmpty(this.getText())) {
			OntologyJPanel ontJPanel = (OntologyJPanel) SwingUtil.findComponent(OntologyJPanel.NAME, this);
			String ontFilePath = ontJPanel.getOntSavingPath();
			if (!Utility.isEmpty(ontFilePath)) {
				try {
					File ontfile = new File(ontFilePath);
					BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(ontfile));
					bufferedWriter.write(this.getText());
					bufferedWriter.close();
					System.out.printf("Save ontology to %s\n", ontfile.getCanonicalPath());
				} catch (IOException e) {
					System.out.printf("Failed to save to %s\n", ontFilePath);
				}
			} else {
				System.err.println("Ontology path is not set!");
			}
		}
	}
}
