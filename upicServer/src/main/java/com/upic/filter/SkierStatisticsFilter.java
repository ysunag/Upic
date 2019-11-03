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
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

@WebFilter("/SkiersFilter")
public class SkierStatisticsFilter implements Filter {
  public static SkierStatistics stats;
//  public static String path;
  public static final int RECORD_BOUND = 499;

  public static final String LIFT_GET = "lift_get";
  public static final String LIFTDAY_GET = "lift_day_get";
  public static final String LIFT_POST = "lift_post";
  private String urlType;
  private static final Logger LOGGER = LogManager.getLogger(ResortsServlet.class.getName());


  @Override
  public void doFilter(ServletRequest sr, ServletResponse sr1, FilterChain fc) throws IOException, ServletException {
    HttpServletRequest hsr = (HttpServletRequest) sr;
    long start = System.currentTimeMillis();
    fc.doFilter(sr, sr1);
    long stop = System.currentTimeMillis();
    long time = stop - start;

    System.out.println("path from filter is " + hsr.getPathInfo());
    System.out.println("time is " + time);

    String[] pathParts = hsr.getPathInfo().split("/");
    System.out.println("hsr.getMethod() is " + hsr.getMethod());

    List<String> list;

    if (pathParts.length == 3) {
      //get list of lifts request
      urlType = LIFT_GET;
      list = stats.getLiftGetRecord();
//      stats.getLiftGetRecord().add(time);
//      System.out.println("stats.getLiftGetRecord().size(): " + stats.getLiftGetRecord().size());
//      if (stats.getLiftGetRecord().size() >= RECORD_BOUND) {
        //write into csv
//        File file = new File(path + LIFT_GET_FILE);
//        try(FileWriter writer = new FileWriter(file, false)) {
//          for(Long s : stats.getLiftGetRecord()) {
//            writer.write(s.toString() + "\n");
//          }
//        } catch (IOException e) {
//          System.err.println("Exception when writing record to csv");
//          e.printStackTrace();
//        }
//        stats.getLiftGetRecord().clear();
//      }
    }
    else if ("GET".equals(hsr.getMethod())) {
      //get list of lifts on a specific day
      urlType = LIFTDAY_GET;
      list = stats.getLiftDayGetRecord();
//      stats.getLiftDayGetRecord().add(time);
//      System.out.println("stats.getLiftDayGetRecord().size(): " + stats.getLiftDayGetRecord().size());
//      if (stats.getLiftDayGetRecord().size() >= RECORD_BOUND) {
//        //write into csv
//        File file = new File(path + LIFTDAY_GET_FILE);
//        try(FileWriter writer = new FileWriter(file, false)) {
//          for(Long s : stats.getLiftDayGetRecord()) {
//            writer.write(s.toString() + "\n");
//          }
//        } catch (IOException e) {
//          System.err.println("Exception when writing record to csv");
//          e.printStackTrace();
//        }
//        stats.getLiftDayGetRecord().clear();
//      }

    } else {
      //add season to specific resort
      urlType = LIFT_POST;
      list = stats.getLiftPostRecord();
//      stats.getLiftPostRecord().add(time);
//      System.out.println("stats.getLiftPostRecord().size(): " + stats.getLiftPostRecord().size());
//      if (stats.getLiftPostRecord().size() >= RECORD_BOUND) {
//        //write into csv
//        File file = new File(path + LIFT_POST_FILE);
//        try(FileWriter writer = new FileWriter(file, false)) {
//          for(Long s : stats.getLiftPostRecord()) {
//            writer.write(s.toString() + "\n");
//          }
//          System.out.println("===================================================================");
//          System.out.println("wrote lift post records to csv");
//          System.out.println("file path is : " + file.getAbsolutePath());
//        } catch (IOException e) {
//          System.err.println("Exception when writing record to csv");
//          e.printStackTrace();
//        }
//        stats.getLiftPostRecord().clear();
//      }
    }


    int size = list.size();

    if (size == RECORD_BOUND) {

      StringBuilder sb = new StringBuilder();
      sb.append("INSERT INTO statistics (latency, url_type)"
              + " VALUES ");
      for (String str : list) {
        sb.append(str);
      }
      sb.append("('" + time + "','" + urlType + "');");

      Connection conn = null;
      try {
        conn = ConnectionPool.getInstance().getConnection();
        Statement stmt = null;
        stmt = conn.createStatement();
//      int random = ThreadLocalRandom.current().nextInt(500);
//      String insertStat = "INSERT INTO statistics (latency, url_type, id)"
//              + " VALUES ('" + time + "','" + urlType + "','" + random + "')";
//        String insertStat = "INSERT INTO statistics (latency, url_type)"
//                + " VALUES ('" + time + "','" + urlType + "')";
        //todo
        String deleteStat = "DELETE FROM statistics" +
                " WHERE" +
                " url_type = '" + urlType + "';";
        stmt.executeUpdate(deleteStat);

        stmt.executeUpdate(sb.toString());
        list.clear();
        stmt.close();
        conn.close();
      } catch (SQLException e) {
        e.printStackTrace();
        LOGGER.error(e.getMessage());
      }
    } else {
      list.add("('" + time + "','" + urlType + "'),");
    }

  }

  @Override
  public void destroy() {
//    if (stats.getLiftGetRecord().size() != 0) {
//      //write into csv
//      File file = new File(path + LIFT_GET_END_FILE);
//      try(FileWriter writer = new FileWriter(file, false)) {
//        for(Long s : stats.getLiftGetRecord()) {
//          writer.write(s.toString() + "\n");
//        }
//      } catch (IOException e) {
//        System.err.println("Exception when writing record to csv");
//        e.printStackTrace();
//      }
//      stats.getLiftGetRecord().clear();
//    }
//
//    if (stats.getLiftDayGetRecord().size() != 0) {
//      //write into csv
//      File file = new File(path + LIFTDAY_GET_END_FILE);
//      try (FileWriter writer = new FileWriter(file, false)) {
//        for (Long s : stats.getLiftDayGetRecord()) {
//          writer.write(s.toString() + "\n");
//        }
//      } catch (IOException e) {
//        System.err.println("Exception when writing record to csv");
//        e.printStackTrace();
//      }
//      stats.getLiftDayGetRecord().clear();
//    }
//
//    if (stats.getLiftPostRecord().size() != 0) {
//      //write into csv
//      File file = new File(path + LIFT_POST_END_FILE);
//      try(FileWriter writer = new FileWriter(file, false)) {
//        for(Long s : stats.getLiftPostRecord()) {
//          writer.write(s.toString()+ "\n");
//        }
//      } catch (IOException e) {
//        System.err.println("Exception when writing record to csv");
//        e.printStackTrace();
//      }
//      stats.getLiftPostRecord().clear();
//    }
  }

  @Override
  public void init(FilterConfig fc) throws ServletException {
    stats = new SkierStatistics();
//    path = "/Users/yang/Documents/NEU/CS6650/Upic/" + STATISTICS_DIR + "/";
//    Path currentRelativePath = Paths.get("");
////    path = currentRelativePath.toAbsolutePath().getParent().toString() + "/" + STATISTICS_DIR;
////    new File(path).mkdirs();
////    path = path + "/";
//    path = "/var/tmp/";
    System.out.println("skier filter initialized");
  }

}

