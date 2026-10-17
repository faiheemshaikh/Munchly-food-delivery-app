package com.tap.model;

import java.math.BigDecimal;

/** Maps to the order_items table (one line of an order). */
public class OrderItem {
    private int orderItemId;
    private int orderId;
    private int menuId;
    private int quantity;
    private BigDecimal unitPrice;   // price at the time the order was placed
    private BigDecimal subtotal;    // quantity * unitPrice

    public OrderItem() { }

    public OrderItem(int orderItemId, int orderId, int menuId, int quantity,
                     BigDecimal unitPrice, BigDecimal subtotal) {
        this.orderItemId = orderItemId;
        this.orderId = orderId;
        this.menuId = menuId;
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
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    @Override
    public String toString() {
        return "OrderItem{orderItemId=" + orderItemId + ", orderId=" + orderId + ", menuId=" + menuId
                + ", quantity=" + quantity + ", unitPrice=" + unitPrice + ", subtotal=" + subtotal + "}";
    }
}
