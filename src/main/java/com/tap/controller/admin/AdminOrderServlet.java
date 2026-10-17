package com.tap.controller.admin;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.tap.dao.DAOException;
import com.tap.dao.OrderDAO;
import com.tap.dao.OrderItemDAO;
import com.tap.dao.UserDAO;
import com.tap.daoimpl.OrderDAOImpl;
import com.tap.daoimpl.OrderItemDAOImpl;
import com.tap.daoimpl.UserDAOImpl;
import com.tap.model.FoodOrder;
import com.tap.model.User;
import com.tap.util.SessionUtil;
import com.tap.util.ValidationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Administrator order management (AdminFilter protects /admin/*).
 *   GET  /admin/orders                  all orders, newest first
 *   GET  /admin/orders/details?orderId  one order with its items
 *   POST /admin/orders                  update status (orderId, status)
 */
@WebServlet({"/admin/orders", "/admin/orders/details"})
public class AdminOrderServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(AdminOrderServlet.class.getName());

    private static final List<String> STATUSES = Arrays.asList(
            FoodOrder.STATUS_PLACED, FoodOrder.STATUS_PREPARING, FoodOrder.STATUS_OUT_FOR_DELIVERY,
            FoodOrder.STATUS_DELIVERED, FoodOrder.STATUS_CANCELLED);

    private final OrderDAO orderDAO = new OrderDAOImpl();
    private final OrderItemDAO orderItemDAO = new OrderItemDAOImpl();
    private final UserDAO userDAO = new UserDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if ("/admin/orders/details".equals(request.getServletPath())) {
            showDetails(request, response);
            return;
        }
        // user id -> name, so the table can show who ordered (no password hashes are loaded)
        Map<Integer, String> names = new HashMap<>();
        for (User u : userDAO.getAllUsers()) {
            names.put(u.getUserId(), u.getUsername());
        }
        request.setAttribute("orders", orderDAO.getAllOrders());
        request.setAttribute("usernames", names);
        request.setAttribute("statuses", STATUSES);
        request.getRequestDispatcher("/WEB-INF/views/admin/orders.jsp").forward(request, response);
    }

    private void showDetails(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer orderId = ValidationUtil.parsePositiveInt(request.getParameter("orderId"));
        FoodOrder order = orderId == null ? null : orderDAO.getOrderById(orderId);   // admins may see any order
        if (order == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        User customer = userDAO.getUserById(order.getUserId());
        request.setAttribute("order", order);
        request.setAttribute("orderLines", orderItemDAO.getOrderLinesByOrderId(orderId));
        request.setAttribute("customerName", customer == null ? "" : customer.getUsername());
        request.setAttribute("backPath", "/admin/orders");
        request.setAttribute("backLabel", "Back to all orders");
        request.getRequestDispatcher("/WEB-INF/views/order-details.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer orderId = ValidationUtil.parsePositiveInt(request.getParameter("orderId"));
        String status = ValidationUtil.clean(request.getParameter("status"));

        if (orderId == null || !STATUSES.contains(status)) {       // only known statuses are accepted
            SessionUtil.flashError(request.getSession(), "That request was not valid.");
        } else {
            try {
                if (orderDAO.updateOrderStatus(orderId, status)) {
                    SessionUtil.flashSuccess(request.getSession(), "Order #" + orderId + " is now " + status + ".");
                } else {
                    SessionUtil.flashError(request.getSession(), "Order not found.");
                }
            } catch (DAOException e) {
                LOG.log(Level.SEVERE, "Could not update order status", e);
                SessionUtil.flashError(request.getSession(), "Could not update the order. Please try again.");
            }
        }
        response.sendRedirect(request.getContextPath() + "/admin/orders");
    }
}
