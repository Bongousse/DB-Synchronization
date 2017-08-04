package exerd.utilizing.com.processor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DbConnection {
	private String dbms;
	private String ip;
	private String listenerPort;
	private String sid;
	private String id;
	private String password;
	private String driver;
	private String preUrl;
	private String url;

	public DbConnection(Properties properties) {
		dbms = properties.getProperty("DBMS");
		ip = properties.getProperty("IP");// "182.162.100.120";
		listenerPort = properties.getProperty("PORT"); // "10110";
		sid = properties.getProperty("SID"); // "orcl";
		id = properties.getProperty("ID"); // "kbpoc";
		password = properties.getProperty("PASSWORD"); // "infrabxm0204";
	}

	public DbConnection(String dbms, String ip, String listenerPort, String sid, String id, String password) {
		this.dbms = dbms;
		this.ip = ip;
		this.listenerPort = listenerPort;
		this.sid = sid;
		this.id = id;
		this.password = password;
	}

	public Connection connection() throws SQLException, ClassNotFoundException {

		if ("ORACLE".equals(dbms)) {
			driver = "oracle.jdbc.driver.OracleDriver";
			preUrl = "jdbc:oracle:thin:@";
			url = preUrl + ip + ":" + listenerPort + ":" + sid;
		} else if ("POSTGRESQL".equals(dbms)) {
			driver = "org.postgresql.Driver";
			preUrl = "jdbc:postgresql://";
			url = preUrl + ip + ":" + listenerPort + "/" + sid;
		}

		Connection conn = null;
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, id, password);
			System.out.println("커넥션 성공");
		} catch (SQLException e) {
			System.out.println("커넥션 실패  error:" + e.getMessage());
			throw e;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw e;
		}

		return conn;
	}
}
