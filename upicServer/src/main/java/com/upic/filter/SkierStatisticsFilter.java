package com.upic.filter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

@WebFilter("/SkiersFilter")
public class SkierStatisticsFilter implements Filter {
  public static SkierStatistics stats;
  public static String path;
  public static final String STATISTICS_DIR = "statistics";
  public static final String LIFT_GET_FILE = "liftGet.csv";
  public static final String LIFT_GET_END_FILE = "liftGetEnd.csv";
  public static final String LIFTDAY_GET_FILE = "liftDayGet.csv";
  public static final String LIFTDAY_GET_END_FILE = "liftDayGetEnd.csv";
  public static final String LIFT_POST_FILE = "liftPost.csv";
  public static final String LIFT_POST_END_FILE = "liftPostEnd.csv";

  @Override
  public void doFilter(ServletRequest sr, ServletResponse sr1, FilterChain fc) throws IOException, ServletException {
    HttpServletRequest hsr = (HttpServletRequest) sr;
    long start = System.currentTimeMillis();
    fc.doFilter(sr, sr1);
    long stop = System.currentTimeMillis();
    long time = stop - start;


    if (hsr.getPathInfo() == null) {
      //get list of lifts request

      stats.getLiftGetRecord().add(time);
      if (stats.getLiftGetRecord().size() == 10000) {
        //write into csv
        File file = new File(path + LIFT_GET_FILE);
        try(FileWriter writer = new FileWriter(file, false)) {
          for(Long s : stats.getLiftGetRecord()) {
            writer.write(s.toString() + "\n");
          }
        } catch (IOException e) {
          System.err.println("Exception when writing record to csv");
          e.printStackTrace();
        }
        stats.getLiftGetRecord().clear();
      }
    }
    else if ("GET".equals(hsr.getMethod())) {
      //get list of lifts on a specific day
      stats.getLiftDayGetRecord().add(time);
      if (stats.getLiftDayGetRecord().size() == 10000) {
        //write into csv
        File file = new File(path + LIFTDAY_GET_FILE);
        try(FileWriter writer = new FileWriter(file, false)) {
          for(Long s : stats.getLiftDayGetRecord()) {
            writer.write(s.toString() + "\n");
          }
        } catch (IOException e) {
          System.err.println("Exception when writing record to csv");
          e.printStackTrace();
        }
        stats.getLiftDayGetRecord().clear();
      }

    } else {
      //add season to specific resort
      stats.getLiftPostRecord().add(time);
      if (stats.getLiftPostRecord().size() == 10000) {
        //write into csv
        File file = new File(path + LIFT_POST_FILE);
        try(FileWriter writer = new FileWriter(file, false)) {
          for(Long s : stats.getLiftPostRecord()) {
            writer.write(s.toString() + "\n");
          }
        } catch (IOException e) {
          System.err.println("Exception when writing record to csv");
          e.printStackTrace();
        }
        stats.getLiftPostRecord().clear();
      }
    }
  }

  @Override
  public void destroy() {
    //todo write into csv
    if (stats.getLiftGetRecord().size() != 0) {
      //write into csv
      File file = new File(path + LIFT_GET_END_FILE);
      try(FileWriter writer = new FileWriter(file, false)) {
        for(Long s : stats.getLiftGetRecord()) {
          writer.write(s.toString() + "\n");
        }
      } catch (IOException e) {
        System.err.println("Exception when writing record to csv");
        e.printStackTrace();
      }
      stats.getLiftGetRecord().clear();
    }

    if (stats.getLiftDayGetRecord().size() != 0) {
      //write into csv
      File file = new File(path + LIFTDAY_GET_END_FILE);
      try (FileWriter writer = new FileWriter(file, false)) {
        for (Long s : stats.getLiftDayGetRecord()) {
          writer.write(s.toString() + "\n");
        }
      } catch (IOException e) {
        System.err.println("Exception when writing record to csv");
        e.printStackTrace();
      }
      stats.getLiftDayGetRecord().clear();
    }

    if (stats.getLiftPostRecord().size() != 0) {
      //write into csv
      File file = new File(path + LIFT_POST_END_FILE);
      try(FileWriter writer = new FileWriter(file, false)) {
        for(Long s : stats.getLiftPostRecord()) {
          writer.write(s.toString()+ "\n");
        }
      } catch (IOException e) {
        System.err.println("Exception when writing record to csv");
        e.printStackTrace();
      }
      stats.getLiftPostRecord().clear();
    }
  }

  @Override
  public void init(FilterConfig fc) throws ServletException {
    stats = new SkierStatistics();
    Path currentRelativePath = Paths.get("");
    path = currentRelativePath.toAbsolutePath().getParent().toString() + "/" + STATISTICS_DIR + "/";
    new File(path + LIFT_GET_FILE);
    new File(path + LIFT_GET_END_FILE);
    new File(path + LIFTDAY_GET_FILE);
    new File(path + LIFTDAY_GET_END_FILE);
    new File(path + LIFT_POST_FILE);
    new File(path + LIFT_POST_END_FILE);
  }

}

