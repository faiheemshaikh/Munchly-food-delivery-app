package com.tap.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * CSRF protection with the "synchronizer token" pattern: each session gets a random secret token,
 * every HTML form includes it as a hidden field, and every POST must send it back.
 * A malicious site can make your browser send a POST, but it cannot read your token, so its POST is rejected.
 */
public final class CsrfUtil {

    public static final String PARAM_NAME = "csrfToken";
    private static final SecureRandom RANDOM = new SecureRandom();

    private CsrfUtil() { }

    /** Returns this session's token, creating it on first use. */
    public static String getOrCreateToken(HttpSession session) {
        Object existing = session.getAttribute(SessionUtil.CSRF_TOKEN);
        if (existing instanceof String) {
            return (String) existing;
        }
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        session.setAttribute(SessionUtil.CSRF_TOKEN, token);
        return token;
    }

    /** True if the request carries the token that belongs to its session. */
    public static boolean isValid(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        Object expected = session.getAttribute(SessionUtil.CSRF_TOKEN);
        String actual = request.getParameter(PARAM_NAME);
        if (actual == null) {
            actual = request.getHeader("X-CSRF-Token");
        }
        if (!(expected instanceof String) || actual == null) {
            return false;
        }
        // Constant-time comparison so response timing reveals nothing about the token.
        return MessageDigest.isEqual(((String) expected).getBytes(StandardCharsets.UTF_8),
                actual.getBytes(StandardCharsets.UTF_8));
    }
}
