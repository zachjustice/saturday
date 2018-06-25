package saturday.filters;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import saturday.domain.accessTokens.AccessToken;
import saturday.exceptions.ResourceNotFoundException;
import saturday.services.AccessTokenService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static java.util.Collections.emptyList;

public class AuthenticationFilter extends GenericFilterBean {
    private static final String TOKEN_PREFIX = "Bearer";
    private static final String HEADER_STRING = "Authorization";

    private AccessTokenService accessTokenService;

    public AuthenticationFilter(AccessTokenService accessTokenService) {
        this.accessTokenService = accessTokenService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest)request;

        //extract token from header
        String token = httpRequest.getHeader(HEADER_STRING);
        if (token == null) {
            chain.doFilter(request, response);
            return;
        }

        //get and check whether token is valid ( token.replace(TOKEN_PREFIX, "")from DB or file wherever you are storing the token)
        token = token.replace(TOKEN_PREFIX, "").trim();

        UsernamePasswordAuthenticationToken authentication;
        try {
            AccessToken accessToken = accessTokenService.findByToken(token);
            String email = accessToken.getEntity().getEmail();
            authentication = new UsernamePasswordAuthenticationToken(email, null, emptyList());
        } catch (ResourceNotFoundException e) {
            authentication = null;
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }
}
