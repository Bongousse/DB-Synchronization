package exerd.utilizing.com.processor;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import exerd.utilizing.com.domain.Column;

public class ColumnComparer {

	private static TableReader tableReader;

	private static Column findColumn(List<Column> columnList, String columnName) {
		for (Column column : columnList) {
			if (columnName.equals(column.getName())) {
				return column;
			}
		}
		return null;
	}

	private static void compareColumn(List<Column> ddlColumnList, List<Column> dbColumnList) {
		if (dbColumnList.size() == 0) {
			// case 4: there is no table in db
			System.out.println("[CASE4] There is no table in database\n");
			return;
		}

		for (Column ddlColumn : ddlColumnList) {
			Column dbColumn = findColumn(dbColumnList, ddlColumn.getName());
			if (dbColumn == null) {
				// case 1: there is no column in db
				System.out.println("[CASE1] dbColumn: " + dbColumn + " ddlColumn" + ddlColumn);
				// Common: ALTER TABLE 테이블명 ADD 컬럼명 데이터 유형 [NOT NULL];
			} else {
				if (ddlColumn.compareTo(dbColumn) != 0) {
					// case 2: there is difference between ddl and db
					System.out.println("[CASE2] dbColumn: " + dbColumn + " ddlColumn" + ddlColumn);
					// Oracle: ALTER TABLE 테이블명 MODIFY (컬럼명 데이터유형 [NOT NULL]);
					// PostgreSQL: ALTER TABLE 테이블명 ALTER COLUMN 컬럼명 TYPE 데이터유형;
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
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream("config.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		DdlReader ddlReader = new DdlReader();

		String filePath = properties.getProperty("DDL_FILE_PATH");

		String ddlText = ddlReader.readDdl(filePath);
		// System.out.println("DDL TEXT: " + ddlText);

		DbConnection dbConnection = new DbConnection();
		Connection conn = dbConnection.connection(properties);
		tableReader = new TableReader(conn);

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