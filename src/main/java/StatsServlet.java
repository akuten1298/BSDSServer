import com.google.gson.Gson;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/stats/*")
public class StatsServlet extends HttpServlet {

    private static MongoCollection<Document> collection;

    private static String NUM_LIKES = "numLikes";
    private static String NUM_DISLIKES = "numDislikes";
    private static String USER_ID = "userId";
    @Override
    public void init() throws ServletException {
        String connectionString = "mongodb+srv://twinder_username:twinder_password@twindercluster.rdueczb.mongodb.net/?retryWrites=true&w=majority";
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .build();
        MongoClient mongoClient = MongoClients.create(settings);
        MongoDatabase database = mongoClient.getDatabase("twinder");
        collection = database.getCollection("stats");
        System.out.println("Stats init called");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        res.setContentType("application/json");
        String urlPath = req.getPathInfo();
        System.out.println("URL Path: " + urlPath);
        ServletOutputStream printWriter = res.getOutputStream();
        Gson gson = new Gson();

        // check we have a URL!
        if (urlPath == null || urlPath.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            InvalidResponse invalidResponse = new InvalidResponse("missing parameters");
            String resp = gson.toJson(invalidResponse);
            printWriter.print(resp);
        } else {
            String userId = urlPath.substring(1);
            res.setStatus(HttpServletResponse.SC_OK);

            Document myDoc = collection.find(Filters.eq(USER_ID, userId)).first();
            StatsResponse statsResponse;
            if(myDoc != null) {
                statsResponse = new StatsResponse(myDoc.getInteger(NUM_LIKES), myDoc.getInteger(NUM_DISLIKES));
                String resp = gson.toJson(statsResponse);
                printWriter.print(resp);
            } else {
                res.setStatus(HttpServletResponse.SC_NOT_FOUND);
                InvalidResponse invalidResponse = new InvalidResponse("User Not Found");
                String resp = gson.toJson(invalidResponse);
                printWriter.print(resp);
            }
        }

        printWriter.flush();
        printWriter.close();
    }
}
