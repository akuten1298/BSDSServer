import com.mongodb.client.MongoClient;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author aakash
 */
public class MyServletContextListener implements ServletContextListener {

  public void contextDestroyed(ServletContextEvent sce) {
    RMQChannelPool rmqChannelPool = RMQChannelPool.getInstance();
    MongoConfig mongoConfig = MongoConfig.getInstance();
    MongoClient mongoClient = mongoConfig.getMongoClient();
    try {
      rmqChannelPool.close();
      mongoClient.close();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

    System.out.println("Server stopped!");
  }

  public void contextInitialized(ServletContextEvent sce) {
    // Code to be executed when the server starts
    System.out.println("Server started!");
    RMQChannelPool rmqChannelPool = RMQChannelPool.getInstance();
    MongoConfig mongoConfig = MongoConfig.getInstance();
  }
}
