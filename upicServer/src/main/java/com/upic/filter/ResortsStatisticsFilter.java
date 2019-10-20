package com.upic.filter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

@WebFilter("/ResortsFilter")
public class ResortsStatisticsFilter implements Filter {
  public static ResortsStatistics stats;
  public static String path;
  public static final int RECORD_BOUND = 1000;

  public static final String STATISTICS_DIR = "statistics";
  public static final String RESORT_GET_FILE = "resortGet.csv";
  public static final String RESORT_GET_END_FILE = "resortGetEnd.csv";
  public static final String SEASON_GET_FILE = "seasonGet.csv";
  public static final String SEASON_GET_END_FILE = "seasonGetEnd.csv";
  public static final String SEASON_POST_FILE = "seasonPost.csv";
  public static final String SEASON_POST_END_FILE = "seasonPostEnd.csv";

  @Override
  public void doFilter(ServletRequest sr, ServletResponse sr1, FilterChain fc) throws IOException, ServletException {
    HttpServletRequest hsr = (HttpServletRequest) sr;
    long start = System.currentTimeMillis();
    fc.doFilter(sr, sr1);
    long stop = System.currentTimeMillis();
    long time = stop - start;


    if (hsr.getPathInfo() == null) {
      //get list of resorts request

      stats.getResortsGetRecord().add(time);
      if (stats.getResortsGetRecord().size() >= RECORD_BOUND) {
        //write into csv
        File file = new File(path + RESORT_GET_FILE);
        try(FileWriter writer = new FileWriter(file, false)) {
          for(Long s : stats.getResortsGetRecord()) {
            writer.write(s.toString() + "\n");
          }
        } catch (IOException e) {
          System.err.println("Exception when writing record to csv");
          e.printStackTrace();
        }
        stats.getResortsGetRecord().clear();
      }
    }
    else if ("GET".equals(hsr.getMethod())) {
      //get list of seasons for specific resort
      stats.getSeasonsGetRecord().add(time);
      if (stats.getSeasonsGetRecord().size() >= RECORD_BOUND) {
        //write into csv
        File file = new File(path + SEASON_GET_FILE);
        try(FileWriter writer = new FileWriter(file, false)) {
          for(Long s : stats.getSeasonsGetRecord()) {
            writer.write(s.toString() + "\n");
          }
        } catch (IOException e) {
          System.err.println("Exception when writing record to csv");
          e.printStackTrace();
        }
        stats.getSeasonsGetRecord().clear();
      }

    } else {
      //add season to specific resort
      stats.getSeasonsPostRecord().add(time);
      if (stats.getSeasonsPostRecord().size() >= RECORD_BOUND) {
        //write into csv
        File file = new File(path + SEASON_POST_FILE);
        try(FileWriter writer = new FileWriter(file, false)) {
          for(Long s : stats.getSeasonsPostRecord()) {
            writer.write(s.toString() + "\n");
          }
        } catch (IOException e) {
          System.err.println("Exception when writing record to csv");
          e.printStackTrace();
        }
        stats.getSeasonsPostRecord().clear();
      }
    }
  }

  @Override
  public void destroy() {
    //todo write into csv
    if (stats.getResortsGetRecord().size() != 0) {
      //write into csv
      File file = new File(path + RESORT_GET_END_FILE);
      try(FileWriter writer = new FileWriter(file, false)) {
        for(Long s : stats.getResortsGetRecord()) {
          writer.write(s.toString() + "\n");
        }
      } catch (IOException e) {
        System.err.println("Exception when writing record to csv");
        e.printStackTrace();
      }
      stats.getResortsGetRecord().clear();
    }

    if (stats.getSeasonsGetRecord().size() != 0) {
      //write into csv
      File file = new File(path + SEASON_GET_END_FILE);
      try (FileWriter writer = new FileWriter(file, false)) {
        for (Long s : stats.getSeasonsGetRecord()) {
          writer.write(s.toString() + "\n");
        }
      } catch (IOException e) {
        System.err.println("Exception when writing record to csv");
        e.printStackTrace();
      }
      stats.getSeasonsGetRecord().clear();
    }

      if (stats.getSeasonsPostRecord().size() != 0) {
        //write into csv
        File file = new File(path + SEASON_POST_END_FILE);
        try(FileWriter writer = new FileWriter(file, false)) {
          for(Long s : stats.getSeasonsPostRecord()) {
            writer.write(s.toString()+ "\n");
          }
        } catch (IOException e) {
          System.err.println("Exception when writing record to csv");
          e.printStackTrace();
        }
        stats.getSeasonsPostRecord().clear();
      }
  }

  @Override
  public void init(FilterConfig fc) throws ServletException {
    stats = new ResortsStatistics();
//    Path currentRelativePath = Paths.get("");
//    path = currentRelativePath.toAbsolutePath().getParent().toString() + "/" + STATISTICS_DIR + "/";
    path = "/Users/yang/Documents/NEU/CS6650/Upic/" + STATISTICS_DIR + "/";
  }

}
