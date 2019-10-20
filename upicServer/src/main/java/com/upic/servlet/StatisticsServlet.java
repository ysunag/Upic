package com.upic.servlet;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.IOUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.upic.filter.ResortsStatisticsFilter.RESORT_GET_END_FILE;
import static com.upic.filter.ResortsStatisticsFilter.RESORT_GET_FILE;
import static com.upic.filter.ResortsStatisticsFilter.SEASON_GET_END_FILE;
import static com.upic.filter.ResortsStatisticsFilter.SEASON_GET_FILE;
import static com.upic.filter.ResortsStatisticsFilter.SEASON_POST_END_FILE;
import static com.upic.filter.ResortsStatisticsFilter.SEASON_POST_FILE;
import static com.upic.filter.ResortsStatisticsFilter.STATISTICS_DIR;
import static com.upic.filter.SkierStatisticsFilter.LIFTDAY_GET_END_FILE;
import static com.upic.filter.SkierStatisticsFilter.LIFTDAY_GET_FILE;
import static com.upic.filter.SkierStatisticsFilter.LIFT_GET_END_FILE;
import static com.upic.filter.SkierStatisticsFilter.LIFT_GET_FILE;
import static com.upic.filter.SkierStatisticsFilter.LIFT_POST_END_FILE;
import static com.upic.filter.SkierStatisticsFilter.LIFT_POST_FILE;

@WebServlet(urlPatterns = "/statistics/*")
public class StatisticsServlet extends HttpServlet {
  public static final String GET = "get";
  public static final String POST = "post";

  public static final String RESORT = "/resorts";
  public static final String SEASON = "/resorts/{resortID}/seasons";

  public static final String LIFT_ALL = "/skiers/{skierID}/vertical";
  public static final String LIFT = "/skiers/{resortID}/seasons/{seasonID}/days/{dayID}/skiers/{skierID}";


  private static final Logger LOGGER = LogManager.getLogger(StatisticsServlet.class.getName());


  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doGet(request, response);
  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String path = "/Users/yang/Documents/NEU/CS6650/Upic/" + STATISTICS_DIR + "/";
//    Path currentRelativePath = Paths.get("");
//    String path = currentRelativePath.toAbsolutePath().getParent().toString() + "/" + STATISTICS_DIR + "/";
    String fileName = "";
    String endFileName = "";


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

      String body = IOUtils.toString(request.getReader());
      System.out.println(body);
      JsonParser parser = new JsonParser();
      JsonElement element = parser.parse(body);
      JsonObject jsonObject = element.getAsJsonObject();
      String operation = jsonObject.get("operation").getAsString();

      if (pathInfo.equals(RESORT)) {
        fileName = RESORT_GET_FILE;
        endFileName = RESORT_GET_END_FILE;
      }

      if (pathInfo.equals(SEASON) && operation.equals(GET)) {
        fileName = SEASON_GET_FILE;
        endFileName = SEASON_GET_END_FILE;
      }

      if (pathInfo.equals(SEASON) && operation.equals(POST)) {
        fileName = SEASON_POST_FILE;
        endFileName = SEASON_POST_END_FILE;
      }

      if (pathInfo.equals(LIFT_ALL)) {
        fileName = LIFT_GET_FILE;
        endFileName = LIFT_GET_END_FILE;
      }

      if (pathInfo.equals(LIFT) && operation.equals(GET)) {
        fileName = LIFTDAY_GET_FILE;
        endFileName = LIFTDAY_GET_END_FILE;
      }

      if (pathInfo.equals(LIFT) && operation.equals (POST)) {
        fileName = LIFT_POST_FILE;
        endFileName = LIFT_POST_END_FILE;
      }

      long[] result = getStatistics(path + endFileName, path + fileName);
      JSONObject obj = new JSONObject();
      obj.put("URL", pathInfo);
      obj.put("operation", operation);
      obj.put("mean", result[0]);
      obj.put("max", result[1]);
      response.setStatus(HttpServletResponse.SC_OK);
      out.write(obj.toString());
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  private long[] getStatistics(String endFileName, String fileName) {
    long max = -1;
    long total = 0;
    int count = 0;

    String line = "";
    long record = 0;
    try (BufferedReader br = new BufferedReader(new FileReader(endFileName))) {

      while ((line = br.readLine()) != null) {

        record = Long.parseLong(line);
        total += record;
        max = max < record? record : max;
        count += 1;

      }

    } catch (IOException e) {
      e.printStackTrace();
    }

    try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {

      while ((line = br.readLine()) != null) {
        record = Long.parseLong(line);
        total += record;
        max = max < record? record : max;
        count += 1;

      }

    } catch (IOException e) {
      e.printStackTrace();
    }
    long mean = count > 0? total / count : -1;
    return new long[]{mean, max};
  }
}
