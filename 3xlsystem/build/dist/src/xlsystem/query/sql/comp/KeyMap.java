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

package xlsystem.query.sql.comp;


public class KeyMap extends Atom {
     final TableName leftTableName;
	 final String leftFieldName;
	 final TableName rightTableName;
	 final String rightFieldName;

    public KeyMap(TableName leftTableName, String leftFieldName, TableName rightTableName, String rightFieldName) {
        this.leftTableName = leftTableName;
    	this.leftFieldName = leftFieldName;
    	this.rightTableName = rightTableName;
        this.rightFieldName = rightFieldName;
    }

    public String getLeftFieldName() {
        return leftFieldName;
    }

    public String getRightFieldName() {
        return rightFieldName;
    }

    public StringBuilder appendTo(StringBuilder sb) {
        return appendTo(leftTableName.getAlias(), rightTableName.getAlias(), sb);
    }

    public StringBuilder appendTo(String leftAlias, String rightAlias, StringBuilder sb) {
        sb.append(leftAlias).append('.').append(leftFieldName);
        sb.append(" = ");
        sb.append(rightAlias).append('.').append(rightFieldName);
        return sb;
    }
}
