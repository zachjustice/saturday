package saturday.filters;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import saturday.domain.AccountCredentials;
import saturday.services.EntityServiceImpl;
import saturday.utils.HTTPUtils;
import saturday.utils.TokenAuthenticationUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {

    private EntityServiceImpl entityService;


    public JWTLoginFilter(HttpMethod method, String url, AuthenticationManager authManager) {
        super(new AntPathRequestMatcher(url, method.name()));
        setAuthenticationManager(authManager);
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest req, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        JsonObject json = null;

        try {
            json = HTTPUtils.getPostBodyAsJson(req);
        } catch (Exception e) {
            logger.info("Encountered malformed request body while logging in user: " + e.getMessage());
            e.printStackTrace();
        }

        JsonElement emailElement = json.get("email");
        JsonElement passwordElement = json.get("password");

        String email = emailElement == null ? "" : emailElement.getAsString();
        String password = passwordElement == null ? "" : passwordElement.getAsString();

        logger.info("LOGGED IN AS USER " + email);

        AccountCredentials accountCredentials = new AccountCredentials(email, password);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                accountCredentials.getEmail(),
                accountCredentials.getPasswordHash(),
                Collections.emptyList()
        );

        HTTPUtils.setCORSHeaders(req, response);
        response.setHeader("Access-Control-Expose-Headers", "Authorization");

        return getAuthenticationManager().authenticate(token);
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest req,
            HttpServletResponse res, FilterChain chain,
            Authentication auth) throws IOException, ServletException {

        TokenAuthenticationUtils.addAuthentication(res, auth.getName());
    }
}
