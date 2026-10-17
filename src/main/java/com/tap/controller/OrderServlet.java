package com.tap.controller;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.tap.dao.DAOException;
import com.tap.dao.OrderDAO;
import com.tap.dao.OrderItemDAO;
import com.tap.daoimpl.OrderDAOImpl;
import com.tap.daoimpl.OrderItemDAOImpl;
import com.tap.model.FoodOrder;
import com.tap.model.OrderLine;
import com.tap.util.SessionUtil;
import com.tap.util.ValidationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Customer order pages (all protected by AuthFilter):
 *   GET /order-confirmation?orderId=N : shown right after checkout
 *   GET /orders                       : My Orders (this user's orders only)
 *   GET /orders/details?orderId=N     : items of one order
 * Ownership is enforced in SQL: an order is found only if it belongs to the logged-in user.
 * Someone else's order and a nonexistent order produce the same 404, so nothing is revealed.
 */
@WebServlet({"/order-confirmation", "/orders", "/orders/details"})
public class OrderServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(OrderServlet.class.getName());

    private final OrderDAO orderDAO = new OrderDAOImpl();
    private final OrderItemDAO orderItemDAO = new OrderItemDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        try {
            switch (path) {
                case "/orders":
                    showMyOrders(request, response);
                    break;
                case "/orders/details":
                    showOrder(request, response, "/WEB-INF/views/order-details.jsp");
                    break;
                default:    // /order-confirmation
                    showOrder(request, response, "/WEB-INF/views/order-confirmation.jsp");
            }
        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Order page failed: " + path, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void showMyOrders(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int userId = SessionUtil.getUserId(request);          // from the session, never from a parameter
        List<FoodOrder> orders = orderDAO.getOrdersByUserId(userId);
        request.setAttribute("orders", orders);
        request.getRequestDispatcher("/WEB-INF/views/my-orders.jsp").forward(request, response);
    }

    private void showOrder(HttpServletRequest request, HttpServletResponse response, String view)
            throws ServletException, IOException {
        Integer orderId = ValidationUtil.parsePositiveInt(request.getParameter("orderId"));
        if (orderId == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        int userId = SessionUtil.getUserId(request);
        FoodOrder order = orderDAO.getOrderByIdAndUserId(orderId, userId);   // WHERE order_id = ? AND user_id = ?
        if (order == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        request.setAttribute("order", order);
        request.setAttribute("orderLines", orderItemDAO.getOrderLinesByOrderId(orderId));
        request.setAttribute("backPath", "/orders");
        request.setAttribute("backLabel", "Back to My Orders");
        request.getRequestDispatcher(view).forward(request, response);
    }
}
