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

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.semanticweb.owl.model.AxiomType;
import org.semanticweb.owl.model.OWLAnnotationAxiom;
import org.semanticweb.owl.model.OWLAntiSymmetricObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLAxiomAnnotationAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLClassAssertionAxiom;
import org.semanticweb.owl.model.OWLClassAxiom;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLDataPropertyAxiom;
import org.semanticweb.owl.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owl.model.OWLDataPropertyExpression;
import org.semanticweb.owl.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLDataSubPropertyAxiom;
import org.semanticweb.owl.model.OWLDeclarationAxiom;
import org.semanticweb.owl.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owl.model.OWLDisjointClassesAxiom;
import org.semanticweb.owl.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owl.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owl.model.OWLDisjointUnionAxiom;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLEntityAnnotationAxiom;
import org.semanticweb.owl.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owl.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owl.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owl.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owl.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLImportsDeclaration;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLIndividualAxiom;
import org.semanticweb.owl.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owl.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLLogicalAxiom;
import org.semanticweb.owl.model.OWLNamedObjectVisitor;
import org.semanticweb.owl.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyChainSubPropertyAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyExpression;
import org.semanticweb.owl.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLObjectSubPropertyAxiom;
import org.semanticweb.owl.model.OWLObjectVisitor;
import org.semanticweb.owl.model.OWLObjectVisitorEx;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyAnnotationAxiom;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.OWLPropertyAxiom;
import org.semanticweb.owl.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLSameIndividualsAxiom;
import org.semanticweb.owl.model.OWLSubClassAxiom;
import org.semanticweb.owl.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owl.model.SWRLRule;
import org.semanticweb.owl.model.UnknownOWLOntologyException;


import xlsystem.common.Constants;
import xlsystem.common.Configure;

public class OwlOntology implements OWLOntology {
	Map<String, List<Property>> propName2Props = null; // propertyName: the
														// properties of the
														// same propertyName
	Map<String, OwlClass> className2Class = null; // owlClassName: OwlClass

	OWLOntology ont = null;
	Map<String, List<Property>> className2Props = null; // owlCalssName:All the
														// properties including
														// the inherited.
	MultiValueMap className2ParentNames = null;
	MultiKeyMap classNamePropName2Prop = null; // (className, propName): Prop

	public OwlOntology(OWLOntology ont) {
		this.propName2Props = new HashMap<String, List<Property>>(10, 0.9f);
		this.className2Class = new HashMap<String, OwlClass>();

		this.ont = ont;
		this.className2Props = new HashMap<String, List<Property>>();
		className2ParentNames = MultiValueMap.decorate(new HashMap<String, Set<String>>(), HashSet.class);
		classNamePropName2Prop = new MultiKeyMap();
	}

	public void addOwlClass(OwlClass owlClass) {
		className2Class.put(owlClass.getName().toLowerCase(), owlClass);
	}

	public OwlClass getOwlClass(String name) {
		return this.className2Class.get(name.toLowerCase());
	}

	public boolean isOwlClassDefined(String name){
		return  this.className2Class.get(name)!=null;
	}
	
	public boolean containOwlClass(String name) {
		return this.className2Class.containsKey(name.toLowerCase());
	}

	public Map<String, OwlClass> getOwlClassesMap() {
		return className2Class;
	}

	public Collection<String> getPropertyNames() {
		return propName2Props.keySet();
	}

	public Collection<String> getClassTableNames() {
		return className2Class.keySet();
	}

	public Collection<OwlClass> getClassTables() {
		return className2Class.values();
	}

	public void setClassTablesMap(Map<String, OwlClass> owlClassesMap) {
		this.className2Class = owlClassesMap;
	}

	public void addProperty(Property property) {
		List<Property> properties = propName2Props.get(property.getPGName());
		if (properties == null) {
			properties = new ArrayList<Property>();
		}
		properties.add(property);
		propName2Props.put(property.getPGName(), properties);
	}

