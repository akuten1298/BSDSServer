import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author aakash
 */
public class RMQProducer {

  private static final String QUEUE_NAME = "twinder_queue";

  private ConnectionFactory factory;
  private Connection connection;
  private Channel channel = null;

  private static RMQProducer instance = null;

  public static RMQProducer getInstance() {
    if (instance == null) {
      instance = new RMQProducer();
    }
    return instance;
  }

  public RMQProducer() {
    factory = new ConnectionFactory();
    factory.setHost("localhost");
    try {
      connection = factory.newConnection();
      channel = connection.createChannel();
      channel.queueDeclare(QUEUE_NAME, false, false, false, null);
    } catch (IOException | TimeoutException e) {
      System.out.println(e.getMessage());
    }
  }

  public void produceMessage(SwipeRequest swipeRequest) {
    String message = swipeRequest.convertToQueueMessage();
    try {
      channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
      System.out.println(" [x] Sent '" + message + "'");
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }

  public ConnectionFactory getFactory() {
    return factory;
  }

  public Connection getConnection() {
    return connection;
  }

  public Channel getChannel() {
    return channel;
  }


}
