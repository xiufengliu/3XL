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

package xlsystem.query.sql.comp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import xlsystem.common.Constants;
import xlsystem.common.Utility;
import xlsystem.common.XLException;

public final class SQLSelect extends SQLStatement<SQLSelect> implements InsertSource {

	private List<TableName> tableNames;
	private List<FieldValue> fieldValues;
	private List<KeyMap> keyMaps;
	private Relation relation;
	private List<Condition> whereConditions;
	private ConditionList conditionList;

	private Map<String, TableName> tableNamesMap;
	private Map<String,String> fieldTableNamesMap; 

	public SQLSelect(List<TableName> tableNames, List<FieldValue> fieldValues, List<KeyMap> keyMaps, List<Condition> whereConditions, ConditionList conditionList, Relation relation) {
		this.tableNames = tableNames;
		this.fieldValues = fieldValues;
		this.keyMaps = keyMaps;
		this.whereConditions = whereConditions;
		this.conditionList = conditionList;
		this.relation = relation;
	}

	public SQLSelect() {
		tableNames = new ArrayList<TableName>();
		keyMaps = new ArrayList<KeyMap>();
		relation = new Relation(tableNames, keyMaps);
		fieldValues = new ArrayList<FieldValue>();
		whereConditions = new ArrayList<Condition>();
		conditionList = null;
		
		tableNamesMap = new Hashtable<String, TableName>();
		fieldTableNamesMap =  new Hashtable<String, String>();
	}

	public Collection<FieldValue> getFieldValues() {
		return fieldValues;
	}

	public Relation getRelation() {
		return relation;
	}

	public Collection<Condition> getWhereConditions() {
		return whereConditions;
	}

	public void addTableName(TableName tableName) {
		if (!tableNamesMap.containsKey(tableName.getAlias())) {
			tableNames.add(tableName);
			tableNamesMap.put(tableName.getAlias(), tableName);
			tableNamesMap.put(tableName.getTableName(), tableName);
		}
	}

	public TableName getTableName(String name) {
		return tableNamesMap.get(name);
	}

	public void addFieldValue(FieldValue fieldValue) throws XLException {
		String tname = fieldValue.getTableName();
		String fname = fieldValue.getAlias();
		String tnameAdded = fieldTableNamesMap.get(fname);
		if (tnameAdded == null) {
			fieldValues.add(fieldValue);
			fieldTableNamesMap.put(fname, tname);
		} else {
			if (!tnameAdded.equalsIgnoreCase(tname)) {
				throw new XLException(Constants.ILLEGAL_QUERY);
			}
		}
	}

	public void addKeyMap(KeyMap keyMap) {
		keyMaps.add(keyMap);
	}

	public void addWhereCondition(Condition condition) {
		whereConditions.add(condition);
	}

	public void setConditionList(ConditionList conditionList) {
		this.conditionList = conditionList;
	}

	public ConditionList getConditionList() {
		return conditionList;
	}
	
	public StringBuilder appendTo(StringBuilder sb) {
		sb.append("SELECT");

		Utility.appendTo(sb, fieldValues, " ", null, ",");
		sb.append(" FROM ");
		relation.appendTo(sb);

		int addWhere = 0;
		List<KeyMap> keyMaps = relation.getKeyMaps();
		if (keyMaps != null && !keyMaps.isEmpty()) {
			sb.append(" WHERE ");
			Utility.appendTo(sb, keyMaps, " ", null, " AND ");
			++addWhere;
		}

		if (whereConditions != null && !whereConditions.isEmpty()) {
			if (addWhere==0) {
				sb.append(" WHERE ");
				++addWhere;
			} else {
				sb.append(" AND ");
			}
			Utility.appendTo(sb, whereConditions, " ", null, " AND ");
		}
		
		if (conditionList!=null){
			if (addWhere==0) {
				sb.append(" WHERE ");
				++addWhere;
			} else {
				sb.append(" AND ");
			}
			conditionList.appendTo(sb);
		}
		
		return sb;
	}


}
