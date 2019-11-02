package com.upic.servlet;


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
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet(urlPatterns = "/resorts/*")
public class ResortsServlet extends HttpServlet {
  public static final int RETRY_TIME = 20;
  public static final Logger LOGGER = LogManager.getLogger(ResortsServlet.class.getName());

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

      Connection conn = null;
      Boolean retry = true;
      int i = 0;
      while (retry && i < RETRY_TIME) {
        try {
          conn = ConnectionPool.getInstance().getConnection();
          Statement stmt = null;
          stmt = conn.createStatement();
          String insertYear = "INSERT INTO season (season_id, resort_id)"
                  + " VALUES (" + yearInfo + "," + resortId + ")";
          stmt.executeUpdate(insertYear);
          stmt.close();
          retry = false;
        } catch (SQLException e) {
          e.printStackTrace();
          LOGGER.error(e.getMessage());
          System.out.println(e.getErrorCode());
          if (e.getErrorCode() == 1062) {
            retry = false;
          } else {
            i++;
          }
        } finally {
          conn.close();
        }
      }
      if (retry) {
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      } else {
        response.setStatus(HttpServletResponse.SC_CREATED);
      }

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

        Connection conn = null;
        try {

          conn = ConnectionPool.getInstance().getConnection();
          Statement stmt = null;
          stmt = conn.createStatement();
          String getStep = "SELECT * FROM resort;";
          ResultSet rs = stmt.executeQuery(getStep);
          JSONArray jsonArray = new JSONArray();

          while(rs.next()){
            JSONObject resortObj = new JSONObject();
            resortObj.put("resortName", rs.getString("resort_name"));
            resortObj.put("resortID", rs.getInt("resort_id"));
            jsonArray.put(resortObj);
          }
          JSONObject obj = new JSONObject();
          obj.put("resorts", jsonArray);
          response.setStatus(HttpServletResponse.SC_OK);
          out.write(obj.toString());
          stmt.close();
          conn.close();

        } catch (SQLException e) {
          e.printStackTrace();
        }

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
        System.out.println("resortId is " + resortId);

        Connection conn = null;
        try {

          conn = ConnectionPool.getInstance().getConnection();
          Statement stmt = null;
          stmt = conn.createStatement();
          String getSeasons = "SELECT DISTINCT" +
                  " season_id" +
                  " FROM" +
                  " season" +
                  " WHERE" +
                  " resort_id = " + resortId + ";";
          ResultSet rs = stmt.executeQuery(getSeasons);
          JSONObject obj = new JSONObject();

          List<String> list = new ArrayList<>();
          while (rs.next()) {
            list.add(rs.getString("season_id"));
          }
          obj.put("seasons", list);
          response.setStatus(HttpServletResponse.SC_OK);
          out.write(obj.toString());
          stmt.close();
          conn.close();
        } catch (SQLException e) {
          e.printStackTrace();
          response.setStatus(HttpServletResponse.SC_NOT_FOUND);
          out.write("{\"error\":\"seasons of given resort not found\"}");
          return;
        }
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
