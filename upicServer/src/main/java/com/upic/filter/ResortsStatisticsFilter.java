package com.upic.filter;

import com.upic.servlet.ResortsServlet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
import javax.sql.DataSource;

@WebFilter("/ResortsFilter")
public class ResortsStatisticsFilter implements Filter {
  public static ResortsStatistics stats;
  public static final int RECORD_BOUND = 499;

  public static final String RESORT_GET = "resort_get";
  public static final String SEASON_GET = "season_get";
  public static final String SEASON_POST = "season_post";
  private String urlType;
  private static final Logger LOGGER = LogManager.getLogger(ResortsServlet.class.getName());

  private FilterConfig config;
  @Override
  public void doFilter(ServletRequest sr, ServletResponse sr1, FilterChain fc)
          throws IOException, ServletException {

    HttpServletRequest hsr = (HttpServletRequest) sr;

    long start = System.currentTimeMillis();
    fc.doFilter(sr, sr1);
    long stop = System.currentTimeMillis();
    long time = stop - start;

    DataSource pool = (DataSource) config.getServletContext().getAttribute("my-pool");
    if (hsr.getPathInfo() == null) {
      //get list of resorts request
      urlType = RESORT_GET;
    }
    else if ("GET".equals(hsr.getMethod())) {
      //get list of seasons for specific resort
      urlType = SEASON_GET;
    } else {
      //add season to specific resort
      urlType = SEASON_POST;
    }

    int size = stats.getRecords().size();

    if (size == RECORD_BOUND) {

      StringBuilder sb = new StringBuilder();
      sb.append("INSERT INTO statistics (latency, url_type)"
              + " VALUES ");
      for (String str : stats.getRecords()) {
        sb.append(str);
      }
      sb.append("('" + time + "','" + urlType + "');");

      Connection conn = null;
      try {
        conn = pool.getConnection();
//        conn = ConnectionPool.getInstance().getConnection();
//        Statement stmt = null;
////        stmt = conn.createStatement();
        PreparedStatement stmt = conn.prepareStatement(sb.toString());
        stmt.executeUpdate();
//        stmt.executeUpdate(sb.toString());
        stats.getRecords().clear();
        stmt.close();
        conn.close();
      } catch (SQLException e) {
        e.printStackTrace();
        LOGGER.error(e.getMessage());
      }
    } else {
      stats.getRecords().add("('" + time + "','" + urlType + "'),");
    }


  }

  @Override
  public void destroy() {
  }

  @Override
  public void init(FilterConfig fc) throws ServletException {
    stats = new ResortsStatistics();
    this.config = fc;
    System.out.println("resort filter initialized");
  }

}
