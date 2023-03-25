package Assignment2;

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

  private static final String PRIVATE_RMQ_EC2 = "172.31.29.115";
  private static final String PUBLIC_RMQ_EC2 = "54.202.102.238";
  private static final int RMQ_LB_PORT = 5672;
  private static final String LOCALHOST = "localhost";
  private static final int MAX_CHANNELS = 400;
  boolean persistent = true;
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
    factory.setHost(PUBLIC_RMQ_EC2);
    setUserCredentials(factory);
    try {
      connection = factory.newConnection();
      channelPool = new LinkedBlockingQueue<>(MAX_CHANNELS);

      for (int i = 0; i < MAX_CHANNELS; i++) {
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, persistent, false, false, null);
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

  public void setUserCredentials(ConnectionFactory factory) {
      factory.setUsername("guest");
      factory.setPassword("guest");
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
