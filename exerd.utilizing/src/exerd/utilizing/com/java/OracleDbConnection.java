package exerd.utilizing.com.java;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class OracleDbConnection {
	public Connection connection() {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream("config.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		String dbms = properties.getProperty("DBMS");
		String ip = properties.getProperty("IP");// "182.162.100.120";
		String listenerPort = properties.getProperty("PORT"); // "10110";
		String sid = properties.getProperty("SID"); // "orcl";
		String id = properties.getProperty("ID"); // "kbpoc";
		String password = properties.getProperty("PASSWORD"); // "infrabxm0204";

		String driver = null, preUrl = null, url = null;
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
			System.out.println("커넥션 실패");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return conn;
	}
}
