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

package xlclient.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;


public  class PopupMouseListener extends MouseAdapter {
	private JPopupMenu popup;
	private JComponent component;

	public PopupMouseListener(JPopupMenu popup, JComponent component) {
		this.popup = popup;
		this.component = component;
	}


	private void showMenuIfPopupTrigger(MouseEvent e) {
		if (e.isPopupTrigger()) {
			popup.show(component, e.getX() + 3, e.getY() + 3);
		}
	}

	public void mousePressed(MouseEvent e) {
		showMenuIfPopupTrigger(e);
	}

	public void mouseReleased(MouseEvent e) {
		showMenuIfPopupTrigger(e);
	}

}
