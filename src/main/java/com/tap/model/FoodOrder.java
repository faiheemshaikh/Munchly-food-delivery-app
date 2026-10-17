package com.tap.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/** Maps to the orders table. Named FoodOrder to avoid clashing with SQL's ORDER keyword. */
public class FoodOrder {
    private int orderId;
    private int userId;
    private Timestamp orderDate;
    private String deliveryAddress;
    private BigDecimal totalAmount;
    private String status;

    public static final String STATUS_PLACED = "PLACED";
    public static final String STATUS_PREPARING = "PREPARING";
    public static final String STATUS_OUT_FOR_DELIVERY = "OUT_FOR_DELIVERY";
    public static final String STATUS_DELIVERED = "DELIVERED";
    public static final String STATUS_CANCELLED = "CANCELLED";

    public FoodOrder() { }

    public FoodOrder(int orderId, int userId, Timestamp orderDate, String deliveryAddress,
                     BigDecimal totalAmount, String status) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderDate = orderDate;
        this.deliveryAddress = deliveryAddress;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public Timestamp getOrderDate() { return orderDate; }
    public void setOrderDate(Timestamp orderDate) { this.orderDate = orderDate; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "FoodOrder{orderId=" + orderId + ", userId=" + userId + ", orderDate=" + orderDate
                + ", totalAmount=" + totalAmount + ", status='" + status + "'}";
    }
}
