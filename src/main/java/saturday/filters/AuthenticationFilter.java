package saturday.filters;

import org.postgresql.util.Base64;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.filter.GenericFilterBean;
import saturday.domain.accessTokenTypes.AccessTokenType;
import saturday.domain.accessTokens.AccessToken;
import saturday.exceptions.ResourceNotFoundException;
import saturday.services.AccessTokenService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

public class AuthenticationFilter extends GenericFilterBean {
    private static final String TOKEN_PREFIX = "Bearer";
    private static final String HEADER_STRING = "Authorization";

    private AccessTokenService accessTokenService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthenticationFilter(
            AccessTokenService accessTokenService,
            BCryptPasswordEncoder bCryptPasswordEncoder
    ) {
        this.accessTokenService = accessTokenService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
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
        String emailAndToken = new String(Base64.decode(base64EncodedEmailAndToken));
        String[] emailAndTokenArr = emailAndToken.split(":");

        if (emailAndTokenArr.length != 2) {
            throw new IllegalArgumentException("Invalid bearer token");
        }

        String email = emailAndTokenArr[0];
        String rawToken = emailAndTokenArr[1];
        validateToken(email, rawToken);

        chain.doFilter(request, response);
    }

    private void validateToken(String email, String rawToken) {
        List<AccessToken> accessTokens;
        try {
            accessTokens = accessTokenService.findByEmailAndTypeId(email, AccessTokenType.BEARER_TOKEN);
        } catch (ResourceNotFoundException e) {
            accessTokens = new ArrayList<>();
        }

        Optional<AccessToken> matchingToken = accessTokens
                .stream()
                .filter(accessToken -> bCryptPasswordEncoder.matches(rawToken, accessToken.getToken()))
                .findFirst();

        matchingToken.ifPresent(this::setSecurityContext);
    }

    private void setSecurityContext(AccessToken accessToken) {
        List<AccessToken> accessTokens;
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                accessToken.getEntity().getEmail(),
                null,
                emptyList()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
