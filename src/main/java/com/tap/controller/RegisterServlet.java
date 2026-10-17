package com.tap.controller;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.tap.dao.DAOException;
import com.tap.dao.DuplicateEmailException;
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

/** GET /register shows the form; POST /register validates it and creates a CUSTOMER account. */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(RegisterServlet.class.getName());
    private static final String VIEW = "/WEB-INF/views/register.jsp";

    private final UserDAO userDAO = new UserDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (SessionUtil.isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }
        request.getRequestDispatcher(VIEW).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = ValidationUtil.clean(request.getParameter("username"));
        String email = ValidationUtil.clean(request.getParameter("email")).toLowerCase();
        String phone = ValidationUtil.normalizePhone(request.getParameter("phone"));
        String address = ValidationUtil.clean(request.getParameter("address"));
        String password = request.getParameter("password");
        String confirm = request.getParameter("confirmPassword");

        Map<String, String> errors = new LinkedHashMap<>();

        if (!ValidationUtil.isValidUsername(username)) {
            errors.put("username", "Enter a name of 2-50 characters (letters, numbers, space, . _ -).");
        }
        if (ValidationUtil.isBlank(email)) {
            errors.put("email", "Email is required.");
        } else if (!ValidationUtil.isValidEmail(email)) {
            errors.put("email", "Enter a valid email address.");
        }
        if (!ValidationUtil.isValidOptionalPhone(phone)) {
            errors.put("phone", "Enter 7-14 digits, optionally starting with +.");
        }
        if (!ValidationUtil.isValidOptionalAddress(address)) {
            errors.put("address", "Address must be 255 characters or fewer.");
        }
        if (!ValidationUtil.isStrongPassword(password)) {
            errors.put("password", "Use at least 8 characters including a letter and a number.");
        } else if (!ValidationUtil.isPasswordLengthOk(password)) {
            errors.put("password", "Password is too long (maximum 72 bytes).");
        } else if (!password.equals(confirm)) {
            errors.put("confirmPassword", "Passwords do not match.");
        }

        if (errors.isEmpty()) {
            try {
                if (userDAO.emailExists(email)) {
                    errors.put("email", "An account with this email already exists.");
                } else {
                    User user = new User();
                    user.setUsername(username);
                    user.setEmail(email);
                    user.setPasswordHash(PasswordUtil.hash(password));
                    user.setPhone(phone.isEmpty() ? null : phone);
                    user.setAddress(address.isEmpty() ? null : address);
                    user.setRole(User.ROLE_CUSTOMER);        // never taken from the browser
                    userDAO.registerUser(user);

                    // Redirect-after-POST: refreshing the login page cannot re-submit the form.
                    response.sendRedirect(request.getContextPath() + "/login?registered=1");
                    return;
                }
            } catch (DuplicateEmailException e) {            // two people registering at the same moment
                errors.put("email", "An account with this email already exists.");
            } catch (DAOException e) {
                LOG.log(Level.SEVERE, "Registration failed", e);
                request.setAttribute("formError", "We could not create your account right now. Please try again later.");
            }
        }

        // Show the form again with messages and the values typed (never the passwords).
        Map<String, String> form = new LinkedHashMap<>();
        form.put("username", username);
        form.put("email", email);
        form.put("phone", phone);
        form.put("address", address);
        request.setAttribute("form", form);
        request.setAttribute("errors", errors);
        request.getRequestDispatcher(VIEW).forward(request, response);
    }
}
