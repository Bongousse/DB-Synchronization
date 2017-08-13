package exerd.utilizing.com.sqlwriter;

import java.util.List;

import exerd.utilizing.com.domain.Column;
import exerd.utilizing.com.domain.CompColumn;

public class PostgreSqlWriter extends ASqlWriter {

	@Override
	public String writeDdl(String tableName, List<CompColumn> columnList) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String writeAddColumn(String tableName, Column column) {
		String query = "ALTER TABLE " + tableName + " ADD COLUMN " + column.getName() + " " + column.getType();
		if (column.getSize() != 0) {
			query += "(" + column.getSize() + ")";
		}
		if (column.getNullable() == 0) {
			query += " NOT NULL";
		}
		query += ";";

		System.out.println(" - Query: " + query);
		return query;
	}

	@Override
	public String writeAlterColumn(String tableName, Column column) {
		// ALTER TABLE 테이블명 ALTER COLUMN 컬럼명 TYPE 데이터유형;
		String query = "ALTER TABLE " + tableName + " ALTER COLUMN " + column.getName() + " TYPE " + column.getType();
		if (column.getSize() != 0) {
			query += "(" + column.getSize() + ")";
		}
		if (column.getNullable() == 0) {
			query += " NOT NULL";
		}
		query += ";";

		System.out.println(" - Query: " + query);
		return query;
	}

	@Override
	public String writeDropColumn(String tableName, Column column) {
		String query = "ALTER TABLE " + tableName + " DROP COLUMN " + column.getName() + ";";

		System.out.println(" - Query: " + query);
		return query;

	}

}
