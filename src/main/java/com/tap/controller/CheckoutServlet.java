package com.tap.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.tap.dao.DAOException;
import com.tap.dao.OrderDAO;
import com.tap.dao.UserDAO;
import com.tap.daoimpl.OrderDAOImpl;
import com.tap.daoimpl.UserDAOImpl;
import com.tap.model.CartItem;
import com.tap.model.CartLine;
import com.tap.model.FoodOrder;
import com.tap.model.OrderItem;
import com.tap.model.User;
import com.tap.service.CartService;
import com.tap.service.CartService.CartView;
import com.tap.util.SessionUtil;
import com.tap.util.ValidationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Protected by AuthFilter.
 * GET  /checkout : order summary + delivery address form.
 * POST /checkout : re-validates the cart against MySQL, builds the order with TRUSTED prices and saves it
 *                  with OrderDAO.createOrder (one JDBC transaction), clears the cart, redirects to the confirmation.
 */
@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(CheckoutServlet.class.getName());
    private static final String VIEW = "/WEB-INF/views/checkout.jsp";

    private final CartService cartService = new CartService();
    private final OrderDAO orderDAO = new OrderDAOImpl();
    private final UserDAO userDAO = new UserDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        CartView view = cartService.buildView(session);

        if (view.getLines().isEmpty()) {
            SessionUtil.flashError(session, view.getNotices().isEmpty()
                    ? "Your cart is empty. Add some food first." : String.join(" ", view.getNotices()));
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        User user = userDAO.getUserById(SessionUtil.getUserId(request));
        String defaultAddress = (user == null || user.getAddress() == null) ? "" : user.getAddress();
        showCheckout(request, response, view, defaultAddress);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String ctx = request.getContextPath();
        int userId = SessionUtil.getUserId(request);        // from the session, never from the form

        // Lock this session's cart so a double-click cannot create two orders from the same cart.
        Map<Integer, CartItem> cart = CartService.getCart(session);
        synchronized (cart) {
            try {
                // 1. Rebuild the cart from MySQL: items may have become unavailable since they were added.
                CartView view = cartService.buildView(session);
                if (!view.getNotices().isEmpty()) {
                    SessionUtil.flashError(session, String.join(" ", view.getNotices()) + " Please review your cart.");
                    response.sendRedirect(ctx + "/cart");
                    return;
                }
                if (view.getLines().isEmpty()) {
                    SessionUtil.flashError(session, "Your cart is empty.");
                    response.sendRedirect(ctx + "/cart");
                    return;
                }

                // 2. The total the customer saw must still be the real total (a price may have changed).
                //    The submitted value is used ONLY for this comparison, never as the amount charged.
                BigDecimal shownTotal = parseMoney(request.getParameter("displayedTotal"));
                if (shownTotal == null || shownTotal.compareTo(view.getTotal()) != 0) {
                    SessionUtil.flashError(session, "Prices in your cart have changed. Please review your order and place it again.");
                    response.sendRedirect(ctx + "/checkout");
                    return;
                }

                // 3. Validate the delivery address.
                String address = ValidationUtil.clean(request.getParameter("deliveryAddress"));
                if (address.length() < 10 || address.length() > 255) {
                    request.setAttribute("addressError", "Please enter a full delivery address (10 to 255 characters).");
                    showCheckout(request, response, view, address);
                    return;
                }

                // 4. Build the order from trusted values.
                FoodOrder order = new FoodOrder();
                order.setUserId(userId);
                order.setDeliveryAddress(address);
                order.setTotalAmount(view.getTotal());
                order.setStatus(FoodOrder.STATUS_PLACED);

                List<OrderItem> items = new ArrayList<>();
                for (CartLine line : view.getLines()) {
                    OrderItem item = new OrderItem();
                    item.setMenuId(line.getMenuId());
                    item.setQuantity(line.getQuantity());
                    item.setUnitPrice(line.getUnitPrice());
                    item.setSubtotal(line.getSubtotal());
                    items.add(item);
                }

                // 5. Save order + items in ONE transaction. Throws DAOException (and rolls back) on failure.
                int orderId = orderDAO.createOrder(order, items);

                // 6. Only after a successful commit is the cart emptied.
                CartService.clear(session);
                response.sendRedirect(ctx + "/order-confirmation?orderId=" + orderId);

            } catch (DAOException e) {
                LOG.log(Level.SEVERE, "Checkout failed for user " + userId, e);
                request.setAttribute("formError",
                        "We could not place your order. Nothing was charged and your cart is unchanged. Please try again.");
                CartView view = cartService.buildView(session);
                showCheckout(request, response, view,
                        ValidationUtil.clean(request.getParameter("deliveryAddress")));
            }
        }
    }

    private void showCheckout(HttpServletRequest request, HttpServletResponse response,
                              CartView view, String address) throws ServletException, IOException {
        request.setAttribute("checkoutLines", view.getLines());
        request.setAttribute("checkoutTotal", view.getTotal());
        request.setAttribute("cartNotices", view.getNotices());
        request.setAttribute("cartRestaurantName", view.getRestaurantName());
        request.setAttribute("deliveryAddress", address);
        request.getRequestDispatcher(VIEW).forward(request, response);
    }

    private BigDecimal parseMoney(String value) {
        if (value == null) {
            return null;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
