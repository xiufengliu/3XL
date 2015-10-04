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

package xlsystem.query.sql;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import xlsystem.common.Constants;
import xlsystem.common.RDF;
import xlsystem.common.Utility;
import xlsystem.common.XLException;
import xlsystem.ontology.OwlOntManager;
import xlsystem.ontology.OwlOntology;
import xlsystem.query.sql.build.LitMultiPropInArrayProcessor;
import xlsystem.query.sql.build.LitMultiPropInTableProcessor;
import xlsystem.query.sql.build.LitSinglePropProcessor;
import xlsystem.query.sql.build.ObjMultiPropInArrayProcessor;
import xlsystem.query.sql.build.ObjMultiPropInTableProcessor;
import xlsystem.query.sql.build.ObjSinglePropProcessor;
import xlsystem.query.sql.build.Processor;
import xlsystem.query.sql.build.ResourceSubjectProcessor;
import xlsystem.query.sql.build.TableNameProcessor;
import xlsystem.query.sql.comp.SQLSelect;
import xlsystem.query.sql.comp.TableName;

public class SQLBuilder {

	public static Map<Integer, Processor> processorMap = new Hashtable<Integer, Processor>();
	static {
		processorMap.put(Constants.TABLE_NAME, new TableNameProcessor());
		processorMap.put(Constants.OBJ_MP_TAB, new ObjMultiPropInTableProcessor());
		processorMap.put(Constants.OBJ_MP_ARR, new ObjMultiPropInArrayProcessor());
		processorMap.put(Constants.OBJ_SIG, new ObjSinglePropProcessor());
		processorMap.put(Constants.LIT_MP_TAB, new LitMultiPropInTableProcessor());
		processorMap.put(Constants.LIT_MP_ARR, new LitMultiPropInArrayProcessor());
		processorMap.put(Constants.LIT_SIG, new LitSinglePropProcessor());
		processorMap.put(Constants.SUBJ_IS_KNOWN, new ResourceSubjectProcessor());
	}

	public static String buildSQL(List<RDF> rdfs) throws XLException {
		try {
			SQLSelect sqlSelect = new SQLSelect();
			for (RDF rdf : rdfs) {
				int type = adapt(rdf, sqlSelect);
				Processor p = processorMap.get(type);
				p.process(rdf, sqlSelect);
			}
			return sqlSelect.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw new XLException("Failed to build sql!");
		}
	}

	public static int adapt(RDF rdf, SQLSelect sqlSelect) {
		String s = rdf.getSub();
		String p = Utility.removeNS2(rdf.getPre());
		OwlOntology ont = OwlOntManager.getInstance();
		
		if ("type".equalsIgnoreCase(p)) {
			return Constants.TABLE_NAME;
		} else {
			if (Utility.isParam(s)) {
				TableName tableName = sqlSelect.getTableName(Utility.getParam(s));
				String ct = tableName.getTableName();
				return ont.getPropType(ct, p);
			} else {
				return Constants.SUBJ_IS_KNOWN;
			}
		}
	}

	public static void main(String[] args) {
		List<RDF> rdfs = new ArrayList<RDF>();
		// Query 1
		
//		rdfs.add(new RDF("GraduateStudent", "type", "?X"));
//		rdfs.add(new RDF("?X", "takesCourse", "http://www.Department0.University0.edu/GraduateCourse0"));
        
		
		// Query 2
		
//		  rdfs.add(new RDF("GraduateStudent", "type", "?X"));
//		  rdfs.add(new RDF("University", "type", "?Y")); 
//		  rdfs.add(new  RDF("Department", "type", "?Z")); 
//		  rdfs.add(new RDF("?X", "memberOf",  "?Z")); 
//		  rdfs.add(new RDF("?Z", "subOrganizationOf", "?Y"));
//		  rdfs.add(new RDF("?X", "undergraduateDegreeFrom", "?Y"));
		

		// Query 3
		
//		rdfs.add(new RDF("Publication", "type", "?X"));
//		rdfs.add(new RDF("?X", "publicationAuthor",  "http://www.Department0.University0.edu/AssistantProfessor0"));
		  
		  
		  // Query 4 
		
//		  rdfs.add(new RDF("Professor", "type", "?X"));
//		  rdfs.add(new RDF("?X", "worksFor",  "http://www.Department0.University0.edu"));
//		  rdfs.add(new RDF("?X",  "name", "?Y1")); 
//		  rdfs.add(new RDF("?X", "emailAddress", "?Y2"));
//		  rdfs.add(new RDF("?X", "telephone", "?Y3"));
//	
		  
		  // Query 5 
		
//		rdfs.add(new RDF("Person", "type", "?X"));
//		rdfs.add(new RDF("?X", "memberOf", "http://www.Department0.University0.edu"));

		  
		  //Query 6 
//		rdfs.add(new RDF("Student", "type", "?X"));

		  
		
		//Query 7 
		
//		rdfs.add(new RDF("Student", "type", "?X"));
//		rdfs.add(new RDF("Course", "type", "?Y"));
//		rdfs.add(new RDF("http://www.Department0.University0.edu/AssociateProfessor0", "teacherOf", "?Y"));
//		rdfs.add(new RDF("?X", "takesCourse", "?Y"));
    
		
		//Query 8
		
//		rdfs.add(new RDF("Student", "type", "?X"));
//		rdfs.add(new RDF("Department", "type", "?Y"));
//		rdfs.add(new RDF("?X", "memberOf", "?Y"));
//		rdfs.add(new RDF("?Y", "subOrganizationOf", "http://www.University0.edu"));
//		rdfs.add(new RDF("?X", "emailAddress", "?Z"));

		
		//Query 9
		
		rdfs.add(new RDF("Student", "type", "?X"));
		rdfs.add(new RDF("Faculty", "type", "?Y"));
		rdfs.add(new RDF("Course", "type", "?Z"));
		rdfs.add(new RDF("?X", "advisor", "?Y"));
		rdfs.add(new RDF("?X", "takesCourse", "?Z"));
		rdfs.add(new RDF("?Y", "teacherOf", "?Z"));

		
		//Query 10
	
//		rdfs.add(new RDF("Student", "type", "?X"));
//		rdfs.add(new RDF("?X", "takesCourse", "http://www.Department0.University0.edu/GraduateCourse0"));

		
		//Query 11
		
//		rdfs.add(new RDF("ResearchGroup", "type", "?X"));
//		rdfs.add(new RDF("?X", "subOrganizationOf", "http://www.Department0.University0.edu"));

		//Query 12 Note: We have changed "Chair" to "FullProfessor" as no inference is implemented in our program
		
//		rdfs.add(new RDF("FullProfessor", "type", "?X"));
//		rdfs.add(new RDF("Department", "type", "?Y"));
//		rdfs.add(new RDF("?X", "worksFor", "?Y"));
//		rdfs.add(new RDF("?Y", "subOrganizationOf", "http://www.University0.edu"));

		
		//Query 13  Note: Not implemented as inference is required.
		/*
		rdfs.add(new RDF("Person", "type", "?X"));
		rdfs.add(new RDF("?X", "hasAlumnus", "http://www.University0.edu"));
		*/
		
		//Query 14 
//		rdfs.add(new RDF("UndergraduateStudent", "type", "?X"));

	
		try {
			System.out.println(SQLBuilder.buildSQL(rdfs));
		} catch (XLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	

	}
}
