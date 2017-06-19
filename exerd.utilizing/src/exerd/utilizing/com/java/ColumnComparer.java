package exerd.utilizing.com.java;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import exerd.utilizing.com.domain.Column;

public class ColumnComparer {
	
	private static OracleTableReader tableReader;

	private static void splitDdl(String ddlText) {
		String[] ddlList = ddlText.split("CREATE TABLE ");

		System.out.println("DDL SIZE:" + (ddlList.length - 1));
		for (String ddl : ddlList) {
			if (ddl.equals(""))
				continue;

			String tableName = ddl.substring(0, ddl.indexOf("(")).trim();
			System.out.println("TABLE NAME:" + tableName);

			String content = ddl.substring(ddl.indexOf("(") + 1, ddl.lastIndexOf(")")).trim();
			String[] columnStringList = content.split("\n");
			System.out.println("COLUMN SIZE:" + columnStringList.length);

			List<Column> columnList = new ArrayList<Column>();
			for (String columnString : columnStringList) {
				Column column = new Column();
				columnString = columnString.trim();
				column.setName(columnString.split(" ")[0]);
				String typeWithSize = columnString.split(" ")[1];
				if (typeWithSize.contains("(")) {
					column.setType(typeWithSize.substring(0, typeWithSize.indexOf("(")));
					column.setSize(Integer
							.valueOf(typeWithSize.substring(typeWithSize.indexOf("(") + 1, typeWithSize.indexOf(")"))));
				} else {
					column.setType(typeWithSize);
				}
				column.setNullable(columnString.contains("NOT NULL") ? 0 : 1);

				columnList.add(column);
			}
			System.out.println("COLUMN LIST: " + columnList);
			
			List<Column> dbColumnList = tableReader.readTableColumns(tableName);
			
		}

	}

	private static void process() {
		DdlReader ddlReader = new DdlReader();

		String filePath = "./ddl.txt";

		String ddlText = ddlReader.readDdl(filePath);
		System.out.println("DDL TEXT: " + ddlText);
		
		OracleDbConnection oracleDbConnection = new OracleDbConnection();
		Connection conn = oracleDbConnection.connection();
		tableReader = new OracleTableReader(conn);

		splitDdl(ddlText);
	}

	public static void main(String[] args) {
		process();
	}
}

/*
 * CREATE TABLE BXT_MSG_SYS_EXEC_HIST ( EVENT_UID VARCHAR(32) NOT NULL, SYS_ID
 * VARCHAR(100) NOT NULL, SND_RCV_CLCF_CD VARCHAR(1) NULL, PROC_DTTM VARCHAR(23)
 * NULL, PROC_USER_ID VARCHAR(100) NULL, PROC_STATUS_CD VARCHAR(1) NULL, ERR_MSG
 * VARCHAR(2000) NULL )
 */