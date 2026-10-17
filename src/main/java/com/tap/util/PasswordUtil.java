package com.tap.util;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * Password hashing with BCrypt. BCrypt is deliberately slow and adds a random salt to every hash,
 * so two users with the same password get different hashes and brute-forcing is expensive.
 * The hash string (60 characters) contains the algorithm, cost and salt, so nothing else needs storing.
 */
public final class PasswordUtil {

    /** Work factor: each +1 doubles the time. 12 takes roughly a quarter of a second. */
    private static final int COST = 12;

    private PasswordUtil() { }

    /** Returns a BCrypt hash of the plain-text password. */
    public static String hash(String plainPassword) {
        return BCrypt.withDefaults().hashToString(COST, plainPassword.toCharArray());
    }

    /** True if the plain-text password matches the stored hash. Never throws for malformed hashes. */
    public static boolean verify(String plainPassword, String storedHash) {
        if (plainPassword == null || storedHash == null) {
            return false;
        }
        try {
            return BCrypt.verifyer().verify(plainPassword.toCharArray(), storedHash).verified;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
