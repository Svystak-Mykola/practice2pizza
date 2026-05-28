package pizzeria.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Stack;

public class ConnectionPool {
  private static final String URL = System.getenv().getOrDefault(
      "URPIZZA_DB_URL", "jdbc:postgresql://localhost:5432/pizzeria_db?TimeZone=Europe/Kyiv");
  private static final String USER = System.getenv().getOrDefault("URPIZZA_DB_USER", "postgres");
  private static final String PASSWORD = System.getenv().getOrDefault("URPIZZA_DB_PASSWORD", "H27735311");
  private static final Stack<Connection> pool = new Stack<>();

  public static synchronized Connection getConnection() throws SQLException {
    while (!pool.isEmpty()) {
      Connection conn = pool.pop();
      if (!conn.isClosed()) return conn;
    }
    return DriverManager.getConnection(URL, USER, PASSWORD);
  }

  public static synchronized void releaseConnection(Connection connection) {
    if (connection != null) pool.push(connection);
  }
}
