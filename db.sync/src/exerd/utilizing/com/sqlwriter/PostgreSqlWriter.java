package exerd.utilizing.com.sqlwriter;

import exerd.utilizing.com.constants.IConstants;
import exerd.utilizing.com.domain.Column;
import exerd.utilizing.com.domain.Table;

public class PostgreSqlWriter extends ASqlWriter {

	@Override
	public String writeDdl(Table table) {
		return table.getDdl();
	}

	@Override
	public String writeAddColumn(String tableName, Column column) {
		String query = "ALTER TABLE " + tableName + " ADD COLUMN " + column.getName() + " " + column.getType();
		if (column.getSize() != 0) {
			query += "(" + column.getSize() + ")";
		}
		if (column.getNullable() == 0) {
			// TODO DEFALUT 확인 필요
			query += " NOT NULL DEFAULT ";
			if (IConstants.POSTGRESQL_COLUMN_TYPE.NUMERIC.equals(column.getType())) {
				query += "0";
			} else {
				query += "''";
			}
		}
		query += ";";

		System.out.println(" - Query: " + query);
		return query;
	}

	@Override
	public String writeAlterColumn(String tableName, Column column) {
		//TODO varchar -> integer, varchar -> bytea 등 타입 변환이 있을 때 처리 필요 (hint: using col_name::type)
		// ALTER TABLE 테이블명 ALTER COLUMN 컬럼명 TYPE 데이터유형;
		String query = "ALTER TABLE " + tableName + " ALTER COLUMN " + column.getName() + " TYPE " + column.getType();
		if (column.getSize() != 0) {
			query += "(" + column.getSize() + ")";
		}
		if (column.getNullable() == 0) {
			query += ", ALTER COLUMN " + column.getName() + " SET NOT NULL";
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
