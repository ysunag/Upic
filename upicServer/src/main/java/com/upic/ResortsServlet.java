package com.upic;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet(urlPatterns = "/resorts/*")
public class ResortsServlet extends HttpServlet {
  private static final Logger LOGGER = LogManager.getLogger(ResortsServlet.class.getName());

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    try {
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");
      PrintWriter out = response.getWriter();

      String pathInfo = request.getPathInfo();
      if(pathInfo == null) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        out.write("{\"message\":\"invalid request\"}");
        return;
      }

      String[] pathParts = pathInfo.split("/");

      if (pathParts == null|| pathParts.length != 3 || !pathParts[2].equals("seasons")) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        out.write("{\"message\":\"invalid request\"}");
        return;
      }
      if (!isInteger(pathParts[1], 10)) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        out.write("{\"message\":\"Invalid Resort ID supplied\"}");
        return;
      }
      int resortId = Integer.parseInt(pathParts[1]);


      String yearInfo;

      String body = IOUtils.toString(request.getReader());
      JsonParser parser = new JsonParser();
      JsonElement element = parser.parse(body);
      JsonObject jsonObject = element.getAsJsonObject();
      yearInfo = jsonObject.get("year").getAsString();

      System.out.println(yearInfo);
      if (!isInteger(yearInfo, 10)) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        out.write("{\"message\":\"Invalid year supplied\"}");
        return;
      }
      int year = Integer.parseInt(yearInfo);

      // todo add year to the resort
      Connection conn = null;
      try {
        conn = ConnectionPool.getInstance().getConnection();
        Statement stmt = null;
        stmt = conn.createStatement();
        String insertYear = "INSERT INTO seasons (season_id, resort_id)"
                + "VALUES (" + yearInfo + "," + resortId + ")";
        stmt.executeUpdate(insertYear);
        conn.close();
      } catch (SQLException e) {
        e.printStackTrace();
        LOGGER.error(e.getMessage());
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
      }

      response.setStatus(HttpServletResponse.SC_CREATED);
      out.print("{\"message\":\"add new season in year" + year + " for resort " + resortId + " request received\"}");
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    try {
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");
      PrintWriter out = response.getWriter();

      String pathInfo = request.getPathInfo();
      if(pathInfo == null) {
        response.setStatus(HttpServletResponse.SC_OK);
        out.print("{\"message\":\"getResorts request received\"}");

        Connection conn = null;
        try {

          conn = ConnectionPool.getInstance().getConnection();
          Statement stmt = null;
          stmt = conn.createStatement();
          String getStep = "SELECT * FROM resort;";
          ResultSet rs = stmt.executeQuery(getStep);
          JSONArray json = new JSONArray();

          while(rs.next()){
            JSONObject obj = new JSONObject();
            obj.put("resortName", rs.getString("resort_name"));
            obj.put("resortID", rs.getInt("resort_id"));
            json.put(obj);
          }

          response.setStatus(HttpServletResponse.SC_OK);
          out.write(json.toString());

        } catch (SQLException e) {
          e.printStackTrace();
        }

//        response.setStatus(HttpServletResponse.SC_OK);
//        out.write("{\"message\":\"get a list of seasons for the specified resort request received\"}");
      } else {
        String[] pathParts = pathInfo.split("/");
        System.out.println(pathParts.length);
        if (pathParts == null|| pathParts.length != 3 || !pathParts[2].equals("seasons")) {
          response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
          out.write("{\"message\":\"invalid request\"}");
          return;
        }
        if (!isInteger(pathParts[1], 10)) {
          response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
          out.write("{\"message\":\"Invalid Resort ID supplied\"}");
          return;
        }
        int resortId = Integer.parseInt(pathParts[1]);
//        todo check if resort can be found when connecting to database
//        if (!resortIdExist) {
//          response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//          out.write("{\"message\":\"Resort not found\"}");
//          return;
//        }

        Connection conn = null;
        try {

          conn = ConnectionPool.getInstance().getConnection();
          Statement stmt = null;
          stmt = conn.createStatement();
          String getStep = "SELECT DISTINCT" +
                  " season_id" +
                  " FROM" +
                  " season" +
                  " WHERE" +
                  " resort_id = '" + resortId + "';";
          ResultSet rs = stmt.executeQuery(getStep);
          JSONObject obj = new JSONObject();
          obj.put("seasons", rs.getArray("season_id"));
          response.setStatus(HttpServletResponse.SC_OK);
          out.write(obj.toString());
        } catch (SQLException e) {
          e.printStackTrace();
          response.setStatus(HttpServletResponse.SC_NOT_FOUND);
          out.write("{\"error\":\"seasons of given resort not found\"}");
        }
        response.setStatus(HttpServletResponse.SC_OK);
        out.write("{\"message\":\"get a list of seasons for the specified resort request received\"}");
      }
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  private boolean isInteger(String s, int radix) {
    if(s.isEmpty()) return false;
    for(int i = 0; i < s.length(); i++) {
      if(i == 0 && s.charAt(i) == '-') {
        if(s.length() == 1) return false;
      }
      if(Character.digit(s.charAt(i),radix) < 0) return false;
    }
    return true;
  }
}
