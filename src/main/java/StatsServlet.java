import com.google.gson.Gson;
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
    private static String STATS_DB = "stats";

    @Override
    public void init() throws ServletException {
        MongoConfig mongoConfig = MongoConfig.getInstance();
        MongoDatabase database = mongoConfig.getDatabase();
        collection = database.getCollection(STATS_DB);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        res.setContentType("application/json");
        String urlPath = req.getPathInfo();
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
