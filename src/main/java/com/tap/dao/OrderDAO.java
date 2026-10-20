package com.tap.dao;

import java.util.List;

import com.tap.model.FoodOrder;
import com.tap.model.OrderItem;

/** Database operations for the orders table. */
public interface OrderDAO {

    /**
     * Saves an order and all its items in ONE transaction: either everything is stored or nothing is.
     * Fills in order.orderId and returns it.
     * The order's totalAmount must equal the sum of the items' subtotals, and each subtotal must equal
     * unitPrice * quantity; otherwise IllegalArgumentException is thrown before touching the database.
     */
    int createOrder(FoodOrder order, List<OrderItem> items);

    /** Returns the order or null. Does NOT check ownership; customer pages must use getOrderByIdAndUserId. */
    FoodOrder getOrderById(int orderId);
    
    /** Returns one representative menu image for each order. */
    String getRepresentativeImageByOrderId(int orderId);

    /** Returns the order only if it belongs to this user, otherwise null (ownership check in SQL). */
    FoodOrder getOrderByIdAndUserId(int orderId, int userId);

    /** One user's orders, newest first. */
    List<FoodOrder> getOrdersByUserId(int userId);

    /** Admin: all orders, newest first. */
    List<FoodOrder> getAllOrders();

    /** Sets the status (one of the FoodOrder.STATUS_* values). True if a row changed. */
    boolean updateOrderStatus(int orderId, String status);
}
