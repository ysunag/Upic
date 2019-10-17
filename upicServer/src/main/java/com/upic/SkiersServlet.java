package com.upic;

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

@WebServlet(urlPatterns = "/skiers/*")
public class SkiersServlet extends HttpServlet {
  private static final Logger LOGGER = LogManager.getLogger(ResortsServlet.class.getName());
  private static final String VERTICAL = "vertical";
  private static final String SEASONS = "seasons";
  private static final String DAYS = "days";
  private static final String SKIERS = "skiers";



  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//    EntityManagerFactory emf =
//            (EntityManagerFactory)getServletContext().getAttribute("emf");
//    EntityManager em = emf.createEntityManager();

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

        // todo check each id is valid when connecting to database

//
//        if (!resortIdExist) {
//          response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//          out.write("{\"error\":\"Resort not found\"}");
//          return;
//        }
//
//        if (!seasonIdExist) {
//          response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//          out.write("{\"error\":\"Season not found\"}");
//          return;
//        }


        String body = IOUtils.toString(request.getReader());
        System.out.println(body);
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(body);
        JsonObject jsonObject = element.getAsJsonObject();
        String timeInfo = jsonObject.get("time").getAsString();
        String liftIdInfo = jsonObject.get("liftID").getAsString();
        System.out.println(timeInfo);
        System.out.println(liftIdInfo);

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

        //todo create record in databse
//        em.getTransaction().begin();
//        LiftEntity record = new LiftEntity();
//        record.setDayId(dayId);
//        record.setResortId(resortId);
//        record.setSeasonId(seasonId);
//        record.setLiftTime(time);
//        record.setSkierId(skierId);
//        em.persist(record);
//        em.getTransaction().commit();
        Connection conn = null;
        try {
          conn = ConnectionPool.getInstance().getConnection();
          Statement stmt = null;
          stmt = conn.createStatement();
          String insertRecord = "INSERT INTO lift (resort_id, season_id, day_id, skier_id, lift_time)"
                  + "VALUES (" + resortId + "," + seasonId + ","  + dayId
                  + "," + skierId + "," + time + ")";
          stmt.executeUpdate(insertRecord);
          conn.close();
        } catch (SQLException e) {
          e.printStackTrace();
          LOGGER.error(e.getMessage());
          response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }

        response.setStatus(HttpServletResponse.SC_CREATED);
        out.write("{\"message\":\"create lift request received\"}");
        return;
      }
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//    EntityManagerFactory emf =
//            (EntityManagerFactory)getServletContext().getAttribute("emf");
//    EntityManager em = emf.createEntityManager();


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

          //todo: get return json from database
//          Query q = em.createQuery("select l from LiftEntity l");
//          List<LiftEntity> liftList = q.getResultList();
//
//
//          if (liftList.size() <= 0) {
//            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//            out.write("{\"error\":\"Skier not found\"}");
//            return;
//          }

          //todo: form liftlist to json

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
                    " resort_id = '" + resortId + "' AND season_id = '"
                    + seasonId + "' AND skier_id = '" + skierId + "';";
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

          } catch (SQLException e) {
            e.printStackTrace();
          }

          //out.write("{\"message\":\"get total vertical for skiers request received\"}");
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

        //todo: get return json from database
//        Query q = em.createQuery("select l from LiftEntity l");
//        List<LiftEntity> liftDayList = q.getResultList();

//
//        if (liftDayList.size() <= 0) {
//          response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//          out.write("{\"error\":\"get vertical for skiers on specific day not found\"}");
//          return;
//        }

        //todo: form liftlist to json
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
                  " resort_id = '" + resortId + "' AND season_id = '"
                  + seasonId + "' AND skier_id = '" + skierId + "' AND day_id = '" + dayId + "';";
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
          response.setStatus(HttpServletResponse.SC_OK);
          out.write(totalDayVert);
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
