package com.tap.dao;

/** Thrown by UserDAO.registerUser when the UNIQUE constraint on users.email is violated. */
public class DuplicateEmailException extends DAOException {
    private static final long serialVersionUID = 1L;

    public DuplicateEmailException(String message, Throwable cause) { super(message, cause); }
}
