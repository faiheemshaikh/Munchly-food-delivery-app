package com.tap.filter;

import java.io.IOException;

import com.tap.util.SessionUtil;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Guards customer pages (profile, cart, checkout, orders, logout): anyone not logged in
 * is redirected to /login. Also tells browsers not to cache these pages, so pressing Back after
 * logging out does not reveal private information.
 */
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (!SessionUtil.isLoggedIn(request)) {
            SessionUtil.redirectToLogin(request, response);
            return;
        }
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        chain.doFilter(req, res);
    }
}
