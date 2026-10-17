package com.tap.dao;

/**
 * Thrown by DAO classes when a database operation fails.
 * Unchecked (RuntimeException) so servlets are not forced to wrap every call in try/catch,
 * but servlets that talk to the DB should still catch it and show a friendly error page.
 * The message is safe to log; it never contains passwords.
 */
public class DAOException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DAOException(String message) { super(message); }
    public DAOException(String message, Throwable cause) { super(message, cause); }
}
