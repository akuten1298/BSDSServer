/**
 * @author aakash
 */

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "SwipeServlet", value = "/SwipeServlet")
public class SwipeServlet extends HttpServlet {
  private static final String QUEUE_NAME = "twinder_queue";
  private static final String LEFT_URL_VERIFICATION = "left";
  private static final String RIGHT_URL_VERIFICATION = "right";

  private final AMQP.BasicProperties properties = MessageProperties.PERSISTENT_TEXT_PLAIN;

  @Override
  public void init() throws ServletException {
    super.init();
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
  }

  public void produceMessage(SwipeRequest swipeRequest) {
    String message = swipeRequest.convertToQueueMessage();
    RMQChannelPool rmqChannelPool = RMQChannelPool.getInstance();
    try {
      Channel channel = rmqChannelPool.getChannelFromPool();
      channel.basicPublish("", QUEUE_NAME, properties, message.getBytes());
      rmqChannelPool.returnChannelToPool(channel);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

}
