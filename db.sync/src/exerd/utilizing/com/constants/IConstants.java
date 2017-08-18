package exerd.utilizing.com.constants;

public interface IConstants {
	interface DBMS {
		String ORACLE = "ORACLE";
		String POSTGRESQL = "POSTGRESQL";
	}

	interface COMP_TYPE_CD {
		String EQUAL = "E";
		String DIFFERENT = "D";
		String NONE_EXISTENT = "N";
		String UNNECESSARY = "U";
	}

	interface ORACLE_COLUMN_TYPE {
		String VARCHAR = "VARCHAR";
		String VARCHAR2 = "VARCHAR2";
		String NUMBER = "NUMBER";
	}

	interface POSTGRESQL_COLUMN_TYPE {
		String VARCHAR = "VARCHAR";
		String NUMERIC = "NUMERIC";
	}

	String ALL_SPACE_WITHOUT_NEWLINE = "[\\s&&[^\\n]]+";

}
