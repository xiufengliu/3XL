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

import java.applet.Applet;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.filechooser.FileFilter;

import xlclient.common.Configure;

/**
 * Copyright (C) 2004 NNL Technology AB Visit www.infonode.net for information
 * about InfoNode(R) products and how to contact NNL Technology AB.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */

// $Id: ComponentUtil.java,v 1.25 2005/12/04 13:46:04 jesper Exp $

public class SwingUtil {
	public final static String owl = "owl";
	public final static String rdf = "rdf";

	public static Component getTopLevelAncestor(Component c) {
		while (c != null) {
			if (c instanceof Window || c instanceof Applet)
				break;
			c = c.getParent();
		}
		return c;
	}

	public static List<Component> getAllChildComponents(final Container c) {
		Component[] comps = c.getComponents();
		List<Component> compList = new ArrayList<Component>();
		for (Component comp : comps) {
			compList.add(comp);
			if (comp instanceof Container) {
				compList.addAll(getAllChildComponents((Container) comp));
			}
		}
		return compList;
	}

	public static List<Component> getAllComponents(final Component anywhere) {
		Component topContainer = getTopLevelAncestor(anywhere);
		List<Component> compList = getAllChildComponents((Container) topContainer);
		compList.add(topContainer);
		return compList;
	}

	public static Component findChildComponent(String componentName, Component component) {
		Component found = null;
		if (component.getName() != null && component.getName().equals(componentName)) {
			found = component;
		} else {
			for (Component child : ((Container) component).getComponents()) {
				found = findChildComponent(componentName, child);

				if (found != null)
					break;
			}
		}
		return found;
	}

	public static Component findComponent(String componentName, Component anywhere) {
		Component top = getTopLevelAncestor(anywhere);
		return findChildComponent(componentName, top);
	}

	public static String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	public static File getDirectoryChoice(Component owner, String defaultDir, String title) {
		return getDirectoryChoice(owner, new File(defaultDir), title);
	}

	/**
	 * Open a JFileChooser dialog for selecting a directory and return the
	 * selected directory.
	 * 
	 * 
	 * @param owner
	 *            The frame or dialog that controls the invokation of this
	 *            dialog.
	 * @param defaultDir
	 *            The directory to show when the dialog opens.
	 * @param title
	 *            Tile for the dialog.
	 * 
	 * @return The selected directory as a File. Null if user cancels dialog
	 *         without a selection.
	 * 
	 */
	public static File getDirectoryChoice(Component owner, File defaultDir, String title) {
		//
		// There is apparently a bug in the native Windows FileSystem class that
		// occurs when you use a file chooser and there is a security manager
		// active. An error dialog is displayed indicating there is no disk in
		// Drive A:. To avoid this, the security manager is temporarily set to
		// null and then reset after the file chooser is closed.
		//
		SecurityManager sm = null;
		JFileChooser chooser = null;
		File choice = null;

		sm = System.getSecurityManager();
		System.setSecurityManager(null);
		chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		if ((defaultDir != null) && defaultDir.exists() && defaultDir.isDirectory()) {
			chooser.setCurrentDirectory(defaultDir);
			chooser.setSelectedFile(defaultDir);
		}
		chooser.setDialogTitle(title);
		chooser.setApproveButtonText("OK");
		int v = chooser.showOpenDialog(owner);

		owner.requestFocus();
		switch (v) {
		case JFileChooser.APPROVE_OPTION:
			if (chooser.getSelectedFile() != null) {
				if (chooser.getSelectedFile().exists()) {
					choice = chooser.getSelectedFile();
				} else {
					File parentFile = new File(chooser.getSelectedFile().getParent());

					choice = parentFile;
				}
			}
			break;
		case JFileChooser.CANCEL_OPTION:
		case JFileChooser.ERROR_OPTION:
		}
		chooser.removeAll();
		chooser = null;
		System.setSecurityManager(sm);
		return choice;
	}

	/**
	 * Get a file selection using the FileChooser dialog.
	 * 
	 * @param owner
	 *            The parent of this modal dialog.
	 * @param defaultSelection
	 *            The default file selection as a string.
	 * @param filter
	 *            An extension filter
	 * @param title
	 *            The caption for the dialog.
	 * 
	 * @return A selected file or null if no selection is made.
	 */
	public static File getFileChoice(Component owner, String defaultSelection, FileFilter filter, String title) {
		return SwingUtil.getFileChoice(owner, new File(defaultSelection), filter, title);
	}

