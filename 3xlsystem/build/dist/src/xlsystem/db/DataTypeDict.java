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

package xlsystem.db;

import java.util.Hashtable;

public class DataTypeDict {

	public final static Hashtable<String, String> typeMapper = new Hashtable<String, String>();
	public final static String DT_PREFIX = "";
	
	public final static String PG_VARCHAR = "text";
	public final static String PG_DATE = "date";
	public final static String PG_TIMESTAMP = "timestamp";
	public final static String PG_INT = "int";
	public final static String PG_BIGINT = "bigint";
	public final static String PG_FLOAT = "float";
	public final static String PG_TIME = "time";

	
	static {
		typeMapper.put(DT_PREFIX + "string", PG_VARCHAR);
		typeMapper.put(DT_PREFIX + "date",  PG_DATE);
		typeMapper.put(DT_PREFIX + "dateTime", PG_TIMESTAMP);
		typeMapper.put(DT_PREFIX + "int", PG_INT);
		typeMapper.put(DT_PREFIX + "float", PG_FLOAT);
		typeMapper.put(DT_PREFIX + "bigint", PG_BIGINT);
		typeMapper.put(DT_PREFIX + "time", PG_TIME);
		typeMapper.put(DT_PREFIX + "varchar", PG_VARCHAR);
		typeMapper.put(DT_PREFIX + "nonnegativeinteger", PG_INT);
	}

	public static String convertToDBDataType(String dataType){
		return  typeMapper.get(dataType);
	}
	
	public static boolean containOWLDataType(String dataType){
		return  typeMapper.containsKey(dataType);
	}
	
	public static boolean isNumbericType(String str){
		return PG_BIGINT.equals(str) || PG_INT.equals(str) || PG_FLOAT.equals(str);
	}
}
