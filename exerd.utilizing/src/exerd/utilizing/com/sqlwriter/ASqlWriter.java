package exerd.utilizing.com.sqlwriter;

import java.util.List;
import java.util.Map;

import exerd.utilizing.com.constants.IConstants;
import exerd.utilizing.com.domain.Column;
import exerd.utilizing.com.domain.Table;

public abstract class ASqlWriter {
	public abstract String writeDdl(String tableName, List<Column> columnList);

	public abstract String writeAddColumn(String tableName, Column column);

	public abstract String writeAlterColumn(String tableName, Column column);

	public abstract String writeDropColumn(String tableName, Column column);

	public String generateSql(Map<Table, List<Column>> tableColumnMap) {

		StringBuffer strBuffer = new StringBuffer();

		for (Table table : tableColumnMap.keySet()) {
			String tableName = table.getTableName();
			List<Column> columnList = tableColumnMap.get(tableName);

			if (columnList == null) {
				continue;
			}

			for (Column column : columnList) {
				switch (column.getCompTypeCd()) {
				case IConstants.COMP_TYPE_CD.EQUAL:
					break;
				case IConstants.COMP_TYPE_CD.DIFFERENT:
					strBuffer.append(writeAlterColumn(tableName, column));
					break;
				case IConstants.COMP_TYPE_CD.NONE_EXISTENT:
					strBuffer.append(writeAddColumn(tableName, column));
					break;
				case IConstants.COMP_TYPE_CD.UNNECESSARY:
					strBuffer.append(writeDropColumn(tableName, column));
					break;
				}
			}
			strBuffer.append("\n");
		}

		return strBuffer.toString();
	}
}
