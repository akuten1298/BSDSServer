/**
 * @author aakash
 */

import Assignment2.RMQChannelPool;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "SwipeServlet", value = "/SwipeServlet")
public class SwipeServlet extends HttpServlet {
  private DynamoDbClient dynamoDbClient;
  private static final String QUEUE_NAME = "twinder_queue";
  private static final String LEFT_URL_VERIFICATION = "left";
  private static final String RIGHT_URL_VERIFICATION = "right";
  @Override
  public void init() throws ServletException {
    super.init();

    // Initialize the DynamoDB client
    dynamoDbClient = DynamoDbClient.create();
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    String urlPath = request.getPathInfo();
    urlPath = urlPath.replace("/", "");

    if(!(LEFT_URL_VERIFICATION.equals(urlPath) || RIGHT_URL_VERIFICATION.equals(urlPath))) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    } else {
      Gson gson = new Gson();
      try {
        StringBuilder stringBuilder = new StringBuilder();
        String reader = "";
        while ((reader = request.getReader().readLine()) != null) {
          stringBuilder.append(reader);
        }

        SwipeRequest swipeRequest = (SwipeRequest) gson.fromJson(stringBuilder.toString(), SwipeRequest.class);
        swipeRequest.setSwipeDirection(urlPath);

        PrintWriter out = response.getWriter();

        if(swipeRequest.getSwiper().equals("") || swipeRequest.getSwipee().equals("")) {
          response.setStatus(HttpServletResponse.SC_NOT_FOUND);
          out.println("Please provide non empty data");
        } else if(swipeRequest.getComment().length() > 256) {
          response.setStatus(HttpServletResponse.SC_NOT_FOUND);
          out.println("Comments are too long! Please stay within 256 characters");
        } else {
          response.setStatus(HttpServletResponse.SC_CREATED);
          out.println("We will keep this in mind and heart ;)");

          produceMessage(swipeRequest);
        }
      } catch (Exception ex) {
        ex.printStackTrace();
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      }
    }

    //Sample DDB
    String tableName = "your_Ftable_name";
    String primaryKey = "your_primary_key";
    String primaryKeyValue = "your_primary_key_value";

    GetItemRequest getItemRequest = GetItemRequest.builder()
            .tableName(tableName)
            .key(Key.builder().put(primaryKey, AttributeValue.builder().s(primaryKeyValue).build()).build())
            .build();

    try {
      GetItemResponse getItemResponse = dynamoDbClient.getItem(getItemRequest);
      resp.getWriter().println("Item retrieved: " + getItemResponse.item());
    } catch (DynamoDbException e) {
      e.printStackTrace();
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      resp.getWriter().println("Error retrieving item from DynamoDB: " + e.getMessage());
    }
  }

  public void produceMessage(SwipeRequest swipeRequest) {
    String message = swipeRequest.convertToQueueMessage();
    RMQChannelPool rmqChannelPool = RMQChannelPool.getInstance();
    try {
      Channel channel = rmqChannelPool.getChannelFromPool();
      channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
      rmqChannelPool.returnChannelToPool(channel);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

  }

  @Override
  public void destroy() {
    super.destroy();
    dynamoDbClient.close();
  }

}
