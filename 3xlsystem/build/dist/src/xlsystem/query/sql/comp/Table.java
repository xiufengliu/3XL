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


public final class Table extends Atom {
    private final TableName tableName;
    private final Joined joined;

    public Table(TableName tableName) {
        this(tableName, null);
    }

    public Table(TableName tableName, Joined joined) {
        this.tableName = tableName;
        this.joined = joined;
    }

    public TableName getTableName() {
        return tableName;
    }

    public Joined getJoined() {
        return joined;
    }

    public StringBuilder appendTo(StringBuilder sb) {
        tableName.appendTo(sb);
        if (joined != null) {
            joined.appendTo(tableName.getAlias(), sb);
        }
        return sb;
    }
}
