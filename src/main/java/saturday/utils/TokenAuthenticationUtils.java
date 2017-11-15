package saturday.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

import static java.util.Collections.emptyList;

public class TokenAuthenticationUtils {
    private static final String TOKEN_PREFIX = "Bearer";
    private static final String HEADER_STRING = "Authorization";

    private static String SECRET = "7834g32b2oeuqpidu!ddfghj)(4567cret";

    private static long EXPIRATIONTIME = 864000000;

    private static final Logger logger = LoggerFactory.getLogger(TokenAuthenticationUtils.class);

    public static String addAuthentication(HttpServletResponse res, String username) {
        logger.info("JWT DATE: " + new Date(System.currentTimeMillis()) + " vs " + (new Date(System.currentTimeMillis() + EXPIRATIONTIME)));
        String JWT = Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();

        res.addHeader(HEADER_STRING, TOKEN_PREFIX + " " + JWT);
        return JWT;
    }

    public static Authentication getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(HEADER_STRING);

        if (token == null) {
            return null;
        }

        // parse the filters.
        String user = Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                .getBody()
                .getSubject();

        return user != null ?
                new UsernamePasswordAuthenticationToken(user, null, emptyList()) :
                null;
    }
}
