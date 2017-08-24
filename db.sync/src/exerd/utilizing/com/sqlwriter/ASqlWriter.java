package exerd.utilizing.com.sqlwriter;

import java.util.List;
import java.util.Map;

import exerd.utilizing.com.constants.IConstants;
import exerd.utilizing.com.domain.Column;
import exerd.utilizing.com.domain.CompColumn;
import exerd.utilizing.com.domain.Table;

public abstract class ASqlWriter {
	public abstract String writeDdl(Table table);

	public abstract String writeAddColumn(String tableName, Column column);

	public abstract String writeAlterColumn(String tableName, Column column);

	public abstract String writeDropColumn(String tableName, Column column);

	public String generateSql(Map<Table, List<CompColumn>> tableColumnMap) {

		StringBuffer strBuffer = new StringBuffer();

		for (Table table : tableColumnMap.keySet()) {
			String tableName = table.getTableName();
			List<CompColumn> columnList = tableColumnMap.get(table);

			if (columnList == null) {
				continue;
			}

			if (table.isNoneExistent()) {
				strBuffer.append("--" + tableName + "\n");
				strBuffer.append(writeDdl(table));
				strBuffer.append("\n");
			} else {
				boolean firstFlag = true;
				for (CompColumn compColumn : columnList) {
					if (firstFlag && !IConstants.COMP_TYPE_CD.EQUAL.equals(compColumn.getCompTypeCd())) {
						strBuffer.append("--" + tableName + "\n");
						firstFlag = false;
					}
					switch (compColumn.getCompTypeCd()) {
					case IConstants.COMP_TYPE_CD.EQUAL:
						break;
					case IConstants.COMP_TYPE_CD.DIFFERENT:
						strBuffer.append(writeAlterColumn(tableName, compColumn.getDdlColumn()) + "\n");
						break;
					case IConstants.COMP_TYPE_CD.NONE_EXISTENT:
						strBuffer.append(writeAddColumn(tableName, compColumn.getDdlColumn()) + "\n");
						break;
					case IConstants.COMP_TYPE_CD.UNNECESSARY:
						strBuffer.append(writeDropColumn(tableName, compColumn.getDbColumn()) + "\n");
						break;
					}
				}
				if (!firstFlag) {
					strBuffer.append("\n");
				}
			}
		}

		return strBuffer.toString();
	}
}
