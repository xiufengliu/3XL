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


public  class BooleanCondition extends Condition {
     final Value left;
     final String op;
     final Value right;

    public BooleanCondition(Value left, String op, Value right) {
        this.left = left;
        this.op = op;
        this.right = right;
    }

    public Value getLeft() {
        return left;
    }

    public String getOp() {
        return op;
    }

    public Value getRight() {
        return right;
    }

    public StringBuilder appendTo(StringBuilder sb) {
        left.appendTo(sb);
        sb.append(' ').append(op).append(' ');
        right.appendTo(sb);
        return sb;
    }
}
