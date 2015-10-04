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

import java.util.HashSet;
import java.util.Set;

/**
 * toader
 * 
 * @author xiliu
 */
public class Constants {
	
	public static final String REG_NUMBER = "[-+]?[0-9]*\\.?[0-9]+$";

	public static final int NEW = 1; // New ValueHolder
	public static final int LOADED = 2; // ValueHolder from DB or Buffer are taken
										// as OLD

	public static final String USE_MP = "Use mp";
	public static final String NS = "NS";
	public static final String OVERFLOW = "IsOverflow";
	// public static final String SCHEMA = "xlsystem";

	public static final int OBJ_SIG = 1;
	public static final int OBJ_MP_TAB = 2;
	public static final int OBJ_MP_ARR = 3;
	public static final int LIT_SIG = 4;
	public static final int LIT_MP_TAB = 5;
	public static final int LIT_MP_ARR = 6;

	public static final int TABLE_NAME = 7;
	public static final int SUBJ_IS_KNOWN = 8;
	
	
	// The followings are for the data source
	public static final String EOT = "---END OF QUERY---\n";
	
	// The Exception Message
	public static final String ILLEGAL_QUERY = "Illegal query statements!";
	
	
	public static final int OBJ = 8;
	public  static final int MLP = 4;
	public static final int MPT = 2;
	
	public static final int LIT = 6;
	public static final int SIG = 10;
	public static final int ARR = 12;
	
	public static String PATTERN = "[^A-Z^a-z^0-9]";
	public static final Set<String> PG_KEYWORDS = new HashSet<String>();
	
	
	static{
		PG_KEYWORDS.add("all");
		PG_KEYWORDS.add("and");
		PG_KEYWORDS.add("array");
		PG_KEYWORDS.add("default");
		PG_KEYWORDS.add("any");
		PG_KEYWORDS.add("column");
		PG_KEYWORDS.add("constraint");
		PG_KEYWORDS.add("default");
		PG_KEYWORDS.add("check");
		PG_KEYWORDS.add("case");
		PG_KEYWORDS.add("both");
		PG_KEYWORDS.add("binary");
		PG_KEYWORDS.add("as");
		PG_KEYWORDS.add("asc");
		PG_KEYWORDS.add("distinct");
		PG_KEYWORDS.add("desc");
		PG_KEYWORDS.add("collate");
		PG_KEYWORDS.add("type");
		PG_KEYWORDS.add("date");
		
	}
		
	
	
	public static final String  NS_PURL = "http://purl.org/dc/elements/1.1/";
	public static final String NS_RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#"; 
}
