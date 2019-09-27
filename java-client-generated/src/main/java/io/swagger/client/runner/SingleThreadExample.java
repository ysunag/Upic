package io.swagger.client.runner;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.ResortsApi;
import io.swagger.client.model.SeasonsList;

public class SingleThreadExample {
  public static final String basePath = "http://localhost:8080";
  public static void main(String[] args) {

    ResortsApi apiInstance = new ResortsApi();
    ApiClient client = apiInstance.getApiClient();
    client.setBasePath(basePath);
    Integer resortID = 56; // Integer | ID of the resort of interest
    try {
      SeasonsList result = apiInstance.getResortSeasons(resortID);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ResortsApi#getResortSeasons");
      e.printStackTrace();
    }
  }
}
