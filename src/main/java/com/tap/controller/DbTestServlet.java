package com.tap.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import com.tap.dao.DAOException;
import com.tap.dao.MenuDAO;
import com.tap.dao.RestaurantDAO;
import com.tap.daoimpl.MenuDAOImpl;
import com.tap.daoimpl.RestaurantDAOImpl;
import com.tap.util.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Setup helper: open http://localhost:8080/food-delivery-app/db-test to check the MySQL connection.
 * Only answers requests from the same computer. Delete this class before showing the app to others.
 */
@WebServlet("/db-test")
public class DbTestServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String remote = request.getRemoteAddr();
        boolean local = "127.0.0.1".equals(remote) || "0:0:0:0:0:0:0:1".equals(remote) || "::1".equals(remote);
        if (!local) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.println("This page is available only from the server computer.");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            out.println("1. Connected to MySQL: " + (con.isValid(2) ? "YES" : "NO"));
            out.println("   Database: " + con.getCatalog());
        } catch (SQLException e) {
            getServletContext().log("db-test connection failure", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("1. Connected to MySQL: NO");
            out.println("   Problem : " + e.getMessage());
            out.println("   SQLState: " + e.getSQLState() + "   Error code: " + e.getErrorCode());
            out.println("   See the troubleshooting table in the Phase 3 notes. Full details are in the Tomcat console.");
            return;
        }

        try {
            RestaurantDAO restaurantDAO = new RestaurantDAOImpl();
            MenuDAO menuDAO = new MenuDAOImpl();
            out.println("2. Active restaurants found : " + restaurantDAO.getAllActiveRestaurants().size()
                    + "   (expected 20 after seed.sql)");
            out.println("3. Menu items found         : " + menuDAO.getAllMenuItems().size()
                    + "   (expected 122 after seed.sql)");
            out.println("4. Items for restaurant 1   : " + menuDAO.getAvailableMenuItemsByRestaurantId(1).size()
                    + "   (expected 6)");
            out.println();
            out.println("All checks finished. The DAO layer can read from MySQL.");
        } catch (DAOException e) {
            getServletContext().log("db-test DAO failure", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("Connected, but a query failed. Did you run schema.sql and seed.sql?");
            out.println("Details are in the Tomcat console.");
        }
    }
}
