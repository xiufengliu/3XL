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
import api.Load;

public class LoadMain extends XLcmd {

	public static final String[] run_usage = new String[] { "Usage: xlsystem.LoadMain  [--timing] [--config=/path/to/config.xml] --src=/path/to/ntriple" };

	protected final Arg timingArg = new Arg("timing");
	protected final Arg srcArg = new Arg(true, "src", null);
	protected final Arg configArg = new Arg(true, "config", "./config.xml");

	public LoadMain() {
		getCommandLine().add(srcArg);
		getCommandLine().add(timingArg);
		getCommandLine().add(configArg);
	}

	public static void main(String[] args) {
		LoadMain loadMain = new LoadMain();
		loadMain.setUsage(run_usage);
		loadMain.init(args);
		loadMain.exec();
	}

	@Override
	public void exec() {
		String configPath = getCommandLine().getArg(configArg).getValue();
		Configure config = Configure.newInstance(configPath);
		boolean timing = getCommandLine().contains(timingArg);
		String dataSourceDir = getCommandLine().getArg(srcArg).getValue();
		Load loader = new Load(config);
		loader.start(dataSourceDir, timing);
	}

}
