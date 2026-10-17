package com.tap.util;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.tap.model.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * One place that knows which things we keep in the HTTP session.
 * The session stores ONLY the user's id, name and role (never the password hash).
 * "Who is logged in" always comes from here, never from request parameters.
 */
public final class SessionUtil {

    public static final String USER_ID = "userId";
    public static final String USERNAME = "username";
    public static final String ROLE = "role";
    public static final String CSRF_TOKEN = "csrfToken";
    public static final String CART = "cart";               // Map<Integer, CartItem>, see CartService
    public static final String CART_COUNT = "cartCount";    // total quantity, shown in the navigation bar
    public static final String FLASH_SUCCESS = "flashSuccess";
    public static final String FLASH_ERROR = "flashError";

    private static final int SESSION_TIMEOUT_SECONDS = 30 * 60;

    private SessionUtil() { }

    /** Marks the user as logged in. Changes the session id first to prevent session fixation. */
    public static void startSession(HttpServletRequest request, User user) {
        HttpSession session = request.getSession(true);
        request.changeSessionId();
        session.setAttribute(USER_ID, user.getUserId());
        session.setAttribute(USERNAME, user.getUsername());
        session.setAttribute(ROLE, user.getRole());
        session.setMaxInactiveInterval(SESSION_TIMEOUT_SECONDS);
    }

    /** One-time message shown (and then removed) by the header on the next page. Survives a redirect. */
    public static void flashSuccess(HttpSession session, String message) {
        session.setAttribute(FLASH_SUCCESS, message);
    }

    public static void flashError(HttpSession session, String message) {
        session.setAttribute(FLASH_ERROR, message);
    }

    /** The logged-in user's id, or null if nobody is logged in. */
    public static Integer getUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object value = session.getAttribute(USER_ID);
        return value instanceof Integer ? (Integer) value : null;
    }

    public static String getRole(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object value = session.getAttribute(ROLE);
        return value instanceof String ? (String) value : null;
    }

    public static boolean isLoggedIn(HttpServletRequest request) {
        return getUserId(request) != null;
    }

    public static boolean isAdmin(HttpServletRequest request) {
        return User.ROLE_ADMIN.equals(getRole(request));
    }

    /**
     * Sends a not-logged-in visitor to the login page. For GET requests the page they wanted is
     * remembered in ?next=... (only local paths are ever accepted back; see ValidationUtil.isSafeLocalPath).
     */
    public static void redirectToLogin(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String ctx = request.getContextPath();
        if ("GET".equals(request.getMethod())) {
            StringBuilder target = new StringBuilder(request.getServletPath());
            if (request.getPathInfo() != null) {
                target.append(request.getPathInfo());
            }
            if (request.getQueryString() != null) {
                target.append('?').append(request.getQueryString());
            }
            response.sendRedirect(ctx + "/login?next="
                    + URLEncoder.encode(target.toString(), StandardCharsets.UTF_8));
        } else {
            response.sendRedirect(ctx + "/login");
        }
    }
}
