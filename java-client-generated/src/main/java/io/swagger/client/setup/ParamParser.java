package io.swagger.client.setup;

public class ParamParser {

  public static int MAX_THREAD = 256;
  public static int MAX_SKIERS = 50000;
  public static int MAX_LIFTS = 60;
  public static int MIN_LIFTS = 5;
  public static int MAX_MEAN_LIFTS = 20;


  private String[] args;

  public ParamParser(String[] args) {
    this.args = new String[args.length];
    int index = 0;
    for (String str: args) {
      this.args[index] = str;
      index++;
    }
  }

  public int getNumOfMaxThreads() {
    int numOfThread = Integer.parseInt(args[0]);
    if (numOfThread > MAX_THREAD || numOfThread < 0) {
      throw new IllegalArgumentException("Invalid maximum thread number!");
    }
    return numOfThread;
  }

  public int getNumOfMaxSkiers() {
    int numOfThread = Integer.parseInt(args[1]);
    if (numOfThread > MAX_SKIERS || numOfThread < 0) {
      throw new IllegalArgumentException("Invalid maximum skier number!");
    }
    return numOfThread;
  }

  public int getNumOfSkiLifts() {
    int numOfLifts = Integer.parseInt(args[2]);
    if (numOfLifts > MAX_LIFTS || numOfLifts < MIN_LIFTS) {
      throw new IllegalArgumentException("Invalid lifts number!");
    }
    return numOfLifts;
  }

  public int getNumOfMeanSkiLifts() {
    int numOfMeanLifts = Integer.parseInt(args[3]);
    if (numOfMeanLifts > MAX_MEAN_LIFTS || numOfMeanLifts < 0) {
      throw new IllegalArgumentException("Invalid mean numbers of ski lifts each skier rides each " +
              "day!");
    }
    return numOfMeanLifts;
  }

  public String getServerAddress() {
    return args[4];
  }

}
