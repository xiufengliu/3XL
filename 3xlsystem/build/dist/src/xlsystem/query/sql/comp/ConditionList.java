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

import xlsystem.common.Utility;

public final class ConditionList extends Condition implements Iterable<Condition> {
    private final Joiner joiner;
    private final List<Condition> conditions;

    public ConditionList(Joiner joiner, List<Condition> conditions) {
        this.joiner = joiner;
        this.conditions = conditions;
    }

    public void add(Condition condition) {
        conditions.add(condition);
    }

    public Joiner getJoiner() {
        return joiner;
    }

    public Iterator<Condition> iterator() {
        return conditions.iterator();
    }

    public StringBuilder appendTo(StringBuilder sb) {
        sb.append('(');
        Utility.appendTo(sb, conditions, " ", " ", joiner.toString());
        sb.append(')');
        return sb;
    }
}
