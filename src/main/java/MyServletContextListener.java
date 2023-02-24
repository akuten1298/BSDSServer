import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author aakash
 */
public class MyServletContextListener implements ServletContextListener {

  public void contextDestroyed(ServletContextEvent sce) {
    // Code to be executed when the server stops
//    RMQProducer producer = RMQProducer.getInstance();
//    try {
//      producer.getChannel().close();
//    } catch (IOException | TimeoutException e) {
//      System.out.println(e.getMessage());
//    }

    RMQChannelPool rmqChannelPool = RMQChannelPool.getInstance();
    try {
      rmqChannelPool.close();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

    System.out.println("Server stopped!");
  }

  public void contextInitialized(ServletContextEvent sce) {
    // Code to be executed when the server starts
    System.out.println("Server started!");
  }
}
