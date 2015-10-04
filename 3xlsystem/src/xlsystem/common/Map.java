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


import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class Map {
	
	@PrimaryKey
	private String uri;
	
	private int id;
	private String ct, oldCt;
	private boolean ctLocked;
	
	
	public Map(){}
	
	public Map(String uri, int id,  String ct, String oldCt){
		this.uri = uri;
		this.id = id;
		this.ct = ct;
		this.oldCt = oldCt;
	}
	
	public Map(String uri, int id,  String ct){
		this.uri = uri;
		this.id = id;
		this.ct = ct;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getUri() {
		return uri;
	}
	public void setCt(String ct) {
		this.ct = ct;
	}
	public String getCt() {
		return ct;
	}
	
	public void setOldCt(String oldCt) {
		this.oldCt = oldCt;
	}

	public String getOldCt() {
		return oldCt;
	}

	public void setCtLocked(boolean ctLocked) {
		this.ctLocked = ctLocked;
	}

	public boolean isCtLocked() {
		return ctLocked;
	}
	
	public boolean isOverflow(){
		return "xl_overflow".equals(ct);
	}
	
	public String toString(){
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("id=").append(id).append("\n");
		strBuilder.append("uri=").append(uri).append("\n");
		strBuilder.append("ct=").append(ct).append("\n");
		strBuilder.append("oldCt=").append(oldCt).append("\n");
		strBuilder.append("ctLocked=").append(ctLocked).append("\n");
		return strBuilder.toString();
	}
}
