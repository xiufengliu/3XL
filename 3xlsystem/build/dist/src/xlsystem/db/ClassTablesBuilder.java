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

package xlsystem.db;

import java.util.ArrayList;
import java.util.List;

import xlsystem.ontology.OwlOntology;
import xlsystem.ontology.OwlClass;
import xlsystem.ontology.Property;

public class ClassTablesBuilder implements Builder {

	OwlOntology ont;
	List<DbComponent> classTables;

	public ClassTablesBuilder(OwlOntology ont) {
		this.ont = ont;
		this.classTables = new ArrayList<DbComponent>();
	}

	@Override
	public List<DbComponent> buildComponents() {
		this.createClassTable(ont.getOwlClass("thing"));
		return classTables;
	}

	private void createClassTable(OwlClass owlClass) {
		String name = owlClass.getName();
		List<Property> properties = null;
		String parent = null;
		if (owlClass.getParent()==null){
			properties = new ArrayList<Property>();
			properties.add(new Property("id", DataTypeDict.PG_BIGINT, false, owlClass));
			properties.add(new Property("uri", DataTypeDict.PG_VARCHAR, false, owlClass));
		} else {
			properties = owlClass.getProperties();
			parent = owlClass.getParent().getName();
		}
		classTables.add(new ClassTable(name, properties, parent));
		for (OwlClass child : owlClass.getChildren()) {
			createClassTable(child);
		}
	}
}
