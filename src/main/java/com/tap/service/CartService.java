package com.tap.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.tap.dao.MenuDAO;
import com.tap.dao.RestaurantDAO;
import com.tap.daoimpl.MenuDAOImpl;
import com.tap.daoimpl.RestaurantDAOImpl;
import com.tap.model.CartItem;
import com.tap.model.CartLine;
import com.tap.model.MenuItem;
import com.tap.model.Restaurant;
import com.tap.util.SessionUtil;

import jakarta.servlet.http.HttpSession;

/**
 * All the cart rules in one place, shared by CartServlet and CheckoutServlet so the servlets stay thin.
 *
 * The cart lives in the HTTP session as Map&lt;menuId, CartItem&gt; and stores ONLY ids and quantities.
 * Names and prices are always re-read from MySQL (buildView), so a browser can never choose a price.
 * Rules: one restaurant per cart, 1 to MAX_QTY of each item, only available items.
 * Logging out or a session timeout destroys the session and therefore the cart.
 */
public class CartService {

    public static final int MAX_QTY = 20;

    private final MenuDAO menuDAO = new MenuDAOImpl();
    private final RestaurantDAO restaurantDAO = new RestaurantDAOImpl();

    // ------------------------------------------------------------ results

    /** Outcome of a cart change: success flag, a message for the user, and the item's restaurant (0 if unknown). */
    public static class Result {
        private final boolean success;
        private final String message;
        private final int restaurantId;

        Result(boolean success, String message, int restaurantId) {
            this.success = success;
            this.message = message;
            this.restaurantId = restaurantId;
        }
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public int getRestaurantId() { return restaurantId; }
    }

    /** The cart as it should be shown: trusted lines, trusted total, and notices about removed items. */
    public static class CartView {
        private final List<CartLine> lines;
        private final BigDecimal total;
        private final List<String> notices;
        private final int restaurantId;
        private final String restaurantName;

        CartView(List<CartLine> lines, BigDecimal total, List<String> notices, int restaurantId, String restaurantName) {
            this.lines = lines;
            this.total = total;
            this.notices = notices;
            this.restaurantId = restaurantId;
            this.restaurantName = restaurantName;
        }
        public List<CartLine> getLines() { return lines; }
        public BigDecimal getTotal() { return total; }
        public List<String> getNotices() { return notices; }
        public int getRestaurantId() { return restaurantId; }
        public String getRestaurantName() { return restaurantName; }
    }

    // ------------------------------------------------------------ session access

    /** Returns the session's cart map, creating an empty one on first use. */
    @SuppressWarnings("unchecked")
    public static Map<Integer, CartItem> getCart(HttpSession session) {
        Object existing = session.getAttribute(SessionUtil.CART);
        if (existing instanceof Map) {
            return (Map<Integer, CartItem>) existing;
        }
        Map<Integer, CartItem> cart = new LinkedHashMap<>();
        session.setAttribute(SessionUtil.CART, cart);
        return cart;
    }

    /** Empties the cart (after a successful order, or when the user clears it). */
    public static void clear(HttpSession session) {
        Map<Integer, CartItem> cart = getCart(session);
        synchronized (cart) {
            cart.clear();
            updateCount(session, cart);
        }
    }

    /** Keeps the little number shown next to "Cart" in the navigation bar up to date. */
    private static void updateCount(HttpSession session, Map<Integer, CartItem> cart) {
        int total = 0;
        for (CartItem item : cart.values()) {
            total += item.getQuantity();
        }
        session.setAttribute(SessionUtil.CART_COUNT, total);
    }

    // ------------------------------------------------------------ changes

