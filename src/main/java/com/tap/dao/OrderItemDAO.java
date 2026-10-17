package com.tap.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.tap.model.OrderItem;
import com.tap.model.OrderLine;

/** Database operations for the order_items table. */
public interface OrderItemDAO {

    /** Inserts one order item using its own connection. Returns the generated id. */
    int addOrderItem(OrderItem item);

    /**
     * Inserts one order item using a connection supplied by the caller.
     * OrderDAOImpl.createOrder uses this so the order row and its items share ONE connection,
     * which is what makes a single JDBC transaction possible. The caller owns commit/rollback/close.
     */
    int addOrderItem(Connection con, OrderItem item) throws SQLException;

    /** Plain order_items rows for an order. */
    List<OrderItem> getOrderItemsByOrderId(int orderId);

    /** Order items joined with menu item name and image, for display. */
    List<OrderLine> getOrderLinesByOrderId(int orderId);
}
