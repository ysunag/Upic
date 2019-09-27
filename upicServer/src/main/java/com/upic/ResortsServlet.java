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

//      String body = getBody(request);
      String yearInfo;

      StringBuffer jb = new StringBuffer();
      String line = null;
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
        yearInfo = jsonObject.getString("message");
      } catch (JSONException e) {
        // crash and burn
        LOGGER.error("Error parsing JSON request string");
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        out.write("{\"message\":\"Error parsing JSON request string\"}");
        return;
      }

      System.out.println(yearInfo);
      if (!isInteger(yearInfo, 10)) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        out.write("{\"message\":\"Invalid year supplied\"}");
        return;
      }
      int year = Integer.parseInt(yearInfo);

      // todo add year to the resort

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
          //response.sendError(HttpServletResponse.SC_BAD_REQUEST, "invalid request");
          return;
        }
        int resortId = Integer.parseInt(pathParts[1]);
        if (resortId > 100) {
          response.setStatus(HttpServletResponse.SC_NOT_FOUND);
          out.write("{\"message\":\"Resort not found\"}");
          return;
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

//  public static String getBody(HttpServletRequest request) throws IOException {
//
//    String body = null;
//    StringBuilder sb = new StringBuilder();
//    BufferedReader bufferedReader = null;
//
//    try {
//      InputStream inputStream = request.getInputStream();
//      if (inputStream != null) {
//        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//        char[] charBuffer = new char[128];
//        int bytesRead = -1;
//        while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
//          sb.append(charBuffer, 0, bytesRead);
//        }
//      } else {
//        sb.append("");
//      }
//    } catch (IOException e) {
//      LOGGER.error(e.getMessage());
//    } finally {
//      if (bufferedReader != null) {
//        try {
//          bufferedReader.close();
//        } catch (IOException e) {
//          LOGGER.error(e.getMessage());
//        }
//      }
//    }
//
//    body = sb.toString();
//    return body;
//  }

  // todo: output and parse json. dummy data to check not found and id valid
}
