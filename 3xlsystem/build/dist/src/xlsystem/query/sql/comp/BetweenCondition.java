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


public final class BetweenCondition extends Condition {
	private final Value left;
	private final Value r1;
	private final Value r2;

	public BetweenCondition(Value left, Value r1, Value r2) {
		this.left = left;
		this.r1 = r1;
		this.r2 = r2;
	}

	public Value getLeft() {
		return left;
	}

	public Value getR1() {
		return r1;
	}

	public Value getR2() {
		return r2;
	}

	public StringBuilder appendTo(StringBuilder sb) {
		left.appendTo(sb);
		sb.append(" BETWEEN ");
		r1.appendTo(sb);
		sb.append(" AND ");
		r2.appendTo(sb);
		return sb;
	}
}
