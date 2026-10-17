package com.tap.model;

import java.io.Serializable;

/**
 * One entry in the session cart. It stores ONLY the menu id and quantity.
 * Names and prices are always re-read from MySQL, so a tampered browser cannot change a price.
 * Serializable because HTTP sessions may be saved to disk by the server.
 */
public class CartItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private int menuId;
    private int quantity;

    public CartItem() { }

    public CartItem(int menuId, int quantity) {
        this.menuId = menuId;
        this.quantity = quantity;
    }

    public int getMenuId() { return menuId; }
    public void setMenuId(int menuId) { this.menuId = menuId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    @Override
    public String toString() {
        return "CartItem{menuId=" + menuId + ", quantity=" + quantity + "}";
    }
}
