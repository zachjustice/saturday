package storyboard.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import storyboard.utils.HTTPUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by zachjustice on 8/1/17.
 */
@Component
public class SimpleCorsFilter implements Filter{

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        logger.info("CORS FILTER: " + request.getHeader("Origin"));

        HTTPUtils.setCORSHeaders(request, response);

        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {

    }
}
