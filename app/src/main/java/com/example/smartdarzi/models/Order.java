package com.example.smartdarzi.models;

import java.io.Serializable;

/**
 * Represents one row in the {@code orders} table, enriched with the product
 * name obtained via a JOIN query.
 */
public class Order implements Serializable {

    private final int    id;
    private final int    userId;
    private final int    productId;
    private final String productName;     // from JOIN with products table
    private final int    quantity;
    private final double totalAmount;
    private final String orderDate;
    private final String status;
    private final String deliveryAddress;

    public Order(int id, int userId, int productId, String productName,
                 int quantity, double totalAmount, String orderDate,
                 String status, String deliveryAddress) {
        this.id              = id;
        this.userId          = userId;
        this.productId       = productId;
        this.productName     = productName;
        this.quantity        = quantity;
        this.totalAmount     = totalAmount;
        this.orderDate       = orderDate;
        this.status          = status;
        this.deliveryAddress = deliveryAddress;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public int    getId()              { return id; }
    public int    getUserId()          { return userId; }
    public int    getProductId()       { return productId; }
    public String getProductName()     { return productName; }
    public int    getQuantity()        { return quantity; }
    public double getTotalAmount()     { return totalAmount; }
    public String getOrderDate()       { return orderDate; }
    public String getStatus()          { return status; }
    public String getDeliveryAddress() { return deliveryAddress; }
}

