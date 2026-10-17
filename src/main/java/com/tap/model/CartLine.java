package com.tap.model;

import java.math.BigDecimal;

/**
 * View-model for one row of the cart page: a CartItem (id + quantity from the session)
 * combined with the TRUSTED name and price read from MySQL. Never stored in the session.
 */
public class CartLine {
    private int menuId;
    private int restaurantId;
    private String name;
    private String imagePath;
    private BigDecimal unitPrice;
    private int quantity;
    private BigDecimal subtotal;

    public CartLine() { }

    public CartLine(int menuId, int restaurantId, String name, String imagePath,
                    BigDecimal unitPrice, int quantity) {
        this.menuId = menuId;
        this.restaurantId = restaurantId;
        this.name = name;
        this.imagePath = imagePath;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public int getMenuId() { return menuId; }
    public int getRestaurantId() { return restaurantId; }
    public String getName() { return name; }
    public String getImagePath() { return imagePath; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public int getQuantity() { return quantity; }
    public BigDecimal getSubtotal() { return subtotal; }

    @Override
    public String toString() {
        return "CartLine{menuId=" + menuId + ", name='" + name + "', unitPrice=" + unitPrice
                + ", quantity=" + quantity + ", subtotal=" + subtotal + "}";
    }
}
