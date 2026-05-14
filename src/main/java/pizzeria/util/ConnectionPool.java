package pizzeria.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Stack;

public class ConnectionPool {
  private static final String URL = "jdbc:sqlite:pizzeria.db";
  private static final Stack<Connection> pool = new Stack<>();

  public static synchronized Connection getConnection() throws SQLException {
    if (pool.isEmpty()) return DriverManager.getConnection(URL);
    return pool.pop();
  }

  public static synchronized void releaseConnection(Connection connection) {
    if (connection != null) pool.push(connection);
  }
}
