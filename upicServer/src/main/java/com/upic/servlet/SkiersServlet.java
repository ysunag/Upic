package com.upic.servlet;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.IOUtils;
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

import static com.upic.servlet.ResortsServlet.RETRY_TIME;

@WebServlet(urlPatterns = "/skiers/*")
public class SkiersServlet extends HttpServlet {
  private static final Logger LOGGER = LogManager.getLogger(ResortsServlet.class.getName());
  private static final String VERTICAL = "vertical";
  private static final String SEASONS = "seasons";
  private static final String DAYS = "days";
  private static final String SKIERS = "skiers";

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    try {
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");
      PrintWriter out = response.getWriter();

      String pathInfo = request.getPathInfo();
      if(pathInfo == null) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        out.write("{\"message\":\"invalid request\"}");
      } else {
        String[] pathParts = pathInfo.split("/");
        if (pathParts == null || pathParts.length != 8) {
          response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
          out.write("{\"message\":\"invalid request\"}");
          return;
        }

        if (!pathParts[2].equals(SEASONS) || !pathParts[4].equals(DAYS) || !pathParts[6].equals(SKIERS)) {
          response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
          out.write("{\"message\":\"Invalid request\"}");
          return;
        }

        for (int i = 1; i <= 7; i += 2) {
          if (!isInteger(pathParts[i], 10)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"message\":\"Invalid Skier ID supplied\"}");
            return;
          }
        }

        int resortId = Integer.parseInt(pathParts[1]);
        String seasonId = pathParts[3];
        String dayId = pathParts[5];
        int skierId = Integer.parseInt(pathParts[7]);


        String body = IOUtils.toString(request.getReader());
        //System.out.println(body);
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(body);
        JsonObject jsonObject = element.getAsJsonObject();
        String timeInfo = jsonObject.get("time").getAsString();
        String liftIdInfo = jsonObject.get("liftID").getAsString();
//        System.out.println(timeInfo);
//        System.out.println(liftIdInfo);

        if (!isInteger(timeInfo, 10)) {
          response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
          out.write("{\"message\":\"Invalid time supplied\"}");
          return;
        }
        int time = Integer.parseInt(timeInfo);
        if (!isInteger(liftIdInfo, 10)) {
          response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
          out.write("{\"message\":\"Invalid liftId supplied\"}");
          return;
        }
        int liftId = Integer.parseInt(liftIdInfo);


        Boolean retry = true;
        int i = 0;

        Connection conn = null;

        while (retry && i < RETRY_TIME) {
          try {
            conn = ConnectionPool.getInstance().getConnection();
            Statement stmt = null;
            stmt = conn.createStatement();
            String insertRecord = "INSERT INTO lift (resort_id, season_id, day_id, skier_id, lift_time, lift_id)"
                    + "VALUES (" + resortId + "," + seasonId + "," + dayId
                    + "," + skierId + "," + time + "," + liftId + ")";
            stmt.executeUpdate(insertRecord);
            conn.close();
            retry = false;
            break;
          } catch (SQLException e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage());
            System.out.println(e.getErrorCode());
          if (e.getErrorCode() == 1062) {
            retry = false;
          } else {
              i++;
          }
          }
        }
        if (retry) {
          response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } else {
          response.setStatus(HttpServletResponse.SC_CREATED);
          out.write("{\"message\":\"create lift request received\"}");
        }
        conn.close();
        return;
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
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        out.write("{\"message\":\"invalid request\"}");
      } else {
        String[] pathParts = pathInfo.split("/");
        if (pathParts == null || (pathParts.length != 3 && pathParts.length != 8)) {
          response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
          out.write("{\"message\":\"invalid request\"}");
          return;
        }

        if (pathParts.length == 3) {
          if (!pathParts[2].equals(VERTICAL)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"message\":\"invalid request\"}");
            return;
          }
          if (!isInteger(pathParts[1], 10)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"message\":\"Invalid Skier ID supplied\"}");
            return;
          }

          int skierId = Integer.parseInt(pathParts[1]);
          String resortId = request.getParameter("resort");
          String seasonId = request.getParameter("season");

          Connection conn = null;
          try {
            int totalVert = 0;
            conn = ConnectionPool.getInstance().getConnection();
            Statement stmt = null;
            stmt = conn.createStatement();
            String getStep = "SELECT" +
                    " lift_id" +
                    " FROM" +
                    " lift" +
                    " WHERE" +
                    " resort_id = " + resortId + " AND season_id = "
                    + seasonId + " AND skier_id = " + skierId + ";";
            ResultSet rs = stmt.executeQuery(getStep);
            if (rs.next()) {
              totalVert = 1;
            } else {
              response.setStatus(HttpServletResponse.SC_NOT_FOUND);
              out.write("{\"error\":\"Skier vertical not found\"}");
              return;
            }
            while(rs.next()){
              totalVert += 1;
            }
            JSONObject responseJson = new JSONObject();
            responseJson.put("seasonID", seasonId);
            responseJson.put("totalVert", totalVert);
            response.setStatus(HttpServletResponse.SC_OK);
            out.write(responseJson.toString());
            conn.close();

          } catch (SQLException e) {
            e.printStackTrace();
          }
          return;
        }

        if (!pathParts[2].equals(SEASONS) || !pathParts[4].equals(DAYS) || !pathParts[6].equals(SKIERS)) {
          response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
          out.write("{\"message\":\"Invalid request\"}");
          return;
        }

        for (int i = 1; i <= 7; i += 2) {
          if (!isInteger(pathParts[i], 10)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"message\":\"Invalid Skier ID supplied\"}");
            return;
          }
        }

        int resortId = Integer.parseInt(pathParts[1]);
        int seasonId = Integer.parseInt(pathParts[3]);
        int dayId = Integer.parseInt(pathParts[5]);
        int skierId = Integer.parseInt(pathParts[7]);

        Connection conn = null;
        try {
          int totalDayVert = 0;
          conn = ConnectionPool.getInstance().getConnection();
          Statement stmt = null;
          stmt = conn.createStatement();
          String getStep = "SELECT" +
                  " lift_id" +
                  " FROM" +
                  " lift" +
                  " WHERE" +
                  " resort_id = " + resortId + " AND season_id = "
                  + seasonId + " AND skier_id = " + skierId + " AND day_id = " + dayId + ";";
          ResultSet rs = stmt.executeQuery(getStep);
          if (rs.next()) {
            totalDayVert = 1;
          } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.write("{\"error\":\"Skier vertical on specific day not found\"}");
            return;
          }
          while(rs.next()){
            totalDayVert += 1;
          }
         // System.out.println("day vert is " + totalDayVert);
          response.setStatus(HttpServletResponse.SC_OK);
          out.write(Integer.toString(totalDayVert));
          conn.close();
          return;
        } catch (SQLException e) {
          e.printStackTrace();
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
