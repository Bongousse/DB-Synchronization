package exerd.utilizing.com.sqlwriter;

public class SqlWriterFactory {
	public static ISqlWriter getSqlWriter(String dbms){
		switch(dbms){
		case "ORACLE":
			return new OracleSqlWriter();
		case "POSTGRESQL":
			return new PostgreSqlWriter();
		}
		return null;
	}
}