	public boolean isObjProp(String owlClassName, String propertyName) {
		Property prop = (Property) this.classNamePropName2Prop.get(owlClassName.toLowerCase(), propertyName.toLowerCase());
		if (prop != null)
			return prop.isObjProp();
		return false;
	}

	public boolean isDefined(String propertyName) {
		return this.propName2Props.containsKey(propertyName.toLowerCase());
	}

	public List<String> getClassTableNames(String propertyName) {
		if (propName2Props.containsKey(propertyName)) {
			List<Property> props = propName2Props.get(propertyName);
			List<String> classTableNames = new ArrayList<String>();
			for (Property prop : props) {
				classTableNames.add(prop.getDomain().getName().toLowerCase());
			}
			return classTableNames;
		} else {
			return null;
		}
	}
	
	public boolean moreSpecific(String owlClassName1, String owlClassName2) {
		return className2ParentNames.getCollection(owlClassName1).contains(owlClassName2);
	}

	public Collection<String> getDomains(String propertyName) {
		Set<String> domains = new HashSet<String>();
		for (Property property : propName2Props.get(propertyName.toLowerCase())) {
			domains.add(property.getDomain().getName().toLowerCase());
		}
		return domains;
	}

	public String getDomain(String propertyName, String rangeOwlClassName) {
		for (Property property : propName2Props.get(propertyName.toLowerCase())) {
			if (property.getRange() != null && property.getRange().getName().equalsIgnoreCase(rangeOwlClassName)) {
				return property.getDomain().getName();
			}
		}
		return null;
	}

	public String getRange(String owlClassName, String propertyName) {
		Property prop = (Property) this.classNamePropName2Prop.get(owlClassName.toLowerCase(), propertyName.toLowerCase());
		if (prop != null) {
			return prop.getRange().getName().toLowerCase();
		}
		return null;
	}

	public void beautify() {
		Collection<OwlClass> owlClasses = this.className2Class.values();
		for (OwlClass owlClass : owlClasses) {
			OwlClass parent = owlClass.getParent();
			if (parent != null) {
				parent.addChildNode(owlClass);
			}
		}

		for (OwlClass owlClass : owlClasses) {
			String className = owlClass.getName().toLowerCase();
			List<Property> allProps = new ArrayList<Property>();
			travel(owlClass, allProps, this.className2ParentNames);
			className2Props.put(className, allProps);
			for (Property prop : allProps) {
				classNamePropName2Prop.put(className, prop.getPGName(), prop);
			}
		}

		this.ont = null;
	}

	protected void travel(OwlClass owlClass, List<Property> allProps, MultiValueMap className2ParentNames) {
		OwlClass parent = owlClass.getParent();
		if (parent != null) {
			travel(parent, allProps, className2ParentNames);
			className2ParentNames.put(owlClass.getName().toLowerCase(), parent.getName().toLowerCase());
		}
		allProps.addAll(owlClass.getProperties());
	}

	@SuppressWarnings("unchecked")
	public List<Property> getPropsByOwlClassName(String name) {
		return className2Props.get(name.toLowerCase());
	}

	public String getMPTabName(String owlClassName, String propertyName) {
		Property prop = (Property) this.classNamePropName2Prop.get(owlClassName.toLowerCase(), propertyName.toLowerCase());
		if (prop != null && prop.isMultiProp()) {
			return prop.getDomain().getName().toLowerCase() + "_" + propertyName.toLowerCase();
		}
		return null;
	}

	public boolean isMPProp(String owlClassName, String propertyName) {
		Property prop = (Property) this.classNamePropName2Prop.get(owlClassName.toLowerCase(), propertyName.toLowerCase());
		if (prop != null) {
			return prop.isMultiProp();
		}
		return false;
	}

	public int getPropType(String owlClassName, String propertyName) {
		Property prop = (Property) this.classNamePropName2Prop.get(owlClassName.toLowerCase(), propertyName.toLowerCase());
		if (prop != null) {
			return getPropType(prop);
		}
		return -1;
	}

