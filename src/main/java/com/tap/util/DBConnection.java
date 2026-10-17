package com.tap.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hands out JDBC connections using settings from db.properties (found on the classpath,
 * i.e. src/main/resources/db.properties). Credentials are never hardcoded here.
 *
 * Every call to getConnection() opens a NEW connection; callers must close it
 * (DAO classes do that with try-with-resources). For a student project this is fine;
 * a production app would use a connection pool.
 */
public final class DBConnection {

    private static final Logger LOG = Logger.getLogger(DBConnection.class.getName());
    private static final String CONFIG_FILE = "db.properties";
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    private static final String DEFAULT_PARAMS =
            "sslMode=DISABLED&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";

    private static String url;
    private static String username;
    private static String password;
    private static boolean loaded = false;

    private DBConnection() { }

    /** Opens a new connection. The caller must close it. */
    public static Connection getConnection() throws SQLException {
        loadConfig();
        return DriverManager.getConnection(url, username, password);
    }

    private static synchronized void loadConfig() throws SQLException {
        if (loaded) {
            return;
        }
        Properties props = new Properties();
        try (InputStream in = DBConnection.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (in == null) {
                throw new SQLException("Configuration file '" + CONFIG_FILE + "' not found on the classpath. "
                        + "Copy db.properties.example to src/main/resources/db.properties and edit it.");
            }
            props.load(in);
        } catch (IOException e) {
            throw new SQLException("Could not read " + CONFIG_FILE, e);
        }

        String host = props.getProperty("db.host", "localhost").trim();
        String port = props.getProperty("db.port", "3306").trim();
        String dbName = required(props, "db.name");
        username = required(props, "db.username");
        password = props.getProperty("db.password", "");
        String params = props.getProperty("db.params", DEFAULT_PARAMS).trim();

        url = "jdbc:mysql://" + host + ":" + port + "/" + dbName + "?" + params;

        try {
            // Explicit load: makes sure the driver is registered inside Tomcat's web-app class loader.
            Class.forName(DRIVER_CLASS);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL driver (mysql-connector-j) is missing from the classpath.", e);
        }

        // Log where we connect, never the password.
        LOG.log(Level.INFO, "Database configured: host={0}, port={1}, db={2}, user={3}",
                new Object[] { host, port, dbName, username });
        loaded = true;
    }

    private static String required(Properties props, String key) throws SQLException {
        String value = props.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            throw new SQLException("Missing required setting '" + key + "' in " + CONFIG_FILE);
        }
        return value.trim();
    }
}
