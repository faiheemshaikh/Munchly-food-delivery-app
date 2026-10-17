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
import com.tap.dao.MenuDAO;
import com.tap.model.MenuItem;
import com.tap.util.DBConnection;

public class MenuDAOImpl implements MenuDAO {

    private static final Logger LOG = Logger.getLogger(MenuDAOImpl.class.getName());
    private static final String COLS =
            "menu_id, restaurant_id, name, description, price, image_path, category, available";

    @Override
    public int addMenuItem(MenuItem m) {
        String sql = "INSERT INTO menu_items (restaurant_id, name, description, price, image_path, category, available) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, m.getRestaurantId());
            ps.setString(2, m.getName());
            ps.setString(3, m.getDescription());
            ps.setBigDecimal(4, m.getPrice());
            ps.setString(5, m.getImagePath());
            ps.setString(6, m.getCategory());
            ps.setBoolean(7, m.isAvailable());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    m.setMenuId(keys.getInt(1));
                    return m.getMenuId();
                }
            }
            throw new DAOException("Menu item was inserted but no generated key was returned");
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "addMenuItem failed", e);
            throw new DAOException("Could not add menu item", e);
        }
    }

    @Override
    public MenuItem getMenuItemById(int menuId) {
        String sql = "SELECT " + COLS + " FROM menu_items WHERE menu_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, menuId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapMenuItem(rs) : null;
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "getMenuItemById failed for id " + menuId, e);
            throw new DAOException("Could not load menu item", e);
        }
    }

    @Override
    public List<MenuItem> getAllMenuItems() {
        return queryList("SELECT " + COLS + " FROM menu_items ORDER BY restaurant_id, category, name", 0);
    }

    @Override
    public List<MenuItem> getMenuItemsByRestaurantId(int restaurantId) {
        return queryList("SELECT " + COLS + " FROM menu_items WHERE restaurant_id = ? ORDER BY category, name",
                restaurantId);
    }

    @Override
    public List<MenuItem> getAvailableMenuItemsByRestaurantId(int restaurantId) {
        return queryList("SELECT " + COLS + " FROM menu_items WHERE restaurant_id = ? AND available = 1 "
                + "ORDER BY category, name", restaurantId);
    }

    @Override
    public boolean updateMenuItem(MenuItem m) {
        String sql = "UPDATE menu_items SET name = ?, description = ?, price = ?, image_path = ?, "
                   + "category = ?, available = ? WHERE menu_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, m.getName());
            ps.setString(2, m.getDescription());
            ps.setBigDecimal(3, m.getPrice());
            ps.setString(4, m.getImagePath());
            ps.setString(5, m.getCategory());
            ps.setBoolean(6, m.isAvailable());
            ps.setInt(7, m.getMenuId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "updateMenuItem failed for id " + m.getMenuId(), e);
            throw new DAOException("Could not update menu item", e);
        }
    }

    @Override
    public boolean setMenuItemAvailability(int menuId, boolean available) {
        String sql = "UPDATE menu_items SET available = ? WHERE menu_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, available);
            ps.setInt(2, menuId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "setMenuItemAvailability failed for id " + menuId, e);
            throw new DAOException("Could not change item availability", e);
        }
    }

    /**
     * Runs a SELECT that has either no parameter (restaurantId == 0 and no '?' in the SQL)
     * or exactly one restaurant-id parameter.
     */
    private List<MenuItem> queryList(String sql, int restaurantId) {
        List<MenuItem> list = new ArrayList<>();
        boolean hasParam = sql.indexOf('?') >= 0;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (hasParam) {
                ps.setInt(1, restaurantId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapMenuItem(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Menu list query failed", e);
            throw new DAOException("Could not load menu items", e);
        }
    }

    private MenuItem mapMenuItem(ResultSet rs) throws SQLException {
        MenuItem m = new MenuItem();
        m.setMenuId(rs.getInt("menu_id"));
        m.setRestaurantId(rs.getInt("restaurant_id"));
        m.setName(rs.getString("name"));
        m.setDescription(rs.getString("description"));
        m.setPrice(rs.getBigDecimal("price"));
        m.setImagePath(rs.getString("image_path"));
        m.setCategory(rs.getString("category"));
        m.setAvailable(rs.getBoolean("available"));
        return m;
    }
}
