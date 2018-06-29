package saturday.filters;

import org.postgresql.util.Base64;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.filter.GenericFilterBean;
import saturday.delegates.AccessTokenDelegate;
import saturday.domain.accessTokenTypes.AccessTokenType;
import saturday.domain.accessTokens.AccessToken;
import saturday.domain.accessTokens.BearerToken;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

public class AuthenticationFilter extends GenericFilterBean {
    private static final String TOKEN_PREFIX = "Bearer";
    private static final String HEADER_STRING = "Authorization";

    private AccessTokenDelegate accessTokenDelegate;

    public AuthenticationFilter(
            AccessTokenDelegate accessTokenDelegate
    ) {
        this.accessTokenDelegate = accessTokenDelegate;
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest)request;

        // extract token from header
        String base64EncodedEmailAndToken = httpRequest.getHeader(HEADER_STRING);
        if (base64EncodedEmailAndToken == null) {
            chain.doFilter(request, response);
            return;
        }

        // get and check whether token is valid ( token.replace(TOKEN_PREFIX, "")
        // from DB or file wherever you are storing the token)
        base64EncodedEmailAndToken = base64EncodedEmailAndToken.replace(TOKEN_PREFIX, "").trim();

        Optional<AccessToken> accessTokenOptional = accessTokenDelegate.validate(
                base64EncodedEmailAndToken,
                AccessTokenType.BEARER_TOKEN
        );

        accessTokenOptional.ifPresent(this::setSecurityContext);

        chain.doFilter(request, response);
    }

    private void setSecurityContext(AccessToken accessToken) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                accessToken.getEntity().getEmail(),
                null,
                emptyList()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
