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

package xlsystem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import xlsystem.common.Arg;
import xlsystem.common.Utility;
import xlsystem.common.Configure;
import xlsystem.common.XLException;
import xlsystem.common.XLcmd;
import xlsystem.query.QueryServer;
import api.Query;

public class QueryMain extends XLcmd {

	public static final String[] run_usage = new String[] { "Usage: QueryMain [--runmode [single|server]] [--timing] [--config=/path/to/config.xml] --query=/path/to/query.txt" };

	public static final String[] queryformat_usage = new String[] { "QueryFile format:", "1. Point-wise query: (s, p, o)",
			"    where s, p, o can be '*' to match anything, for example: (http://www.University0.edu, *, *)", "2. Composite queries, for example:", "    (?X type Professor)",
			"    (?X worksFor http://www.Department0.University0.edu)", "    (?X name ?Y1)", "    (?X emailAddress ?Y2)", "    (?X telephone ?Y3)" };

	protected final Arg configArg = new Arg(true, "config", "./config.xml");
	private Arg queryArg = new Arg(true, "query", "");
	private Arg runModeArg = new Arg(true, "runmode", "single");
	protected final Arg timingArg = new Arg("timing");

	private boolean timing = false;

	public QueryMain() {
		getCommandLine().add(configArg);
		getCommandLine().add(queryArg);
		getCommandLine().add(runModeArg);
		getCommandLine().add(timingArg);
	}

	public static void main(String[] argv) {
		QueryMain xlquery = new QueryMain();
		xlquery.setUsage(run_usage);
		xlquery.init(argv);
		xlquery.exec();
	}

	@Override
	public void exec() {
		Arg mode = cmdLine.getArg(runModeArg);
		timing = cmdLine.contains(timingArg);
		String configPath = cmdLine.getArg(configArg).getValue();
		Configure.newInstance(configPath);

		if ("server".equalsIgnoreCase(mode.getValue())) {
			new QueryServer(9000, 6, timing).serve();
		} else {
			exec1();
		}
	}

	protected void exec1() {
		BufferedReader reader = null;
		BufferedWriter writer = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(cmdLine.getArg(queryArg).getValue()))));
			writer = new BufferedWriter(new PrintWriter(System.out));
			//outputWriter = new FileWriter(new File(savePath));;
			
			new Query().query(reader, writer);
		} catch (XLException xe) {
			xe.printStackTrace();
			System.out.println(xe.getMessage());
			System.out.println(Utility.array2StrByDelim(queryformat_usage, "\n"));
		} catch (FileNotFoundException e) {
			usage();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(Utility.array2StrByDelim(queryformat_usage, "\n"));
		} finally {
			try {
				writer.close();
				reader.close();
			} catch (Exception e) {
			}
		}
	}

}
