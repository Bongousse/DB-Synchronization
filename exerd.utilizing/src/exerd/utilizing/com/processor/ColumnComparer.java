package exerd.utilizing.com.processor;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import exerd.utilizing.com.constants.IConstants;
import exerd.utilizing.com.domain.Column;
import exerd.utilizing.com.domain.Table;
import exerd.utilizing.com.sqlwriter.ASqlWriter;
import exerd.utilizing.com.sqlwriter.SqlWriterFactory;

public class ColumnComparer {

	private TableReader tableReader;

	private ASqlWriter sqlWriter;

	private String dbms;

	private String ddlFilePath;

	private DbConnection dbConnection;

	private Map<Table, List<Column>> tableColumnMap;

	private int totalTableCount, currentTableCount;

	private Column findColumn(List<Column> columnList, String columnName) {
		for (Column column : columnList) {
			if (columnName.equals(column.getName())) {
				return column;
			}
		}
		return null;
	}

	private List<Column> getCompareColumn(Table table, List<Column> ddlColumnList, List<Column> dbColumnList) {
		String tableName = table.getTableName();
		List<Column> resultCompColumnList = new ArrayList<Column>();
		if (dbColumnList.size() == 0) {
			table.setNoneExistent(true);
			// case 4: there is no table in db
			System.out.println("[CASE4] There is no table in database\n");
			return null;
		}

		int differentColumnCount = 0;
		for (Column ddlColumn : ddlColumnList) {
			Column dbColumn = findColumn(dbColumnList, ddlColumn.getName());
			if (dbColumn == null) {
				// case 1: there is no column in db
				// Common: ALTER TABLE 테이블명 ADD 컬럼명 데이터 유형 [NOT NULL];
				ddlColumn.setCompTypeCd(IConstants.COMP_TYPE_CD.NONE_EXISTENT);
				sqlWriter.writeAddColumn(tableName, ddlColumn);
				differentColumnCount++;
				System.out.println("[CASE1] dbColumn: " + dbColumn + " ddlColumn: " + ddlColumn);
			} else {
				if (ddlColumn.compareTo(dbColumn) != 0) {
					// case 2: there is difference between ddl and db
					// Oracle: ALTER TABLE 테이블명 MODIFY (컬럼명 데이터유형 [NOT NULL]);
					// PostgreSQL: ALTER TABLE 테이블명 ALTER COLUMN 컬럼명 TYPE 데이터유형;
					ddlColumn.setCompTypeCd(IConstants.COMP_TYPE_CD.DIFFERENT);
					sqlWriter.writeAlterColumn(tableName, ddlColumn);
					differentColumnCount++;
					System.out.println("[CASE2] dbColumn: " + dbColumn + " ddlColumn: " + ddlColumn);
				} else {
					ddlColumn.setCompTypeCd(IConstants.COMP_TYPE_CD.EQUAL);
				}
			}
			resultCompColumnList.add(ddlColumn);
		}

		for (Column dbColumn : dbColumnList) {
			Column ddlColumn = findColumn(ddlColumnList, dbColumn.getName());
			if (ddlColumn == null) {
				// case 3: there is no column in ddl
				dbColumn.setCompTypeCd(IConstants.COMP_TYPE_CD.UNNECESSARY);
				sqlWriter.writeDropColumn(tableName, dbColumn);
				differentColumnCount++;
				resultCompColumnList.add(dbColumn);
				System.out.println("[CASE3] dbColumn: " + dbColumn + " ddlColumn: " + ddlColumn);
			}
		}

		System.out.println("differentColumnCount: " + differentColumnCount);
		System.out.println();

		table.setDifferentColumnCount(differentColumnCount);

		return resultCompColumnList;
	}

	public int getCurrentProgressPercent() {
		if (totalTableCount == 0)
			return 0;
		return (currentTableCount * 100) / totalTableCount;
	}

