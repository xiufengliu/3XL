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

package xlsystem.load;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.supercsv.io.ICsvMapWriter;


import xlsystem.common.Utility;
import xlsystem.common.Configure;
import xlsystem.common.XLException;
import xlsystem.ontology.OwlOntology;
import xlsystem.ontology.Property;

public class ValueHolderMemToPgProcessor implements Processor {

	Map<String, ICsvMapWriter> csvWriterMap;
	Map<String, String[]> headerMap;

	ICsvMapWriter writer;
	PropertyVisitor visitor;
	OwlOntology ont;

	KVStore kvStore;
	DAO dao;
	String csvPath;

	public ValueHolderMemToPgProcessor(OwlOntology ont, KVStore kvStore, DAO dao) {
		this.csvWriterMap = new HashMap<String, ICsvMapWriter>();
		this.headerMap = new HashMap<String, String[]>();
		this.visitor = VisitorFactory.getVisitor(Configure.getInstance().isUseMP());
		this.ont = ont;
		this.kvStore = kvStore;
		this.dao = dao;
		
		try {
			File path = new File(Configure.getInstance().getCsvPath());
			path.mkdirs();
			this.csvPath = new StringBuilder(path.getCanonicalPath()).append(File.separator).toString();
		} catch (IOException e) {
			 throw new IllegalArgumentException(e.getCause());
		}
	}

	@Override
	public void process(Object obj) throws XLException {
		try {
			ValueHolder vh = (ValueHolder) obj;		
			// store map to MapStore (here is berkeley DB)
			xlsystem.common.Map map = new xlsystem.common.Map(vh.getUri(), vh.getId(), vh.getName());
			map.setCtLocked(vh.isNameLocked());
			kvStore.put(map);
			
			if (!vh.isOverflow()) {
				String classTableName = vh.getName();
				List<Property> props = ont.getPropsByOwlClassName(classTableName);
				

				HashMap<String, Object> row = new HashMap<String, Object>();
				row.put("id", vh.getId());
				row.put("uri", vh.getUri());

				visitor.setConfig(this.csvPath, vh, row, csvWriterMap, headerMap);
				
				for (Property prop : props)
					prop.accept(visitor);

				String[] header = getHeader(classTableName, row.keySet());
				ICsvMapWriter writer = getMapWriter(classTableName);
				writer.write(row, header);
			} else {
				HashMap<String, Object> row = new HashMap<String, Object>();
				row.put("id", vh.getId());
				row.put("sub", vh.getUri());
				row.put("pre", vh.getValue("pre"));
				row.put("obj", vh.getValue("obj"));
				
				ICsvMapWriter writer = getMapWriter("xl_overflow");
				String[] header = new String[]{"id","sub","pre","obj"};
				this.put("xl_overflow", header);
				writer.write(row, header);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new XLException(e.getMessage());
		}

	}

	//@Override
	public void end1() throws XLException {
		try {
			if (csvWriterMap.size() > 0) {
				List<Runnable> tasks = new ArrayList<Runnable>();
				for (String tableName : csvWriterMap.keySet()) {
					ICsvMapWriter writer = csvWriterMap.get(tableName);
					if (writer != null) {
						writer.close();
					}
					tasks.add(new CsvToPgLoader(dao, tableName,headerMap.get(tableName)));	
				}
				csvWriterMap.clear();
				
				long t1 = System.nanoTime();
				if (dao.isClean()){
					long t2 = System.nanoTime();
					JobScheduler.run(tasks);
					long t3 = System.nanoTime();
					System.out.printf("WaitDBClean=%.2f; CSV2DB=%.2f \n", (t2-t1)/1000000000.0, (t3-t2)/1000000000.0);
				}
			}
			visitor.end();
		} catch (Exception e) {
			e.printStackTrace();
			throw new XLException(e.getMessage());
		}
	}
	
	@Override
	public void end() throws XLException {
		try {
			//long total = 0;
			if (dao.isClean() && csvWriterMap.size() > 0) {
				for (String tableName : csvWriterMap.keySet()) {
					ICsvMapWriter writer = csvWriterMap.get(tableName);
					if (writer != null) {
						writer.close();
					}
					//long t1 = System.nanoTime();
					this.dao.load(this.csvPath, tableName, headerMap.get(tableName));
					//total += System.nanoTime() - t1;
				}
				csvWriterMap.clear();
				//System.out.printf("CSV2DB=%.2f \n", total/1000000000.0);
			}
			visitor.end();
		} catch (Exception e) {
			e.printStackTrace();
			throw new XLException(e.getMessage());
		}
	}

	private String[] getHeader(String name, Set<String> headerSet) {
		String[] header = headerMap.get(name);
		if (header == null) {
			List<String> headerList = new ArrayList<String>(headerSet);
			Collections.sort(headerList);
			header = headerList.toArray(new String[] {});
			this.put(name, header);
		}
		return header;
	}

	private void put(String name, String[] header) {
		if (!headerMap.containsKey(name))
			headerMap.put(name, header);
	}
	
	private ICsvMapWriter getMapWriter(String name) throws IOException {
		ICsvMapWriter writer = csvWriterMap.get(name);
		if (writer == null) {
			StringBuilder buf = new StringBuilder(this.csvPath).append(name).append(".csv");
			writer = Utility.getMapWriter(buf.toString());
			csvWriterMap.put(name, writer);
		}
		return writer;
	}

}
