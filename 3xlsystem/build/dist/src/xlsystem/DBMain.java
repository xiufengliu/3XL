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

import xlsystem.common.Arg;
import xlsystem.common.Configure;
import xlsystem.common.XLcmd;
import api.GenSchema;

public class DBMain extends XLcmd {

	public static final String[] run_usage = new String[] { "Usage: xlsystem.DBMain [--verbose] [--config=/path/to/config.xml]" };
	protected final Arg configArg = new Arg(true, "config", "./config.xml");
	protected final Arg verboseArg = new Arg("verbose");

	public DBMain() {
		getCommandLine().add(configArg);
		getCommandLine().add(verboseArg);
	}

	@Override
	protected void exec() {
		Configure config = Configure.newInstance(getCommandLine().getArg(configArg).getValue());
		GenSchema genSchema = new GenSchema(config);
		genSchema.transformOwlToDbSchema();
		genSchema.createSchemaInDatabase();
		if (getCommandLine().contains(verboseArg)) {
			System.out.println(genSchema.toString());
		}
	}

	public static void main(String[] args) {
		DBMain dbMain = new DBMain();
		dbMain.setUsage(run_usage);
		dbMain.init(args);
		dbMain.exec();
	}
}
