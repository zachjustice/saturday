package storyboard.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;

/**
 * Created by zachjustice on 7/29/17.
 */
public class HTTPUtils {
    public static JsonObject getPostBodyAsJson(HttpServletRequest req) throws Exception {
        StringBuffer jb = new StringBuffer();
        String line;

        try {
            BufferedReader reader = req.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);
        } catch (Exception e) {
            // TODO Should be a bad request body exception or something
            throw new Exception(e);
        }

        String rawJSON = jb.toString();
        JsonObject returnedJson = new JsonObject();

        if( !StringUtils.isEmpty(rawJSON) ) {
            JsonParser j = new JsonParser();
            returnedJson = j.parse(rawJSON).getAsJsonObject();
        }

        return returnedJson;
    }

    public static HttpServletResponse setCORSHeaders(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PATCH, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With, remember-me");
        return response;
    }
}
