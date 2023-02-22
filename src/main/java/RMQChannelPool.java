import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeoutException;

/**
 * @author aakash
 */
public class RMQChannelPool {

  private static final String QUEUE_NAME = "twinder_queue";
  private static final int MAX_CHANNELS = 10;
  private BlockingQueue<Channel> channelPool;
  private Connection connection;

  private static RMQChannelPool instance = null;

  public static RMQChannelPool getInstance() {
    if (instance == null) {
      instance = new RMQChannelPool();
    }
    return instance;
  }

  public RMQChannelPool() {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    try {
      connection = factory.newConnection();
      channelPool = new LinkedBlockingQueue<>(MAX_CHANNELS);

      for (int i = 0; i < MAX_CHANNELS; i++) {
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        channelPool.offer(channel);
      }
    } catch (IOException | TimeoutException e) {
      System.out.println(e.getMessage());
    }
  }

  public Channel getChannelFromPool() throws InterruptedException {
    return channelPool.take();
  }

  public void returnChannelToPool(Channel channel) throws Exception {
    channelPool.offer(channel);
  }

  public void close() throws Exception {
    for (Channel channel : channelPool) {
      channel.close();
    }
    connection.close();
  }

  public BlockingQueue<Channel> getChannelPool() {
    return channelPool;
  }

  public Connection getConnection() {
    return connection;
  }

}