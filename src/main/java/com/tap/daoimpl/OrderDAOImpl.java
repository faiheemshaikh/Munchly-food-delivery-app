package com.tap.daoimpl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.tap.dao.DAOException;
import com.tap.dao.OrderDAO;
import com.tap.dao.OrderItemDAO;
import com.tap.model.FoodOrder;
import com.tap.model.OrderItem;
import com.tap.util.DBConnection;

public class OrderDAOImpl implements OrderDAO {

    private static final Logger LOG = Logger.getLogger(OrderDAOImpl.class.getName());
    private static final String COLS =
            "order_id, user_id, order_date, delivery_address, total_amount, status";

    private final OrderItemDAO orderItemDAO = new OrderItemDAOImpl();

    @Override
    public int createOrder(FoodOrder order, List<OrderItem> items) {
        validateBeforeSaving(order, items);

        String insertOrder = "INSERT INTO orders (user_id, delivery_address, total_amount, status) VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection()) {
            try {
                con.setAutoCommit(false);                       // 1. start the transaction

                int orderId;
                try (PreparedStatement ps = con.prepareStatement(insertOrder, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, order.getUserId());
                    ps.setString(2, order.getDeliveryAddress());
                    ps.setBigDecimal(3, order.getTotalAmount());
                    ps.setString(4, order.getStatus() == null ? FoodOrder.STATUS_PLACED : order.getStatus());
                    ps.executeUpdate();                         // 2. insert the order row
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("Order inserted but no generated key was returned");
                        }
                        orderId = keys.getInt(1);               // 3. read the new order id
                    }
                }

                for (OrderItem item : items) {                  // 4. insert every item with that id,
                    item.setOrderId(orderId);                   //    on the SAME connection
                    orderItemDAO.addOrderItem(con, item);
                }

                con.commit();                                   // 5. everything worked: make it permanent
                order.setOrderId(orderId);
                return orderId;

            } catch (SQLException | RuntimeException e) {
                rollbackQuietly(con);                           // 6. anything failed: undo everything
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                LOG.log(Level.SEVERE, "createOrder failed; transaction rolled back", e);
                throw new DAOException("Could not place order", e);
            } finally {
                try {
                    con.setAutoCommit(true);                    // 7. restore normal connection state
                } catch (SQLException e) {
                    LOG.log(Level.WARNING, "Could not restore auto-commit", e);
                }
            }
        } catch (SQLException e) {                              // failure to get/close the connection itself
            LOG.log(Level.SEVERE, "createOrder: database connection problem", e);
            throw new DAOException("Could not place order", e);
        }
    }

    @Override
    public FoodOrder getOrderById(int orderId) {
        return queryOne("SELECT " + COLS + " FROM orders WHERE order_id = ?", orderId, -1);
    }

    @Override
    public FoodOrder getOrderByIdAndUserId(int orderId, int userId) {
        return queryOne("SELECT " + COLS + " FROM orders WHERE order_id = ? AND user_id = ?", orderId, userId);
    }

    @Override
    public List<FoodOrder> getOrdersByUserId(int userId) {
        String sql = "SELECT " + COLS + " FROM orders WHERE user_id = ? ORDER BY order_date DESC, order_id DESC";
        List<FoodOrder> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapOrder(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "getOrdersByUserId failed for user " + userId, e);
            throw new DAOException("Could not load orders", e);
        }
    }
    
    
    @Override
    public String getRepresentativeImageByOrderId(int orderId) {
        String sql =
            "SELECT m.image_path " +
            "FROM order_items oi " +
            "JOIN menu_items m ON m.menu_id = oi.menu_id " +
            "WHERE oi.order_id = ? " +
            "ORDER BY oi.order_item_id " +
            "LIMIT 1";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, orderId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("image_path");
                }
                return null;
            }

        } catch (SQLException e) {
            LOG.log(Level.SEVERE,
                    "Could not load representative image for order " + orderId, e);
            throw new DAOException("Could not load order image", e);
        }
    }
    
    

    @Override
    public List<FoodOrder> getAllOrders() {
        String sql = "SELECT " + COLS + " FROM orders ORDER BY order_date DESC, order_id DESC";
        List<FoodOrder> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapOrder(rs));
            }
            return list;
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "getAllOrders failed", e);
            throw new DAOException("Could not load orders", e);
        }
    }

    @Override
    public boolean updateOrderStatus(int orderId, String status) {
        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "updateOrderStatus failed for order " + orderId, e);
            throw new DAOException("Could not update order status", e);
        }
    }

    // ---------------------------------------------------------------- helpers

    /** Sanity check so a programming mistake can never store an order whose numbers do not add up. */
    private void validateBeforeSaving(FoodOrder order, List<OrderItem> items) {
        if (order == null || items == null || items.isEmpty()) {
            throw new IllegalArgumentException("An order needs at least one item");
        }
        if (order.getTotalAmount() == null) {
            throw new IllegalArgumentException("Order total is missing");
        }
        BigDecimal sum = BigDecimal.ZERO;
        for (OrderItem item : items) {
            if (item.getQuantity() <= 0 || item.getUnitPrice() == null || item.getSubtotal() == null) {
                throw new IllegalArgumentException("Invalid order item: " + item);
            }
            BigDecimal expected = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            if (expected.compareTo(item.getSubtotal()) != 0) {
                throw new IllegalArgumentException("Subtotal does not equal unitPrice x quantity: " + item);
            }
            sum = sum.add(item.getSubtotal());
        }
        if (sum.compareTo(order.getTotalAmount()) != 0) {
            throw new IllegalArgumentException("Order total does not equal the sum of its items");
        }
    }

    private void rollbackQuietly(Connection con) {
        try {
            con.rollback();
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "Rollback failed", ex);
        }
    }

    /** Runs a SELECT with one id parameter (and an optional second one if userId >= 0). */
    private FoodOrder queryOne(String sql, int orderId, int userId) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            if (userId >= 0) {
                ps.setInt(2, userId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapOrder(rs) : null;
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Order lookup failed for order " + orderId, e);
            throw new DAOException("Could not load order", e);
        }
    }

    private FoodOrder mapOrder(ResultSet rs) throws SQLException {
        FoodOrder o = new FoodOrder();
        o.setOrderId(rs.getInt("order_id"));
        o.setUserId(rs.getInt("user_id"));
        o.setOrderDate(rs.getTimestamp("order_date"));
        o.setDeliveryAddress(rs.getString("delivery_address"));
        o.setTotalAmount(rs.getBigDecimal("total_amount"));
        o.setStatus(rs.getString("status"));
        return o;
    }
}
