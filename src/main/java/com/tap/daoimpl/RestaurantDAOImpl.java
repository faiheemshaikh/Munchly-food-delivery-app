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
import com.tap.dao.RestaurantDAO;
import com.tap.model.Restaurant;
import com.tap.util.DBConnection;

public class RestaurantDAOImpl implements RestaurantDAO {

    private static final Logger LOG = Logger.getLogger(RestaurantDAOImpl.class.getName());
    private static final String COLS =
            "restaurant_id, name, description, cuisine, address, image_path, rating, active";

    @Override
    public int addRestaurant(Restaurant r) {
        String sql = "INSERT INTO restaurants (name, description, cuisine, address, image_path, rating, active) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, r.getName());
            ps.setString(2, r.getDescription());
            ps.setString(3, r.getCuisine());
            ps.setString(4, r.getAddress());
            ps.setString(5, r.getImagePath());
            ps.setBigDecimal(6, r.getRating());
            ps.setBoolean(7, r.isActive());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    r.setRestaurantId(keys.getInt(1));
                    return r.getRestaurantId();
                }
            }
            throw new DAOException("Restaurant was inserted but no generated key was returned");
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "addRestaurant failed", e);
            throw new DAOException("Could not add restaurant", e);
        }
    }

    @Override
    public Restaurant getRestaurantById(int restaurantId) {
        String sql = "SELECT " + COLS + " FROM restaurants WHERE restaurant_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, restaurantId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRestaurant(rs) : null;
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "getRestaurantById failed for id " + restaurantId, e);
            throw new DAOException("Could not load restaurant", e);
        }
    }

    @Override
    public List<Restaurant> getAllActiveRestaurants() {
        return queryList("SELECT " + COLS + " FROM restaurants WHERE active = 1 ORDER BY rating DESC, name");
    }

    @Override
    public List<Restaurant> getAllRestaurants() {
        return queryList("SELECT " + COLS + " FROM restaurants ORDER BY restaurant_id");
    }

    @Override
    public boolean updateRestaurant(Restaurant r) {
        String sql = "UPDATE restaurants SET name = ?, description = ?, cuisine = ?, address = ?, "
                   + "image_path = ?, rating = ?, active = ? WHERE restaurant_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, r.getName());
            ps.setString(2, r.getDescription());
            ps.setString(3, r.getCuisine());
            ps.setString(4, r.getAddress());
            ps.setString(5, r.getImagePath());
            ps.setBigDecimal(6, r.getRating());
            ps.setBoolean(7, r.isActive());
            ps.setInt(8, r.getRestaurantId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "updateRestaurant failed for id " + r.getRestaurantId(), e);
            throw new DAOException("Could not update restaurant", e);
        }
    }

    @Override
    public boolean setRestaurantActive(int restaurantId, boolean active) {
        String sql = "UPDATE restaurants SET active = ? WHERE restaurant_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, active);
            ps.setInt(2, restaurantId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "setRestaurantActive failed for id " + restaurantId, e);
            throw new DAOException("Could not change restaurant status", e);
        }
    }

    /** Runs a parameter-free SELECT and maps every row. */
    private List<Restaurant> queryList(String sql) {
        List<Restaurant> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRestaurant(rs));
            }
            return list;
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Restaurant list query failed", e);
            throw new DAOException("Could not load restaurants", e);
        }
    }

    private Restaurant mapRestaurant(ResultSet rs) throws SQLException {
        Restaurant r = new Restaurant();
        r.setRestaurantId(rs.getInt("restaurant_id"));
        r.setName(rs.getString("name"));
        r.setDescription(rs.getString("description"));
        r.setCuisine(rs.getString("cuisine"));
        r.setAddress(rs.getString("address"));
        r.setImagePath(rs.getString("image_path"));
        r.setRating(rs.getBigDecimal("rating"));
        r.setActive(rs.getBoolean("active"));
        return r;
    }
}
