package com.tap.dao;

import java.util.List;

import com.tap.model.Restaurant;

/** Database operations for the restaurants table. */
public interface RestaurantDAO {

    /** Inserts a restaurant and returns its generated id. */
    int addRestaurant(Restaurant restaurant);

    /** Returns the restaurant (active or not), or null if the id does not exist. */
    Restaurant getRestaurantById(int restaurantId);

    /** Customer home page: only restaurants with active = 1, ordered by rating (best first), then name. */
    List<Restaurant> getAllActiveRestaurants();

    /** Admin: every restaurant including inactive ones. */
    List<Restaurant> getAllRestaurants();

    /** Updates all editable columns. True if a row changed. */
    boolean updateRestaurant(Restaurant restaurant);

    /** Soft delete / restore: sets the active flag. True if a row changed. */
    boolean setRestaurantActive(int restaurantId, boolean active);
}
