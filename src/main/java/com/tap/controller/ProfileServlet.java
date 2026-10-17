package com.tap.controller;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.tap.dao.DAOException;
import com.tap.dao.UserDAO;
import com.tap.daoimpl.UserDAOImpl;
import com.tap.model.User;
import com.tap.util.SessionUtil;
import com.tap.util.ValidationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Protected by AuthFilter. Shows the logged-in user's account and lets them edit name, phone and address.
 * The user id ALWAYS comes from the session, never from the request.
 */
@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(ProfileServlet.class.getName());
    private static final String VIEW = "/WEB-INF/views/profile.jsp";

    private final UserDAO userDAO = new UserDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User account = userDAO.getUserById(SessionUtil.getUserId(request));
        if (account == null) {                              // account vanished: end the session
            request.getSession().invalidate();
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        request.setAttribute("account", account);
        request.getRequestDispatcher(VIEW).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int userId = SessionUtil.getUserId(request);

        String username = ValidationUtil.clean(request.getParameter("username"));
        String phone = ValidationUtil.normalizePhone(request.getParameter("phone"));
        String address = ValidationUtil.clean(request.getParameter("address"));

        Map<String, String> errors = new LinkedHashMap<>();
        if (!ValidationUtil.isValidUsername(username)) {
            errors.put("username", "Enter a name of 2-50 characters (letters, numbers, space, . _ -).");
        }
        if (!ValidationUtil.isValidOptionalPhone(phone)) {
            errors.put("phone", "Enter 7-14 digits, optionally starting with +.");
        }
        if (!ValidationUtil.isValidOptionalAddress(address)) {
            errors.put("address", "Address must be 255 characters or fewer.");
        }

        try {
            if (errors.isEmpty()) {
                User update = new User();
                update.setUserId(userId);
                update.setUsername(username);
                update.setPhone(phone.isEmpty() ? null : phone);
                update.setAddress(address.isEmpty() ? null : address);
                userDAO.updateUser(update);
                request.getSession().setAttribute(SessionUtil.USERNAME, username);
                response.sendRedirect(request.getContextPath() + "/profile?updated=1");
                return;
            }

            User account = userDAO.getUserById(userId);     // for the read-only email line
            request.setAttribute("account", account);
            Map<String, String> form = new LinkedHashMap<>();
            form.put("username", username);
            form.put("phone", phone);
            form.put("address", address);
            request.setAttribute("form", form);
            request.setAttribute("errors", errors);
        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Profile update failed", e);
            request.setAttribute("formError", "We could not save your changes right now. Please try again later.");
            request.setAttribute("account", userDAO.getUserById(userId));
        }
        request.getRequestDispatcher(VIEW).forward(request, response);
    }
}
