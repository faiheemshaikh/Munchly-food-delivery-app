package com.tap.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.tap.dao.DAOException;
import com.tap.dao.UserDAO;
import com.tap.daoimpl.UserDAOImpl;
import com.tap.model.User;
import com.tap.util.PasswordUtil;
import com.tap.util.SessionUtil;
import com.tap.util.ValidationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** GET /login shows the form; POST /login checks email + password and starts the session. */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(LoginServlet.class.getName());
    private static final String VIEW = "/WEB-INF/views/login.jsp";

    /** Verified against when the email is unknown, so "no such user" takes as long as "wrong password". */
    private static final String DUMMY_HASH = PasswordUtil.hash("not-a-real-password-1");

    private final UserDAO userDAO = new UserDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (SessionUtil.isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }
        request.setAttribute("next", safeNext(request.getParameter("next")));
        request.getRequestDispatcher(VIEW).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = ValidationUtil.clean(request.getParameter("email")).toLowerCase();
        String password = request.getParameter("password");
        String next = safeNext(request.getParameter("next"));
        String error = null;

        if (ValidationUtil.isBlank(email) || password == null || password.isEmpty()) {
            error = "Please enter your email and password.";
        } else {
            try {
                User user = userDAO.getUserByEmail(email);
                boolean passwordOk;
                if (user == null) {
                    PasswordUtil.verify(password, DUMMY_HASH);   // result ignored; only burns the same time
                    passwordOk = false;
                } else {
                    passwordOk = PasswordUtil.verify(password, user.getPasswordHash());
                }

                if (!passwordOk) {
                    error = "Invalid email or password.";        // same message for both cases on purpose
                } else if (!user.isActive()) {
                    error = "This account has been disabled.";
                } else {
                    SessionUtil.startSession(request, user);     // new session id + id/name/role only
                    response.sendRedirect(request.getContextPath() + chooseDestination(user, next));
                    return;
                }
            } catch (DAOException e) {
                LOG.log(Level.SEVERE, "Login failed because of a database problem", e);
                error = "We could not log you in right now. Please try again later.";
            }
        }

        Map<String, String> form = new HashMap<>();
        form.put("email", email);                                // never send the password back
        request.setAttribute("form", form);
        request.setAttribute("loginError", error);
        request.setAttribute("next", next);
        request.getRequestDispatcher(VIEW).forward(request, response);
    }

    /** Where to go after login: the page they originally wanted, else /admin for admins, else /home. */
    private String chooseDestination(User user, String next) {
        if (!next.isEmpty()) {
            return next;
        }
        return User.ROLE_ADMIN.equals(user.getRole()) ? "/admin" : "/home";
    }

    /** Returns the ?next= value only if it is a safe path inside this app; otherwise "". */
    private String safeNext(String next) {
        if (!ValidationUtil.isSafeLocalPath(next)
                || next.startsWith("/login") || next.startsWith("/register") || next.startsWith("/logout")) {
            return "";
        }
        return next;
    }
}
