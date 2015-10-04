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

package api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FileUtils;

import xlsystem.common.TimeTracer;
import xlsystem.common.Configure;
import xlsystem.load.Loader;
import xlsystem.load.NTriplesParser;

public class Load {

	private static String TIMING_TOTAL_TIME = "total";
	
	public Load(Configure config) {
	}
	
	public String start(String dataSourceDir, boolean timing) {
		TimeTracer timeTracer = TimeTracer.thiz(timing);
		timeTracer.start(TIMING_TOTAL_TIME);
		try {
			Loader loader = new Loader();
			NTriplesParser ntripleParser = new NTriplesParser();
			ntripleParser.setLoader(loader);

			Collection<File> files = FileUtils.listFiles(new File(dataSourceDir), new String[] { "txt", "n3" }, true);
			List<File> fileList = new ArrayList<File>(files);
			Collections.sort(fileList, new DirAlphaComparator());
			for (File n3file : fileList) {
				FileInputStream stream = FileUtils.openInputStream(n3file);
				BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"), 16 * 1024);
				ntripleParser.parse(reader);
				reader.close();
			}
			loader.end();
			return timeTracer.end(TIMING_TOTAL_TIME, "Total time");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	class DirAlphaComparator implements Comparator<File> {
		public int compare(File filea, File fileb) {
			if (filea.isDirectory() && !fileb.isDirectory()) {
				return -1;
			} else if (!filea.isDirectory() && fileb.isDirectory()) {
				return 1;
			} else {
				return filea.getName().compareToIgnoreCase(fileb.getName());
			}
		}
	}
}
