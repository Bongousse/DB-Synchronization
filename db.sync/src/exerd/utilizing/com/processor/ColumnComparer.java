package exerd.utilizing.com.processor;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exerd.utilizing.com.constants.IConstants;
import exerd.utilizing.com.domain.Column;
import exerd.utilizing.com.domain.CompColumn;
import exerd.utilizing.com.domain.Table;

public class ColumnComparer {

	private final Logger logger = LoggerFactory.getLogger(ColumnComparer.class);

	private TableReader tableReader;

	private String dbms;

	private String ddlFilePath;

	private DbConnection dbConnection;

	private TreeMap<Table, List<CompColumn>> tableColumnMap;

	private int totalTableCount, currentTableCount;

	private Column findColumn(List<Column> columnList, String columnName) {
		for (Column column : columnList) {
			if (columnName.equals(column.getName())) {
				return column;
			}
		}
		return null;
	}

	private List<CompColumn> getCompareColumn(Table table, List<Column> ddlColumnList, List<Column> dbColumnList) {
		List<CompColumn> result = new ArrayList<CompColumn>();
		if (dbColumnList.size() == 0) {
			table.setNoneExistent(true);
			// case 4: there is no table in db
			logger.trace("[CASE4] There is no table in database");

			for (Column ddlColumn : ddlColumnList) {
				CompColumn compColumn = new CompColumn();
				compColumn.setDdlColumn(ddlColumn);
				compColumn.setCompTypeCd(IConstants.COMP_TYPE_CD.NONE_EXISTENT);
				result.add(compColumn);
			}
			return result;
		}

		int differentColumnCount = 0;
		for (Column ddlColumn : ddlColumnList) {
			Column dbColumn = findColumn(dbColumnList, ddlColumn.getName());

			CompColumn compColumn = new CompColumn();
			compColumn.setDdlColumn(ddlColumn);
			compColumn.setDbColumn(dbColumn);

			if (dbColumn == null) {
				// case 1: there is no column in db
				// Common: ALTER TABLE 테이블명 ADD 컬럼명 데이터 유형 [NOT NULL];
				compColumn.setCompTypeCd(IConstants.COMP_TYPE_CD.NONE_EXISTENT);
				differentColumnCount++;
				logger.trace("[CASE1] dbColumn: " + dbColumn + " ddlColumn: " + ddlColumn);
			} else {
				if (ddlColumn.compareTo(dbColumn) != 0) {
					// case 2: there is difference between ddl and db
					// Oracle: ALTER TABLE 테이블명 MODIFY (컬럼명 데이터유형 [NOT NULL]);
					// PostgreSQL: ALTER TABLE 테이블명 ALTER COLUMN 컬럼명 TYPE 데이터유형;
					compColumn.setCompTypeCd(IConstants.COMP_TYPE_CD.DIFFERENT);
					differentColumnCount++;
					logger.trace("[CASE2] dbColumn: " + dbColumn + " ddlColumn: " + ddlColumn);
				} else {
					compColumn.setCompTypeCd(IConstants.COMP_TYPE_CD.EQUAL);
				}
			}
			result.add(compColumn);
		}

		for (Column dbColumn : dbColumnList) {
			Column ddlColumn = findColumn(ddlColumnList, dbColumn.getName());

			if (ddlColumn == null) {
				CompColumn compColumn = new CompColumn();
				compColumn.setDbColumn(dbColumn);
				compColumn.setDdlColumn(ddlColumn);

				// case 3: there is no column in ddl
				compColumn.setCompTypeCd(IConstants.COMP_TYPE_CD.UNNECESSARY);
				differentColumnCount++;
				result.add(compColumn);
				logger.trace("[CASE3] dbColumn: " + dbColumn + " ddlColumn: " + ddlColumn);
			}
		}

		logger.debug("differentColumnCount: " + differentColumnCount + "\n");

		table.setDifferentColumnCount(differentColumnCount);

		return result;
	}

	public int getCurrentProgressPercent() {
		if (totalTableCount == 0)
			return 0;
		return (currentTableCount * 100) / totalTableCount;
	}

	private TreeMap<Table, List<CompColumn>> makeTableColumnMap(String ddlText) {
		tableColumnMap = new TreeMap<Table, List<CompColumn>>();

		String[] ddlList = ddlText.split("CREATE TABLE ");

		logger.info("TABLE SIZE:" + (ddlList.length - 1));
		totalTableCount = ddlList.length - 1;
		for (String ddl : ddlList) {
			if (ddl.equals(""))
				continue;
			currentTableCount++;

			String tableName = ddl.substring(0, ddl.indexOf("(")).trim();
			logger.debug("TABLE NAME:" + tableName);

			String content = ddl.substring(ddl.indexOf("(") + 1, ddl.indexOf(");", ddl.indexOf("("))).trim();
			String[] columnStringList = content.split("\n");
			if (columnStringList.length == 1 && columnStringList[0].equals("")) {
				logger.debug("COLUMN SIZE:" + 0);
				continue;
			} else {
				logger.debug("COLUMN SIZE:" + columnStringList.length);
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
			logger.debug("COLUMN LIST: " + columnList);

			Table table = new Table();
			table.setTableName(tableName);
			table.setDdl("CREATE TABLE " + ddl);

			List<Column> dbColumnList = tableReader.getTableColumnList(tableName);

			List<CompColumn> compColumnList = getCompareColumn(table, columnList, dbColumnList);

			tableColumnMap.put(table, compColumnList);
		}

		return tableColumnMap;
	}

	public TreeMap<Table, List<CompColumn>> process() throws Exception {
		DdlReader ddlReader = new DdlReader();

		String ddlText = ddlReader.readDdl(ddlFilePath);
		// System.out.println("DDL TEXT: " + ddlText);

		try {
			Connection conn = dbConnection.connection();
			tableReader = new TableReader(conn, dbms);
		} catch (ClassNotFoundException | SQLException e) {
			throw e;
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

		try {
			c.process();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}

/*
 * CREATE TABLE BXT_MSG_SYS_EXEC_HIST ( EVENT_UID VARCHAR(32) NOT NULL, SYS_ID
 * VARCHAR(100) NOT NULL, SND_RCV_CLCF_CD VARCHAR(1) NULL, PROC_DTTM VARCHAR(23)
 * NULL, PROC_USER_ID VARCHAR(100) NULL, PROC_STATUS_CD VARCHAR(1) NULL, ERR_MSG
 * VARCHAR(2000) NULL )
 */