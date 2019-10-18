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
      if (stats.getResortsGetRecord().size() == 10000) {
        //write into csv
        File file = new File("resortGet.csv");
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
      String singleRecord = "seasonGet" + "," + time + "\n";
      stats.getSeasonsGetRecord().add(time);
      if (stats.getSeasonsGetRecord().size() == 10000) {
        //write into csv
        File file = new File("seasonGet.csv");
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
      String singleRecord = "seasonPost" + "," + time + "\n";
      stats.getSeasonsPostRecord().add(time);
      if (stats.getSeasonsPostRecord().size() == 10000) {
        //write into csv
        File file = new File("seasonPost.csv");
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
      File file = new File("resortGetEnd.csv");
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
      File file = new File("seasonGetEnd.csv");
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
        File file = new File("seasonPostEnd.csv");
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
  }

}
