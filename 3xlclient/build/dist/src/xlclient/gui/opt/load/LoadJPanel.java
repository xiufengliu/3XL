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

import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class LoadJPanel extends JPanel {

	
	public LoadJPanel() {
		
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		//add(Box.createRigidArea(new Dimension(0, 20)));
		JPanel bdbJPanel = new BDBJPanel();
		add(bdbJPanel);

		//add(Box.createRigidArea(new Dimension(0, 20)));
		JPanel mainMemJPanel = new DataBufferJPanel();
		add(mainMemJPanel);
		
		//add(Box.createRigidArea(new Dimension(0, 20)));
		JPanel csvTempJPanel = new CSVJPanel();
		add(csvTempJPanel);
		
		//add(Box.createRigidArea(new Dimension(0, 20)));
		JPanel dataSourceJPanel = new DataSourceJPanel();
		add(dataSourceJPanel);
		
				
		//add(Box.createRigidArea(new Dimension(0, 20)));
		JPanel saveAndLoadJPanel = new SaveAndLoadJPanel();
		add(saveAndLoadJPanel);
		
		
		
	}
}
