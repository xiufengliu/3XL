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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;


public class CommandLine {

	protected String usage;

	protected Hashtable<String, Arg> args = new Hashtable<String, Arg>();
	protected List<Arg> items = new ArrayList<Arg>();

	public CommandLine() {
	}

	public void setUsage(String usageMessage) {
		usage = usageMessage;
	}

	public boolean hasArgs() {
		return args.size() > 0;
	}

	public Iterator<Arg> args() {
		return args.values().iterator();
	}

	public int numArgs() {
		return args.size();
	}

	public void process(String[] argv) throws java.lang.IllegalArgumentException {
		List<String> argList = new ArrayList<String>();
		argList.addAll(Arrays.asList(argv));

		
		for (int i=0; i < argList.size(); i++) {
			String argStr = argList.get(i);
			if (endProcessing(argStr))
				break;

			// If the flag has a "=" or :, it is long form --arg=value.
			// Split and insert the arg
			int j1 = argStr.indexOf('=');
			int j2 = argStr.indexOf(':');
			int j = Integer.MAX_VALUE;

			if (j1 > 0 && j1 < j)
				j = j1;
			if (j2 > 0 && j2 < j)
				j = j2;
			String value = null;
			if (j != Integer.MAX_VALUE) {
				value = argStr.substring(j + 1);
				argStr = argStr.substring(0, j);
			}

			argStr = argStr.substring(argStr.lastIndexOf("-") + 1);

			Arg arg = args.get(argStr);
			if (arg != null) {
				if (arg.takesValue()) {
					if (value != null) {
						items.add(arg);
						arg.setValue(value);
					} else {
						++i;
						if (i == argList.size() || argList.get(i).startsWith("-")) {
							throw new IllegalArgumentException("No value for argument: " + arg.getName());
						} else {
							items.add(arg);
							arg.setValue(argList.get(i));
						}
					}
				} else {
					items.add(arg);
				}
			}
		}
		this.checkArgs();
	}

	public void checkArgs() throws java.lang.IllegalArgumentException {
		args.values().retainAll(items);
		if (!args.isEmpty()) {
			for (String argName : args.keySet()) {
				Arg arg = args.get(argName);
				if (arg.takesValue() && arg.getValue() == null) {
					throw new IllegalArgumentException("No value for argument: " + arg.getName());
				}
			}
		} else {
			throw new IllegalArgumentException("No arguments provided! ");
		}
	}

	public boolean endProcessing(String argStr) {
		return !argStr.startsWith("-") || argStr.equals("--") || argStr.equals("-");
	}

	public boolean contains(String argName) {
		return args.containsKey(argName);
	}

	public boolean contains(Arg arg) {
		return getArg(arg) != null;
	}

	public Arg getArg(Arg arg) {
		Arg returned = null;
		for (Iterator<Arg> iter = args.values().iterator(); iter.hasNext();) {
			Arg a = iter.next();
			if (arg.matches(a))
				returned = a;
		}
		return returned;
	}

	public String getValue(String argName) {
		if (args.containsKey(argName))
			return args.get(argName).getValue();
		else
			return null;
	}



	public CommandLine add(Arg arg) {
		args.put(arg.getName(), arg);
		if (arg.takesValue()){
			items.add(arg);
		}
		return this;
	}

	public String toString(){
		String s = "";
		for (String argName: args.keySet()){
			Arg arg = args.get(argName);
			s += arg.toString() + "\n";
		}
		return s;
	}
}
