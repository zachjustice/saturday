package saturday.utils;

import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by zachjustice on 7/29/17.
 */
public class HTTPUtils {
    private static final String TOKEN_PREFIX = "Bearer";
    private static final String HEADER_STRING = "Authorization";

    public static void addAuthenticationHeader(HttpServletResponse response, String token) {
        response.addHeader(HEADER_STRING, TOKEN_PREFIX + " " + token);
    }

    public static HttpServletResponse setCORSHeaders(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, DELETE, PUT, OPTIONS, PATCH");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With, remember-me");
        return response;
    }

    public static HttpHeaders createHeaders(String password){
        return createHeaders(null, password);
    }

    public static HttpHeaders createHeaders(String username, String password){
        if (StringUtils.isEmpty(password)) {
            throw new IllegalArgumentException("Empty password provided to createHeaders");
        }

        String plainCreds = password;
        if (!StringUtils.isEmpty(username)) {
            plainCreds = username + ":" + plainCreds;
        }

        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);
        return headers;
    }
}