    /** Adds quantity of a menu item after checking the database: exists, available, restaurant active, same restaurant. */
    public Result add(HttpSession session, int menuId, int quantity) {
        if (quantity < 1 || quantity > MAX_QTY) {
            return new Result(false, "Please choose a quantity between 1 and " + MAX_QTY + ".", 0);
        }
        MenuItem item = menuDAO.getMenuItemById(menuId);
        if (item == null || !item.isAvailable()) {
            return new Result(false, "Sorry, that item is no longer available.", item == null ? 0 : item.getRestaurantId());
        }
        Restaurant restaurant = restaurantDAO.getRestaurantById(item.getRestaurantId());
        if (restaurant == null || !restaurant.isActive()) {
            return new Result(false, "Sorry, this restaurant is not taking orders right now.", 0);
        }

        Map<Integer, CartItem> cart = getCart(session);
        synchronized (cart) {
            if (isFromAnotherRestaurant(cart, item.getRestaurantId())) {
                return new Result(false, "Your cart already has items from another restaurant. "
                        + "Clear your cart or place that order first.", item.getRestaurantId());
            }
            CartItem existing = cart.get(menuId);
            int newQty = (existing == null ? 0 : existing.getQuantity()) + quantity;
            String message = "Added " + item.getName() + " to your cart.";
            if (newQty > MAX_QTY) {
                newQty = MAX_QTY;
                message = "Maximum " + MAX_QTY + " of each item per order. Your cart has " + MAX_QTY + " x " + item.getName() + ".";
            }
            cart.put(menuId, new CartItem(menuId, newQty));
            updateCount(session, cart);
            return new Result(true, message, item.getRestaurantId());
        }
    }

    /** Adds delta (+1 or -1) to an item already in the cart; reaching 0 removes it; capped at MAX_QTY. */
    public Result changeQuantity(HttpSession session, int menuId, int delta) {
        Map<Integer, CartItem> cart = getCart(session);
        synchronized (cart) {
            CartItem item = cart.get(menuId);
            if (item == null) {
                return new Result(false, "That item is not in your cart.", 0);
            }
            int newQty = item.getQuantity() + delta;
            if (newQty <= 0) {
                cart.remove(menuId);
            } else {
                item.setQuantity(Math.min(newQty, MAX_QTY));
            }
            updateCount(session, cart);
            return new Result(true, null, 0);
        }
    }

    public Result remove(HttpSession session, int menuId) {
        Map<Integer, CartItem> cart = getCart(session);
        synchronized (cart) {
            cart.remove(menuId);
            updateCount(session, cart);
            return new Result(true, "Item removed from your cart.", 0);
        }
    }

    // ------------------------------------------------------------ trusted view

    /**
     * Rebuilds the cart from the database. Items that no longer exist, are unavailable, or belong to a
     * deactivated restaurant are removed from the session cart and reported in notices.
     * Prices and the total come ONLY from MySQL.
     */
    public CartView buildView(HttpSession session) {
        Map<Integer, CartItem> cart = getCart(session);
        List<CartLine> lines = new ArrayList<>();
        List<String> notices = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        Map<Integer, Restaurant> restaurantCache = new HashMap<>();
        int restaurantId = 0;
        String restaurantName = "";

        synchronized (cart) {
            Iterator<CartItem> it = cart.values().iterator();
            while (it.hasNext()) {
                CartItem cartItem = it.next();
                MenuItem menu = menuDAO.getMenuItemById(cartItem.getMenuId());
                if (menu == null || !menu.isAvailable()) {
                    notices.add((menu == null ? "An item" : menu.getName())
                            + " is no longer available and was removed from your cart.");
                    it.remove();
                    continue;
                }
                Restaurant restaurant = restaurantCache.get(menu.getRestaurantId());
                if (restaurant == null) {
                    restaurant = restaurantDAO.getRestaurantById(menu.getRestaurantId());
                    restaurantCache.put(menu.getRestaurantId(), restaurant);
                }
                if (restaurant == null || !restaurant.isActive()) {
                    notices.add(menu.getName() + " was removed because its restaurant is not taking orders right now.");
                    it.remove();
                    continue;
                }
                int qty = Math.max(1, Math.min(cartItem.getQuantity(), MAX_QTY));
                CartLine line = new CartLine(menu.getMenuId(), menu.getRestaurantId(), menu.getName(),
                        menu.getImagePath(), menu.getPrice(), qty);
                lines.add(line);
                total = total.add(line.getSubtotal());
                restaurantId = restaurant.getRestaurantId();
                restaurantName = restaurant.getName();
            }
            updateCount(session, cart);
        }
        return new CartView(lines, total, notices, restaurantId, restaurantName);
    }

    // ------------------------------------------------------------ helpers

    /** True if the cart already holds items and the first one we can still find belongs to a different restaurant. */
    private boolean isFromAnotherRestaurant(Map<Integer, CartItem> cart, int restaurantId) {
        for (Integer existingId : cart.keySet()) {
            MenuItem other = menuDAO.getMenuItemById(existingId);
            if (other != null) {
                return other.getRestaurantId() != restaurantId;
            }
        }
        return false;
    }
}
