package com.tap.util;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/** Small, reusable server-side validation helpers. Browser-side checks can be bypassed; these cannot. */
public final class ValidationUtil {

    private static final Pattern EMAIL =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$");
    private static final Pattern USERNAME = Pattern.compile("^[\\p{L}\\p{N} ._-]{2,50}$");
    private static final Pattern PHONE = Pattern.compile("^\\+?[0-9]{7,14}$");
    private static final Pattern IMAGE_PATH = Pattern.compile("^images/[A-Za-z0-9_./-]{1,200}$");

    private ValidationUtil() { }

    /** Null becomes "", otherwise the trimmed text. */
    public static String clean(String value) {
        return value == null ? "" : value.trim();
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        return email != null && email.length() <= 100 && EMAIL.matcher(email).matches();
    }

    public static boolean isValidUsername(String username) {
        return username != null && USERNAME.matcher(username).matches();
    }

    /** Removes spaces and hyphens so "98765 43210" and "98765-43210" are stored the same way. */
    public static String normalizePhone(String phone) {
        return clean(phone).replaceAll("[\\s-]", "");
    }

    /** Phone is optional: an empty value is valid. */
    public static boolean isValidOptionalPhone(String normalizedPhone) {
        return normalizedPhone.isEmpty() || PHONE.matcher(normalizedPhone).matches();
    }

    /** Address is optional here; when present it must fit the database column. */
    public static boolean isValidOptionalAddress(String address) {
        return address.length() <= 255;
    }

    /** 8+ characters, at least one letter and one digit. */
    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean letter = false;
        boolean digit = false;
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) {
                letter = true;
            } else if (Character.isDigit(c)) {
                digit = true;
            }
        }
        return letter && digit;
    }

    /** BCrypt only uses the first 72 bytes, so we refuse longer passwords instead of silently truncating. */
    public static boolean isPasswordLengthOk(String password) {
        return password != null && password.getBytes(StandardCharsets.UTF_8).length <= 72;
    }

    /** Image paths are optional; when given they must be a simple relative path under images/ (no "..", no URLs). */
    public static boolean isValidOptionalImagePath(String path) {
        return path.isEmpty() || (IMAGE_PATH.matcher(path).matches() && !path.contains(".."));
    }

    /** Parses a decimal number such as "199.50". Returns null for null, empty or non-numeric text. */
    public static BigDecimal parseDecimal(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** True if the number has at most this many digits after the decimal point (199.50 and 200 pass for scale 2). */
    public static boolean hasAtMostScale(BigDecimal value, int scale) {
        return value.stripTrailingZeros().scale() <= scale;
    }

    /** Parses a positive int (ids, quantities). Returns null for null, junk, zero or negative values. */
    public static Integer parsePositiveInt(String value) {
        if (value == null) {
            return null;
        }
        try {
            int n = Integer.parseInt(value.trim());
            return n > 0 ? n : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * True if the value is a path inside THIS application (like "/menu?restaurantId=3").
     * Rejects "//evil.com", "http://...", backslashes and control characters, which prevents open redirects.
     */
    public static boolean isSafeLocalPath(String path) {
        if (path == null || path.isEmpty() || path.charAt(0) != '/') {
            return false;
        }
        if (path.length() > 1 && (path.charAt(1) == '/' || path.charAt(1) == '\\')) {
            return false;
        }
        for (int i = 0; i < path.length(); i++) {
            char c = path.charAt(i);
            if (c < 0x20 || c == 0x7f || c == '\\') {
                return false;
            }
        }
        return true;
    }
}
