package exerd.utilizing.com.sqlwriter;

import java.util.List;

import exerd.utilizing.com.domain.Column;

public interface ISqlWriter {
	public void writeDdl(String tableName, List<Column> columnList);
	public void writeAddColumn(String tableName, Column column);
	public void writeAlterColumn(String tableName, Column column);
	public void writeDropColumn(String tableName, Column column);
}
