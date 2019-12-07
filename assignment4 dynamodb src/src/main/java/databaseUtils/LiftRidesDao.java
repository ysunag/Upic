package databaseUtils;


import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.swagger.client.model.SkierVertical;
import io.swagger.client.model.SkierVerticalResorts;
import io.swagger.client.model.LiftRide;

public class LiftRidesDao {

    protected ConnectionManager connectionManager;
    // Single pattern: instantiation is limited to one object.
    private static LiftRidesDao instance = null;
    protected LiftRidesDao() {
        connectionManager = new ConnectionManager();
    }
    public static LiftRidesDao getInstance() {
        if(instance == null) {
            instance = new LiftRidesDao();
        }
        return instance;
    }


    public SkierVertical getTotalVertical (int skierId, int resortId, int seasonId) throws SQLException {
        SkierVertical skierVertical = new SkierVertical();
        Connection connection = null;
        PreparedStatement selectStmt = null;
        ResultSet results = null;
        String whereStmt = "WHERE ResortId=? AND SkierId=?";
        if (seasonId > 0) {
            whereStmt = "WHERE ResortId=? AND SkierId=? AND SeasonId=?";
        }
        String selectVertical = "SELECT SeasonId, SUM(Vertical) AS TotalVertical " +
            "FROM (SELECT SeasonId, Vertical FROM LiftRides " +
            whereStmt +
            ") AS V " +
            "GROUP BY SeasonId;";

        try {
            connection = connectionManager.getConnection();
            selectStmt = connection.prepareStatement(selectVertical);
            selectStmt.setInt(1, resortId);
            selectStmt.setInt(2, skierId);
//            if (seasonId > 0) {
//                selectStmt.setInt(3, seasonId);
//            }
            results = selectStmt.executeQuery();

            while(results.next()) {
                int totalVertical = results.getInt("TotalVertical");
                seasonId = results.getInt("SeasonId");
                SkierVerticalResorts skierVerticalResorts = new SkierVerticalResorts();
                skierVerticalResorts.setSeasonID(String.valueOf(seasonId));
                skierVerticalResorts.setTotalVert(totalVertical);
                skierVertical.addResortsItem(skierVerticalResorts);
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            if(connection != null) {
                connection.close();
            }
            if(selectStmt != null) {
                selectStmt.close();
            }
            if(results != null) {
                results.close();
            }
        }
        return skierVertical;
    }

    public int getVerticalBySkier (int resortId, int seasonId, int dayId, int skierId) throws SQLException {
        int vertical = 0;
        Connection connection = null;
        PreparedStatement selectStmt = null;
        ResultSet results = null;
        String selectVertical = "SELECT SUM(Vertical) AS TotalVertical " +
            "FROM (SELECT Vertical FROM LiftRides " +
            "WHERE ResortId=? AND SeasonId=? AND DayId=? AND SkierId=?) AS V;";

        try {
            connection = connectionManager.getConnection();
            selectStmt = connection.prepareStatement(selectVertical);
            selectStmt.setInt(1, resortId);
            selectStmt.setInt(2, seasonId);
            selectStmt.setInt(3, dayId);
            selectStmt.setInt(4, skierId);
            results = selectStmt.executeQuery();

            if(results.next()) {
                vertical = results.getInt("TotalVertical");
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            if(connection != null) {
                connection.close();
            }
            if(selectStmt != null) {
                selectStmt.close();
            }
            if(results != null) {
                results.close();
            }
        }
        return vertical;
    }

    public int getVerticalBySkierFromDynamodb (int resortId, int seasonId, int dayId, int skierId) {
//        int vertical = 0;
////        Connection connection = null;
////        PreparedStatement selectStmt = null;
////        ResultSet results = null;
////        String selectVertical = "SELECT SUM(Vertical) AS TotalVertical " +
////                "FROM (SELECT Vertical FROM LiftRides " +
////                "WHERE ResortId=? AND SeasonId=? AND DayId=? AND SkierId=?) AS V;";
////
////        try {
////            connection = connectionManager.getConnection();
////            selectStmt = connection.prepareStatement(selectVertical);
////            selectStmt.setInt(1, resortId);
////            selectStmt.setInt(2, seasonId);
////            selectStmt.setInt(3, dayId);
////            selectStmt.setInt(4, skierId);
////            results = selectStmt.executeQuery();
////
////            if(results.next()) {
////                vertical = results.getInt("TotalVertical");
////            }
////        } catch (SQLException e) {
////            throw e;
////        } finally {
////            if(connection != null) {
////                connection.close();
////            }
////            if(selectStmt != null) {
////                selectStmt.close();
////            }
////            if(results != null) {
////                results.close();
////            }
////        }
////        return vertical;

//        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
//                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-west-2"))
//                .build();

//        AWSCredentials credentials = new BasicAWSCredentials(
//                "AKIAUGCJ3Q37D6RYLA25",
//                "DHQok8CV6Fw+ZsWGIU5hPO9Tdn8pdehnPygMSeeJ"
//        );
//        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
//                .withCredentials(new AWSStaticCredentialsProvider(credentials))
//                .withRegion(Regions.US_WEST_2)
//                .build();
        AmazonDynamoDB client = DynamodbClient.getClient();

        DynamoDB dynamoDB = new DynamoDB(client);

        Table table = dynamoDB.getTable("Upic6650");

//        HashMap<String, String> nameMap = new HashMap<String, String>();
//        nameMap.put("#id", "SkierId");
//
//        HashMap<String, Object> valueMap = new HashMap<String, Object>();
//        valueMap.put(":yyyy", 1985);
        String locationDaySkier = resortId + "-" + seasonId + "-" + dayId + "-" + skierId;

        HashMap<String, String> nameMap = new HashMap<String, String>();
        nameMap.put("#lds", "LocationDaySkier");

        HashMap<String, Object> valueMap = new HashMap<String, Object>();
        valueMap.put(":ldsValue", locationDaySkier);


        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("#lds = :ldsValue").withNameMap(nameMap)
                .withValueMap(valueMap);

        ItemCollection<QueryOutcome> items = null;
        Iterator<Item> iterator = null;
        Item item = null;
        int vertical = 0;
//
//        querySpec.withProjectionExpression("#id, LocationAndTime")
//                .withKeyConditionExpression("#id = :skierId and title between :letter1 and :letter2").withNameMap(nameMap)
//                .withValueMap(valueMap);

        try {
//            System.out.println("Movies from 1992 - titles A-L, with genres and lead actor");
            items = table.query(querySpec);

            iterator = items.iterator();
            while (iterator.hasNext()) {
                item = iterator.next();
                vertical = item.getNumber("Vertical").intValue();
//                System.out.println(item.getNumber("year") + ": " + item.getString("title") + " " + item.getMap("info"));
            }

        }
        catch (Exception e) {
//            System.err.println("Unable to query movies from 1992:");
//            System.err.println(e.getMessage());
        }
        return vertical;
    }




    public void insertLiftRide (int resortId, int seasonId, int dayId, int skierId, LiftRide liftRide) throws SQLException {
        String insertLiftRide = "INSERT INTO LiftRides(ResortId, SeasonId, DayId, SkierId, StartTime, LiftId, Vertical) VALUES(?,?,?,?,?,?,?);";
        Connection connection = null;
        PreparedStatement insertStmt = null;

        try {
            connection = connectionManager.getConnection();
            insertStmt = connection.prepareStatement(insertLiftRide);
            insertStmt.setInt(1, resortId);
            insertStmt.setInt(2, seasonId);
            insertStmt.setInt(3, dayId);
            insertStmt.setInt(4, skierId);
            insertStmt.setInt(5, liftRide.getTime());
            insertStmt.setInt(6, liftRide.getLiftID());
            insertStmt.setInt(7, 10 * liftRide.getLiftID());
            insertStmt.executeUpdate();
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw e;
            }
        } finally {
            if(connection != null) {
                connection.close();
            }
            if(insertStmt != null) {
                insertStmt.close();
            }
        }
    }

    public void insertLiftRideToDynamodb (int resortId, int seasonId, int dayId, int skierId, LiftRide liftRide) {
//        String insertLiftRide = "INSERT INTO LiftRides(ResortId, SeasonId, DayId, SkierId, StartTime, LiftId, Vertical) VALUES(?,?,?,?,?,?,?);";
//        Connection connection = null;
//        PreparedStatement insertStmt = null;
//
//        try {
//            connection = connectionManager.getConnection();
//            insertStmt = connection.prepareStatement(insertLiftRide);
//            insertStmt.setInt(1, resortId);
//            insertStmt.setInt(2, seasonId);
//            insertStmt.setInt(3, dayId);
//            insertStmt.setInt(4, skierId);
//            insertStmt.setInt(5, liftRide.getTime());
//            insertStmt.setInt(6, liftRide.getLiftID());
//            insertStmt.setInt(7, 10 * liftRide.getLiftID());
//            insertStmt.executeUpdate();
//        } catch (SQLException e) {
//            if (e.getErrorCode() == 1062) {
//                throw e;
//            }
//        } finally {
//            if(connection != null) {
//                connection.close();
//            }
//            if(insertStmt != null) {
//                insertStmt.close();
//            }
//        }


//        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
//                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-west-2"))
//                .build();
//        AWSCredentials credentials = new BasicAWSCredentials(
//            "AKIAUGCJ3Q37D6RYLA25",
//            "DHQok8CV6Fw+ZsWGIU5hPO9Tdn8pdehnPygMSeeJ"
//    );
//        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
//                .withCredentials(new AWSStaticCredentialsProvider(credentials))
//                .withRegion(Regions.US_WEST_2)
//                .build();

        AmazonDynamoDB client = DynamodbClient.getClient();

        DynamoDB dynamoDB = new DynamoDB(client);

        Table table = dynamoDB.getTable("Upic6650");


//        final Map<String, Object> infoMap = new HashMap<String, Object>();
//        infoMap.put("plot", "Nothing happens at all.");
//        infoMap.put("rating", 0);
        String loctionDaySkier = resortId + "-" + seasonId + "-" + dayId + "-" + skierId;

        try {
            PutItemOutcome outcome = table
                    .putItem(new Item()
                            .withPrimaryKey("LocationDaySkier", loctionDaySkier, "Time", liftRide.getTime())
                            .withInt("Vertical", 10 * liftRide.getLiftID()).withInt("LiftId", liftRide.getLiftID()));

        }
        catch (Exception e) {
            System.err.println("Unable to add item");
            System.err.println(e.getMessage());
        }
    }



}
