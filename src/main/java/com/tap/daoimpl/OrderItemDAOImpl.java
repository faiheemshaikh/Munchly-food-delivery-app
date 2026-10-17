package com.tap.daoimpl;

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
import com.tap.dao.OrderItemDAO;
import com.tap.model.OrderItem;
import com.tap.model.OrderLine;
import com.tap.util.DBConnection;

public class OrderItemDAOImpl implements OrderItemDAO {

    private static final Logger LOG = Logger.getLogger(OrderItemDAOImpl.class.getName());

    private static final String INSERT_SQL =
            "INSERT INTO order_items (order_id, menu_id, quantity, unit_price, subtotal) VALUES (?, ?, ?, ?, ?)";

    @Override
    public int addOrderItem(OrderItem item) {
        try (Connection con = DBConnection.getConnection()) {
            return addOrderItem(con, item);
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "addOrderItem failed", e);
            throw new DAOException("Could not add order item", e);
        }
    }

    /** Uses the caller's connection and does NOT close it: the caller controls the transaction. */
    @Override
    public int addOrderItem(Connection con, OrderItem item) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, item.getOrderId());
            ps.setInt(2, item.getMenuId());
            ps.setInt(3, item.getQuantity());
            ps.setBigDecimal(4, item.getUnitPrice());
            ps.setBigDecimal(5, item.getSubtotal());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    item.setOrderItemId(keys.getInt(1));
                    return item.getOrderItemId();
                }
            }
            throw new SQLException("Order item inserted but no generated key was returned");
        }
    }

    @Override
    public List<OrderItem> getOrderItemsByOrderId(int orderId) {
        String sql = "SELECT order_item_id, order_id, menu_id, quantity, unit_price, subtotal "
                   + "FROM order_items WHERE order_id = ? ORDER BY order_item_id";
        List<OrderItem> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderItem oi = new OrderItem();
                    oi.setOrderItemId(rs.getInt("order_item_id"));
                    oi.setOrderId(rs.getInt("order_id"));
                    oi.setMenuId(rs.getInt("menu_id"));
                    oi.setQuantity(rs.getInt("quantity"));
                    oi.setUnitPrice(rs.getBigDecimal("unit_price"));
                    oi.setSubtotal(rs.getBigDecimal("subtotal"));
                    list.add(oi);
                }
            }
            return list;
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "getOrderItemsByOrderId failed for order " + orderId, e);
            throw new DAOException("Could not load order items", e);
        }
    }

    @Override
    public List<OrderLine> getOrderLinesByOrderId(int orderId) {
        String sql = "SELECT oi.order_item_id, oi.order_id, oi.menu_id, m.name, m.image_path, "
                   + "oi.quantity, oi.unit_price, oi.subtotal "
                   + "FROM order_items oi JOIN menu_items m ON m.menu_id = oi.menu_id "
                   + "WHERE oi.order_id = ? ORDER BY oi.order_item_id";
        List<OrderLine> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderLine ol = new OrderLine();
                    ol.setOrderItemId(rs.getInt("order_item_id"));
                    ol.setOrderId(rs.getInt("order_id"));
                    ol.setMenuId(rs.getInt("menu_id"));
                    ol.setItemName(rs.getString("name"));
                    ol.setImagePath(rs.getString("image_path"));
                    ol.setQuantity(rs.getInt("quantity"));
                    ol.setUnitPrice(rs.getBigDecimal("unit_price"));
                    ol.setSubtotal(rs.getBigDecimal("subtotal"));
                    list.add(ol);
                }
            }
            return list;
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "getOrderLinesByOrderId failed for order " + orderId, e);
            throw new DAOException("Could not load order lines", e);
        }
    }
}
