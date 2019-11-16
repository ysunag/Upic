package com.upic.servlet;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;

@WebListener("Creates a connection pool that is stored in the Servlet's context for later use.")
public class ConnectionPoolContextListener implements ServletContextListener {


  private static final String CLOUD_SQL_CONNECTION_NAME = System.getenv(
          "CLOUD_SQL_CONNECTION_NAME");
  private static final String DB_USER = System.getenv("DB_USER");
  private static final String DB_PASS = System.getenv("DB_PASS");
  private static final String DB_NAME = System.getenv("DB_NAME");

  private DataSource createConnectionPool() {
    HikariConfig config = new HikariConfig();

    // Configure which instance and what database user to connect with.
    config.setJdbcUrl(String.format("jdbc:mysql:///%s", DB_NAME));
    config.setUsername(DB_USER); // e.g. "root", "postgres"
    config.setPassword(DB_PASS); // e.g. "my-password"

    config.addDataSourceProperty("socketFactory", "com.google.cloud.sql.mysql.SocketFactory");
    config.addDataSourceProperty("cloudSqlInstance", CLOUD_SQL_CONNECTION_NAME);
    config.addDataSourceProperty("useSSL", "false");


    config.setMaximumPoolSize(300);

    config.setMinimumIdle(5);

    config.setConnectionTimeout(60000); // 60 seconds

    config.setIdleTimeout(600000); // 10 minutes

    config.setMaxLifetime(1800000); // 30 minutes

    DataSource pool = new HikariDataSource(config);

    return pool;
  }

//      private void createTable(DataSource pool) throws SQLException {
//
//        try (Connection conn = pool.getConnection()) {
//          PreparedStatement createTableStatement = conn.prepareStatement(
//              "CREATE TABLE IF NOT EXISTS votes ( "
//                  + "vote_id SERIAL NOT NULL, time_cast timestamp NOT NULL, candidate CHAR(6) NOT NULL,"
//                  + " PRIMARY KEY (vote_id) );"
//          );
//          createTableStatement.execute();
//        }
//      }

      @Override
      public void contextDestroyed(ServletContextEvent event) {

        HikariDataSource pool = (HikariDataSource) event.getServletContext().getAttribute("my-pool");
        if (pool != null) {
          pool.close();
        }
      }

      @Override
      public void contextInitialized(ServletContextEvent event) {

        DataSource pool = (DataSource) event.getServletContext().getAttribute("my-pool");
        if (pool == null) {
          pool = createConnectionPool();
          event.getServletContext().setAttribute("my-pool", pool);
        }
//        try {
//          createTable(pool);
//        } catch (SQLException ex) {
//          throw new RuntimeException("Unable to verify table schema. Please double check the steps"
//              + "in the README and try again.", ex);
//        }
      }
  }