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

import java.util.Set;

import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataMaxCardinalityRestriction;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLObjectMaxCardinalityRestriction;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLSubClassAxiom;
import org.semanticweb.owl.util.OWLOntologyWalker;
import org.semanticweb.owl.util.OWLOntologyWalkerVisitor;

import xlsystem.db.DataTypeDict;

public class OwlOntWalkerVisitor extends OWLOntologyWalkerVisitor<Object> {
	OwlOntology ont = null;
	OWLOntologyWalker walker = null;

	public OwlOntWalkerVisitor(OWLOntologyWalker walker, OwlOntology ont) {
		super(walker);
		this.ont = ont;
		this.walker = walker;
	}

	public Object visit(OWLClass desc) {
		ensureClassTable(desc.toString());
		return null;
	}

	public Object visit(OWLSubClassAxiom desc) {
		String className = desc.getSubClass().toString();
		String superClass = desc.getSuperClass().toString();
		OwlClass classTable = ensureClassTable(className);

		if (!desc.getSuperClass().isLiteral()) {
			if (superClass.indexOf("DataMaxCardinality") != -1) {
				String propertyName = String.valueOf(desc.getSuperClass().getDataPropertiesInSignature().toArray()[0]);
				Property property = ensureProperty(propertyName, classTable);
				property.setMultiProp(false);
			} else if (superClass.indexOf("ObjectMaxCardinality") != -1) {
				String propertyName = String.valueOf(desc.getSuperClass().getObjectPropertiesInSignature().toArray()[0]);
				Property property = ensureProperty(propertyName, classTable);
				property.setMultiProp(false);
			}
		} else {
			String parentName = desc.getSuperClass().toString();
			OwlClass parent = ensureClassTable(parentName);
			classTable.setParent(parent);
		}
		return null;
	}

	public Object visit(OWLDataProperty property) {
		String propertyName = property.toString();
		String dataType = String.valueOf(property.getRanges(this.ont).toArray()[0]);
		Set<OWLDescription> owlDescs = property.getDomains(this.ont);
		for (OWLDescription owlDesc : owlDescs) {
			Object[] doms = (Object[]) owlDesc.getClassesInSignature().toArray();
			for (int i = 0; i < doms.length; ++i) {
				String className = String.valueOf(doms[i]);
				OwlClass classTable = ensureClassTable(className);
				Property prop = ensureProperty(propertyName, classTable);
				prop.setDataType(DataTypeDict.convertToDBDataType(dataType));
			}
		}
		return null;
	}

	public Object visit(OWLObjectProperty property) {
		String propertyName = property.toString();
		OWLDescription owlrange = (OWLDescription) property.getRanges(this.ont).toArray()[0];
		String rangeName = String.valueOf(owlrange.getClassesInSignature().toArray()[0]);

		Set<OWLDescription> owldoms = property.getDomains(this.ont);
		for (OWLDescription owldom : owldoms) {
			Object[] doms = (Object[]) owldom.getClassesInSignature().toArray();
			for (int i = 0; i < doms.length; ++i) {
				String className = String.valueOf(doms[i]);
				OwlClass classTable = ensureClassTable(className);
				OwlClass rangeTable = ensureClassTable(rangeName);
				Property prop = ensureProperty(propertyName, classTable);
				prop.setDataType(DataTypeDict.PG_BIGINT);
				prop.setRange(rangeTable);
			}
		}
		return null;
	}

	private Property ensureProperty(String propertyName, OwlClass classTable) {
		Property property = classTable.getProperty(propertyName);
		if (property == null) {
			property = new Property(propertyName, null, classTable);
			classTable.addProperty(property);
			ont.addProperty(property);
		}
		return property;
	}

	private OwlClass ensureClassTable(String className) {
		OwlClass classTable = ont.getOwlClass(className);
		if (classTable == null) {
			classTable = new OwlClass(className);
			ont.addOwlClass(classTable);
		}
		return classTable;
	}

	public Object visit(OWLDataMaxCardinalityRestriction desc) {
		// System.out.println(desc.getProperty() + ":" +
		// desc.getProperty().getDomains(this.ont));
		return null;
	}

	public Object visit(OWLObjectMaxCardinalityRestriction desc) {
		// System.out.println(desc.getProperty() + ":" +
		// desc.getProperty().getDomains(this.ont));
		return null;
	}
}
