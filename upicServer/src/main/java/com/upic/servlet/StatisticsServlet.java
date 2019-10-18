package com.upic.servlet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import static com.upic.filter.ResortsStatisticsFilter.STATISTICS_DIR;
import static com.upic.filter.SkierStatisticsFilter.LIFTDAY_GET_END_FILE;
import static com.upic.filter.SkierStatisticsFilter.LIFTDAY_GET_FILE;
import static com.upic.filter.SkierStatisticsFilter.LIFT_GET_END_FILE;
import static com.upic.filter.SkierStatisticsFilter.LIFT_GET_FILE;

@WebServlet(urlPatterns = "/statistics/*")
public class StatisticsServlet extends HttpServlet {
  public static final String RESORT_GET = "resortGet";
  public static final String SEASON_GET = "seasonGet";
  public static final String SEASON_POST = "seasonPost";

  public static final String LIFT_GET = "liftGet";
  public static final String LIFTDAY_GET = "liftDayGet";
  public static final String LIFT_POST = "liftPost";

  private static final Logger LOGGER = LogManager.getLogger(StatisticsServlet.class.getName());


  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doGet(request, response);
  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    //todo read stored files and calculate
    Path currentRelativePath = Paths.get("");
    String path = currentRelativePath.toAbsolutePath().getParent().toString() + "/" + STATISTICS_DIR + "/";
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
      String req = pathInfo.split("/")[1];
      if (req.equals(RESORT_GET)) {
        fileName = RESORT_GET_FILE;
        endFileName = RESORT_GET_END_FILE;
      }

      if (req.equals(SEASON_GET)) {
        fileName = SEASON_GET_FILE;
        endFileName = SEASON_GET_END_FILE;
      }

      if (req.equals(SEASON_POST)) {
        fileName = SEASON_POST;
        endFileName = SEASON_POST;
      }

      if (req.equals(LIFT_GET)) {
        fileName = LIFT_GET_FILE;
        endFileName = LIFT_GET_END_FILE;
      }

      if (req.equals(LIFTDAY_GET)) {
        fileName = LIFTDAY_GET_FILE;
        endFileName = LIFTDAY_GET_END_FILE;
      }

      if (req.equals(LIFT_POST)) {
        fileName = LIFT_POST;
        endFileName = LIFT_POST;
      }

      long[] result = getStatistics(path + endFileName, path + fileName);
      JSONObject obj = new JSONObject();
      obj.put(req + "_mean_latency", result[0]);
      obj.put(req + "_max_latency", result[1]);
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
