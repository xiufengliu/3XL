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

package xlsystem.query;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xlsystem.common.RDF;
import xlsystem.common.Utility;

public class Parser {

	private static final String TRIPLE_REGEX = "([^, ()]+)";
	private static final String QUERY_REGEX = "(\\([^()]*\\))";

	public static List<RDF> getQueries(BufferedReader reader) throws IOException {
		StringBuffer content = new StringBuffer();
		String line = null;
		while ((line = reader.readLine()) != null) {
			content.append(line);
		}
		List<String> triples = getTriples(content.toString());
		List<RDF> rdfs = new ArrayList<RDF>();
		List<RDF> rdfs_other = new ArrayList<RDF>();
		for (String triple : triples) {
			RDF rdf = getRdf(triple);
			String pred = Utility.removeNS2(rdf.getPre());
			if ("type".equalsIgnoreCase(pred)) {
				rdfs.add(rdf);
			} else {
				rdfs_other.add(rdf);
			}
		}
		rdfs.addAll(rdfs_other);
		return rdfs;
	}

	public static List<String> getTriples(String s) {
		Pattern pattern = Pattern.compile(QUERY_REGEX, Pattern.MULTILINE | Pattern.UNIX_LINES);
		Matcher matcher = pattern.matcher(s);
		List<String> triples = new ArrayList<String>();
		while (matcher.find()) {
			String match = matcher.group();
			triples.add(match);
		}
		return triples;
	}

	public static RDF getRdf(String s) {
		Pattern pattern = Pattern.compile(TRIPLE_REGEX);
		Matcher matcher = pattern.matcher(s);
		String[] elems = new String[3];
		int i = 0;
		while (matcher.find()) {
			String e = matcher.group();
			elems[i++] = e.trim();
		}
		return new RDF( elems[0],elems[1], elems[2]);
	}



	public static void main(String[] args) {
	//	Parser parser = new Parser();
	}
}
