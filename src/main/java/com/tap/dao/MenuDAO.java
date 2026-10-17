package com.tap.dao;

import java.util.List;

import com.tap.model.MenuItem;

/** Database operations for the menu_items table. */
public interface MenuDAO {

    /** Inserts a menu item and returns its generated id. */
    int addMenuItem(MenuItem item);

    /** Returns the item, or null if the id does not exist. */
    MenuItem getMenuItemById(int menuId);

    /** Every item of every restaurant (admin/reporting only; never use on a restaurant page). */
    List<MenuItem> getAllMenuItems();

    /** All items of ONE restaurant, available or not (admin). Ordered by category, then name. */
    List<MenuItem> getMenuItemsByRestaurantId(int restaurantId);

    /** Only items with available = 1 for ONE restaurant (customer menu page). */
    List<MenuItem> getAvailableMenuItemsByRestaurantId(int restaurantId);

    /** Updates name, description, price, image, category and availability. True if a row changed. */
    boolean updateMenuItem(MenuItem item);

    /** Marks an item available/unavailable instead of deleting it. True if a row changed. */
    boolean setMenuItemAvailability(int menuId, boolean available);
}
