package exerd.utilizing.com.processor;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import exerd.utilizing.com.domain.Column;

public class TableReader {
	private Connection conn;
	
	public TableReader(Connection conn){
		this.conn = conn;
	}
	
	public List<Column> readTableColumns(String tableName) {
		List<Column> columns = new ArrayList<Column>();
		
		try {
			DatabaseMetaData metaData = conn.getMetaData();
			ResultSet columnsResultSet = metaData.getColumns(null, null, tableName, null);
			
			while(columnsResultSet.next()){
				String name = columnsResultSet.getString("COLUMN_NAME").toUpperCase();
				String type = columnsResultSet.getString("TYPE_NAME").toUpperCase();
				String size = columnsResultSet.getString("COLUMN_SIZE");
				String nullable = columnsResultSet.getString("NULLABLE");
				String remarks = columnsResultSet.getString("REMARKS");
//				System.out.print(name);
//				System.out.print("\t" + columnsResultSet.getString("TYPE_NAME"));
//				System.out.print("\t" + columnsResultSet.getString("COLUMN_SIZE"));
//				System.out.print("\t" + columnsResultSet.getString("NULLABLE"));
//				System.out.print("\t" + remarks);
//				System.out.println(); 
				
				Column column = new Column();
				column.setName(name);
				column.setType(type);
				column.setSize(Integer.valueOf(size));
				column.setNullable(Integer.valueOf(nullable));
				column.setComment(remarks);
				
				columns.add(column);
			} 
		} catch (SQLException e) {
			System.out.println("SQL 에러");
		}
		
		return columns;
	}
	
//	public static void main(String[] args) {
//		OracleDbConnection oc = new OracleDbConnection();
//		Connection conn = oc.connection();
//		OracleConnection oraCon = (OracleConnection)conn;
//		oraCon.setRemarksReporting(true);
//		readTableColumns(oraCon, "BXM_LOG_ERR_TM");
//	}
}
