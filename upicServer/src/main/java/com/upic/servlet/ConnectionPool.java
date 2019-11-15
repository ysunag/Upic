//package com.upic.servlet;
//
//import org.apache.commons.dbcp2.BasicDataSource;
//
//import java.sql.SQLException;
//
//public class ConnectionPool {
//
//  private static BasicDataSource basicDataSource;
//
//  private ConnectionPool(){}
//
//  public static BasicDataSource getInstance() {
//    if (basicDataSource != null) {
//      return basicDataSource;
//    }
//    try {
//      Class.forName("com.mysql.cj.jdbc.Driver");
//    } catch (ClassNotFoundException e) {
//      e.printStackTrace();
//    }
//    basicDataSource = new BasicDataSource();
////    basicDataSource.setUrl("jdbc:mysql://localhost:3306/upic_schema?useSSL=false&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
//    basicDataSource.setUrl("jdbc:mysql://database-2.cgc3osq6curw.us-west-2.rds.amazonaws.com:3306/upic?useSSL=false&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
////    basicDataSource.setUsername("root");
////    basicDataSource.setPassword("rootroot");
//    basicDataSource.setUsername("admin");
//    basicDataSource.setPassword("adminadmin");
//    basicDataSource.setMinIdle(1);
//    basicDataSource.setMaxIdle(300);
//    basicDataSource.setMaxTotal(300);
//    basicDataSource.setMaxOpenPreparedStatements(100000000);
//    return basicDataSource;
//  }
//
//  public static void close() {
//    try {
//      basicDataSource.close();
//    } catch (SQLException e) {
//      e.printStackTrace();
//    }
//  }
//}