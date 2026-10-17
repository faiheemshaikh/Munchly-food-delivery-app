package com.tap.controller.admin;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.tap.dao.DAOException;
import com.tap.dao.RestaurantDAO;
import com.tap.daoimpl.RestaurantDAOImpl;
import com.tap.model.Restaurant;
import com.tap.util.SessionUtil;
import com.tap.util.ValidationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Administrator restaurant management (AdminFilter protects /admin/*).
 *   GET  /admin/restaurants       list all restaurants (active and inactive)
 *   POST /admin/restaurants       action = activate | deactivate (soft delete, history is never lost)
 *   GET  /admin/restaurants/form  empty form (add) or ?restaurantId=N (edit)
 *   POST /admin/restaurants/form  save (insert or update)
 */
@WebServlet({"/admin/restaurants", "/admin/restaurants/form"})
public class AdminRestaurantServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(AdminRestaurantServlet.class.getName());
    private static final String FORM_PATH = "/admin/restaurants/form";
    private static final String LIST_VIEW = "/WEB-INF/views/admin/restaurants.jsp";
    private static final String FORM_VIEW = "/WEB-INF/views/admin/restaurant-form.jsp";

    private final RestaurantDAO restaurantDAO = new RestaurantDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!FORM_PATH.equals(request.getServletPath())) {
            request.setAttribute("restaurants", restaurantDAO.getAllRestaurants());
            request.getRequestDispatcher(LIST_VIEW).forward(request, response);
            return;
        }

        // Form: edit when restaurantId is present, otherwise add.
        String idParam = request.getParameter("restaurantId");
        Map<String, String> form = new LinkedHashMap<>();
        if (ValidationUtil.isBlank(idParam)) {
            form.put("rating", "0.0");
            form.put("active", "true");
            request.setAttribute("editing", Boolean.FALSE);
        } else {
            Integer id = ValidationUtil.parsePositiveInt(idParam);
            Restaurant r = id == null ? null : restaurantDAO.getRestaurantById(id);
            if (r == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            form.put("restaurantId", String.valueOf(r.getRestaurantId()));
            form.put("name", r.getName());
            form.put("description", r.getDescription() == null ? "" : r.getDescription());
            form.put("cuisine", r.getCuisine());
            form.put("address", r.getAddress());
            form.put("imagePath", r.getImagePath() == null ? "" : r.getImagePath());
            form.put("rating", r.getRating().toPlainString());
            form.put("active", String.valueOf(r.isActive()));
            request.setAttribute("editing", Boolean.TRUE);
        }
        request.setAttribute("form", form);
        request.getRequestDispatcher(FORM_VIEW).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (FORM_PATH.equals(request.getServletPath())) {
            save(request, response);
        } else {
            toggleActive(request, response);
        }
    }

    private void toggleActive(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String ctx = request.getContextPath();
        String action = ValidationUtil.clean(request.getParameter("action"));
        Integer id = ValidationUtil.parsePositiveInt(request.getParameter("restaurantId"));
        boolean validAction = "activate".equals(action) || "deactivate".equals(action);

        if (id == null || !validAction) {
            SessionUtil.flashError(request.getSession(), "That request was not valid.");
        } else {
            try {
                boolean activate = "activate".equals(action);
                if (restaurantDAO.setRestaurantActive(id, activate)) {
                    SessionUtil.flashSuccess(request.getSession(),
                            activate ? "Restaurant activated." : "Restaurant deactivated. Customers can no longer see it.");
                } else {
                    SessionUtil.flashError(request.getSession(), "Restaurant not found.");
                }
            } catch (DAOException e) {
                LOG.log(Level.SEVERE, "Could not change restaurant status", e);
                SessionUtil.flashError(request.getSession(), "Could not update the restaurant. Please try again.");
            }
        }
        response.sendRedirect(ctx + "/admin/restaurants");
    }

    private void save(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = ValidationUtil.clean(request.getParameter("restaurantId"));
        boolean editing = !idParam.isEmpty();
        Integer id = editing ? ValidationUtil.parsePositiveInt(idParam) : null;
        if (editing && id == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String name = ValidationUtil.clean(request.getParameter("name"));
        String cuisine = ValidationUtil.clean(request.getParameter("cuisine"));
        String address = ValidationUtil.clean(request.getParameter("address"));
        String description = ValidationUtil.clean(request.getParameter("description"));
        String imagePath = ValidationUtil.clean(request.getParameter("imagePath"));
        String ratingText = ValidationUtil.clean(request.getParameter("rating"));
        boolean active = "on".equals(request.getParameter("active"));

        Map<String, String> errors = new LinkedHashMap<>();
        if (name.isEmpty() || name.length() > 100) {
            errors.put("name", "Name is required (maximum 100 characters).");
        }
        if (cuisine.isEmpty() || cuisine.length() > 100) {
            errors.put("cuisine", "Cuisine is required (maximum 100 characters).");
        }
        if (address.isEmpty() || address.length() > 255) {
            errors.put("address", "Address is required (maximum 255 characters).");
        }
        if (description.length() > 500) {
            errors.put("description", "Description must be 500 characters or fewer.");
        }
        if (!ValidationUtil.isValidOptionalImagePath(imagePath)) {
            errors.put("imagePath", "Use a path like images/restaurants/my-photo.jpg (letters, numbers, _ - . /).");
        }
        BigDecimal rating = ValidationUtil.parseDecimal(ratingText);
        if (rating == null || rating.signum() < 0 || rating.compareTo(new BigDecimal("5")) > 0
                || !ValidationUtil.hasAtMostScale(rating, 1)) {
            errors.put("rating", "Rating must be between 0 and 5 with at most one decimal (for example 4.3).");
        }

        Map<String, String> form = new LinkedHashMap<>();
        form.put("restaurantId", idParam);
        form.put("name", name);
        form.put("cuisine", cuisine);
        form.put("address", address);
        form.put("description", description);
        form.put("imagePath", imagePath);
        form.put("rating", ratingText);
        form.put("active", String.valueOf(active));

        if (errors.isEmpty()) {
            try {
                Restaurant r = new Restaurant();
                r.setName(name);
                r.setCuisine(cuisine);
                r.setAddress(address);
                r.setDescription(description.isEmpty() ? null : description);
                r.setImagePath(imagePath.isEmpty() ? null : imagePath);
                r.setRating(rating.setScale(1, RoundingMode.HALF_UP));
                r.setActive(active);

                if (editing) {
                    r.setRestaurantId(id);
                    if (!restaurantDAO.updateRestaurant(r)) {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND);
                        return;
                    }
                    SessionUtil.flashSuccess(request.getSession(), "Restaurant updated.");
                } else {
                    restaurantDAO.addRestaurant(r);
                    SessionUtil.flashSuccess(request.getSession(), "Restaurant added.");
                }
                response.sendRedirect(request.getContextPath() + "/admin/restaurants");
                return;
            } catch (DAOException e) {
                LOG.log(Level.SEVERE, "Could not save restaurant", e);
                request.setAttribute("formError", "Could not save the restaurant. Please try again.");
            }
        }
        request.setAttribute("editing", editing);
        request.setAttribute("form", form);
        request.setAttribute("errors", errors);
        request.getRequestDispatcher(FORM_VIEW).forward(request, response);
    }
}