	private Map<Table, List<Column>> makeTableColumnMap(String ddlText) {
		tableColumnMap = new LinkedHashMap<Table, List<Column>>();

		String[] ddlList = ddlText.split("CREATE TABLE ");

		System.out.println("TABLE SIZE:" + (ddlList.length - 1));
		totalTableCount = ddlList.length - 1;
		for (String ddl : ddlList) {
			if (ddl.equals(""))
				continue;
			currentTableCount++;

			String tableName = ddl.substring(0, ddl.indexOf("(")).trim();
			System.out.println("TABLE NAME:" + tableName);

			String content = ddl.substring(ddl.indexOf("(") + 1, ddl.indexOf(");", ddl.indexOf("("))).trim();
			String[] columnStringList = content.split("\n");
			if (columnStringList.length == 1 && columnStringList[0].equals("")) {
				System.out.println("COLUMN SIZE:" + 0);
				continue;
			} else {
				System.out.println("COLUMN SIZE:" + columnStringList.length);
			}

			List<Column> columnList = new ArrayList<Column>();
			for (String columnString : columnStringList) {

				columnString = columnString.replaceAll(IConstants.ALL_SPACE_WITHOUT_NEWLINE, " ");

				Column column = new Column();
				columnString = columnString.trim();
				column.setName(columnString.split(" ")[0]);
				if (columnString.contains("(")) {
					String typeSection = columnString.split(" ")[1];
					String type = typeSection.substring(0, typeSection.indexOf("("));
					column.setType(type);
					String sizeSection = columnString.substring(columnString.indexOf("(") + 1,
							columnString.indexOf(")"));
					if (sizeSection.contains(",")) {
						sizeSection = sizeSection.substring(0, sizeSection.indexOf(",") - 1);
					}
					column.setSize(Integer.valueOf(sizeSection));
				} else {
					String type = columnString.split(" ")[1];
					column.setType(type);
				}
				column.setNullable(columnString.contains("NOT NULL") ? 0 : 1);

				columnList.add(column);
			}
			System.out.println("COLUMN LIST: " + columnList);

			Table table = new Table();
			table.setTableName(tableName);

			List<Column> dbColumnList = tableReader.getTableColumnList(tableName);

			List<Column> compColumnList = getCompareColumn(table, columnList, dbColumnList);

			tableColumnMap.put(table, compColumnList);
		}

		return tableColumnMap;
	}

	public Map<Table, List<Column>> process() {
		sqlWriter = SqlWriterFactory.getSqlWriter(dbms);

		DdlReader ddlReader = new DdlReader();

		String ddlText = ddlReader.readDdl(ddlFilePath);
		// System.out.println("DDL TEXT: " + ddlText);

		try {
			Connection conn = dbConnection.connection();
			tableReader = new TableReader(conn, dbms);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

		return makeTableColumnMap(ddlText);
	}

	public ColumnComparer(Properties properties) {
		ddlFilePath = properties.getProperty("DDL_FILE_PATH");

		dbms = properties.getProperty("DBMS");

		dbConnection = new DbConnection(properties);
	}

	public ColumnComparer(String ddlPath, String dbms, String ip, String listenerPort, String sid, String id,
			String password) {
		this.ddlFilePath = ddlPath;

		this.dbms = dbms;

		dbConnection = new DbConnection(dbms, ip, listenerPort, sid, id, password);
	}

	public static void main(String[] args) {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream("config.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		ColumnComparer c = new ColumnComparer(properties);

		c.process();

	}
}

/*
 * CREATE TABLE BXT_MSG_SYS_EXEC_HIST ( EVENT_UID VARCHAR(32) NOT NULL, SYS_ID
 * VARCHAR(100) NOT NULL, SND_RCV_CLCF_CD VARCHAR(1) NULL, PROC_DTTM VARCHAR(23)
 * NULL, PROC_USER_ID VARCHAR(100) NULL, PROC_STATUS_CD VARCHAR(1) NULL, ERR_MSG
 * VARCHAR(2000) NULL )
 */