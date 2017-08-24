package exerd.utilizing.com.domain;

public class Table {
	private String tableName;
	private int differentColumnCount;
	private boolean isNoneExistent;
	private String ddl;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Table [tableName=");
		builder.append(tableName);
		builder.append(", differentColumnCount=");
		builder.append(differentColumnCount);
		builder.append(", isNoneExistent=");
		builder.append(isNoneExistent);
		builder.append(", ddl=");
		builder.append(ddl);
		builder.append("]");
		return builder.toString();
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public int getDifferentColumnCount() {
		return differentColumnCount;
	}

	public void setDifferentColumnCount(int differentColumnCount) {
		this.differentColumnCount = differentColumnCount;
	}

	public boolean isNoneExistent() {
		return isNoneExistent;
	}

	public void setNoneExistent(boolean isNoneExistent) {
		this.isNoneExistent = isNoneExistent;
	}

	public String getDdl() {
		return ddl;
	}

	public void setDdl(String ddl) {
		this.ddl = ddl;
	}

}
