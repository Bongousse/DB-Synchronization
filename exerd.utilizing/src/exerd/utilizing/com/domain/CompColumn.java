package exerd.utilizing.com.domain;

public class CompColumn {
	private Column ddlColumn;
	private Column dbColumn;
	private String compTypeCd;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DdlNDbColumn [ddlColumn=");
		builder.append(ddlColumn);
		builder.append(", dbColumn=");
		builder.append(dbColumn);
		builder.append(", compTypeCd=");
		builder.append(compTypeCd);
		builder.append("]");
		return builder.toString();
	}

	public Column getDdlColumn() {
		return ddlColumn;
	}

	public void setDdlColumn(Column ddlColumn) {
		this.ddlColumn = ddlColumn;
	}

	public Column getDbColumn() {
		return dbColumn;
	}

	public void setDbColumn(Column dbColumn) {
		this.dbColumn = dbColumn;
	}

	public String getCompTypeCd() {
		return compTypeCd;
	}

	public void setCompTypeCd(String compTypeCd) {
		this.compTypeCd = compTypeCd;
	}

}