	public int getPropType(Property property) {
		boolean isUsingMultiPropTable = Configure.getInstance().isUseMP();
		if (property.isMultiProp()) {// Multi-valued Property
			if (isUsingMultiPropTable) {
				if (property.isObjProp()) {
					return Constants.OBJ_MP_TAB;
				} else {
					return Constants.LIT_MP_TAB;
				}
			} else {
				if (property.isObjProp()) {
					return Constants.OBJ_MP_ARR;
				} else {
					return Constants.LIT_MP_ARR;
				}
			}
		} else {// Singular-valued Property
			if (property.isObjProp()) {
				return Constants.OBJ_SIG;
			} else {
				return Constants.LIT_SIG;
			}
		}
	}

	public Collection<Property> getPropertiesByType(String owlClassName, Collection<Integer> types) {
		Set<Property> propsByType = new HashSet<Property>();
		List<Property> properties = this.className2Props.get(owlClassName.toLowerCase());
		for (Property prop : properties) {
			for (int type : types) {
				if (type == getPropType(prop)) {
					propsByType.add(prop);
				}
			}
		}
		return propsByType;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		this.print(buf, className2Class.get("thing"));
		return buf.toString();
	}

	private void print(StringBuffer buf, OwlClass owlClass) {
		buf.append(owlClass.toString()).append("\n");
		for (OwlClass child : owlClass.getChildren()) {
			print(buf, child);
		}
	}

	@Override
	public boolean containsAxiom(OWLAxiom arg0) {
		return ont.containsAxiom(arg0);
	}

	@Override
	public boolean containsClassReference(URI arg0) {
		return ont.containsClassReference(arg0);
	}

	@Override
	public boolean containsDataPropertyReference(URI arg0) {
		return ont.containsDataPropertyReference(arg0);
	}

	@Override
	public boolean containsDataTypeReference(URI arg0) {

		return ont.containsDataTypeReference(arg0);
	}

	@Override
	public boolean containsEntityDeclaration(OWLEntity arg0) {

		return ont.containsEntityDeclaration(arg0);
	}

	@Override
	public boolean containsEntityReference(OWLEntity arg0) {

		return ont.containsEntityReference(arg0);
	}

	@Override
	public boolean containsIndividualReference(URI arg0) {

		return ont.containsIndividualReference(arg0);
	}

