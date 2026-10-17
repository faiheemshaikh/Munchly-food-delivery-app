package com.tap.controller.admin;

import java.io.IOException;

import com.tap.dao.MenuDAO;
import com.tap.dao.OrderDAO;
import com.tap.dao.RestaurantDAO;
import com.tap.dao.UserDAO;
import com.tap.daoimpl.MenuDAOImpl;
import com.tap.daoimpl.OrderDAOImpl;
import com.tap.daoimpl.RestaurantDAOImpl;
import com.tap.daoimpl.UserDAOImpl;
import com.tap.model.FoodOrder;
import com.tap.model.Restaurant;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** GET /admin : summary numbers. Protected by AdminFilter (web.xml). */
@WebServlet("/admin")
public class AdminDashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final RestaurantDAO restaurantDAO = new RestaurantDAOImpl();
    private final MenuDAO menuDAO = new MenuDAOImpl();
    private final OrderDAO orderDAO = new OrderDAOImpl();
    private final UserDAO userDAO = new UserDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int active = 0;
        int total = 0;
        for (Restaurant r : restaurantDAO.getAllRestaurants()) {
            total++;
            if (r.isActive()) {
                active++;
            }
        }
        int newOrders = 0;
        int orderCount = 0;
        for (FoodOrder o : orderDAO.getAllOrders()) {
            orderCount++;
            if (FoodOrder.STATUS_PLACED.equals(o.getStatus())) {
                newOrders++;
            }
        }
        request.setAttribute("restaurantTotal", total);
        request.setAttribute("restaurantActive", active);
        request.setAttribute("menuItemCount", menuDAO.getAllMenuItems().size());
        request.setAttribute("orderCount", orderCount);
        request.setAttribute("newOrderCount", newOrders);
        request.setAttribute("userCount", userDAO.getAllUsers().size());
        request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(request, response);
    }
}
