package com.tap.model;

import java.math.BigDecimal;

/** Maps to the restaurants table. */
public class Restaurant {
    private int restaurantId;
    private String name;
    private String description;
    private String cuisine;
    private String address;
    private String imagePath;     // relative to the webapp folder, e.g. images/restaurants/x.jpg
    private BigDecimal rating;    // 0.0 - 5.0
    private boolean active;

    public Restaurant() { }

    public Restaurant(int restaurantId, String name, String description, String cuisine,
                      String address, String imagePath, BigDecimal rating, boolean active) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.description = description;
        this.cuisine = cuisine;
        this.address = address;
        this.imagePath = imagePath;
        this.rating = rating;
        this.active = active;
    }

    public int getRestaurantId() { return restaurantId; }
    public void setRestaurantId(int restaurantId) { this.restaurantId = restaurantId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCuisine() { return cuisine; }
    public void setCuisine(String cuisine) { this.cuisine = cuisine; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() {
        return "Restaurant{restaurantId=" + restaurantId + ", name='" + name + "', cuisine='"
                + cuisine + "', rating=" + rating + ", active=" + active + "}";
    }
}
