package saturday.utils;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

        String token = createToken(username);
        res.addHeader(HEADER_STRING, TOKEN_PREFIX + " " + token);
        return token;
    }

    public static String createToken(String username) {
        return createToken(username, EXPIRATIONTIME);
    }

    public static String createToken(String username, long expirationTime) {
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    public static Authentication getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(HEADER_STRING);

        if (token == null) {
            return null;
        }

        String user;
        try {
            // parse the filters.
            user = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                    .getBody()
                    .getSubject();
        } catch(ExpiredJwtException | MalformedJwtException ex) {
            user = null;
        }

        return user != null ?
                new UsernamePasswordAuthenticationToken(user, null, emptyList()) :
                null;
    }
}
