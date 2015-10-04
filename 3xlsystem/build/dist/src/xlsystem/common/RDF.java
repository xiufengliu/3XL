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

import java.io.Serializable;

public class RDF implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String sub;
	protected String pre, noPrefixPre;


	protected String obj;
	protected boolean isObjLiteral = false;
	

	public RDF(String sub, String pre, String obj) {
		this.sub = sub;
		this.pre = pre;
		this.obj = obj;
	}

	public RDF() {
		// TODO Auto-generated constructor stub
	}

	public String getSub() {
		return sub;
	}

	public void setSub(String sub) {
		this.sub = sub;
	}

	public String getPre() {
		return pre;
	}

	public void setPre(String pre) {
		this.pre = pre;
	}

	public String getObj() {
		return obj;
	}

	public void setObj(String obj) {
		this.obj = obj;
	}
	
	
	public String getNoPrefixPre() {
		return noPrefixPre;
	}

	public void setNoPrefixPre(String noPrefixPre) {
		this.noPrefixPre = noPrefixPre;
	}

	
	public void setObjIsLiteral(boolean isObjLiteral){
		this.isObjLiteral = isObjLiteral;
	}
	
	
	public boolean isObjLiteral(){
		return isObjLiteral;
	} 
	

	public String toString(){
		return "("+sub+","+ pre+","+ obj+")";
	}
}
