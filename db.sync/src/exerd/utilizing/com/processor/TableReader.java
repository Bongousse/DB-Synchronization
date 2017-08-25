package exerd.utilizing.com.processor;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import exerd.utilizing.com.constants.IConstants;
import exerd.utilizing.com.domain.Column;

public class TableReader {
	private Connection conn;
	private String dbms;

	public TableReader(Connection conn, String dbms) {
		this.conn = conn;
		this.dbms = dbms;
	}

	public List<Column> getTableColumnList(String tableName) {
		List<Column> columnList = new ArrayList<Column>();

		try {
			DatabaseMetaData metaData = conn.getMetaData();
			if (IConstants.DBMS.ORACLE.equals(dbms)) {
				tableName = tableName.toUpperCase();
			} else if (IConstants.DBMS.POSTGRESQL.equals(dbms)) {
				tableName = tableName.toLowerCase();
			}
			ResultSet columnsResultSet = metaData.getColumns(null, null, tableName, null);

			while (columnsResultSet.next()) {
				String name = columnsResultSet.getString("COLUMN_NAME").toUpperCase();
				String type = columnsResultSet.getString("TYPE_NAME").toUpperCase();
				String size = columnsResultSet.getString("COLUMN_SIZE");
				String nullable = columnsResultSet.getString("NULLABLE");
				String remarks = columnsResultSet.getString("REMARKS");

				Column column = new Column();
				column.setName(name);
				column.setType(type);
				column.setSize(Integer.valueOf(size));
				if (type.contains("TIMESTAMP")) {
					column.setType("TIMESTAMP");
					if (type.contains("(") && type.contains(")")) {
						column.setSize(Integer.valueOf(type.substring(type.indexOf("(") + 1, type.indexOf(")"))));
					}
				} else if(type.equals("DATE")){
					column.setSize(0);
				}
				column.setNullable(Integer.valueOf(nullable));
				column.setComment(remarks);

				columnList.add(column);
			}
		} catch (SQLException e) {
			System.out.println("SQL 에러");
		}

		return columnList;
	}

}
