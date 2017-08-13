package exerd.utilizing.com.sqlwriter;

import java.util.List;

import exerd.utilizing.com.domain.Column;

public class OracleSqlWriter extends ASqlWriter {

	@Override
	public String writeDdl(String tableName, List<Column> columnList) {
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
			query += " NOT NULL";
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
