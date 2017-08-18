package exerd.utilizing.com.sqlwriter;

import java.util.List;

import exerd.utilizing.com.constants.IConstants;
import exerd.utilizing.com.domain.Column;
import exerd.utilizing.com.domain.CompColumn;

public class OracleSqlWriter extends ASqlWriter {

	@Override
	public String writeDdl(String tableName, List<CompColumn> columnList) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String writeAddColumn(String tableName, Column column) {
		String query = "ALTER TABLE " + tableName + " ADD " + column.getName() + " " + column.getType();
		if (column.getSize() != 0) {
			query += "(" + column.getSize() + ")";
		}
		if (column.getNullable() == 0) {
			query += " NOT NULL DEFAULT ";
			if (IConstants.ORACLE_COLUMN_TYPE.NUMBER.equals(column.getType())) {
				query += "0";
			} else {
				query += "''";
			}
		}
		query += ";";
		return query;
	}

	@Override
	public String writeAlterColumn(String tableName, Column column) {
		String query = "ALTER TABLE " + tableName + " MODIFY " + column.getName() + " " + column.getType();
		if (column.getSize() != 0) {
			query += "(" + column.getSize() + ")";
		}
		if (column.getNullable() == 0) {
			query += " NOT NULL";
		}
		query += ";";
		return query;
	}

	@Override
	public String writeDropColumn(String tableName, Column column) {
		String query = "ALTER TABLE " + tableName + " DROP COLUMN " + column.getName() + ";";
		return query;
	}

}
