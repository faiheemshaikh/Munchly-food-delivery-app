package com.tap.controller;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.tap.dao.DAOException;
import com.tap.dao.MenuDAO;
import com.tap.dao.RestaurantDAO;
import com.tap.daoimpl.MenuDAOImpl;
import com.tap.daoimpl.RestaurantDAOImpl;
import com.tap.model.MenuItem;
import com.tap.model.Restaurant;
import com.tap.util.ValidationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * GET /menu?restaurantId=N : shows ONE restaurant and ONLY that restaurant's available items.
 * A missing, non-numeric, unknown or inactive id gives the friendly 404 page, never a crash.
 */
@WebServlet("/menu")
public class MenuServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(MenuServlet.class.getName());

    private final RestaurantDAO restaurantDAO = new RestaurantDAOImpl();
    private final MenuDAO menuDAO = new MenuDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer restaurantId = ValidationUtil.parsePositiveInt(request.getParameter("restaurantId"));
        if (restaurantId == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try {
            Restaurant restaurant = restaurantDAO.getRestaurantById(restaurantId);
            if (restaurant == null || !restaurant.isActive()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            // Only this restaurant's items, only the ones currently available.
            List<MenuItem> items = menuDAO.getAvailableMenuItemsByRestaurantId(restaurantId);

            // Group by category, keeping the order the DAO returned (alphabetical).
            Map<String, List<MenuItem>> byCategory = new LinkedHashMap<>();
            for (MenuItem item : items) {
                String category = ValidationUtil.isBlank(item.getCategory()) ? "Other" : item.getCategory();
                byCategory.computeIfAbsent(category, k -> new ArrayList<>()).add(item);
            }

            request.setAttribute("restaurant", restaurant);
            request.setAttribute("itemCount", items.size());
            request.setAttribute("menuByCategory", byCategory);
            request.getRequestDispatcher("/WEB-INF/views/menu.jsp").forward(request, response);

        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Could not load menu for restaurant " + restaurantId, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);   // -> friendly error page
        }
    }
}
