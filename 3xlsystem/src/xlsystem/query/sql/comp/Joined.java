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

import java.util.Iterator;
import java.util.List;


public final class Joined extends Atom implements Iterable<KeyMap> {
    private final boolean isOptional;
    private final TableName tableName;
    private final List<KeyMap> keyMaps;
    private final Joined joined;

    public Joined(boolean isOptional, TableName tableName, List<KeyMap> keyMaps) {
        this(isOptional, tableName, keyMaps, null);
    }

    public Joined(boolean isOptional, TableName tableName, List<KeyMap> keyMaps, Joined joined) {
        this.isOptional = isOptional;
        this.tableName = tableName;
        this.keyMaps = keyMaps;
        this.joined = joined;
    }

    public boolean isOptional() {
        return isOptional;
    }

    public TableName getTableName() {
        return tableName;
    }

    public Iterator<KeyMap> iterator() {
        return keyMaps.iterator();
    }

    public Joined getJoined() {
        return joined;
    }

    public StringBuilder appendTo(StringBuilder sb) {
        return appendTo("left", sb);
    }

    public StringBuilder appendTo(String leftAlias, StringBuilder sb) {
        sb.append(isOptional ? " LEFT JOIN " : " JOIN ");
        return appendToRest(leftAlias, sb);
    }

    public StringBuilder appendToRest(String leftAlias, StringBuilder sb) {
        tableName.appendTo(sb);
        sb.append(" ON ");
        for (int i = 0; i < keyMaps.size(); i++) {
            if (i != 0) sb.append(" AND ");
            keyMaps.get(i).appendTo(leftAlias, tableName.getAlias(), sb);
        }
        if (joined != null) {
            joined.appendTo(tableName.getAlias(), sb);
        }
        return sb;
    }
}