	@Override
	public boolean containsObjectPropertyReference(URI arg0) {

		return ont.containsObjectPropertyReference(arg0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<OWLAnnotationAxiom> getAnnotationAxioms() {

		return ont.getAnnotationAxioms();
	}

	@Override
	public Set<URI> getAnnotationURIs() {

		return ont.getAnnotationURIs();
	}

	@Override
	public Set<OWLAxiomAnnotationAxiom> getAnnotations(OWLAxiom arg0) {

		return ont.getAnnotations(arg0);
	}

	@Override
	public Set<OWLOntologyAnnotationAxiom> getAnnotations(OWLOntology arg0) {

		return ont.getAnnotations(arg0);
	}

	@SuppressWarnings("deprecation")
	@Override
	public OWLAntiSymmetricObjectPropertyAxiom getAntiSymmetricObjectPropertyAxiom(OWLObjectPropertyExpression arg0) {

		return ont.getAntiSymmetricObjectPropertyAxiom(arg0);
	}

	@Override
	public OWLAntiSymmetricObjectPropertyAxiom getAsymmetricObjectPropertyAxiom(OWLObjectPropertyExpression arg0) {

		return ont.getAsymmetricObjectPropertyAxiom(arg0);
	}

	@Override
	public int getAxiomCount() {

		return ont.getAxiomCount();
	}

	@Override
	public <T extends OWLAxiom> int getAxiomCount(AxiomType<T> arg0) {

		return ont.getAxiomCount(arg0);
	}

	@Override
	public Set<OWLAxiom> getAxioms() {

		return ont.getAxioms();
	}

	@Override
	public <T extends OWLAxiom> Set<T> getAxioms(AxiomType<T> arg0) {

		return ont.getAxioms(arg0);
	}

	@Override
	public Set<OWLClassAxiom> getAxioms(OWLClass arg0) {

		return ont.getAxioms(arg0);
	}

	@Override
	public Set<OWLObjectPropertyAxiom> getAxioms(OWLObjectPropertyExpression arg0) {

		return ont.getAxioms(arg0);
	}

	@Override
	public Set<OWLDataPropertyAxiom> getAxioms(OWLDataProperty arg0) {

		return ont.getAxioms(arg0);
	}

	@Override
	public Set<OWLIndividualAxiom> getAxioms(OWLIndividual arg0) {

		return ont.getAxioms(arg0);
	}

	@Override
	public Set<OWLClassAssertionAxiom> getClassAssertionAxioms(OWLIndividual arg0) {

		return ont.getClassAssertionAxioms(arg0);
	}

	@Override
	public Set<OWLClassAssertionAxiom> getClassAssertionAxioms(OWLClass arg0) {

		return ont.getClassAssertionAxioms(arg0);
	}

	@Override
	public Set<OWLClassAxiom> getClassAxioms() {

		return ont.getClassAxioms();
	}

	@Override
	public Set<OWLDataPropertyAssertionAxiom> getDataPropertyAssertionAxioms(OWLIndividual arg0) {

		return ont.getDataPropertyAssertionAxioms(arg0);
	}

	@Override
	public Set<OWLPropertyAxiom> getDataPropertyAxioms() {

		return ont.getDataPropertyAxioms();
	}

	@Override
	public Set<OWLDataPropertyDomainAxiom> getDataPropertyDomainAxioms(OWLDataProperty arg0) {

		return ont.getDataPropertyDomainAxioms(arg0);
	}

	@Override
	public Set<OWLDataPropertyRangeAxiom> getDataPropertyRangeAxiom(OWLDataProperty arg0) {

		return ont.getDataPropertyRangeAxiom(arg0);
	}

	@Override
	public Set<OWLDataSubPropertyAxiom> getDataSubPropertyAxiomsForLHS(OWLDataProperty arg0) {

		return ont.getDataSubPropertyAxiomsForLHS(arg0);
	}

	@Override
	public Set<OWLDataSubPropertyAxiom> getDataSubPropertyAxiomsForRHS(OWLDataPropertyExpression arg0) {

		return ont.getDataSubPropertyAxiomsForRHS(arg0);
	}

	@Override
	public Set<OWLDeclarationAxiom> getDeclarationAxioms() {

		return ont.getDeclarationAxioms();
	}

	@Override
	public Set<OWLDeclarationAxiom> getDeclarationAxioms(OWLEntity arg0) {

		return ont.getDeclarationAxioms(arg0);
	}

	@Override
	public Set<OWLDifferentIndividualsAxiom> getDifferentIndividualAxioms(OWLIndividual arg0) {

		return ont.getDifferentIndividualAxioms(arg0);
	}

	@Override
	public Set<OWLDisjointClassesAxiom> getDisjointClassesAxioms(OWLClass arg0) {

		return ont.getDisjointClassesAxioms(arg0);
	}

	@Override
	public Set<OWLDisjointDataPropertiesAxiom> getDisjointDataPropertiesAxiom(OWLDataProperty arg0) {

		return ont.getDisjointDataPropertiesAxiom(arg0);
	}

	@Override
	public Set<OWLDisjointObjectPropertiesAxiom> getDisjointObjectPropertiesAxiom(OWLObjectPropertyExpression arg0) {

		return ont.getDisjointObjectPropertiesAxiom(arg0);
	}

	@Override
	public Set<OWLDisjointUnionAxiom> getDisjointUnionAxioms(OWLClass arg0) {

		return ont.getDisjointUnionAxioms(arg0);
	}

	@Override
	public Set<OWLEntityAnnotationAxiom> getEntityAnnotationAxioms(OWLEntity arg0) {

		return ont.getEntityAnnotationAxioms(arg0);
	}

	@Override
	public Set<OWLEquivalentClassesAxiom> getEquivalentClassesAxioms(OWLClass arg0) {

		return ont.getEquivalentClassesAxioms(arg0);
	}

	@Override
	public Set<OWLEquivalentDataPropertiesAxiom> getEquivalentDataPropertiesAxiom(OWLDataProperty arg0) {

		return ont.getEquivalentDataPropertiesAxiom(arg0);
	}

	@Override
	public Set<OWLEquivalentObjectPropertiesAxiom> getEquivalentObjectPropertiesAxioms(OWLObjectPropertyExpression arg0) {

		return ont.getEquivalentObjectPropertiesAxioms(arg0);
	}

	@Override
	public OWLFunctionalDataPropertyAxiom getFunctionalDataPropertyAxiom(OWLDataPropertyExpression arg0) {

		return ont.getFunctionalDataPropertyAxiom(arg0);
	}

	@Override
	public OWLFunctionalObjectPropertyAxiom getFunctionalObjectPropertyAxiom(OWLObjectPropertyExpression arg0) {

		return ont.getFunctionalObjectPropertyAxiom(arg0);
	}

	@Override
	public Set<OWLClassAxiom> getGeneralClassAxioms() {

		return ont.getGeneralClassAxioms();
	}

	@Override
	public Set<OWLOntology> getImports(OWLOntologyManager arg0) throws UnknownOWLOntologyException {

		return ont.getImports(arg0);
	}

	@Override
	public Set<OWLImportsDeclaration> getImportsDeclarations() {

		return ont.getImportsDeclarations();
	}

	@Override
	public Set<OWLIndividualAxiom> getIndividualAxioms() {

		return ont.getIndividualAxioms();
	}

	@Override
	public OWLInverseFunctionalObjectPropertyAxiom getInverseFunctionalObjectPropertyAxiom(OWLObjectPropertyExpression arg0) {

		return ont.getInverseFunctionalObjectPropertyAxiom(arg0);
	}

	@Override
	public Set<OWLInverseObjectPropertiesAxiom> getInverseObjectPropertyAxioms(OWLObjectPropertyExpression arg0) {

		return ont.getInverseObjectPropertyAxioms(arg0);
	}

	@Override
	public OWLIrreflexiveObjectPropertyAxiom getIrreflexiveObjectPropertyAxiom(OWLObjectPropertyExpression arg0) {

		return ont.getIrreflexiveObjectPropertyAxiom(arg0);
	}

	@Override
	public int getLogicalAxiomCount() {

		return ont.getLogicalAxiomCount();
	}

	@Override
	public Set<OWLLogicalAxiom> getLogicalAxioms() {

		return ont.getLogicalAxioms();
	}

	@Override
	public Set<OWLNegativeDataPropertyAssertionAxiom> getNegativeDataPropertyAssertionAxioms(OWLIndividual arg0) {

		return ont.getNegativeDataPropertyAssertionAxioms(arg0);
	}

	@Override
	public Set<OWLNegativeObjectPropertyAssertionAxiom> getNegativeObjectPropertyAssertionAxioms(OWLIndividual arg0) {
		return ont.getNegativeObjectPropertyAssertionAxioms(arg0);
	}

	@Override
	public Set<OWLObjectPropertyAssertionAxiom> getObjectPropertyAssertionAxioms(OWLIndividual arg0) {
		return ont.getObjectPropertyAssertionAxioms(arg0);
	}

	@Override
	public Set<OWLPropertyAxiom> getObjectPropertyAxioms() {

		return ont.getObjectPropertyAxioms();
	}

	@Override
	public Set<OWLObjectPropertyDomainAxiom> getObjectPropertyDomainAxioms(OWLObjectPropertyExpression arg0) {

		return ont.getObjectPropertyDomainAxioms(arg0);
	}

	@Override
	public Set<OWLObjectPropertyRangeAxiom> getObjectPropertyRangeAxioms(OWLObjectPropertyExpression arg0) {

		return ont.getObjectPropertyRangeAxioms(arg0);
	}

	@Override
	public Set<OWLObjectSubPropertyAxiom> getObjectSubPropertyAxiomsForLHS(OWLObjectPropertyExpression arg0) {

		return ont.getObjectSubPropertyAxiomsForLHS(arg0);
	}

	@Override
	public Set<OWLObjectSubPropertyAxiom> getObjectSubPropertyAxiomsForRHS(OWLObjectPropertyExpression arg0) {

		return ont.getObjectSubPropertyAxiomsForRHS(arg0);
	}

	@Override
	public Set<OWLOntologyAnnotationAxiom> getOntologyAnnotationAxioms() {

		return ont.getOntologyAnnotationAxioms();
	}

	@Override
	public Set<OWLObjectPropertyChainSubPropertyAxiom> getPropertyChainSubPropertyAxioms() {

		return ont.getPropertyChainSubPropertyAxioms();
	}

	@Override
	public Set<OWLClass> getReferencedClasses() {

		return ont.getReferencedClasses();
	}

	@Override
	public Set<OWLDataProperty> getReferencedDataProperties() {

		return ont.getReferencedDataProperties();
	}

	@Override
	public Set<OWLEntity> getReferencedEntities() {

		return ont.getReferencedEntities();
	}

	@Override
	public Set<OWLIndividual> getReferencedIndividuals() {

		return ont.getReferencedIndividuals();
	}

	@Override
	public Set<OWLObjectProperty> getReferencedObjectProperties() {

		return ont.getReferencedObjectProperties();
	}

	@Override
	public Set<OWLAxiom> getReferencingAxioms(OWLEntity arg0) {

		return ont.getReferencingAxioms(arg0);
	}

	@Override
	public OWLReflexiveObjectPropertyAxiom getReflexiveObjectPropertyAxiom(OWLObjectPropertyExpression arg0) {
		return ont.getReflexiveObjectPropertyAxiom(arg0);
	}

	@Override
	public Set<SWRLRule> getRules() {
		return ont.getRules();
	}

	@Override
	public Set<OWLSameIndividualsAxiom> getSameIndividualAxioms(OWLIndividual arg0) {
		return ont.getSameIndividualAxioms(arg0);
	}

	@Override
	public Set<OWLSubClassAxiom> getSubClassAxiomsForLHS(OWLClass arg0) {
		return ont.getSubClassAxiomsForLHS(arg0);
	}

	@Override
	public Set<OWLSubClassAxiom> getSubClassAxiomsForRHS(OWLClass arg0) {
		return ont.getSubClassAxiomsForRHS(arg0);
	}

	@Override
	public OWLSymmetricObjectPropertyAxiom getSymmetricObjectPropertyAxiom(OWLObjectPropertyExpression arg0) {
		return ont.getSymmetricObjectPropertyAxiom(arg0);
	}

	@Override
	public OWLTransitiveObjectPropertyAxiom getTransitiveObjectPropertyAxiom(OWLObjectPropertyExpression arg0) {
		return ont.getTransitiveObjectPropertyAxiom(arg0);
	}

	@Override
	public boolean isEmpty() {
		return ont.isEmpty();
	}

	@Override
	public boolean isPunned(URI arg0) {
		return ont.isPunned(arg0);
	}

	@Override
	public void accept(OWLNamedObjectVisitor arg0) {
		ont.accept(arg0);
	}

	@Override
	public URI getURI() {
		return ont.getURI();
	}

	@Override
	public void accept(OWLObjectVisitor arg0) {
		ont.accept(arg0);
	}

	@Override
	public <O> O accept(OWLObjectVisitorEx<O> arg0) {
		return ont.accept(arg0);
	}

	@Override
	public Set<OWLClass> getClassesInSignature() {
		return ont.getClassesInSignature();
	}

	@Override
	public Set<OWLDataProperty> getDataPropertiesInSignature() {
		return ont.getDataPropertiesInSignature();
	}

	@Override
	public Set<OWLIndividual> getIndividualsInSignature() {
		return ont.getIndividualsInSignature();
	}

	@Override
	public Set<OWLObjectProperty> getObjectPropertiesInSignature() {
		return ont.getObjectPropertiesInSignature();
	}

	@Override
	public Set<OWLEntity> getSignature() {
		return ont.getSignature();
	}

	@Override
	public int compareTo(OWLObject o) {
		return ont.compareTo(o);
	}
}
