package com.tap.dao;

import java.util.List;

import com.tap.model.User;

/** Database operations for the users table. All methods throw DAOException on SQL failure. */
public interface UserDAO {

    /** Inserts a new user (passwordHash must already be a BCrypt hash). Returns the generated user id.
     *  @throws DuplicateEmailException if the email is already registered. */
    int registerUser(User user);

    /** Returns the user, or null if not found. The returned object has NO passwordHash. */
    User getUserById(int userId);

    /** Returns the user (INCLUDING passwordHash, needed for login), or null if not found. */
    User getUserByEmail(String email);

    /** True if an account with this email already exists. */
    boolean emailExists(String email);

    /** Admin use. Returned users have NO passwordHash. */
    List<User> getAllUsers();

    /** Updates username, phone and address only (not email, role or password). True if a row changed. */
    boolean updateUser(User user);

    /** Soft-deletes: sets active = 0 so order history stays intact. True if a row changed. */
    boolean deactivateUser(int userId);
}
