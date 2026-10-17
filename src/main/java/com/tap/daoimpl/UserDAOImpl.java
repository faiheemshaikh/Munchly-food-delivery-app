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
import com.tap.dao.DuplicateEmailException;
import com.tap.dao.UserDAO;
import com.tap.model.User;
import com.tap.util.DBConnection;

public class UserDAOImpl implements UserDAO {

    private static final Logger LOG = Logger.getLogger(UserDAOImpl.class.getName());
    private static final int MYSQL_DUPLICATE_ENTRY = 1062;

    // Columns WITHOUT the hash: used everywhere except login.
    private static final String COLS_SAFE =
            "user_id, username, email, phone, address, role, active, created_at";
    private static final String COLS_WITH_HASH =
            "user_id, username, email, password_hash, phone, address, role, active, created_at";

    @Override
    public int registerUser(User user) {
        String sql = "INSERT INTO users (username, email, password_hash, phone, address, role) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getPhone());
            ps.setString(5, user.getAddress());
            ps.setString(6, user.getRole() == null ? User.ROLE_CUSTOMER : user.getRole());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    user.setUserId(id);
                    return id;
                }
            }
            throw new DAOException("User was inserted but no generated key was returned");
        } catch (SQLException e) {
            if (e.getErrorCode() == MYSQL_DUPLICATE_ENTRY) {
                throw new DuplicateEmailException("Email already registered", e);
            }
            LOG.log(Level.SEVERE, "registerUser failed", e);
            throw new DAOException("Could not register user", e);
        }
    }

    @Override
    public User getUserById(int userId) {
        String sql = "SELECT " + COLS_SAFE + " FROM users WHERE user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapUser(rs, false) : null;
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "getUserById failed for id " + userId, e);
            throw new DAOException("Could not load user", e);
        }
    }

    @Override
    public User getUserByEmail(String email) {
        String sql = "SELECT " + COLS_WITH_HASH + " FROM users WHERE email = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapUser(rs, true) : null;
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "getUserByEmail failed", e);
            throw new DAOException("Could not load user", e);
        }
    }

    @Override
    public boolean emailExists(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "emailExists failed", e);
            throw new DAOException("Could not check email", e);
        }
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT " + COLS_SAFE + " FROM users ORDER BY user_id";
        List<User> users = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(mapUser(rs, false));
            }
            return users;
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "getAllUsers failed", e);
            throw new DAOException("Could not load users", e);
        }
    }

    @Override
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET username = ?, phone = ?, address = ? WHERE user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPhone());
            ps.setString(3, user.getAddress());
            ps.setInt(4, user.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "updateUser failed for id " + user.getUserId(), e);
            throw new DAOException("Could not update user", e);
        }
    }

    @Override
    public boolean deactivateUser(int userId) {
        String sql = "UPDATE users SET active = 0 WHERE user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "deactivateUser failed for id " + userId, e);
            throw new DAOException("Could not deactivate user", e);
        }
    }

    /** Converts the current ResultSet row into a User. Hash is read only when it was selected. */
    private User mapUser(ResultSet rs, boolean withHash) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("user_id"));
        u.setUsername(rs.getString("username"));
        u.setEmail(rs.getString("email"));
        if (withHash) {
            u.setPasswordHash(rs.getString("password_hash"));
        }
        u.setPhone(rs.getString("phone"));
        u.setAddress(rs.getString("address"));
        u.setRole(rs.getString("role"));
        u.setActive(rs.getBoolean("active"));
        u.setCreatedAt(rs.getTimestamp("created_at"));
        return u;
    }
}
