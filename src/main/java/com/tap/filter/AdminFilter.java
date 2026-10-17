package com.tap.filter;

import java.io.IOException;

import com.tap.dao.UserDAO;
import com.tap.daoimpl.UserDAOImpl;
import com.tap.model.User;
import com.tap.util.SessionUtil;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Guards /admin/*: not logged in -> login page; logged in but not an admin -> 403.
 * The role is checked in the session AND re-read from the database, so an admin who is demoted
 * or deactivated loses access immediately instead of at the next login.
 */
public class AdminFilter implements Filter {

    private final UserDAO userDAO = new UserDAOImpl();

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        Integer userId = SessionUtil.getUserId(request);
        if (userId == null) {
            SessionUtil.redirectToLogin(request, response);
            return;
        }
        if (!SessionUtil.isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        User current = userDAO.getUserById(userId);
        if (current == null || !current.isActive() || !User.ROLE_ADMIN.equals(current.getRole())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        chain.doFilter(req, res);
    }
}
