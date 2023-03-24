import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/stats/*")
public class StatsServlet extends HttpServlet {

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
            // and now validate url path and return the response status code
            // (and maybe also some value if input is valid)
            res.setStatus(HttpServletResponse.SC_OK);
            StatsResponse statsResponse = new StatsResponse();
            String resp = gson.toJson(statsResponse);
            printWriter.print(resp);

            //TODO Implement user not found 404
        }

        printWriter.flush();
        printWriter.close();
    }
}
