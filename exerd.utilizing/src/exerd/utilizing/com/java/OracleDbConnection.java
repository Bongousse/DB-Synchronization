package exerd.utilizing.com.java;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class OracleDbConnection {
	public Connection connection() {
		String ip = "182.162.100.120";
		String listenerPort = "10110";
		String sid = "orcl";
		String id = "kbpoc";
		String password = "infrabxm0204";

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			System.out.println("드라이버 로딩 성공");
		} catch (ClassNotFoundException e) {
			System.out.println("드라이버 로딩 실패");
		}

		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:oracle:thin:@" + ip + ":" + listenerPort + ":" + sid, id,
					password);
			System.out.println("커넥션 성공");
		} catch (SQLException e) {
			System.out.println("커넥션 실패");
		}

		return conn;
	}
}
