package storyboard.security;

import org.springframework.http.HttpMethod;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.security.core.Authentication;
import storyboard.utils.TokenAuthenticationUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class JWTAuthenticationFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        //logger.info("JWT Auth Filter: " + req.getMethod() + " " + HttpMethod.OPTIONS.name());

        Authentication authentication = TokenAuthenticationUtils.getAuthentication((HttpServletRequest) request);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request,response);
    }
}
