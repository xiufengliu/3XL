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

package xlsystem.common;


public abstract class XLcmd {

	protected final Arg argVerbose = new Arg("verbose");

	protected final Arg argHelp = new Arg("help");
	
	
	protected CommandLine cmdLine = new CommandLine();

	private String[] usage = new String[] { "Complain to xlsystem-dev: someone forgot the usage string" };

	protected XLcmd() {
		cmdLine.add(argVerbose);
		cmdLine.add(argHelp);
	}

	protected CommandLine getCommandLine() {
		return cmdLine;
	}

	public void init(String[] args) {
		try {
			cmdLine.process(args);
		} catch (IllegalArgumentException ex) {
			usage();
			System.exit(1);
		}

		if (cmdLine.contains(argHelp)) {
			usage();
			System.exit(0);
		}
	}

	abstract protected void exec();

	protected void setUsage(String a) {
		String[] aa = new String[] { a };
		setUsage(aa);
	}

	public void setUsage(String[] a) {
		usage = a;
	}

	protected void usage() {
		for (int i = 0; i < usage.length; i++) {
			System.err.println(usage[i]);
		}
	}
}
