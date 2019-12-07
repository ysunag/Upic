package databaseUtils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

public class DynamodbClient {
  public static AmazonDynamoDB client;


  public static AmazonDynamoDB getClient() {
    if (client == null) {
      AWSCredentials credentials = new BasicAWSCredentials(
              "AKIAUGCJ3Q37D6RYLA25",
              "DHQok8CV6Fw+ZsWGIU5hPO9Tdn8pdehnPygMSeeJ"
      );
      client = AmazonDynamoDBClientBuilder.standard()
              .withCredentials(new AWSStaticCredentialsProvider(credentials))
              .withRegion(Regions.US_WEST_2)
              .build();
    }
    return client;
  }
}
