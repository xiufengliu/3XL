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
import java.util.Collections;

import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.util.OWLOntologyWalker;


import xlsystem.common.Configure;

public final class OwlOntManager {

	/**
	 * @param args
	 */
	
	private OwlOntManager(){}
	
	public static OwlOntology ontology = null;
	
	public static synchronized OwlOntology getInstance(String owlPath) {
		if (ontology == null) {
			try {
				URI physicalURI = URI.create("file://"+owlPath);
				OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
				OWLOntology ont = manager.loadOntologyFromPhysicalURI(physicalURI);

				ontology = new OwlOntology(ont);
				OWLOntologyWalker walker = new OWLOntologyWalker(Collections.singleton(ont));
				OwlOntWalkerVisitor myvisitor = new OwlOntWalkerVisitor(walker, ontology);
				walker.walkStructure(myvisitor);
				ontology.beautify();
			} catch (OWLOntologyCreationException e) {
				System.out.printf("Cannot load the ontology file at: %s \n", owlPath);
				System.exit(1);
			} finally {
				//System.out.printf("Load OWL: %s\n", owlPath);
			}
		}
		return ontology;
	}

	public static OwlOntology getInstance(){
		return getInstance(Configure.getInstance().getOntPath());
	}
	
	public static void main(String[] args) {
		OwlOntology ont = OwlOntManager.getInstance("/home/xiliu/workspace/3xlsystem/src/xlsystem/ontology/eiao.owl");
		System.out.println(ont);
	}

}
