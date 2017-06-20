package exerd.utilizing.com.java;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import exerd.utilizing.com.domain.Column;

public class ColumnComparer {

	private static OracleTableReader tableReader;

	private static Column findColumn(List<Column> columnList, String columnName) {
		for (Column column : columnList) {
			if (columnName.equals(column.getName())) {
				return column;
			}
		}
		return null;
	}

	private static void compareColumn(List<Column> ddlColumnList, List<Column> dbColumnList) {
		if(dbColumnList.size() == 0){
			// case 4: there is no table in oracle
			System.out.println("[CASE4] There is no table in database\n");
			return;
		}
		
		for (Column ddlColumn : ddlColumnList) {
			Column dbColumn = findColumn(dbColumnList, ddlColumn.getName());
			if (dbColumn == null) {
				// case 1: there is no column in oracle
				System.out.println("[CASE1] dbColumn: " + dbColumn + " ddlColumn" + ddlColumn);
			} else {
				if (ddlColumn.compareTo(dbColumn) != 0) {
					// case 2: there is difference between ddl and db
					System.out.println("[CASE2] dbColumn: " + dbColumn + " ddlColumn" + ddlColumn);
				}
			}
		}

		for (Column dbColumn : dbColumnList) {
			Column ddlColumn = findColumn(ddlColumnList, dbColumn.getName());
			if (ddlColumn == null) {
				// case 3: there is no column in ddl
				System.out.println("[CASE3] dbColumn: " + dbColumn + " ddlColumn" + ddlColumn);
			}
		}
		
		System.out.println();
	}

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
			if (columnStringList.length == 1 && columnStringList[0].equals("")) {
				System.out.println("COLUMN SIZE:" + 0);
				continue;
			} else {
				System.out.println("COLUMN SIZE:" + columnStringList.length);
			}

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

			compareColumn(columnList, dbColumnList);
		}

	}

	private static void process() {
		DdlReader ddlReader = new DdlReader();

		String filePath = "./ddl.txt";

		String ddlText = ddlReader.readDdl(filePath);
		// System.out.println("DDL TEXT: " + ddlText);

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