package com.tap.controller;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.tap.dao.DAOException;
import com.tap.dao.RestaurantDAO;
import com.tap.daoimpl.RestaurantDAOImpl;
import com.tap.model.Restaurant;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** GET /home: loads every ACTIVE restaurant from MySQL and shows them as cards. Public page. */
@WebServlet("/home")
public class HomeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(HomeServlet.class.getName());

    private final RestaurantDAO restaurantDAO = new RestaurantDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Restaurant> restaurants = restaurantDAO.getAllActiveRestaurants();

            // Distinct cuisines (sorted) for the filter chips.
            Set<String> cuisines = new TreeSet<>();
            for (Restaurant r : restaurants) {
                cuisines.add(r.getCuisine());
            }

            request.setAttribute("restaurants", restaurants);
            request.setAttribute("cuisines", cuisines);
        } catch (DAOException e) {
            // Friendly "database unavailable" state instead of a stack trace.
            LOG.log(Level.SEVERE, "Could not load restaurants for the home page", e);
            request.setAttribute("dbError", Boolean.TRUE);
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        }
        request.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(request, response);
    }
}
