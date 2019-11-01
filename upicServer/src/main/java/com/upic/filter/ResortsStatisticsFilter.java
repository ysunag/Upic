package com.upic.filter;

import com.upic.servlet.ConnectionPool;
import com.upic.servlet.ResortsServlet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebFilter("/ResortsFilter")
public class ResortsStatisticsFilter implements Filter {
//  public static ResortsStatistics stats;
//  public static String path;
//  public static final int RECORD_BOUND = 500;

//  public static final String STATISTICS_DIR = "statistics";
  public static final String RESORT_GET = "resort_get";
  public static final String SEASON_GET = "season_get";
  public static final String SEASON_POST = "season_post";
  private String urlType;
  private static final Logger LOGGER = LogManager.getLogger(ResortsServlet.class.getName());


  @Override
  public void doFilter(ServletRequest sr, ServletResponse sr1, FilterChain fc)
          throws IOException, ServletException {
    HttpServletRequest hsr = (HttpServletRequest) sr;
    long start = System.currentTimeMillis();
    fc.doFilter(sr, sr1);
    long stop = System.currentTimeMillis();
    long time = stop - start;


    if (hsr.getPathInfo() == null) {
      //get list of resorts request
      urlType = RESORT_GET;
//      stats.getResortsGetRecord().add(time);
//      if (stats.getResortsGetRecord().size() >= RECORD_BOUND) {
//        //write into csv
//        File file = new File(path + RESORT_GET_FILE);
//        try(FileWriter writer = new FileWriter(file, false)) {
//          for(Long s : stats.getResortsGetRecord()) {
//            writer.write(s.toString() + "\n");
//          }
//        } catch (IOException e) {
//          System.err.println("Exception when writing record to csv");
//          e.printStackTrace();
//        }
//        stats.getResortsGetRecord().clear();
//      }
    }
    else if ("GET".equals(hsr.getMethod())) {
      //get list of seasons for specific resort
      urlType = SEASON_GET;
//      stats.getSeasonsGetRecord().add(time);
//      if (stats.getSeasonsGetRecord().size() >= RECORD_BOUND) {
        //write into csv
//        File file = new File(path + SEASON_GET_FILE);
//        try(FileWriter writer = new FileWriter(file, false)) {
//          for(Long s : stats.getSeasonsGetRecord()) {
//            writer.write(s.toString() + "\n");
//          }
//        } catch (IOException e) {
//          System.err.println("Exception when writing record to csv");
//          e.printStackTrace();
//        }
//        stats.getSeasonsGetRecord().clear();
//      }

    } else {
      //add season to specific resort
      urlType = SEASON_POST;
//      stats.getSeasonsPostRecord().add(time);
//      if (stats.getSeasonsPostRecord().size() >= RECORD_BOUND) {
        //write into csv
//        File file = new File(path + SEASON_POST_FILE);
//        try(FileWriter writer = new FileWriter(file, false)) {
//          for(Long s : stats.getSeasonsPostRecord()) {
//            writer.write(s.toString() + "\n");
//          }
//        } catch (IOException e) {
//          System.err.println("Exception when writing record to csv");
//          e.printStackTrace();
//        }
//        stats.getSeasonsPostRecord().clear();
//      }
    }
        Connection conn = null;
        try {
          conn = ConnectionPool.getInstance().getConnection();
          Statement stmt = null;
          stmt = conn.createStatement();
          String insertStat = "INSERT INTO statistics (latency, url_type)"
          + " VALUES (" + time + "," + urlType + ")";
          stmt.executeUpdate(insertStat);
          conn.close();
        } catch (SQLException e) {
          e.printStackTrace();
          LOGGER.error(e.getMessage());
        }
  }

  @Override
  public void destroy() {

//    if (stats.getResortsGetRecord().size() != 0) {
      //write into csv
//      File file = new File(path + RESORT_GET_END_FILE);
//      try(FileWriter writer = new FileWriter(file, false)) {
//        for(Long s : stats.getResortsGetRecord()) {
//          writer.write(s.toString() + "\n");
//        }
//      } catch (IOException e) {
//        System.err.println("Exception when writing record to csv");
//        e.printStackTrace();
//      }
//      stats.getResortsGetRecord().clear();
//    }
//
//    if (stats.getSeasonsGetRecord().size() != 0) {
      //write into csv
//      File file = new File(path + SEASON_GET_END_FILE);
//      try (FileWriter writer = new FileWriter(file, false)) {
//        for (Long s : stats.getSeasonsGetRecord()) {
//          writer.write(s.toString() + "\n");
//        }
//      } catch (IOException e) {
//        System.err.println("Exception when writing record to csv");
//        e.printStackTrace();
//      }
//      stats.getSeasonsGetRecord().clear();
//    }
//
//      if (stats.getSeasonsPostRecord().size() != 0) {
//        //write into csv
//        File file = new File(path + SEASON_POST_END_FILE);
//        try(FileWriter writer = new FileWriter(file, false)) {
//          for(Long s : stats.getSeasonsPostRecord()) {
//            writer.write(s.toString()+ "\n");
//          }
//        } catch (IOException e) {
//          System.err.println("Exception when writing record to csv");
//          e.printStackTrace();
//        }
//        stats.getSeasonsPostRecord().clear();
//      }
  }

  @Override
  public void init(FilterConfig fc) throws ServletException {
//    stats = new ResortsStatistics();
//    Path currentRelativePath = Paths.get("");
//    path = currentRelativePath.toAbsolutePath().getParent().toString() + "/" + STATISTICS_DIR;
//    new File(path).mkdirs();
//    path = path + "/";
//    path = "/var/tmp/";
    //path = "/Users/yang/Documents/NEU/CS6650/Upic/" + STATISTICS_DIR + "/";
    System.out.println("resort filter initialized");
  }

}
