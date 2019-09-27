package com.upic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

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
        int seasonId = Integer.parseInt(pathParts[3]);
        int dayId = Integer.parseInt(pathParts[5]);
        int skierId = Integer.parseInt(pathParts[7]);

        // todo check each id is valid

        if (skierId > 100) {
          response.setStatus(HttpServletResponse.SC_NOT_FOUND);
          out.write("{\"error\":\"Skier not found\"}");
          return;
        }

        if (resortId > 100) {
          response.setStatus(HttpServletResponse.SC_NOT_FOUND);
          out.write("{\"error\":\"Resort not found\"}");
          return;
        }

        if (seasonId > 100) {
          response.setStatus(HttpServletResponse.SC_NOT_FOUND);
          out.write("{\"error\":\"Season not found\"}");
          return;
        }

        if (dayId > 100) {
          response.setStatus(HttpServletResponse.SC_NOT_FOUND);
          out.write("{\"error\":\"Day not found\"}");
          return;
        }

        //todo: get body json

        StringBuffer jb = new StringBuffer();
        String line = null;
        String timeInfo;
        String liftIdInfo;
        try {
          BufferedReader reader = request.getReader();
          while ((line = reader.readLine()) != null)
            jb.append(line);
        } catch (Exception e) {
          LOGGER.error("Error reading request string");
          response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
          out.write("{\"message\":\"Error reading request string\"}");
          return;
        }
        System.out.println(jb.toString());
        try {
          JSONObject jsonObject = HTTP.toJSONObject(jb.toString());
          System.out.println(jsonObject.toString());

          timeInfo = jsonObject.getString("time");
          liftIdInfo = jsonObject.getString("liftId");
        } catch (JSONException e) {
          // crash and burn
          LOGGER.error("Error parsing JSON request string");
          response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
          out.write("{\"message\":\"Error parsing JSON request string\"}");
          return;
        }

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

        //todo create record

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

          if (skierId > 100) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.write("{\"error\":\"Skier not found\"}");
            return;
          }

          //todo: get resortid and season in query param and get return json

          response.setStatus(HttpServletResponse.SC_OK);
          out.write("{\"message\":\"get total vertical for skiers request received\"}");
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

        // todo check each id is valid

        if (skierId > 100) {
          response.setStatus(HttpServletResponse.SC_NOT_FOUND);
          out.write("{\"error\":\"Skier not found\"}");
          return;
        }

        if (resortId > 100) {
          response.setStatus(HttpServletResponse.SC_NOT_FOUND);
          out.write("{\"error\":\"Resort not found\"}");
          return;
        }

        if (seasonId > 100) {
          response.setStatus(HttpServletResponse.SC_NOT_FOUND);
          out.write("{\"error\":\"Season not found\"}");
          return;
        }

        if (dayId > 100) {
          response.setStatus(HttpServletResponse.SC_NOT_FOUND);
          out.write("{\"error\":\"Day not found\"}");
          return;
        }

        //todo get return json

        response.setStatus(HttpServletResponse.SC_OK);
        out.write("{\"message\":\"get vertical for skiers on specific day request received\"}");
        return;
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
