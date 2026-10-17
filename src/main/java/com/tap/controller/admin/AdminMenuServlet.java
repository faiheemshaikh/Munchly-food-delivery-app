package com.tap.controller.admin;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.tap.dao.DAOException;
import com.tap.dao.MenuDAO;
import com.tap.dao.RestaurantDAO;
import com.tap.daoimpl.MenuDAOImpl;
import com.tap.daoimpl.RestaurantDAOImpl;
import com.tap.model.MenuItem;
import com.tap.model.Restaurant;
import com.tap.util.SessionUtil;
import com.tap.util.ValidationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Administrator menu management (AdminFilter protects /admin/*).
 *   GET  /admin/menu?restaurantId=N          items of one restaurant, including unavailable ones
 *   POST /admin/menu                         action = available | unavailable (menuId)
 *   GET  /admin/menu/form?restaurantId=N     add an item to restaurant N
 *   GET  /admin/menu/form?menuId=M           edit item M
 *   POST /admin/menu/form                    save (insert or update)
 * An item's restaurant is never taken from the form when editing: it is read from the database.
 */
@WebServlet({"/admin/menu", "/admin/menu/form"})
public class AdminMenuServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(AdminMenuServlet.class.getName());
    private static final String FORM_PATH = "/admin/menu/form";
    private static final String LIST_VIEW = "/WEB-INF/views/admin/menu-items.jsp";
    private static final String FORM_VIEW = "/WEB-INF/views/admin/menu-item-form.jsp";
    private static final BigDecimal MAX_PRICE = new BigDecimal("99999.99");

    private final MenuDAO menuDAO = new MenuDAOImpl();
    private final RestaurantDAO restaurantDAO = new RestaurantDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (FORM_PATH.equals(request.getServletPath())) {
            showForm(request, response);
        } else {
            showList(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (FORM_PATH.equals(request.getServletPath())) {
            save(request, response);
        } else {
            toggleAvailability(request, response);
        }
    }

    private void showList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer restaurantId = ValidationUtil.parsePositiveInt(request.getParameter("restaurantId"));
        Restaurant restaurant = restaurantId == null ? null : restaurantDAO.getRestaurantById(restaurantId);
        if (restaurant == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        request.setAttribute("restaurant", restaurant);
        request.setAttribute("items", menuDAO.getMenuItemsByRestaurantId(restaurantId));
        request.getRequestDispatcher(LIST_VIEW).forward(request, response);
    }

    private void showForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Map<String, String> form = new LinkedHashMap<>();
        String menuParam = request.getParameter("menuId");

        if (!ValidationUtil.isBlank(menuParam)) {                       // edit
            Integer menuId = ValidationUtil.parsePositiveInt(menuParam);
            MenuItem item = menuId == null ? null : menuDAO.getMenuItemById(menuId);
            if (item == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            form.put("menuId", String.valueOf(item.getMenuId()));
            form.put("name", item.getName());
            form.put("description", item.getDescription() == null ? "" : item.getDescription());
            form.put("price", item.getPrice().toPlainString());
            form.put("category", item.getCategory() == null ? "" : item.getCategory());
            form.put("imagePath", item.getImagePath() == null ? "" : item.getImagePath());
            form.put("available", String.valueOf(item.isAvailable()));
            request.setAttribute("restaurant", restaurantDAO.getRestaurantById(item.getRestaurantId()));
            request.setAttribute("editing", Boolean.TRUE);
        } else {                                                        // add
            Integer restaurantId = ValidationUtil.parsePositiveInt(request.getParameter("restaurantId"));
            Restaurant restaurant = restaurantId == null ? null : restaurantDAO.getRestaurantById(restaurantId);
            if (restaurant == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            form.put("restaurantId", String.valueOf(restaurantId));
            form.put("available", "true");
            request.setAttribute("restaurant", restaurant);
            request.setAttribute("editing", Boolean.FALSE);
        }
        request.setAttribute("form", form);
        request.getRequestDispatcher(FORM_VIEW).forward(request, response);
    }

    private void toggleAvailability(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String ctx = request.getContextPath();
        String action = ValidationUtil.clean(request.getParameter("action"));
        Integer menuId = ValidationUtil.parsePositiveInt(request.getParameter("menuId"));
        boolean validAction = "available".equals(action) || "unavailable".equals(action);
        String redirectTo = ctx + "/admin/restaurants";

        if (menuId == null || !validAction) {
            SessionUtil.flashError(request.getSession(), "That request was not valid.");
        } else {
            try {
                MenuItem item = menuDAO.getMenuItemById(menuId);
                if (item == null) {
                    SessionUtil.flashError(request.getSession(), "Menu item not found.");
                } else {
                    boolean makeAvailable = "available".equals(action);
                    menuDAO.setMenuItemAvailability(menuId, makeAvailable);
                    SessionUtil.flashSuccess(request.getSession(), item.getName()
                            + (makeAvailable ? " is now available." : " is now marked unavailable."));
                    redirectTo = ctx + "/admin/menu?restaurantId=" + item.getRestaurantId();
                }
            } catch (DAOException e) {
                LOG.log(Level.SEVERE, "Could not change item availability", e);
                SessionUtil.flashError(request.getSession(), "Could not update the item. Please try again.");
            }
        }
        response.sendRedirect(redirectTo);
    }

    private void save(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String menuParam = ValidationUtil.clean(request.getParameter("menuId"));
        boolean editing = !menuParam.isEmpty();
        Integer menuId = editing ? ValidationUtil.parsePositiveInt(menuParam) : null;
        if (editing && menuId == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Which restaurant? When editing, from the database. When adding, from the form but it must exist.
        MenuItem existing = null;
        Restaurant restaurant;
        if (editing) {
            existing = menuDAO.getMenuItemById(menuId);
            restaurant = existing == null ? null : restaurantDAO.getRestaurantById(existing.getRestaurantId());
        } else {
            Integer rid = ValidationUtil.parsePositiveInt(request.getParameter("restaurantId"));
            restaurant = rid == null ? null : restaurantDAO.getRestaurantById(rid);
        }
        if (restaurant == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String name = ValidationUtil.clean(request.getParameter("name"));
        String description = ValidationUtil.clean(request.getParameter("description"));
        String category = ValidationUtil.clean(request.getParameter("category"));
        String imagePath = ValidationUtil.clean(request.getParameter("imagePath"));
        String priceText = ValidationUtil.clean(request.getParameter("price"));
        boolean available = "on".equals(request.getParameter("available"));

        Map<String, String> errors = new LinkedHashMap<>();
        if (name.isEmpty() || name.length() > 100) {
            errors.put("name", "Name is required (maximum 100 characters).");
        }
        if (description.length() > 500) {
            errors.put("description", "Description must be 500 characters or fewer.");
        }
        if (category.length() > 50) {
            errors.put("category", "Category must be 50 characters or fewer.");
        }
        if (!ValidationUtil.isValidOptionalImagePath(imagePath)) {
            errors.put("imagePath", "Use a path like images/food/my-photo.jpg (letters, numbers, _ - . /).");
        }
        BigDecimal price = ValidationUtil.parseDecimal(priceText);
        if (price == null || price.signum() <= 0 || price.compareTo(MAX_PRICE) > 0
                || !ValidationUtil.hasAtMostScale(price, 2)) {
            errors.put("price", "Enter a price above 0 with at most two decimals (for example 199.50).");
        }

        Map<String, String> form = new LinkedHashMap<>();
        form.put("menuId", menuParam);
        form.put("restaurantId", String.valueOf(restaurant.getRestaurantId()));
        form.put("name", name);
        form.put("description", description);
        form.put("price", priceText);
        form.put("category", category);
        form.put("imagePath", imagePath);
        form.put("available", String.valueOf(available));

        if (errors.isEmpty()) {
            try {
                MenuItem item = new MenuItem();
                item.setRestaurantId(restaurant.getRestaurantId());
                item.setName(name);
                item.setDescription(description.isEmpty() ? null : description);
                item.setPrice(price.setScale(2, RoundingMode.HALF_UP));
                item.setCategory(category.isEmpty() ? null : category);
                item.setImagePath(imagePath.isEmpty() ? null : imagePath);
                item.setAvailable(available);

                if (editing) {
                    item.setMenuId(menuId);
                    menuDAO.updateMenuItem(item);
                    SessionUtil.flashSuccess(request.getSession(), "Menu item updated.");
                } else {
                    menuDAO.addMenuItem(item);
                    SessionUtil.flashSuccess(request.getSession(), "Menu item added.");
                }
                response.sendRedirect(request.getContextPath() + "/admin/menu?restaurantId=" + restaurant.getRestaurantId());
                return;
            } catch (DAOException e) {
                LOG.log(Level.SEVERE, "Could not save menu item", e);
                request.setAttribute("formError", "Could not save the menu item. Please try again.");
            }
        }
        request.setAttribute("restaurant", restaurant);
        request.setAttribute("editing", editing);
        request.setAttribute("form", form);
        request.setAttribute("errors", errors);
        request.getRequestDispatcher(FORM_VIEW).forward(request, response);
    }
}
