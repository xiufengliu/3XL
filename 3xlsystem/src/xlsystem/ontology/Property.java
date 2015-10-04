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

package xlsystem.ontology;

import xlsystem.common.Utility;
import xlsystem.common.XLException;
import xlsystem.load.PropertyVisitor;

public class Property {

	String name;
	String pgName;
	String dataType;
	boolean isMultiProp;
	OwlClass domain = null;
	OwlClass range = null;
	
	
	
	public  Property(String name, String dataType,  OwlClass domain){
		this(name, dataType, true, domain);
	}
	
	public  Property(String name, String dataType, boolean isMultiProp,  OwlClass domain){
		this(name, dataType, isMultiProp, domain, null);
	}
	
	public Property(String name, String dataType, boolean isMultiProp,  OwlClass domain, OwlClass range){
		this.name = name;
		this.dataType = dataType;
		this.isMultiProp = isMultiProp;
		this.domain = domain;
		this.range = range;
		this.pgName = Utility.fmt2PGName(name);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		this.pgName = Utility.fmt2PGName(name);
	}
	
	public String getPGName() {
		return pgName;
	}

	public void setPGName(String pgName) {
		this.pgName = pgName;
	}


	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public boolean isMultiProp() {
		return isMultiProp;
	}

	public void setMultiProp(boolean isMultiProp) {
		this.isMultiProp = isMultiProp;
	}

	public boolean isObjProp() {
		return (range!=null);
	}

	public OwlClass getDomain() {
		return domain;
	}

	public void setDomain(OwlClass domain) {
		this.domain = domain;
	}

	public OwlClass getRange() {
		return range;
	}

	public void setRange(OwlClass range) {
		this.range = range;
	}
	
	public String getMPTableName(){
		return domain.getName().toLowerCase()+"_"+this.getPGName();
	}
	
	public void accept(PropertyVisitor visitor) throws XLException {
		visitor.visit(this);
	}

	public String toString(){
		/*String s= name + " " + this.dataType 
		+ "(domain="+domain.getName()+";isMultiProp="+String.valueOf(this.isMultiProp)
		+";isObjProp="+String.valueOf(this.isObjProp());
		if (this.isObjProp()) {
			s+= ";range=" + range.getName();
		}
		s+="),\n";
		return s;*/
		 return this.getPGName();
	}
}
