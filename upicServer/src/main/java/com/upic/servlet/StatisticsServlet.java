package com.upic.servlet;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import static com.upic.filter.ResortsStatisticsFilter.RESORT_GET;
import static com.upic.filter.ResortsStatisticsFilter.SEASON_GET;
import static com.upic.filter.ResortsStatisticsFilter.SEASON_POST;
import static com.upic.filter.SkierStatisticsFilter.LIFTDAY_GET;
import static com.upic.filter.SkierStatisticsFilter.LIFT_GET;
import static com.upic.filter.SkierStatisticsFilter.LIFT_POST;


@WebServlet(urlPatterns = "/statistics/*")
public class StatisticsServlet extends HttpServlet {
  public static final String GET = "GET";
  public static final String POST = "POST";

  public static final String RESORT = "/resorts";
  public static final String SEASON = "/resorts/{resortID}/seasons";

  public static final String LIFT_ALL = "/skiers/{skierID}/vertical";
  public static final String LIFT = "/skiers/{resortID}/seasons/{seasonID}/days/{dayID}/skiers/{skierID}";


  private static final Logger LOGGER = LogManager.getLogger(StatisticsServlet.class.getName());


  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doGet(request, response);
  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    DataSource pool = (DataSource) getServletContext().getAttribute("my-pool");
    String type1 = "";
    String type2 = "";

    try {
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");
      PrintWriter out = response.getWriter();

      String pathInfo = request.getPathInfo();
      if (pathInfo == null) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        out.write("{\"message\":\"invalid request\"}");
        return;
      }

      if (pathInfo.equals(RESORT)) {
        type1 = RESORT_GET;
      }

      if (pathInfo.equals(SEASON)) {
        type1 = SEASON_GET;
        type2 = SEASON_POST;
      }

      if (pathInfo.equals(LIFT_ALL)) {
        type1 = LIFT_GET;
      }

      if (pathInfo.equals(LIFT)) {
        type1 = LIFTDAY_GET;
        type2 = LIFT_POST;
      }

      JSONObject obj = new JSONObject();

      long[] result1 = getStatistics(type1, pool);
      JSONObject obj1 = new JSONObject();
      obj1.put("URL", pathInfo);
      obj1.put("operation", GET);
      obj1.put("mean", result1[0]);
      obj1.put("max", result1[1]);

      JSONArray jsonArray = new JSONArray();
      jsonArray.put(obj1);


      if (type2.length() > 0) {
        long[] result2 = getStatistics(type2, pool);
        JSONObject obj2 = new JSONObject();
        obj2.put("URL", pathInfo);
        obj2.put("operation", POST);
        obj2.put("mean", result2[0]);
        obj2.put("max", result2[1]);
        jsonArray.put(obj2);
      }

      obj.put("endpointStats", jsonArray);
      response.setStatus(HttpServletResponse.SC_OK);
      out.write(obj.toString());
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  private long[] getStatistics(String typeVal, DataSource pool) {
    long max = -1;
    long total = 0;
    int count = 0;


    Connection conn = null;
    try {
      conn = pool.getConnection();
//      conn = ConnectionPool.getInstance().getConnection();
//      Statement stmt = null;
//      stmt = conn.createStatement();

      String getStat = "SELECT * FROM ( SELECT * FROM statistics ORDER BY id DESC LIMIT 1000) AS t " +
              "WHERE t.url_type = '" + typeVal + "';";
      PreparedStatement stmt = conn.prepareStatement(getStat);
      ResultSet rs = stmt.executeQuery(getStat);

      while (rs.next()) {
        long cur = Long.parseLong(rs.getString("latency"));
        max = Math.max(cur, max);
        total += cur;
        count += 1;
      }
      String deleteStat = "DELETE FROM statistics" +
              " WHERE" +
              " url_type = '" + typeVal + "';";
      stmt.executeUpdate(deleteStat);
      stmt.close();
      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    long mean = count > 0? total / count : -1;
    return new long[]{mean, max};

  }
}