	/**
	 * Get a file selection using the FileChooser dialog.
	 * 
	 * @param owner
	 *            The parent of this modal dialog.
	 * @param defaultSelection
	 *            The default file selection as a file.
	 * @param filter
	 *            An extension filter
	 * @param title
	 *            The caption for the dialog.
	 * 
	 * @return A selected file or null if no selection is made.
	 */
	public static File getFileChoice(Component owner, File defaultSelection, FileFilter filter, String title) {
		//
		// There is apparently a bug in the native Windows FileSystem class that
		// occurs when you use a file chooser and there is a security manager
		// active. An error dialog is displayed indicating there is no disk in
		// Drive A:. To avoid this, the security manager is temporarily set to
		// null and then reset after the file chooser is closed.
		//
		SecurityManager sm = null;
		File choice = null;
		JFileChooser chooser = null;

		sm = System.getSecurityManager();
		System.setSecurityManager(null);

		chooser = new JFileChooser();
		if (defaultSelection.isDirectory()) {
			chooser.setCurrentDirectory(defaultSelection);
		} else {
			chooser.setSelectedFile(defaultSelection);
		}
		chooser.setFileFilter(filter);
		chooser.setDialogTitle(title);
		chooser.setApproveButtonText("OK");
		int v = chooser.showOpenDialog(owner);

		owner.requestFocus();
		switch (v) {
		case JFileChooser.APPROVE_OPTION:
			if (chooser.getSelectedFile() != null) {
				choice = chooser.getSelectedFile();
			}
			break;
		case JFileChooser.CANCEL_OPTION:
		case JFileChooser.ERROR_OPTION:
		}
		chooser.removeAll();
		chooser = null;
		System.setSecurityManager(sm);
		return choice;
	}

	/**
	 * Get the point on point on the screen at which to open a dialog or window
	 * for it to appear centered. This point is the top right hand corner of the
	 * container you want to position.
	 * 
	 * 
	 * @param size
	 *            The demensions of the dialog or window to position.
	 * 
	 * @return The top left hand point at which to position the container for it
	 *         to appear centered.
	 * 
	 */
	public static Point getCenteringPoint(Dimension size) {
		Point centeringPoint = new Point();
		Dimension screenSize;

		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if (size.height > screenSize.height) {
			size.height = screenSize.height;
		}
		if (size.width > screenSize.width) {
			size.width = screenSize.width;
		}
		centeringPoint.x = (screenSize.width - size.width) / 2;
		centeringPoint.y = (screenSize.height - size.height) / 2;
		return centeringPoint;
	}

	public static void clearAll(Component anywhere){
		List<Component> comps = SwingUtil.getAllComponents(anywhere);
		for (Component comp : comps) {
			if (comp instanceof CompOperations) {
				CompOperations cnfElem = (CompOperations) comp;
				cnfElem.clear();
			}
		}
	}
	
	public static boolean readAll(Component anywhere){
		boolean isAllFieldsFilled = true;
		List<Component> comps = SwingUtil.getAllComponents(anywhere);
		for (Component comp : comps) {
			if (comp instanceof CompOperations) {
				CompOperations cnfElem = (CompOperations) comp;
				if (!cnfElem.read())
					isAllFieldsFilled = false;
			}
		}
		return isAllFieldsFilled;
	}
	
	public static void writeAll(Component anywhere){
		List<Component> comps = SwingUtil.getAllComponents(anywhere);
		for (Component comp : comps) {
			if (comp instanceof CompOperations) {
				CompOperations cnfElem = (CompOperations) comp;
				cnfElem.write();
			}
		}
	}
	
	public static boolean saveConfig(Configure config, Component comp) {
		try {
			config.save();
			return true;
		} catch (Exception e) {
			File configFile = SwingUtil.getFileChoice(comp, "", null, "Save config");
			if (configFile != null) {
				try {
					String configPath = configFile.getCanonicalPath();
					config.setConfigPath(configPath);
					config.save();
					JFrame mainWin = (JFrame) SwingUtil.getTopLevelAncestor(comp);
					mainWin.setTitle(configPath);
					return true;
				} catch (Exception e1) {
					System.err.println("Failed to save!");
					return false;
				}
			} else {
				return false;
			}
		}
	}
	
	public static JPopupMenu newJPopupMenu(JComponent component) {
		JPopupMenu popupMenu = new JPopupMenu();
		PopupMouseListener mouseListener = new PopupMouseListener(popupMenu, component);
		component.addMouseListener(mouseListener);
		return popupMenu;
	}

}
