//package sqs;
//
//
//import com.amazonaws.auth.AWSCredentials;
//import com.amazonaws.auth.AWSStaticCredentialsProvider;
//import com.amazonaws.auth.BasicAWSCredentials;
//import com.amazonaws.regions.Regions;
//import com.amazonaws.services.sqs.AmazonSQS;
//import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
//import com.amazonaws.services.sqs.model.CreateQueueRequest;
//
//public class SQS {
//  private static AWSCredentials credentials;
//  public static String standardQueueUrl;
//  public static AmazonSQS sqs;
//
//  static {
//    // put your accesskey and secretkey here
//    credentials = new BasicAWSCredentials(
//            "AKIAUGCJ3Q37D6RYLA25",
//            "DHQok8CV6Fw+ZsWGIU5hPO9Tdn8pdehnPygMSeeJ"
//    );
//  }
//
//  public static AmazonSQS getSqs() {
//    if (sqs == null) {
//      if (credentials == null) {
//        credentials = new BasicAWSCredentials(
//                "AKIAUGCJ3Q37D6RYLA25",
//                "DHQok8CV6Fw+ZsWGIU5hPO9Tdn8pdehnPygMSeeJ"
//        );
//      }
//      sqs = AmazonSQSClientBuilder.standard()
//              .withCredentials(new AWSStaticCredentialsProvider(credentials))
//              .withRegion(Regions.US_WEST_2)
//              .build();
//    }
//    return sqs;
//  }
//
//
//  public static String getstandardQueueUrl() {
//    if (standardQueueUrl == null) {
//      AmazonSQS sqs = getSqs();
//      CreateQueueRequest createStandardQueueRequest = new CreateQueueRequest("upic-queue");
//      standardQueueUrl = sqs.createQueue(createStandardQueueRequest)
//              .getQueueUrl();
//    }
//    return standardQueueUrl;
//  }
//
//
//
//}
