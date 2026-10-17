package com.tap.model;

import java.math.BigDecimal;

/** Maps to the menu_items table (one dish offered by one restaurant). */
public class MenuItem {
    private int menuId;
    private int restaurantId;
    private String name;
    private String description;
    private BigDecimal price;
    private String imagePath;
    private String category;
    private boolean available;

    public MenuItem() { }

    public MenuItem(int menuId, int restaurantId, String name, String description,
                    BigDecimal price, String imagePath, String category, boolean available) {
        this.menuId = menuId;
        this.restaurantId = restaurantId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imagePath = imagePath;
        this.category = category;
        this.available = available;
    }

    public int getMenuId() { return menuId; }
    public void setMenuId(int menuId) { this.menuId = menuId; }
    public int getRestaurantId() { return restaurantId; }
    public void setRestaurantId(int restaurantId) { this.restaurantId = restaurantId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public String toString() {
        return "MenuItem{menuId=" + menuId + ", restaurantId=" + restaurantId + ", name='" + name
                + "', price=" + price + ", available=" + available + "}";
    }
}
