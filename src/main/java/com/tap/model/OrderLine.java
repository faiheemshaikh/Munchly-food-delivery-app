package com.tap.model;

import java.math.BigDecimal;

/**
 * View-model: an order_items row joined with the menu item's name and image.
 * It exists so order-details pages can show names without extra queries or SQL in JSPs.
 */
public class OrderLine {
    private int orderItemId;
    private int orderId;
    private int menuId;
    private String itemName;
    private String imagePath;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;

    public OrderLine() { }

    public OrderLine(int orderItemId, int orderId, int menuId, String itemName, String imagePath,
                     int quantity, BigDecimal unitPrice, BigDecimal subtotal) {
        this.orderItemId = orderItemId;
        this.orderId = orderId;
        this.menuId = menuId;
        this.itemName = itemName;
        this.imagePath = imagePath;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = subtotal;
    }

    public int getOrderItemId() { return orderItemId; }
    public void setOrderItemId(int orderItemId) { this.orderItemId = orderItemId; }
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public int getMenuId() { return menuId; }
    public void setMenuId(int menuId) { this.menuId = menuId; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    @Override
    public String toString() {
        return "OrderLine{orderId=" + orderId + ", itemName='" + itemName + "', quantity=" + quantity
                + ", unitPrice=" + unitPrice + ", subtotal=" + subtotal + "}";
    }
}
