package com.tap.filter;

import java.io.IOException;

import com.tap.util.CsrfUtil;
import com.tap.util.SessionUtil;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 1. Puts the session's CSRF token into the request as ${csrfToken} so every JSP form can include it.
 * 2. Rejects any state-changing request (POST, PUT, DELETE, PATCH) whose token is missing or wrong.
 * Static files (css/js/images) are skipped so they do not create sessions.
 */
public class CsrfFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (isStaticResource(request.getServletPath())) {
            chain.doFilter(req, res);
            return;
        }

        String token = CsrfUtil.getOrCreateToken(request.getSession(true));
        request.setAttribute(SessionUtil.CSRF_TOKEN, token);

        if (!isSafeMethod(request.getMethod()) && !CsrfUtil.isValid(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or missing CSRF token");
            return;
        }
        chain.doFilter(req, res);
    }

    private boolean isSafeMethod(String method) {
        return "GET".equals(method) || "HEAD".equals(method)
                || "OPTIONS".equals(method) || "TRACE".equals(method);
    }

    private boolean isStaticResource(String path) {
        return path.startsWith("/css/") || path.startsWith("/js/")
                || path.startsWith("/images/") || path.equals("/favicon.ico");
    }
}
