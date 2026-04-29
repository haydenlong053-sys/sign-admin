package com.ruoyi.web.security;

import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * <p>
 *
 * </p>
 *
 * @author ll
 * @since 2025-10-23 11:45
 */
@Component
public class RequestWrapperFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        chain.doFilter(wrappedRequest, resp);
    }
}