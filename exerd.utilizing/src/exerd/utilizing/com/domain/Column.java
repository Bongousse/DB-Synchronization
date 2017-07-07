package exerd.utilizing.com.domain;

public class Column implements Comparable<Column> {
	private String name;
	private String type;
	private int size;
	private int nullable;
	private String comment;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Column [name=");
		builder.append(name);
		builder.append(", type=");
		builder.append(type);
		builder.append(", size=");
		builder.append(size);
		builder.append(", nullable=");
		builder.append(nullable);
		builder.append(", comment=");
		builder.append(comment);
		builder.append("]");
		return builder.toString();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getNullable() {
		return nullable;
	}

	public void setNullable(int nullable) {
		this.nullable = nullable;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public int compareTo(Column o) {
		if (this.getName().equals(o.getName()) && this.getType().equals(o.getType())
				&& this.getType().equals("BYTEA") && this.getNullable() == o.getNullable()) {
			return 0;
		} else if (this.getName().equals(o.getName()) && this.getType().equals(o.getType())
				&& this.getSize() == o.getSize() && this.getNullable() == o.getNullable()) {
			return 0;
		}
		return 1;
	}

}
