package com.tap.controller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.tap.dao.DAOException;
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
 * GET  /cart : show the cart (rebuilt from trusted database values).
 * POST /cart : action = add | increase | decrease | remove | clear, then redirect (Redirect-after-POST).
 * The browser only ever sends an action, a menu id and a quantity; never a price or a total.
 */
@WebServlet("/cart")
public class CartServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(CartServlet.class.getName());

    private final CartService cartService = new CartService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        CartView view = cartService.buildView(request.getSession());
        request.setAttribute("cartLines", view.getLines());
        request.setAttribute("cartTotal", view.getTotal());
        request.setAttribute("cartNotices", view.getNotices());
        request.setAttribute("cartRestaurantId", view.getRestaurantId());
        request.setAttribute("cartRestaurantName", view.getRestaurantName());
        request.setAttribute("maxQty", CartService.MAX_QTY);
        request.getRequestDispatcher("/WEB-INF/views/cart.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String ctx = request.getContextPath();
        String action = ValidationUtil.clean(request.getParameter("action"));
        Integer menuId = ValidationUtil.parsePositiveInt(request.getParameter("menuId"));
        String redirectTo = ctx + "/cart";

        try {
            switch (action) {
                case "add": {
                    String qtyParam = request.getParameter("quantity");
                    Integer quantity = ValidationUtil.isBlank(qtyParam) ? Integer.valueOf(1)
                            : ValidationUtil.parsePositiveInt(qtyParam);
                    if (menuId == null || quantity == null) {
                        SessionUtil.flashError(session, "That request was not valid.");
                        break;
                    }
                    CartService.Result result = cartService.add(session, menuId, quantity);
                    if (result.isSuccess()) {
                        SessionUtil.flashSuccess(session, result.getMessage());
                        // Stay on the menu so the customer can keep adding items.
                        redirectTo = ctx + "/menu?restaurantId=" + result.getRestaurantId();
                    } else {
                        SessionUtil.flashError(session, result.getMessage());
                    }
                    break;
                }
                case "increase":
                case "decrease": {
                    if (menuId == null) {
                        SessionUtil.flashError(session, "That request was not valid.");
                        break;
                    }
                    int delta = "increase".equals(action) ? 1 : -1;
                    CartService.Result result = cartService.changeQuantity(session, menuId, delta);
                    if (!result.isSuccess()) {
                        SessionUtil.flashError(session, result.getMessage());
                    }
                    break;
                }
                case "remove": {
                    if (menuId == null) {
                        SessionUtil.flashError(session, "That request was not valid.");
                        break;
                    }
                    cartService.remove(session, menuId);
                    break;
                }
                case "clear":
                    CartService.clear(session);
                    SessionUtil.flashSuccess(session, "Your cart is now empty.");
                    break;
                default:
                    SessionUtil.flashError(session, "That request was not valid.");
            }
        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Cart action '" + action + "' failed", e);
            SessionUtil.flashError(session, "Something went wrong. Please try again.");
        }
        response.sendRedirect(redirectTo);
    }
}
