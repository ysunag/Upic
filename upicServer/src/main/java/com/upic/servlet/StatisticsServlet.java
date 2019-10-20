package com.upic.servlet;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.IOUtils;
import org.json.JSONArray;
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
//    String path = "/Users/yang/Documents/NEU/CS6650/Upic/" + STATISTICS_DIR + "/";
//    Path currentRelativePath = Paths.get("");
//    String path = currentRelativePath.toAbsolutePath().getParent().toString() + "/" + STATISTICS_DIR + "/";
    String path = "/var/tmp/";
    String fileName1 = "";
    String endFileName1 = "";
    String fileName2 = "";
    String endFileName2 = "";


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

//      String body = IOUtils.toString(request.getReader());
//      System.out.println(body);
//      JsonParser parser = new JsonParser();
//      JsonElement element = parser.parse(body);
//      JsonObject jsonObject = element.getAsJsonObject();
//      String operation = jsonObject.get("operation").getAsString();

      if (pathInfo.equals(RESORT)) {
        fileName1 = RESORT_GET_FILE;
        endFileName1 = RESORT_GET_END_FILE;
      }

      if (pathInfo.equals(SEASON)) {
        fileName1 = SEASON_GET_FILE;
        endFileName1 = SEASON_GET_END_FILE;

        fileName2 = SEASON_POST_FILE;
        endFileName2 = SEASON_POST_END_FILE;
      }

      if (pathInfo.equals(LIFT_ALL)) {
        fileName1 = LIFT_GET_FILE;
        endFileName1 = LIFT_GET_END_FILE;
      }

      if (pathInfo.equals(LIFT)) {
        fileName1 = LIFTDAY_GET_FILE;
        endFileName1 = LIFTDAY_GET_END_FILE;

        fileName2 = LIFT_POST_FILE;
        endFileName2 = LIFT_POST_END_FILE;
      }

      JSONObject obj = new JSONObject();

      long[] result1 = getStatistics(path + endFileName1, path + fileName1);
      JSONObject obj1 = new JSONObject();
      obj1.put("URL", pathInfo);
      obj1.put("operation", GET);
      obj1.put("mean", result1[0]);
      obj1.put("max", result1[1]);

      JSONArray jsonArray = new JSONArray();
      jsonArray.put(obj1);


      if (endFileName2.length() > 0) {
        long[] result2 = getStatistics(path + endFileName2, path + fileName2);
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
