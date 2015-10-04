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

import java.util.ArrayList;
import java.util.List;

public class OwlClass {
	String name;
	List<Property> properties;
	OwlClass parent;
	List<OwlClass> children;
	
	
	public OwlClass(String name) {
		this(name, null);
	}

	public OwlClass(String name, OwlClass parent) {
		this.name = name;
		this.parent = parent;
		this.properties = new ArrayList<Property>();
		this.children = new ArrayList<OwlClass>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	
	public OwlClass getParent() {
		return parent;
	}

	public void setParent(OwlClass parent) {
		this.parent = parent;
	}

	public List<OwlClass> getChildren() {
		return children;
	}

	public void setChildren(List<OwlClass> children) {
		this.children = children;
	}

	public List<Property> getProperties() {
		return properties;
	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

	public void addProperty(Property property) {
		this.properties.add(property);
	}

	public Property getProperty(String name) {
		for (Property property : properties) {
			if (name.equalsIgnoreCase(property.getName())) {
				return property;
			}
		}
		return null;
	}

	public void addChildNode(OwlClass child) {
		child.parent = this;
		if (!children.contains(child))
			children.add(child);
	}

	public String toString() {
		String s = this.name + "(\n";
		for (Property property : properties) {
			s += property.toString();
		}
		s += ") ";
		if (this.parent != null) {
			s += "inherits " + parent.getName() + "\n";
		}
		return s;
	}
}
